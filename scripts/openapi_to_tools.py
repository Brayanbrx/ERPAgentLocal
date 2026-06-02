#!/usr/bin/env python3
"""Generate Android tool catalog JSON from the OpenAPI contract.

This parser intentionally supports the OpenAPI subset used by this project and
keeps the Android app free from runtime YAML parsing.
"""

from __future__ import annotations

import argparse
import json
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Optional, Tuple


HTTP_METHODS = {"get", "post", "patch", "delete", "put"}


@dataclass
class SchemaInfo:
    required: List[str]
    properties: List[str]


def indent_of(line: str) -> int:
    return len(line) - len(line.lstrip(" "))


def clean_value(value: str) -> str:
    value = value.strip()
    if value.startswith('"') and value.endswith('"'):
        return value[1:-1]
    if value.startswith("'") and value.endswith("'"):
        return value[1:-1]
    return value


def parse_key_value(text: str) -> Tuple[str, str]:
    key, _, value = text.partition(":")
    return key.strip(), clean_value(value)


def collect_block(lines: List[str], start_index: int, parent_indent: int) -> Tuple[List[str], int]:
    block: List[str] = []
    index = start_index
    while index < len(lines):
        line = lines[index]
        stripped = line.strip()
        if stripped and indent_of(line) <= parent_indent:
            break
        block.append(line)
        index += 1
    return block, index


def parse_list_after(lines: List[str], start_index: int, parent_indent: int) -> Tuple[List[str], int]:
    values: List[str] = []
    index = start_index
    while index < len(lines):
        line = lines[index]
        stripped = line.strip()
        if not stripped:
            index += 1
            continue
        if indent_of(line) <= parent_indent:
            break
        if stripped.startswith("- "):
            values.append(clean_value(stripped[2:]))
        index += 1
    return values, index


def parse_schemas(lines: List[str]) -> Dict[str, SchemaInfo]:
    schemas: Dict[str, SchemaInfo] = {}
    schema_name: Optional[str] = None
    schema_indent = 0
    index = 0
    in_schemas = False

    while index < len(lines):
        line = lines[index]
        stripped = line.strip()

        if stripped == "schemas:" and indent_of(line) == 2:
            in_schemas = True
            index += 1
            continue

        if not in_schemas:
            index += 1
            continue

        if stripped and indent_of(line) < 4:
            break

        if stripped.endswith(":") and indent_of(line) == 4:
            schema_name = stripped[:-1]
            schema_indent = indent_of(line)
            schemas[schema_name] = SchemaInfo(required=[], properties=[])
            index += 1
            continue

        if schema_name is None:
            index += 1
            continue

        if stripped == "required:" and indent_of(line) == schema_indent + 2:
            required, next_index = parse_list_after(lines, index + 1, indent_of(line))
            schemas[schema_name].required = required
            index = next_index
            continue

        if stripped == "properties:" and indent_of(line) == schema_indent + 2:
            index += 1
            while index < len(lines):
                prop_line = lines[index]
                prop_text = prop_line.strip()
                prop_indent = indent_of(prop_line)
                if prop_text and prop_indent <= schema_indent + 2:
                    break
                if prop_text.endswith(":") and prop_indent == schema_indent + 4:
                    schemas[schema_name].properties.append(prop_text[:-1])
                index += 1
            continue

        index += 1

    return schemas


def first_ref_schema(lines: Iterable[str]) -> Optional[str]:
    for line in lines:
        stripped = line.strip()
        if stripped.startswith("$ref:"):
            _, value = parse_key_value(stripped)
            marker = "#/components/schemas/"
            if marker in value:
                return value.split(marker, 1)[1]
    return None


def parse_parameters(lines: List[str]) -> List[Dict[str, object]]:
    parameters: List[Dict[str, object]] = []
    index = 0
    while index < len(lines):
        stripped = lines[index].strip()
        if stripped == "parameters:":
            parent_indent = indent_of(lines[index])
            index += 1
            current: Dict[str, object] = {}
            while index < len(lines):
                line = lines[index]
                text = line.strip()
                current_indent = indent_of(line)
                if text and current_indent <= parent_indent:
                    break
                if text.startswith("- "):
                    if current:
                        parameters.append(current)
                    current = {}
                    item = text[2:]
                    if ":" in item:
                        key, value = parse_key_value(item)
                        current[key] = value
                elif ":" in text and current:
                    key, value = parse_key_value(text)
                    if key == "required":
                        current[key] = value.lower() == "true"
                    elif key in {"name", "in", "description"}:
                        current[key] = value
                index += 1
            if current:
                parameters.append(current)
            continue
        index += 1
    return parameters


