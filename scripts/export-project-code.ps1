param(
    [string]$OutputPath = "project-code.md"
)

$ErrorActionPreference = "Stop"

$root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$outputFullPath = Join-Path $root $OutputPath

$excludedDirectories = @(
    ".gradle",
    ".idea",
    ".kotlin",
    ".git",
    "build",
    ".externalNativeBuild",
    ".cxx"
)

$includedExtensions = @(
    ".kt",
    ".kts",
    ".java",
    ".xml",
    ".yaml",
    ".yml",
    ".toml",
    ".properties",
    ".gradle",
    ".md",
    ".txt",
    ".json",
    ".pro",
    ".gitignore"
)

$languageByExtension = @{
    ".kt" = "kotlin"
    ".kts" = "kotlin"
    ".java" = "java"
    ".xml" = "xml"
    ".yaml" = "yaml"
    ".yml" = "yaml"
    ".toml" = "toml"
    ".properties" = "properties"
    ".gradle" = "groovy"
    ".md" = "markdown"
    ".txt" = "text"
    ".json" = "json"
    ".pro" = "proguard"
    ".gitignore" = "gitignore"
}

function Test-IsExcludedPath {
    param([string]$Path)

    $relativePath = Get-RelativePath $Path
    $parts = $relativePath -split '[\\/]+'

    foreach ($part in $parts) {
        if ($excludedDirectories -contains $part) {
            return $true
        }
    }

    return $false
}

function Get-RelativePath {
    param([string]$Path)

    $rootUri = [System.Uri]::new(($root.TrimEnd("\") + "\"))
    $pathUri = [System.Uri]::new($Path)
    return [System.Uri]::UnescapeDataString($rootUri.MakeRelativeUri($pathUri).ToString()).Replace("/", "\")
}

function Get-MarkdownLanguage {
    param([System.IO.FileInfo]$File)

    if ($languageByExtension.ContainsKey($File.Extension)) {
        return $languageByExtension[$File.Extension]
    }

    if ($languageByExtension.ContainsKey($File.Name)) {
        return $languageByExtension[$File.Name]
    }

    return "text"
}

$files = Get-ChildItem -Path $root -File -Recurse |
    Where-Object {
        -not (Test-IsExcludedPath $_.FullName) -and
        $_.FullName -ne $outputFullPath -and
        (
            $includedExtensions -contains $_.Extension -or
            $includedExtensions -contains $_.Name
        )
    } |
    Sort-Object FullName

$builder = [System.Text.StringBuilder]::new()
$generatedAt = Get-Date -Format "yyyy-MM-dd HH:mm:ss zzz"

[void]$builder.AppendLine("# ERPAgentLocal - Project Code")
[void]$builder.AppendLine()
[void]$builder.AppendLine("Generated at: $generatedAt")
[void]$builder.AppendLine()
[void]$builder.AppendLine("Root: $root")
[void]$builder.AppendLine()
[void]$builder.AppendLine("Files included: $($files.Count)")
[void]$builder.AppendLine()
[void]$builder.AppendLine("Excluded directories: $($excludedDirectories -join ', ')")
[void]$builder.AppendLine()
[void]$builder.AppendLine("## Table of Contents")
[void]$builder.AppendLine()

foreach ($file in $files) {
    $relativePath = (Get-RelativePath $file.FullName).Replace("\", "/")
    [void]$builder.AppendLine("- [$relativePath](#$($relativePath.ToLowerInvariant() -replace '[^a-z0-9]+', '-'))")
}

[void]$builder.AppendLine()
[void]$builder.AppendLine("## Files")
[void]$builder.AppendLine()

foreach ($file in $files) {
    $relativePath = (Get-RelativePath $file.FullName).Replace("\", "/")
    $language = Get-MarkdownLanguage $file
    $content = Get-Content -Path $file.FullName -Raw
    if ($null -eq $content) {
        $content = ""
    }

    [void]$builder.AppendLine("### $relativePath")
    [void]$builder.AppendLine()
    [void]$builder.AppendLine("````$language")
    [void]$builder.AppendLine($content.TrimEnd())
    [void]$builder.AppendLine("````")
    [void]$builder.AppendLine()
}

[System.IO.File]::WriteAllText($outputFullPath, $builder.ToString(), [System.Text.UTF8Encoding]::new($false))

Write-Host "Markdown generated: $outputFullPath"
Write-Host "Files included: $($files.Count)"