def parse_operation(path_name: str, method: str, block: List[str], schemas: Dict[str, SchemaInfo]) -> Dict[str, object]:
    operation_id = ""
    summary = ""
    description = ""
    top_indent = min((indent_of(line) for line in block if line.strip()), default=0)

    for line in block:
        stripped = line.strip()
        if ":" not in stripped or indent_of(line) != top_indent:
            continue
        key, value = parse_key_value(stripped)
        if key == "operationId":
            operation_id = value
        elif key == "summary":
            summary = value
        elif key == "description":
            description = value

    if not operation_id:
        raise ValueError(f"Missing operationId for {method.upper()} {path_name}")

    parameters = parse_parameters(block)
    path_params = [p["name"] for p in parameters if p.get("in") == "path"]
    query_params = [p["name"] for p in parameters if p.get("in") == "query"]
    required_params = [p["name"] for p in parameters if p.get("required") is True]

    request_body_block: List[str] = []
    for index, line in enumerate(block):
        if line.strip() == "requestBody:" and indent_of(line) == top_indent:
            request_body_block, _ = collect_block(block, index + 1, indent_of(line))
            break

    request_schema_name = first_ref_schema(request_body_block)
    body_required: List[str] = []
    body_optional: List[str] = []
    if request_schema_name and request_schema_name in schemas:
        schema = schemas[request_schema_name]
        body_required = schema.required
        body_optional = [name for name in schema.properties if name not in set(body_required)]

    rendered_path = path_name
    if query_params:
        rendered_query = "&".join(f"{name}={{{name}}}" for name in query_params)
        rendered_path = f"{path_name}?{rendered_query}"

    required_arguments = unique(
        [name for name in path_params if name in required_params]
        + [name for name in query_params if name in required_params]
        + body_required
    )
    optional_arguments = unique(
        [name for name in query_params if name not in required_params]
        + body_optional
    )

    return {
        "name": operation_id,
        "operationId": operation_id,
        "method": method.upper(),
        "path": rendered_path,
        "description": description or summary,
        "requiredArguments": required_arguments,
        "optionalArguments": optional_arguments,
    }


def unique(values: Iterable[str]) -> List[str]:
    result: List[str] = []
    for value in values:
        if value and value not in result:
            result.append(value)
    return result


def generate_tools(openapi_text: str) -> List[Dict[str, object]]:
    lines = openapi_text.splitlines()
    schemas = parse_schemas(lines)
    tools: List[Dict[str, object]] = []

    index = 0
    in_paths = False
    current_path: Optional[str] = None

    while index < len(lines):
        line = lines[index]
        stripped = line.strip()

        if stripped == "paths:" and indent_of(line) == 0:
            in_paths = True
            index += 1
            continue

        if not in_paths:
            index += 1
            continue

        if stripped == "components:" and indent_of(line) == 0:
            break

        if stripped.endswith(":") and indent_of(line) == 2:
            current_path = stripped[:-1]
            index += 1
            continue

        if current_path and stripped.endswith(":") and indent_of(line) == 4:
            method = stripped[:-1]
            if method in HTTP_METHODS:
                block, next_index = collect_block(lines, index + 1, indent_of(line))
                tools.append(parse_operation(current_path, method, block, schemas))
                index = next_index
                continue

        index += 1

    return tools


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", default="app/src/main/assets/openapi.yaml")
    parser.add_argument("--output", default="app/src/main/assets/tools.generated.json")
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()

    input_path = Path(args.input)
    output_path = Path(args.output)
    generated = json.dumps(
        generate_tools(input_path.read_text(encoding="utf-8")),
        ensure_ascii=False,
        indent=2,
    ) + "\n"

    if args.check:
        current = output_path.read_text(encoding="utf-8")
        if current != generated:
            raise SystemExit("tools.generated.json is out of date. Run scripts/openapi_to_tools.py.")
        return

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(generated, encoding="utf-8")


if __name__ == "__main__":
    main()
