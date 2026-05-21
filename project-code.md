# ERPAgentLocal - Project Code

Generated at: 2026-05-21 11:52:02 -04:00

Root: C:\Users\braya\Desktop\Topicos\ERPAgentLocal

Files included: 68

Excluded directories: .gradle, .idea, .kotlin, .git, build, .externalNativeBuild, .cxx

## Table of Contents

- [.gitignore](#-gitignore)
- [app/.gitignore](#app-gitignore)
- [app/build.gradle.kts](#app-build-gradle-kts)
- [app/proguard-rules.pro](#app-proguard-rules-pro)
- [app/src/androidTest/java/com/brayan/erpagentlocal/ExampleInstrumentedTest.kt](#app-src-androidtest-java-com-brayan-erpagentlocal-exampleinstrumentedtest-kt)
- [app/src/main/AndroidManifest.xml](#app-src-main-androidmanifest-xml)
- [app/src/main/assets/openapi.yaml](#app-src-main-assets-openapi-yaml)
- [app/src/main/assets/tools.json](#app-src-main-assets-tools-json)
- [app/src/main/java/com/brayan/erpagentlocal/agent/AgentAction.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-agentaction-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/AgentActionParser.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-agentactionparser-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/AgentError.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-agenterror-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/AgentErrorMapper.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-agenterrormapper-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/AgentErrorType.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-agenterrortype-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/AgentMemory.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-agentmemory-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/AgentState.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-agentstate-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/AgentStateUpdater.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-agentstateupdater-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/AgentToolCallingMode.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-agenttoolcallingmode-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/ExecutedTool.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-executedtool-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/NativeToolCallingEvaluator.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-nativetoolcallingevaluator-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/NativeToolCallingReadiness.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-nativetoolcallingreadiness-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/ToolCallValidator.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-toolcallvalidator-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/ToolCatalog.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-toolcatalog-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/ToolCatalogLoader.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-toolcatalogloader-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/ToolDefinition.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-tooldefinition-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/ToolExecutionResult.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-toolexecutionresult-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/ToolRegistry.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-toolregistry-kt)
- [app/src/main/java/com/brayan/erpagentlocal/agent/ValidationResult.kt](#app-src-main-java-com-brayan-erpagentlocal-agent-validationresult-kt)
- [app/src/main/java/com/brayan/erpagentlocal/ai/AgentService.kt](#app-src-main-java-com-brayan-erpagentlocal-ai-agentservice-kt)
- [app/src/main/java/com/brayan/erpagentlocal/ai/LocalModelService.kt](#app-src-main-java-com-brayan-erpagentlocal-ai-localmodelservice-kt)
- [app/src/main/java/com/brayan/erpagentlocal/ai/ModelGenerationResult.kt](#app-src-main-java-com-brayan-erpagentlocal-ai-modelgenerationresult-kt)
- [app/src/main/java/com/brayan/erpagentlocal/ai/ModelStatus.kt](#app-src-main-java-com-brayan-erpagentlocal-ai-modelstatus-kt)
- [app/src/main/java/com/brayan/erpagentlocal/ai/PromptProvider.kt](#app-src-main-java-com-brayan-erpagentlocal-ai-promptprovider-kt)
- [app/src/main/java/com/brayan/erpagentlocal/data/AgentApiClient.kt](#app-src-main-java-com-brayan-erpagentlocal-data-agentapiclient-kt)
- [app/src/main/java/com/brayan/erpagentlocal/data/ApiConfig.kt](#app-src-main-java-com-brayan-erpagentlocal-data-apiconfig-kt)
- [app/src/main/java/com/brayan/erpagentlocal/data/HttpMethod.kt](#app-src-main-java-com-brayan-erpagentlocal-data-httpmethod-kt)
- [app/src/main/java/com/brayan/erpagentlocal/data/ToolExecutor.kt](#app-src-main-java-com-brayan-erpagentlocal-data-toolexecutor-kt)
- [app/src/main/java/com/brayan/erpagentlocal/MainActivity.kt](#app-src-main-java-com-brayan-erpagentlocal-mainactivity-kt)
- [app/src/main/java/com/brayan/erpagentlocal/model/ApiResponse.kt](#app-src-main-java-com-brayan-erpagentlocal-model-apiresponse-kt)
- [app/src/main/java/com/brayan/erpagentlocal/model/CustomerDtos.kt](#app-src-main-java-com-brayan-erpagentlocal-model-customerdtos-kt)
- [app/src/main/java/com/brayan/erpagentlocal/model/InventoryDtos.kt](#app-src-main-java-com-brayan-erpagentlocal-model-inventorydtos-kt)
- [app/src/main/java/com/brayan/erpagentlocal/model/ProductDtos.kt](#app-src-main-java-com-brayan-erpagentlocal-model-productdtos-kt)
- [app/src/main/java/com/brayan/erpagentlocal/model/PurchaseDtos.kt](#app-src-main-java-com-brayan-erpagentlocal-model-purchasedtos-kt)
- [app/src/main/java/com/brayan/erpagentlocal/model/SaleDtos.kt](#app-src-main-java-com-brayan-erpagentlocal-model-saledtos-kt)
- [app/src/main/java/com/brayan/erpagentlocal/ui/components/MessageBubble.kt](#app-src-main-java-com-brayan-erpagentlocal-ui-components-messagebubble-kt)
- [app/src/main/java/com/brayan/erpagentlocal/ui/components/StatusCard.kt](#app-src-main-java-com-brayan-erpagentlocal-ui-components-statuscard-kt)
- [app/src/main/java/com/brayan/erpagentlocal/ui/components/TraceViewer.kt](#app-src-main-java-com-brayan-erpagentlocal-ui-components-traceviewer-kt)
- [app/src/main/java/com/brayan/erpagentlocal/ui/screens/ChatScreen.kt](#app-src-main-java-com-brayan-erpagentlocal-ui-screens-chatscreen-kt)
- [app/src/main/java/com/brayan/erpagentlocal/ui/theme/ErpColors.kt](#app-src-main-java-com-brayan-erpagentlocal-ui-theme-erpcolors-kt)
- [app/src/main/java/com/brayan/erpagentlocal/util/JsonUtils.kt](#app-src-main-java-com-brayan-erpagentlocal-util-jsonutils-kt)
- [app/src/main/java/com/brayan/erpagentlocal/util/ModelFileManager.kt](#app-src-main-java-com-brayan-erpagentlocal-util-modelfilemanager-kt)
- [app/src/main/res/drawable/ic_launcher_background.xml](#app-src-main-res-drawable-ic-launcher-background-xml)
- [app/src/main/res/drawable/ic_launcher_foreground.xml](#app-src-main-res-drawable-ic-launcher-foreground-xml)
- [app/src/main/res/mipmap-anydpi/ic_launcher.xml](#app-src-main-res-mipmap-anydpi-ic-launcher-xml)
- [app/src/main/res/mipmap-anydpi/ic_launcher_round.xml](#app-src-main-res-mipmap-anydpi-ic-launcher-round-xml)
- [app/src/main/res/values/colors.xml](#app-src-main-res-values-colors-xml)
- [app/src/main/res/values/strings.xml](#app-src-main-res-values-strings-xml)
- [app/src/main/res/values/themes.xml](#app-src-main-res-values-themes-xml)
- [app/src/main/res/values-night/themes.xml](#app-src-main-res-values-night-themes-xml)
- [app/src/main/res/xml/backup_rules.xml](#app-src-main-res-xml-backup-rules-xml)
- [app/src/main/res/xml/data_extraction_rules.xml](#app-src-main-res-xml-data-extraction-rules-xml)
- [app/src/test/java/com/brayan/erpagentlocal/ExampleUnitTest.kt](#app-src-test-java-com-brayan-erpagentlocal-exampleunittest-kt)
- [build.gradle.kts](#build-gradle-kts)
- [gradle.properties](#gradle-properties)
- [gradle/gradle-daemon-jvm.properties](#gradle-gradle-daemon-jvm-properties)
- [gradle/libs.versions.toml](#gradle-libs-versions-toml)
- [gradle/wrapper/gradle-wrapper.properties](#gradle-wrapper-gradle-wrapper-properties)
- [local.properties](#local-properties)
- [settings.gradle.kts](#settings-gradle-kts)

## Files

### .gitignore

``gitignore
*.iml
.gradle
/local.properties
/.idea/caches
/.idea/libraries
/.idea/modules.xml
/.idea/workspace.xml
/.idea/navEditor.xml
/.idea/assetWizardSettings.xml
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
local.properties
``

### app/.gitignore

``gitignore
/build
``

### app/build.gradle.kts

``kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.brayan.erpagentlocal"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.brayan.erpagentlocal"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // LiteRT-LM para ejecutar modelos .litertlm localmente
    implementation("com.google.ai.edge.litertlm:litertlm-android:0.11.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
``

### app/proguard-rules.pro

``proguard
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
``

### app/src/androidTest/java/com/brayan/erpagentlocal/ExampleInstrumentedTest.kt

``kotlin
package com.brayan.erpagentlocal

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.brayan.erpagentlocal", appContext.packageName)
    }
}
``

### app/src/main/AndroidManifest.xml

``xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.ERPAgentLocal">

        <uses-native-library android:name="libvndksupport.so" android:required="false" />
        <uses-native-library android:name="libOpenCL.so" android:required="false" />


        <activity
            android:name=".MainActivity"
            android:exported="true">

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

        </activity>

    </application>

</manifest>
``

### app/src/main/assets/openapi.yaml

``yaml
openapi: 3.0.3

info:
  title: Serverless ERP API
  version: 1.0.0
  description: >
    API serverless para un ERP simple conectado a una aplicaciÃ³n Android con agente LLM local.
    Este archivo OpenAPI funciona como contrato formal de herramientas para el agente.
    Cada operationId corresponde al nombre de una tool en Android.

servers:
  - url: https://k5nlr60gn9.execute-api.us-east-1.amazonaws.com/Prod
    description: AWS API Gateway Production
  - url: http://127.0.0.1:3000
    description: SAM Local

tags:
  - name: Health
    description: VerificaciÃ³n del estado del backend.
  - name: Customers
    description: GestiÃ³n de clientes.
  - name: Products
    description: GestiÃ³n de productos.
  - name: Inventory
    description: GestiÃ³n de inventario.
  - name: Purchases
    description: Registro de compras.
  - name: Sales
    description: Registro de ventas.

paths:
  /health:
    get:
      tags:
        - Health
      operationId: checkHealth
      summary: Check backend health
      description: Verifica que el backend serverless estÃ© funcionando.
      responses:
        "200":
          description: Backend running
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/HealthResponse"

  /customers:
    post:
      tags:
        - Customers
      operationId: createCustomer
      summary: Create a customer
      description: Crea un cliente en el ERP.
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/CreateCustomerRequest"
            examples:
              default:
                value:
                  firstName: Juan
                  lastName: Perez
                  phone: "70000000"
                  email: juan@email.com
      responses:
        "200":
          description: Customer created
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/CustomerResponse"
        "400":
          description: Invalid request
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/ErrorResponse"

    get:
      tags:
        - Customers
      operationId: listCustomers
      summary: List customers
      description: Lista clientes registrados.
      responses:
        "200":
          description: Customers listed
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/CustomerListResponse"

  /customers/search:
    get:
      tags:
        - Customers
      operationId: searchCustomer
      summary: Search customer by name
      description: Busca clientes por nombre o apellido.
      parameters:
        - name: name
          in: query
          required: true
          description: Nombre o apellido del cliente.
          schema:
            type: string
          example: Juan
      responses:
        "200":
          description: Search result
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/CustomerListResponse"

  /customers/{customerId}:
    patch:
      tags:
        - Customers
      operationId: updateCustomer
      summary: Update customer
      description: Actualiza parcialmente los datos de un cliente.
      parameters:
        - name: customerId
          in: path
          required: true
          schema:
            type: string
          example: CUS-001
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/UpdateCustomerRequest"
      responses:
        "200":
          description: Customer updated
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/CustomerResponse"

    delete:
      tags:
        - Customers
      operationId: deleteCustomer
      summary: Soft delete customer
      description: Desactiva un cliente usando borrado lÃ³gico.
      parameters:
        - name: customerId
          in: path
          required: true
          schema:
            type: string
          example: CUS-001
      responses:
        "200":
          description: Customer disabled
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/CustomerResponse"

  /products:
    post:
      tags:
        - Products
      operationId: createProduct
      summary: Create a product
      description: Crea un producto en el ERP.
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/CreateProductRequest"
            examples:
              default:
                value:
                  name: Cafe
                  description: Producto creado desde agente local
                  unit: unit
                  salePrice: 25
                  purchasePrice: 18
      responses:
        "200":
          description: Product created
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/ProductResponse"

    get:
      tags:
        - Products
      operationId: listProducts
      summary: List products
      description: Lista productos registrados.
      responses:
        "200":
          description: Products listed
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/ProductListResponse"

  /products/search:
    get:
      tags:
        - Products
      operationId: searchProduct
      summary: Search product by name
      description: Busca productos por nombre.
      parameters:
        - name: name
          in: query
          required: true
          description: Nombre del producto.
          schema:
            type: string
          example: Cafe
      responses:
        "200":
          description: Search result
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/ProductListResponse"

  /products/{productId}:
    patch:
      tags:
        - Products
      operationId: updateProduct
      summary: Update product
      description: Actualiza parcialmente los datos de un producto.
      parameters:
        - name: productId
          in: path
          required: true
          schema:
            type: string
          example: PROD-001
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/UpdateProductRequest"
            examples:
              updatePrice:
                value:
                  salePrice: 30
                  purchasePrice: 20
      responses:
        "200":
          description: Product updated
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/ProductResponse"

    delete:
      tags:
        - Products
      operationId: deleteProduct
      summary: Soft delete product
      description: Desactiva un producto usando borrado lÃ³gico.
      parameters:
        - name: productId
          in: path
          required: true
          schema:
            type: string
          example: PROD-001
      responses:
        "200":
          description: Product disabled
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/ProductResponse"

  /inventory/{productId}:
    get:
      tags:
        - Inventory
      operationId: getInventory
      summary: Get inventory by product
      description: Consulta el inventario de un producto.
      parameters:
        - name: productId
          in: path
          required: true
          schema:
            type: string
          example: PROD-001
      responses:
        "200":
          description: Inventory found
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/InventoryResponse"

  /inventory/low-stock:
    get:
      tags:
        - Inventory
      operationId: listLowStock
      summary: List low stock products
      description: Lista productos con stock bajo.
      responses:
        "200":
          description: Low stock products
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/InventoryListResponse"

  /inventory/adjustments:
    post:
      tags:
        - Inventory
      operationId: createInventoryAdjustment
      summary: Create inventory adjustment
      description: Crea un ajuste manual de inventario.
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/CreateInventoryAdjustmentRequest"
            examples:
              default:
                value:
                  productId: PROD-001
                  quantity: 10
                  type: ADJUSTMENT_IN
                  reason: Carga inicial
      responses:
        "200":
          description: Adjustment created
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/InventoryAdjustmentResponse"

  /purchases:
    post:
      tags:
        - Purchases
      operationId: createPurchase
      summary: Create a purchase and increase inventory
      description: Registra una compra, aumenta inventario y crea movimiento de inventario tipo PURCHASE.
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/CreatePurchaseRequest"
            examples:
              default:
                value:
                  productId: PROD-001
                  quantity: 50
                  unitCost: 18
      responses:
        "200":
          description: Purchase created
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/PurchaseResponse"

    get:
      tags:
        - Purchases
      operationId: listPurchases
      summary: List purchases
      description: Lista compras registradas.
      responses:
        "200":
          description: Purchases listed
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/PurchaseListResponse"

  /sales:
    post:
      tags:
        - Sales
      operationId: createSale
      summary: Create a sale and decrease inventory
      description: Registra una venta, descuenta inventario y crea movimiento de inventario tipo SALE.
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/CreateSaleRequest"
            examples:
              default:
                value:
                  customerId: CUS-001
                  items:
                    - productId: PROD-001
                      quantity: 2
      responses:
        "200":
          description: Sale created
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/SaleResponse"

    get:
      tags:
        - Sales
      operationId: listSales
      summary: List sales
      description: Lista ventas registradas.
      responses:
        "200":
          description: Sales listed
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/SaleListResponse"

  /sales/by-customer/{customerId}:
    get:
      tags:
        - Sales
      operationId: listSalesByCustomer
      summary: List sales by customer
      description: Lista ventas de un cliente.
      parameters:
        - name: customerId
          in: path
          required: true
          schema:
            type: string
          example: CUS-001
      responses:
        "200":
          description: Sales found
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/SaleListResponse"

components:
  schemas:
    ApiResponseBase:
      type: object
      properties:
        success:
          type: boolean
          example: true
        message:
          type: string
          example: Operation completed
        data:
          nullable: true

    ErrorResponse:
      type: object
      properties:
        success:
          type: boolean
          example: false
        message:
          type: string
          example: Invalid request
        data:
          nullable: true

    HealthResponse:
      type: object
      properties:
        success:
          type: boolean
          example: true
        message:
          type: string
          example: Service is running
        data:
          type: object
          properties:
            status:
              type: string
              example: ok
            service:
              type: string
              example: serverless-erp

    CreateCustomerRequest:
      type: object
      required:
        - firstName
        - lastName
      properties:
        firstName:
          type: string
          example: Juan
        lastName:
          type: string
          example: Perez
        phone:
          type: string
          example: "70000000"
        email:
          type: string
          example: juan@email.com

    UpdateCustomerRequest:
      type: object
      properties:
        firstName:
          type: string
        lastName:
          type: string
        phone:
          type: string
        email:
          type: string

    Customer:
      type: object
      properties:
        customerId:
          type: string
          example: CUS-001
        firstName:
          type: string
          example: Juan
        lastName:
          type: string
          example: Perez
        phone:
          type: string
          example: "70000000"
        email:
          type: string
          example: juan@email.com
        isActive:
          type: boolean
          example: true
        createdAt:
          type: string
          example: "2026-05-14T18:30:00Z"
        updatedAt:
          type: string
          example: "2026-05-14T18:30:00Z"

    CustomerResponse:
      type: object
      properties:
        success:
          type: boolean
        message:
          type: string
        data:
          $ref: "#/components/schemas/Customer"

    CustomerListResponse:
      type: object
      properties:
        success:
          type: boolean
        message:
          type: string
        data:
          type: array
          items:
            $ref: "#/components/schemas/Customer"

    CreateProductRequest:
      type: object
      required:
        - name
        - salePrice
        - purchasePrice
      properties:
        name:
          type: string
          example: Cafe
        description:
          type: string
          example: Producto creado desde agente local
        unit:
          type: string
          example: unit
        salePrice:
          type: number
          example: 25
        purchasePrice:
          type: number
          example: 18

    UpdateProductRequest:
      type: object
      properties:
        name:
          type: string
        description:
          type: string
        unit:
          type: string
        salePrice:
          type: number
        purchasePrice:
          type: number

    Product:
      type: object
      properties:
        productId:
          type: string
          example: PROD-001
        name:
          type: string
          example: Cafe
        description:
          type: string
          example: Producto creado desde agente local
        unit:
          type: string
          example: unit
        salePrice:
          type: number
          example: 25
        purchasePrice:
          type: number
          example: 18
        isActive:
          type: boolean
          example: true
        createdAt:
          type: string
          example: "2026-05-14T18:30:00Z"
        updatedAt:
          type: string
          example: "2026-05-14T18:30:00Z"

    ProductResponse:
      type: object
      properties:
        success:
          type: boolean
        message:
          type: string
        data:
          $ref: "#/components/schemas/Product"

    ProductListResponse:
      type: object
      properties:
        success:
          type: boolean
        message:
          type: string
        data:
          type: array
          items:
            $ref: "#/components/schemas/Product"

    Inventory:
      type: object
      properties:
        productId:
          type: string
          example: PROD-001
        stock:
          type: integer
          example: 50
        minStock:
          type: integer
          example: 5
        updatedAt:
          type: string
          example: "2026-05-14T18:30:00Z"

    InventoryResponse:
      type: object
      properties:
        success:
          type: boolean
        message:
          type: string
        data:
          $ref: "#/components/schemas/Inventory"

    InventoryListResponse:
      type: object
      properties:
        success:
          type: boolean
        message:
          type: string
        data:
          type: array
          items:
            $ref: "#/components/schemas/Inventory"

    CreateInventoryAdjustmentRequest:
      type: object
      required:
        - productId
        - quantity
        - type
        - reason
      properties:
        productId:
          type: string
          example: PROD-001
        quantity:
          type: integer
          example: 10
        type:
          type: string
          enum:
            - ADJUSTMENT_IN
            - ADJUSTMENT_OUT
          example: ADJUSTMENT_IN
        reason:
          type: string
          example: Carga inicial

    InventoryAdjustmentResponse:
      type: object
      properties:
        success:
          type: boolean
        message:
          type: string
        data:
          type: object

    CreatePurchaseRequest:
      type: object
      required:
        - productId
        - quantity
        - unitCost
      properties:
        productId:
          type: string
          example: PROD-001
        quantity:
          type: integer
          example: 50
        unitCost:
          type: number
          example: 18

    Purchase:
      type: object
      properties:
        purchaseId:
          type: string
          example: PUR-001
        productId:
          type: string
          example: PROD-001
        quantity:
          type: integer
          example: 50
        unitCost:
          type: number
          example: 18
        total:
          type: number
          example: 900
        createdAt:
          type: string
          example: "2026-05-14T18:30:00Z"

    PurchaseResponse:
      type: object
      properties:
        success:
          type: boolean
        message:
          type: string
        data:
          type: object
          properties:
            purchase:
              $ref: "#/components/schemas/Purchase"
            inventory:
              $ref: "#/components/schemas/Inventory"
            movement:
              $ref: "#/components/schemas/InventoryMovement"

    PurchaseListResponse:
      type: object
      properties:
        success:
          type: boolean
        message:
          type: string
        data:
          type: array
          items:
            $ref: "#/components/schemas/Purchase"

    CreateSaleRequest:
      type: object
      required:
        - customerId
        - items
      properties:
        customerId:
          type: string
          example: CUS-001
        items:
          type: array
          items:
            $ref: "#/components/schemas/CreateSaleItemRequest"

    CreateSaleItemRequest:
      type: object
      required:
        - productId
        - quantity
      properties:
        productId:
          type: string
          example: PROD-001
        quantity:
          type: integer
          example: 2

    SaleItem:
      type: object
      properties:
        productId:
          type: string
          example: PROD-001
        productName:
          type: string
          example: Cafe
        quantity:
          type: integer
          example: 2
        unitPrice:
          type: number
          example: 25
        subtotal:
          type: number
          example: 50

    Sale:
      type: object
      properties:
        saleId:
          type: string
          example: SALE-001
        customerId:
          type: string
          example: CUS-001
        items:
          type: array
          items:
            $ref: "#/components/schemas/SaleItem"
        total:
          type: number
          example: 50
        createdAt:
          type: string
          example: "2026-05-14T18:30:00Z"

    SaleResponse:
      type: object
      properties:
        success:
          type: boolean
        message:
          type: string
        data:
          type: object
          properties:
            sale:
              $ref: "#/components/schemas/Sale"
            inventory:
              type: array
              items:
                $ref: "#/components/schemas/Inventory"
            movements:
              type: array
              items:
                $ref: "#/components/schemas/InventoryMovement"

    SaleListResponse:
      type: object
      properties:
        success:
          type: boolean
        message:
          type: string
        data:
          type: array
          items:
            $ref: "#/components/schemas/Sale"

    InventoryMovement:
      type: object
      properties:
        movementId:
          type: string
          example: MOV-001
        productId:
          type: string
          example: PROD-001
        type:
          type: string
          enum:
            - PURCHASE
            - SALE
            - ADJUSTMENT_IN
            - ADJUSTMENT_OUT
          example: SALE
        quantity:
          type: integer
          example: -2
        reason:
          type: string
          example: Sale SALE-001
        createdAt:
          type: string
          example: "2026-05-14T18:30:00Z"
``

### app/src/main/assets/tools.json

``json
[
  {
    "name": "createCustomer",
    "operationId": "createCustomer",
    "method": "POST",
    "path": "/customers",
    "description": "Create a customer in the ERP.",
    "requiredArguments": ["firstName", "lastName"],
    "optionalArguments": ["phone", "email"]
  },
  {
    "name": "searchCustomer",
    "operationId": "searchCustomer",
    "method": "GET",
    "path": "/customers/search?name={name}",
    "description": "Search customers by name or last name.",
    "requiredArguments": ["name"],
    "optionalArguments": []
  },
  {
    "name": "createProduct",
    "operationId": "createProduct",
    "method": "POST",
    "path": "/products",
    "description": "Create a product in the ERP.",
    "requiredArguments": ["name", "salePrice", "purchasePrice"],
    "optionalArguments": ["description", "unit"]
  },
  {
    "name": "searchProduct",
    "operationId": "searchProduct",
    "method": "GET",
    "path": "/products/search?name={name}",
    "description": "Search products by name.",
    "requiredArguments": ["name"],
    "optionalArguments": []
  },
  {
    "name": "updateProduct",
    "operationId": "updateProduct",
    "method": "PATCH",
    "path": "/products/{productId}",
    "description": "Update product information.",
    "requiredArguments": ["productId"],
    "optionalArguments": ["name", "description", "unit", "salePrice", "purchasePrice"]
  },
  {
    "name": "getInventory",
    "operationId": "getInventory",
    "method": "GET",
    "path": "/inventory/{productId}",
    "description": "Get inventory by product ID.",
    "requiredArguments": ["productId"],
    "optionalArguments": []
  },
  {
    "name": "createPurchase",
    "operationId": "createPurchase",
    "method": "POST",
    "path": "/purchases",
    "description": "Create a purchase and increase inventory.",
    "requiredArguments": ["productId", "quantity", "unitCost"],
    "optionalArguments": []
  },
  {
    "name": "createSale",
    "operationId": "createSale",
    "method": "POST",
    "path": "/sales",
    "description": "Create a sale and decrease inventory.",
    "requiredArguments": ["customerId", "items"],
    "optionalArguments": []
  }
]
``

### app/src/main/java/com/brayan/erpagentlocal/agent/AgentAction.kt

``kotlin
package com.brayan.erpagentlocal.agent

import org.json.JSONObject

sealed class AgentAction {

    data class ToolCall(
        val tool: String,
        val arguments: JSONObject
    ) : AgentAction()

    data class AskUser(
        val message: String
    ) : AgentAction()

    data class Final(
        val message: String
    ) : AgentAction()

    data class Invalid(
        val error: String
    ) : AgentAction()
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/AgentActionParser.kt

``kotlin
package com.brayan.erpagentlocal.agent

import com.brayan.erpagentlocal.util.JsonUtils
import org.json.JSONObject

class AgentActionParser {

    fun parse(rawText: String): AgentAction {
        val cleanedText = JsonUtils.extractJsonObject(rawText)

        return try {
            val json = JSONObject(cleanedText.trim())
            val type = json.optString("type").trim()
            val tool = json.optString("tool").trim()

            when {
                type == "tool_call" -> parseToolCall(json)

                type == "ask_user" -> parseAskUser(json)

                type == "final" -> parseFinal(json)

                tool.isNotBlank() && ToolRegistry.exists(tool) -> {
                    parseToolCall(json)
                }

                type.isNotBlank() && ToolRegistry.exists(type) -> {
                    val normalized = JSONObject()
                        .put("type", "tool_call")
                        .put("tool", type)
                        .put("arguments", json.optJSONObject("arguments") ?: JSONObject())

                    parseToolCall(normalized)
                }

                else -> {
                    AgentAction.Invalid(
                        "No pude interpretar la acciÃ³n del modelo. Intenta escribir la instrucciÃ³n de forma mÃ¡s especÃ­fica."
                    )
                }
            }
        } catch (_: Exception) {
            AgentAction.Invalid(
                "No pude interpretar la acciÃ³n del modelo. Intenta escribir la instrucciÃ³n de forma mÃ¡s especÃ­fica."
            )
        }
    }

    private fun parseToolCall(json: JSONObject): AgentAction {
        val tool = json.optString("tool").trim()
        val arguments = json.optJSONObject("arguments") ?: JSONObject()

        if (tool.isBlank()) {
            return AgentAction.Invalid("El campo 'tool' es obligatorio.")
        }

        return AgentAction.ToolCall(
            tool = tool,
            arguments = arguments
        )
    }

    private fun parseAskUser(json: JSONObject): AgentAction {
        val message = json.optString("message").trim()

        if (message.isBlank()) {
            return AgentAction.Invalid("El campo 'message' es obligatorio para ask_user.")
        }

        return AgentAction.AskUser(message)
    }

    private fun parseFinal(json: JSONObject): AgentAction {
        val message = json.optString("message").trim()

        if (message.isBlank()) {
            return AgentAction.Invalid("El campo 'message' es obligatorio para final.")
        }

        return AgentAction.Final(message)
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/AgentError.kt

``kotlin
package com.brayan.erpagentlocal.agent

data class AgentError(
    val type: AgentErrorType,
    val technicalMessage: String,
    val userMessage: String,
    val statusCode: Int? = null,
    val canRetry: Boolean = false,
    val shouldAskUser: Boolean = false
) {

    fun toDebugText(): String {
        return buildString {
            appendLine("AGENT ERROR")
            appendLine()
            appendLine("Type: $type")
            appendLine("Status code: ${statusCode ?: "none"}")
            appendLine("Can retry: $canRetry")
            appendLine("Should ask user: $shouldAskUser")
            appendLine()
            appendLine("User message:")
            appendLine(userMessage)
            appendLine()
            appendLine("Technical message:")
            appendLine(technicalMessage)
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/AgentErrorMapper.kt

``kotlin
package com.brayan.erpagentlocal.agent

import org.json.JSONArray
import org.json.JSONObject

object AgentErrorMapper {

    fun fromHttpResponse(
        response: JSONObject,
        toolName: String? = null
    ): AgentError {
        val statusCode = response.optInt("statusCode", 0).takeIf { it > 0 }
        val message = response.optString("message", "Error desconocido.")
        val error = response.optString("error", "")
        val technicalMessage = if (error.isNotBlank()) error else message
        val normalized = "$message $error".lowercase()

        if (normalized.contains("not enough stock") || statusCode == 409) {
            val availableStock = extractAvailableStock(normalized)

            val userMessage = if (availableStock != null) {
                "No se pudo registrar la venta porque solo hay $availableStock unidades disponibles."
            } else {
                "No se pudo registrar la venta porque no hay stock suficiente."
            }

            return AgentError(
                type = AgentErrorType.CONFLICT,
                technicalMessage = technicalMessage,
                userMessage = userMessage,
                statusCode = statusCode,
                canRetry = false,
                shouldAskUser = false
            )
        }

        return when (statusCode) {
            400 -> AgentError(
                type = AgentErrorType.BAD_REQUEST,
                technicalMessage = technicalMessage,
                userMessage = "No pude completar la operaciÃ³n porque hay datos invÃ¡lidos o incompletos. Revisa la informaciÃ³n e intÃ©ntalo de nuevo.",
                statusCode = statusCode,
                canRetry = true,
                shouldAskUser = true
            )

            404 -> AgentError(
                type = AgentErrorType.NOT_FOUND,
                technicalMessage = technicalMessage,
                userMessage = buildNotFoundUserMessage(message, toolName),
                statusCode = statusCode,
                canRetry = true,
                shouldAskUser = true
            )

            409 -> AgentError(
                type = AgentErrorType.CONFLICT,
                technicalMessage = technicalMessage,
                userMessage = "No pude completar la operaciÃ³n por una regla de negocio: $message",
                statusCode = statusCode,
                canRetry = false,
                shouldAskUser = false
            )

            500 -> AgentError(
                type = AgentErrorType.SERVER_ERROR,
                technicalMessage = technicalMessage,
                userMessage = "El backend tuvo un error interno. Intenta nuevamente mÃ¡s tarde.",
                statusCode = statusCode,
                canRetry = true,
                shouldAskUser = false
            )

            0 -> AgentError(
                type = AgentErrorType.NETWORK_ERROR,
                technicalMessage = technicalMessage,
                userMessage = "No pude conectarme con el backend. Verifica tu conexiÃ³n a internet o que el API Gateway estÃ© activo.",
                statusCode = statusCode,
                canRetry = true,
                shouldAskUser = false
            )

            else -> AgentError(
                type = AgentErrorType.UNKNOWN,
                technicalMessage = technicalMessage,
                userMessage = "No pude completar la operaciÃ³n: $message",
                statusCode = statusCode,
                canRetry = true,
                shouldAskUser = false
            )
        }
    }

    fun fromInvalidJson(
        rawOutput: String,
        error: String
    ): AgentError {
        return AgentError(
            type = AgentErrorType.INVALID_JSON,
            technicalMessage = "$error\n\nRaw output:\n$rawOutput",
            userMessage = "No pude interpretar la acciÃ³n del modelo. Intenta escribir la instrucciÃ³n de forma mÃ¡s especÃ­fica.",
            statusCode = null,
            canRetry = true,
            shouldAskUser = true
        )
    }

    fun fromModelNotReady(): AgentError {
        return AgentError(
            type = AgentErrorType.MODEL_NOT_READY,
            technicalMessage = "Local model is not initialized.",
            userMessage = "El modelo local todavÃ­a no estÃ¡ inicializado. Selecciona el archivo .litertlm y presiona â€œInicializarâ€.",
            canRetry = true,
            shouldAskUser = false
        )
    }

    fun fromException(
        exception: Exception
    ): AgentError {
        val message = exception.message ?: "Error inesperado."

        return AgentError(
            type = AgentErrorType.UNKNOWN,
            technicalMessage = message,
            userMessage = "OcurriÃ³ un error inesperado: $message",
            canRetry = true,
            shouldAskUser = false
        )
    }

    fun isEmptySearchResult(
        toolName: String,
        response: JSONObject
    ): Boolean {
        if (toolName != "searchProduct" && toolName != "searchCustomer") {
            return false
        }

        if (!response.optBoolean("success", false)) {
            return false
        }

        val data = response.opt("data")

        return when (data) {
            is JSONArray -> data.length() == 0
            null -> true
            JSONObject.NULL -> true
            else -> false
        }
    }

    fun emptySearchMessage(
        toolName: String,
        arguments: JSONObject
    ): String {
        val name = arguments.optString("name", "solicitado")

        return when (toolName) {
            "searchProduct" -> "No encontrÃ© el producto \"$name\". Â¿Deseas crearlo primero?"
            "searchCustomer" -> "No encontrÃ© el cliente \"$name\". Â¿Deseas crearlo primero?"
            else -> "No encontrÃ© el recurso solicitado."
        }
    }

    private fun buildNotFoundUserMessage(
        message: String,
        toolName: String?
    ): String {
        val normalized = message.lowercase()

        return when {
            normalized.contains("product") || toolName == "searchProduct" || toolName == "getInventory" ->
                "No encontrÃ© ese producto. Â¿Quieres crearlo primero?"

            normalized.contains("customer") || toolName == "searchCustomer" ->
                "No encontrÃ© ese cliente. Â¿Quieres crearlo primero?"

            else ->
                "No encontrÃ© el recurso solicitado. Revisa el dato o crÃ©alo primero."
        }
    }

    private fun extractAvailableStock(
        message: String
    ): Int? {
        val regex = Regex("available:\\s*(\\d+)", RegexOption.IGNORE_CASE)
        val match = regex.find(message)

        return match
            ?.groupValues
            ?.getOrNull(1)
            ?.toIntOrNull()
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/AgentErrorType.kt

``kotlin
package com.brayan.erpagentlocal.agent

enum class AgentErrorType {
    BAD_REQUEST,
    NOT_FOUND,
    CONFLICT,
    SERVER_ERROR,
    NETWORK_ERROR,
    MODEL_NOT_READY,
    INVALID_JSON,
    TOOL_NOT_FOUND,
    VALIDATION_ERROR,
    UNKNOWN
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/AgentMemory.kt

``kotlin
package com.brayan.erpagentlocal.agent

data class AgentMemoryItem(
    val role: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
) {

    fun asTextLine(): String {
        return "[$createdAt] $role: $content"
    }

    fun asPromptLine(): String {
        return "${role.uppercase()}: $content"
    }
}

class AgentMemory(
    private val maxItems: Int = 30,
    private val maxPromptItems: Int = 12
) {

    private val items: MutableList<AgentMemoryItem> = mutableListOf()

    fun add(role: String, content: String) {
        val cleanRole = role.trim().ifBlank { "unknown" }
        val cleanContent = content.trim()

        if (cleanContent.isBlank()) {
            return
        }

        items.add(
            AgentMemoryItem(
                role = cleanRole,
                content = cleanContent
            )
        )

        trimToMaxItems()
    }

    fun addUser(content: String) {
        add("user", content)
    }

    fun addAssistant(content: String) {
        add("assistant", content)
    }

    fun addTool(
        toolName: String,
        content: String
    ) {
        add("tool:$toolName", content)
    }

    fun addSystem(content: String) {
        add("system", content)
    }

    fun getAll(): List<AgentMemoryItem> {
        return items.toList()
    }

    fun getLast(limit: Int): List<AgentMemoryItem> {
        if (limit <= 0) {
            return emptyList()
        }

        return items.takeLast(limit)
    }

    fun getLastForPrompt(): List<AgentMemoryItem> {
        return getLast(maxPromptItems)
    }

    fun clear() {
        items.clear()
    }

    fun size(): Int {
        return items.size
    }

    fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    fun asText(): String {
        if (items.isEmpty()) {
            return ""
        }

        return buildString {
            items.forEach { item ->
                appendLine(item.asTextLine())
            }
        }
    }

    fun asPromptText(): String {
        if (items.isEmpty()) {
            return "No previous memory."
        }

        return buildString {
            getLastForPrompt().forEach { item ->
                appendLine(item.asPromptLine())
            }
        }
    }

    fun asShortSummary(): String {
        if (items.isEmpty()) {
            return "La memoria estÃ¡ vacÃ­a."
        }

        return buildString {
            appendLine("Mensajes guardados: ${items.size}")
            appendLine()

            getLastForPrompt().forEach { item ->
                appendLine("- ${item.role}: ${item.content.take(160)}")
            }
        }
    }

    private fun trimToMaxItems() {
        if (items.size <= maxItems) {
            return
        }

        val overflow = items.size - maxItems

        repeat(overflow) {
            if (items.isNotEmpty()) {
                items.removeAt(0)
            }
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/AgentState.kt

``kotlin
package com.brayan.erpagentlocal.agent

data class AgentState(
    val lastCustomerId: String? = null,
    val lastCustomerName: String? = null,
    val lastProductId: String? = null,
    val lastProductName: String? = null,
    val lastSaleId: String? = null,
    val lastPurchaseId: String? = null,
    val lastInventoryStock: Int? = null,
    val pendingQuestion: String? = null,
    val executedTools: List<ExecutedTool> = emptyList()
) {

    fun withCustomer(
        customerId: String?,
        customerName: String?
    ): AgentState {
        return copy(
            lastCustomerId = customerId ?: lastCustomerId,
            lastCustomerName = customerName ?: lastCustomerName
        )
    }

    fun withProduct(
        productId: String?,
        productName: String?
    ): AgentState {
        return copy(
            lastProductId = productId ?: lastProductId,
            lastProductName = productName ?: lastProductName
        )
    }

    fun withSale(
        saleId: String?
    ): AgentState {
        return copy(
            lastSaleId = saleId ?: lastSaleId
        )
    }

    fun withPurchase(
        purchaseId: String?
    ): AgentState {
        return copy(
            lastPurchaseId = purchaseId ?: lastPurchaseId
        )
    }

    fun withInventoryStock(
        stock: Int?
    ): AgentState {
        return copy(
            lastInventoryStock = stock ?: lastInventoryStock
        )
    }

    fun withPendingQuestion(
        question: String?
    ): AgentState {
        return copy(
            pendingQuestion = question
        )
    }

    fun clearPendingQuestion(): AgentState {
        return copy(
            pendingQuestion = null
        )
    }

    fun addExecutedTool(
        executedTool: ExecutedTool
    ): AgentState {
        return copy(
            executedTools = executedTools + executedTool
        )
    }

    fun keepLastExecutedTools(
        maxItems: Int
    ): AgentState {
        if (maxItems <= 0) {
            return copy(executedTools = emptyList())
        }

        return copy(
            executedTools = executedTools.takeLast(maxItems)
        )
    }

    fun hasCustomer(): Boolean {
        return !lastCustomerId.isNullOrBlank()
    }

    fun hasProduct(): Boolean {
        return !lastProductId.isNullOrBlank()
    }

    fun hasSale(): Boolean {
        return !lastSaleId.isNullOrBlank()
    }

    fun hasPurchase(): Boolean {
        return !lastPurchaseId.isNullOrBlank()
    }

    fun hasPendingQuestion(): Boolean {
        return !pendingQuestion.isNullOrBlank()
    }

    fun toPromptBlock(): String {
        return buildString {
            appendLine("{")
            appendLine("  \"lastCustomerId\": ${jsonStringOrNull(lastCustomerId)},")
            appendLine("  \"lastCustomerName\": ${jsonStringOrNull(lastCustomerName)},")
            appendLine("  \"lastProductId\": ${jsonStringOrNull(lastProductId)},")
            appendLine("  \"lastProductName\": ${jsonStringOrNull(lastProductName)},")
            appendLine("  \"lastSaleId\": ${jsonStringOrNull(lastSaleId)},")
            appendLine("  \"lastPurchaseId\": ${jsonStringOrNull(lastPurchaseId)},")
            appendLine("  \"lastInventoryStock\": ${lastInventoryStock?.toString() ?: "null"},")
            appendLine("  \"pendingQuestion\": ${jsonStringOrNull(pendingQuestion)},")
            appendLine("  \"executedTools\": [")

            executedTools.forEachIndexed { index, tool ->
                append(tool.toPromptBlock())
                if (index < executedTools.lastIndex) {
                    appendLine(",")
                } else {
                    appendLine()
                }
            }

            appendLine("  ]")
            appendLine("}")
        }
    }

    fun toDebugText(): String {
        return buildString {
            appendLine("AGENT STATE")
            appendLine()
            appendLine("lastCustomerId: ${lastCustomerId ?: "none"}")
            appendLine("lastCustomerName: ${lastCustomerName ?: "none"}")
            appendLine("lastProductId: ${lastProductId ?: "none"}")
            appendLine("lastProductName: ${lastProductName ?: "none"}")
            appendLine("lastSaleId: ${lastSaleId ?: "none"}")
            appendLine("lastPurchaseId: ${lastPurchaseId ?: "none"}")
            appendLine("lastInventoryStock: ${lastInventoryStock ?: "none"}")
            appendLine("pendingQuestion: ${pendingQuestion ?: "none"}")
            appendLine()
            appendLine("Executed tools: ${executedTools.size}")

            executedTools.forEachIndexed { index, executedTool ->
                appendLine()
                appendLine("Executed tool ${index + 1}:")
                appendLine(executedTool.toDebugText())
            }
        }
    }

    private fun jsonStringOrNull(value: String?): String {
        if (value.isNullOrBlank()) {
            return "null"
        }

        return "\"${escape(value)}\""
    }

    private fun escape(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")
    }

    companion object {
        fun empty(): AgentState {
            return AgentState()
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/AgentStateUpdater.kt

``kotlin
package com.brayan.erpagentlocal.agent

import org.json.JSONArray
import org.json.JSONObject

class AgentStateUpdater {

    fun updateFromToolResponse(
        toolName: String,
        arguments: JSONObject,
        response: JSONObject,
        currentState: AgentState
    ): AgentState {
        if (!response.optBoolean("success", false)) {
            return currentState
        }

        val data = response.opt("data") ?: return currentState

        return when (toolName) {
            "createCustomer" -> updateFromCustomerObject(
                customer = data as? JSONObject,
                currentState = currentState,
                fallbackName = buildCustomerNameFromArguments(arguments)
            )

            "searchCustomer" -> updateFromCustomerSearch(
                data = data,
                currentState = currentState
            )

            "createProduct" -> updateFromProductObject(
                product = data as? JSONObject,
                currentState = currentState,
                fallbackName = arguments.optString("name", null)
            )

            "searchProduct" -> updateFromProductSearch(
                data = data,
                currentState = currentState
            )

            "updateProduct" -> updateFromProductObject(
                product = data as? JSONObject,
                currentState = currentState,
                fallbackName = arguments.optString("name", null)
            )

            "getInventory" -> updateFromInventoryObject(
                inventory = data as? JSONObject,
                currentState = currentState
            )

            "createPurchase" -> updateFromPurchaseResponse(
                data = data as? JSONObject,
                currentState = currentState
            )

            "createSale" -> updateFromSaleResponse(
                data = data as? JSONObject,
                currentState = currentState
            )

            else -> currentState
        }
    }

    fun addExecutedTool(
        toolName: String,
        arguments: JSONObject,
        response: JSONObject,
        currentState: AgentState
    ): AgentState {
        val executedTool = ExecutedTool(
            toolName = toolName,
            argumentsJson = arguments.toString(2),
            success = response.optBoolean("success", false),
            resultSummary = response.optString("message", "Tool executed"),
            resultJson = response.toString(2)
        )

        return currentState
            .addExecutedTool(executedTool)
            .keepLastExecutedTools(10)
    }

    fun updateAndStoreExecution(
        toolName: String,
        arguments: JSONObject,
        response: JSONObject,
        currentState: AgentState
    ): AgentState {
        val updatedState = updateFromToolResponse(
            toolName = toolName,
            arguments = arguments,
            response = response,
            currentState = currentState
        )

        return addExecutedTool(
            toolName = toolName,
            arguments = arguments,
            response = response,
            currentState = updatedState
        )
    }

    private fun updateFromCustomerSearch(
        data: Any,
        currentState: AgentState
    ): AgentState {
        val customer = when (data) {
            is JSONArray -> data.optJSONObject(0)
            is JSONObject -> data
            else -> null
        }

        return updateFromCustomerObject(
            customer = customer,
            currentState = currentState,
            fallbackName = null
        )
    }

    private fun updateFromCustomerObject(
        customer: JSONObject?,
        currentState: AgentState,
        fallbackName: String?
    ): AgentState {
        if (customer == null) {
            return currentState
        }

        val customerId = customer.optString("customerId", null)
        val firstName = customer.optString("firstName", "")
        val lastName = customer.optString("lastName", "")

        val customerName = buildString {
            append(firstName)
            if (lastName.isNotBlank()) {
                append(" ")
                append(lastName)
            }
        }.trim().ifBlank {
            fallbackName.orEmpty()
        }

        return currentState.withCustomer(
            customerId = customerId.ifBlank { null },
            customerName = customerName.ifBlank { null }
        )
    }

    private fun updateFromProductSearch(
        data: Any,
        currentState: AgentState
    ): AgentState {
        val product = when (data) {
            is JSONArray -> data.optJSONObject(0)
            is JSONObject -> data
            else -> null
        }

        return updateFromProductObject(
            product = product,
            currentState = currentState,
            fallbackName = null
        )
    }

    private fun updateFromProductObject(
        product: JSONObject?,
        currentState: AgentState,
        fallbackName: String?
    ): AgentState {
        if (product == null) {
            return currentState
        }

        val productId = product.optString("productId", null)
        val productName = product.optString("name", fallbackName.orEmpty())

        return currentState.withProduct(
            productId = productId.ifBlank { null },
            productName = productName.ifBlank { null }
        )
    }

    private fun updateFromInventoryObject(
        inventory: JSONObject?,
        currentState: AgentState
    ): AgentState {
        if (inventory == null) {
            return currentState
        }

        val productId = inventory.optString("productId", null)
        val stock = if (inventory.has("stock")) {
            inventory.optInt("stock")
        } else {
            null
        }

        return currentState
            .withProduct(
                productId = productId.ifBlank { null },
                productName = null
            )
            .withInventoryStock(stock)
    }

    private fun updateFromPurchaseResponse(
        data: JSONObject?,
        currentState: AgentState
    ): AgentState {
        if (data == null) {
            return currentState
        }

        val purchase = data.optJSONObject("purchase")
        val inventory = data.optJSONObject("inventory")

        val purchaseId = purchase?.optString("purchaseId", null)
        val productId = purchase?.optString("productId", null)
        val productName = purchase?.optString("productName", null)

        var newState = currentState
            .withPurchase(purchaseId?.ifBlank { null })
            .withProduct(
                productId = productId?.ifBlank { null },
                productName = productName?.ifBlank { null }
            )

        if (inventory != null) {
            newState = updateFromInventoryObject(
                inventory = inventory,
                currentState = newState
            )
        }

        return newState
    }

    private fun updateFromSaleResponse(
        data: JSONObject?,
        currentState: AgentState
    ): AgentState {
        if (data == null) {
            return currentState
        }

        val sale = data.optJSONObject("sale")
        val inventoryArray = data.optJSONArray("inventory")

        val saleId = sale?.optString("saleId", null)
        val customerId = sale?.optString("customerId", null)
        val customerName = sale?.optString("customerName", null)

        var newState = currentState
            .withSale(saleId?.ifBlank { null })
            .withCustomer(
                customerId = customerId?.ifBlank { null },
                customerName = customerName?.ifBlank { null }
            )

        val firstItem = sale
            ?.optJSONArray("items")
            ?.optJSONObject(0)

        if (firstItem != null) {
            newState = newState.withProduct(
                productId = firstItem.optString("productId", null)?.ifBlank { null },
                productName = firstItem.optString("productName", null)?.ifBlank { null }
            )
        }

        val firstInventory = inventoryArray?.optJSONObject(0)

        if (firstInventory != null) {
            newState = updateFromInventoryObject(
                inventory = firstInventory,
                currentState = newState
            )
        }

        return newState
    }

    private fun buildCustomerNameFromArguments(arguments: JSONObject): String? {
        val firstName = arguments.optString("firstName", "")
        val lastName = arguments.optString("lastName", "")

        return "$firstName $lastName".trim().ifBlank {
            null
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/AgentToolCallingMode.kt

``kotlin
package com.brayan.erpagentlocal.agent

enum class AgentToolCallingMode(
    val title: String,
    val description: String
) {
    JSON_MANUAL(
        title = "JSON tool_call manual",
        description = "El modelo responde JSON, Android parsea, valida y ejecuta tools."
    ),

    NATIVE_LITERT_EXPERIMENTAL(
        title = "LiteRT-LM native tools experimental",
        description = "El modelo usa tools registradas en ConversationConfig. Requiere modelo y API compatibles."
    )
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/ExecutedTool.kt

``kotlin
package com.brayan.erpagentlocal.agent

data class ExecutedTool(
    val toolName: String,
    val argumentsJson: String,
    val success: Boolean,
    val resultSummary: String,
    val resultJson: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {

    fun toPromptBlock(): String {
        return buildString {
            appendLine("{")
            appendLine("  \"toolName\": \"$toolName\",")
            appendLine("  \"success\": $success,")
            appendLine("  \"arguments\": ${argumentsJson.ifBlank { "{}" }},")
            appendLine("  \"resultSummary\": \"${escape(resultSummary)}\"")
            if (!resultJson.isNullOrBlank()) {
                appendLine("  ,\"result\": $resultJson")
            }
            appendLine("}")
        }
    }

    fun toDebugText(): String {
        return buildString {
            appendLine("Tool: $toolName")
            appendLine("Success: $success")
            appendLine("Arguments:")
            appendLine(argumentsJson)
            appendLine("Result summary:")
            appendLine(resultSummary)

            if (!resultJson.isNullOrBlank()) {
                appendLine("Result JSON:")
                appendLine(resultJson)
            }
        }
    }

    private fun escape(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/NativeToolCallingEvaluator.kt

``kotlin
package com.brayan.erpagentlocal.agent

object NativeToolCallingEvaluator {

    fun evaluate(
        modelPath: String?,
        toolCatalog: ToolCatalog
    ): NativeToolCallingReadiness {
        val cleanModelPath = modelPath.orEmpty()
        val modelName = cleanModelPath
            .substringAfterLast('/')
            .substringAfterLast('\\')

        val normalizedName = modelName.lowercase()

        val reasons = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        val looksLikeLiteRtModel = normalizedName.endsWith(".litertlm")
        val looksLikeFunctionGemma =
            normalizedName.contains("functiongemma") ||
                    normalizedName.contains("function-gemma") ||
                    normalizedName.contains("function_gemma")

        val hasTools = toolCatalog.count() > 0

        if (looksLikeLiteRtModel) {
            reasons.add("El archivo seleccionado parece ser un modelo .litertlm.")
        } else {
            warnings.add("El archivo no parece ser .litertlm.")
        }

        if (looksLikeFunctionGemma) {
            reasons.add("El nombre del modelo sugiere FunctionGemma, especializado en function calling.")
        } else {
            warnings.add("El nombre del modelo no sugiere FunctionGemma. Puede funcionar con JSON manual, pero no es ideal para native tools.")
        }

        if (hasTools) {
            reasons.add("El catÃ¡logo tiene ${toolCatalog.count()} tools disponibles.")
        } else {
            warnings.add("No hay tools cargadas en ToolCatalog.")
        }

        val canTryNativeTools = looksLikeLiteRtModel && looksLikeFunctionGemma && hasTools

        val recommendedMode = if (canTryNativeTools) {
            AgentToolCallingMode.NATIVE_LITERT_EXPERIMENTAL
        } else {
            AgentToolCallingMode.JSON_MANUAL
        }

        if (!canTryNativeTools) {
            reasons.add("Se recomienda mantener JSON tool_call manual porque es mÃ¡s estable con el modelo actual.")
        }

        warnings.add(
            "El modo nativo debe activarse solo despuÃ©s de confirmar la API exacta de LiteRT-LM disponible en tu versiÃ³n."
        )

        return NativeToolCallingReadiness(
            canTryNativeTools = canTryNativeTools,
            recommendedMode = recommendedMode,
            modelName = modelName,
            reasons = reasons,
            warnings = warnings
        )
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/NativeToolCallingReadiness.kt

``kotlin
package com.brayan.erpagentlocal.agent

data class NativeToolCallingReadiness(
    val canTryNativeTools: Boolean,
    val recommendedMode: AgentToolCallingMode,
    val modelName: String,
    val reasons: List<String>,
    val warnings: List<String>
) {

    fun toDebugText(): String {
        return buildString {
            appendLine("NATIVE TOOL CALLING READINESS")
            appendLine()
            appendLine("Model: ${modelName.ifBlank { "unknown" }}")
            appendLine("Can try native tools: $canTryNativeTools")
            appendLine("Recommended mode: ${recommendedMode.title}")
            appendLine()
            appendLine("Reasons:")
            if (reasons.isEmpty()) {
                appendLine("- No reasons available.")
            } else {
                reasons.forEach { reason ->
                    appendLine("- $reason")
                }
            }
            appendLine()
            appendLine("Warnings:")
            if (warnings.isEmpty()) {
                appendLine("- No warnings.")
            } else {
                warnings.forEach { warning ->
                    appendLine("- $warning")
                }
            }
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/ToolCallValidator.kt

``kotlin
package com.brayan.erpagentlocal.agent

import org.json.JSONArray
import org.json.JSONObject

class ToolCallValidator(
    private val maxRepeatedSuccessfulCalls: Int = 2
) {

    fun validate(
        action: AgentAction.ToolCall,
        state: AgentState
    ): ValidationResult {
        val toolDefinition = ToolRegistry.find(action.tool)
            ?: return ValidationResult.invalid(
                "La tool '${action.tool}' no estÃ¡ registrada en el catÃ¡logo."
            )

        val requiredValidation = validateRequiredArguments(
            toolDefinition = toolDefinition,
            arguments = action.arguments
        )

        if (!requiredValidation.isValid) {
            return requiredValidation
        }

        val typeValidation = validateArgumentTypes(
            toolName = action.tool,
            arguments = action.arguments
        )

        if (!typeValidation.isValid) {
            return typeValidation
        }

        val idValidation = validateKnownIds(
            toolName = action.tool,
            arguments = action.arguments,
            state = state
        )

        if (!idValidation.isValid) {
            return idValidation
        }

        val repetitionValidation = validateRepeatedSuccessfulCalls(
            action = action,
            state = state
        )

        if (!repetitionValidation.isValid) {
            return repetitionValidation
        }

        return ValidationResult.valid()
    }

    private fun validateRequiredArguments(
        toolDefinition: ToolDefinition,
        arguments: JSONObject
    ): ValidationResult {
        val missingFields = toolDefinition.requiredArguments.filter { field ->
            !arguments.has(field) || isBlankJsonValue(arguments.opt(field))
        }

        if (missingFields.isEmpty()) {
            return ValidationResult.valid()
        }

        val message = when {
            missingFields.contains("customerId") ->
                "Falta el cliente para ejecutar '${toolDefinition.name}'. Primero debo buscar o crear el cliente."

            missingFields.contains("productId") ->
                "Falta el producto para ejecutar '${toolDefinition.name}'. Primero debo buscar o crear el producto."

            missingFields.contains("items") ->
                "Faltan los productos de la venta. Indica quÃ© producto y quÃ© cantidad deseas vender."

            missingFields.contains("quantity") ->
                "Falta la cantidad. Indica cuÃ¡ntas unidades deseas registrar."

            missingFields.contains("unitCost") ->
                "Falta el costo unitario de la compra."

            missingFields.contains("salePrice") ->
                "Falta el precio de venta del producto."

            missingFields.contains("purchasePrice") ->
                "Falta el precio de compra del producto."

            missingFields.contains("firstName") || missingFields.contains("lastName") ->
                "Falta nombre o apellido del cliente."

            else ->
                "Faltan campos requeridos para '${toolDefinition.name}': ${missingFields.joinToString(", ")}"
        }

        return ValidationResult.askUser(message)
    }

    private fun validateArgumentTypes(
        toolName: String,
        arguments: JSONObject
    ): ValidationResult {
        return when (toolName) {
            "createCustomer" -> validateCreateCustomer(arguments)
            "searchCustomer" -> validateSearchByName(arguments, "cliente")
            "createProduct" -> validateCreateProduct(arguments)
            "searchProduct" -> validateSearchByName(arguments, "producto")
            "updateProduct" -> validateUpdateProduct(arguments)
            "getInventory" -> validateGetInventory(arguments)
            "createPurchase" -> validateCreatePurchase(arguments)
            "createSale" -> validateCreateSale(arguments)
            else -> ValidationResult.valid()
        }
    }

    private fun validateCreateCustomer(arguments: JSONObject): ValidationResult {
        if (arguments.optString("firstName").trim().isBlank()) {
            return ValidationResult.askUser("Â¿CuÃ¡l es el nombre del cliente?")
        }

        if (arguments.optString("lastName").trim().isBlank()) {
            return ValidationResult.askUser("Â¿CuÃ¡l es el apellido del cliente?")
        }

        return ValidationResult.valid()
    }

    private fun validateSearchByName(
        arguments: JSONObject,
        entityName: String
    ): ValidationResult {
        val name = arguments.optString("name").trim()

        if (name.isBlank()) {
            return ValidationResult.askUser("Â¿QuÃ© $entityName deseas buscar?")
        }

        return ValidationResult.valid()
    }

    private fun validateCreateProduct(arguments: JSONObject): ValidationResult {
        if (arguments.optString("name").trim().isBlank()) {
            return ValidationResult.askUser("Â¿CuÃ¡l es el nombre del producto?")
        }

        if (!isNumber(arguments, "salePrice")) {
            return ValidationResult.askUser("Â¿CuÃ¡l es el precio de venta del producto?")
        }

        if (!isNumber(arguments, "purchasePrice")) {
            return ValidationResult.askUser("Â¿CuÃ¡l es el precio de compra del producto?")
        }

        val salePrice = arguments.optDouble("salePrice", -1.0)
        val purchasePrice = arguments.optDouble("purchasePrice", -1.0)

        if (salePrice < 0) {
            return ValidationResult.invalid("El precio de venta no puede ser negativo.")
        }

        if (purchasePrice < 0) {
            return ValidationResult.invalid("El precio de compra no puede ser negativo.")
        }

        return ValidationResult.valid()
    }

    private fun validateUpdateProduct(arguments: JSONObject): ValidationResult {
        val productId = arguments.optString("productId").trim()

        if (productId.isBlank()) {
            return ValidationResult.askUser("Falta el producto que deseas actualizar.")
        }

        val hasAnyUpdateField =
            arguments.has("name") ||
                    arguments.has("description") ||
                    arguments.has("unit") ||
                    arguments.has("salePrice") ||
                    arguments.has("purchasePrice")

        if (!hasAnyUpdateField) {
            return ValidationResult.askUser("Â¿QuÃ© dato del producto deseas actualizar?")
        }

        if (arguments.has("salePrice") && !isNumber(arguments, "salePrice")) {
            return ValidationResult.askUser("El precio de venta debe ser un nÃºmero vÃ¡lido.")
        }

        if (arguments.has("purchasePrice") && !isNumber(arguments, "purchasePrice")) {
            return ValidationResult.askUser("El precio de compra debe ser un nÃºmero vÃ¡lido.")
        }

        return ValidationResult.valid()
    }

    private fun validateGetInventory(arguments: JSONObject): ValidationResult {
        val productId = arguments.optString("productId").trim()

        if (productId.isBlank()) {
            return ValidationResult.askUser("Â¿De quÃ© producto deseas consultar el inventario?")
        }

        return ValidationResult.valid()
    }

    private fun validateCreatePurchase(arguments: JSONObject): ValidationResult {
        val productId = arguments.optString("productId").trim()

        if (productId.isBlank()) {
            return ValidationResult.askUser(
                "Falta el producto para registrar la compra. Primero debo buscar o crear el producto."
            )
        }

        if (!isPositiveInt(arguments, "quantity")) {
            return ValidationResult.askUser("Â¿CuÃ¡ntas unidades deseas comprar?")
        }

        if (!isNumber(arguments, "unitCost")) {
            return ValidationResult.askUser("Â¿CuÃ¡l es el costo unitario de la compra?")
        }

        val unitCost = arguments.optDouble("unitCost", -1.0)

        if (unitCost < 0) {
            return ValidationResult.invalid("El costo unitario no puede ser negativo.")
        }

        return ValidationResult.valid()
    }

    private fun validateCreateSale(arguments: JSONObject): ValidationResult {
        val customerId = arguments.optString("customerId").trim()

        if (customerId.isBlank()) {
            return ValidationResult.askUser(
                "Falta el cliente para registrar la venta. Â¿A quÃ© cliente deseas vender?"
            )
        }

        val items = arguments.optJSONArray("items")

        if (items == null || items.length() == 0) {
            return ValidationResult.askUser(
                "Faltan los productos de la venta. Indica producto y cantidad."
            )
        }

        for (index in 0 until items.length()) {
            val item = items.optJSONObject(index)
                ?: return ValidationResult.invalid("Cada item de venta debe ser un objeto JSON.")

            val productId = item.optString("productId").trim()

            if (productId.isBlank()) {
                return ValidationResult.askUser(
                    "Falta el producto en la venta. Primero debo buscar o crear el producto."
                )
            }

            if (!isPositiveInt(item, "quantity")) {
                return ValidationResult.askUser(
                    "Falta una cantidad vÃ¡lida para el producto de la venta."
                )
            }
        }

        return ValidationResult.valid()
    }

    private fun validateKnownIds(
        toolName: String,
        arguments: JSONObject,
        state: AgentState
    ): ValidationResult {
        return when (toolName) {
            "createPurchase" -> {
                val productId = arguments.optString("productId").trim()

                if (!isKnownProductId(productId, state)) {
                    ValidationResult.invalid(
                        "El modelo intentÃ³ usar un productId que no estÃ¡ en el estado ni en resultados previos: $productId"
                    )
                } else {
                    ValidationResult.valid()
                }
            }

            "getInventory" -> {
                val productId = arguments.optString("productId").trim()

                if (!isKnownProductId(productId, state)) {
                    ValidationResult.invalid(
                        "El modelo intentÃ³ consultar inventario con un productId desconocido: $productId"
                    )
                } else {
                    ValidationResult.valid()
                }
            }

            "updateProduct" -> {
                val productId = arguments.optString("productId").trim()

                if (!isKnownProductId(productId, state)) {
                    ValidationResult.invalid(
                        "El modelo intentÃ³ actualizar un producto con productId desconocido: $productId"
                    )
                } else {
                    ValidationResult.valid()
                }
            }

            "createSale" -> {
                val customerId = arguments.optString("customerId").trim()

                if (!isKnownCustomerId(customerId, state)) {
                    return ValidationResult.invalid(
                        "El modelo intentÃ³ usar un customerId que no estÃ¡ en el estado ni en resultados previos: $customerId"
                    )
                }

                val items = arguments.optJSONArray("items") ?: JSONArray()

                for (index in 0 until items.length()) {
                    val item = items.optJSONObject(index) ?: continue
                    val productId = item.optString("productId").trim()

                    if (!isKnownProductId(productId, state)) {
                        return ValidationResult.invalid(
                            "El modelo intentÃ³ vender un producto con productId desconocido: $productId"
                        )
                    }
                }

                ValidationResult.valid()
            }

            else -> ValidationResult.valid()
        }
    }

    private fun validateRepeatedSuccessfulCalls(
        action: AgentAction.ToolCall,
        state: AgentState
    ): ValidationResult {
        val currentArguments = action.arguments.toString()

        val repeatedSuccessfulCalls = state.executedTools.count { executedTool ->
            executedTool.toolName == action.tool &&
                    executedTool.success &&
                    normalizeJsonText(executedTool.argumentsJson) == normalizeJsonText(currentArguments)
        }

        if (repeatedSuccessfulCalls >= maxRepeatedSuccessfulCalls) {
            return ValidationResult.invalid(
                "Se detectÃ³ repeticiÃ³n de la misma tool exitosa: ${action.tool}. Se detuvo para evitar bucles."
            )
        }

        return ValidationResult.valid()
    }

    private fun isKnownCustomerId(
        customerId: String,
        state: AgentState
    ): Boolean {
        if (customerId.isBlank()) {
            return false
        }

        if (state.lastCustomerId == customerId) {
            return true
        }

        return state.executedTools.any { executedTool ->
            executedTool.resultJson?.contains(customerId) == true
        }
    }

    private fun isKnownProductId(
        productId: String,
        state: AgentState
    ): Boolean {
        if (productId.isBlank()) {
            return false
        }

        if (state.lastProductId == productId) {
            return true
        }

        return state.executedTools.any { executedTool ->
            executedTool.resultJson?.contains(productId) == true
        }
    }

    private fun isBlankJsonValue(value: Any?): Boolean {
        return when (value) {
            null -> true
            JSONObject.NULL -> true
            is String -> value.trim().isBlank()
            is JSONArray -> value.length() == 0
            else -> false
        }
    }

    private fun isNumber(
        jsonObject: JSONObject,
        field: String
    ): Boolean {
        if (!jsonObject.has(field)) {
            return false
        }

        return try {
            jsonObject.getDouble(field)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun isPositiveInt(
        jsonObject: JSONObject,
        field: String
    ): Boolean {
        if (!jsonObject.has(field)) {
            return false
        }

        return try {
            jsonObject.getInt(field) > 0
        } catch (_: Exception) {
            false
        }
    }

    private fun normalizeJsonText(value: String): String {
        return value
            .replace("\\s".toRegex(), "")
            .trim()
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/ToolCatalog.kt

``kotlin
package com.brayan.erpagentlocal.agent

class ToolCatalog(
    private val tools: List<ToolDefinition>
) {

    fun getAll(): List<ToolDefinition> {
        return tools.toList()
    }

    fun count(): Int {
        return tools.size
    }

    fun exists(toolName: String): Boolean {
        return tools.any { tool ->
            tool.name == toolName
        }
    }

    fun find(toolName: String): ToolDefinition? {
        return tools.firstOrNull { tool ->
            tool.name == toolName
        }
    }

    fun describeTools(): String {
        return buildString {
            appendLine("TOOLS FROM OPENAPI CONTRACT")
            appendLine()
            appendLine("Each tool name matches an OpenAPI operationId.")
            appendLine()

            tools.forEach { tool ->
                appendLine(tool.toDebugText())
                appendLine()
            }
        }
    }

    fun describeToolsForPrompt(): String {
        return buildString {
            tools.forEach { tool ->
                appendLine(tool.toPromptBlock())
                appendLine()
            }
        }
    }

    fun namesAsText(): String {
        return tools.joinToString(", ") { tool ->
            tool.name
        }
    }

    companion object {

        fun default(): ToolCatalog {
            return ToolCatalog(defaultTools())
        }

        fun defaultTools(): List<ToolDefinition> {
            return listOf(
                ToolDefinition(
                    name = "createCustomer",
                    operationId = "createCustomer",
                    method = "POST",
                    path = "/customers",
                    description = "Create a customer in the ERP.",
                    requiredArguments = listOf("firstName", "lastName"),
                    optionalArguments = listOf("phone", "email")
                ),
                ToolDefinition(
                    name = "searchCustomer",
                    operationId = "searchCustomer",
                    method = "GET",
                    path = "/customers/search?name={name}",
                    description = "Search customers by name or last name.",
                    requiredArguments = listOf("name")
                ),
                ToolDefinition(
                    name = "createProduct",
                    operationId = "createProduct",
                    method = "POST",
                    path = "/products",
                    description = "Create a product in the ERP.",
                    requiredArguments = listOf("name", "salePrice", "purchasePrice"),
                    optionalArguments = listOf("description", "unit")
                ),
                ToolDefinition(
                    name = "searchProduct",
                    operationId = "searchProduct",
                    method = "GET",
                    path = "/products/search?name={name}",
                    description = "Search products by name.",
                    requiredArguments = listOf("name")
                ),
                ToolDefinition(
                    name = "updateProduct",
                    operationId = "updateProduct",
                    method = "PATCH",
                    path = "/products/{productId}",
                    description = "Update product information.",
                    requiredArguments = listOf("productId"),
                    optionalArguments = listOf(
                        "name",
                        "description",
                        "unit",
                        "salePrice",
                        "purchasePrice"
                    )
                ),
                ToolDefinition(
                    name = "getInventory",
                    operationId = "getInventory",
                    method = "GET",
                    path = "/inventory/{productId}",
                    description = "Get inventory by product ID.",
                    requiredArguments = listOf("productId")
                ),
                ToolDefinition(
                    name = "createPurchase",
                    operationId = "createPurchase",
                    method = "POST",
                    path = "/purchases",
                    description = "Create a purchase and increase inventory.",
                    requiredArguments = listOf("productId", "quantity", "unitCost")
                ),
                ToolDefinition(
                    name = "createSale",
                    operationId = "createSale",
                    method = "POST",
                    path = "/sales",
                    description = "Create a sale and decrease inventory.",
                    requiredArguments = listOf("customerId", "items")
                )
            )
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/ToolCatalogLoader.kt

``kotlin
package com.brayan.erpagentlocal.agent

import android.content.Context
import org.json.JSONArray

object ToolCatalogLoader {

    private const val DEFAULT_TOOLS_ASSET = "tools.json"

    fun loadFromAssets(
        context: Context,
        assetFileName: String = DEFAULT_TOOLS_ASSET
    ): ToolCatalog {
        return try {
            val jsonText = context.assets
                .open(assetFileName)
                .bufferedReader()
                .use { reader ->
                    reader.readText()
                }

            parse(jsonText)
        } catch (_: Exception) {
            /*
             * Si falla la lectura del archivo, usamos el catÃ¡logo por defecto
             * para no romper la app durante la presentaciÃ³n.
             */
            ToolCatalog.default()
        }
    }

    fun parse(jsonText: String): ToolCatalog {
        val jsonArray = JSONArray(jsonText)
        val tools = mutableListOf<ToolDefinition>()

        for (index in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(index)

            val requiredArguments = item
                .optJSONArray("requiredArguments")
                .toStringList()

            val optionalArguments = item
                .optJSONArray("optionalArguments")
                .toStringList()

            tools.add(
                ToolDefinition(
                    name = item.getString("name"),
                    operationId = item.optString("operationId", item.getString("name")),
                    method = item.getString("method"),
                    path = item.getString("path"),
                    description = item.optString("description", ""),
                    requiredArguments = requiredArguments,
                    optionalArguments = optionalArguments
                )
            )
        }

        return ToolCatalog(tools)
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) {
            return emptyList()
        }

        val result = mutableListOf<String>()

        for (index in 0 until length()) {
            result.add(optString(index))
        }

        return result.filter { value ->
            value.isNotBlank()
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/ToolDefinition.kt

``kotlin
package com.brayan.erpagentlocal.agent

data class ToolDefinition(
    val name: String,
    val operationId: String,
    val method: String,
    val path: String,
    val description: String,
    val requiredArguments: List<String>,
    val optionalArguments: List<String> = emptyList()
) {
    fun hasRequiredArguments(argumentNames: Set<String>): Boolean {
        return requiredArguments.all { requiredArgument ->
            argumentNames.contains(requiredArgument)
        }
    }

    fun allArgumentNames(): List<String> {
        return requiredArguments + optionalArguments
    }

    fun toPromptBlock(): String {
        return buildString {
            appendLine("{")
            appendLine("  \"name\": \"$name\",")
            appendLine("  \"operationId\": \"$operationId\",")
            appendLine("  \"method\": \"$method\",")
            appendLine("  \"path\": \"$path\",")
            appendLine("  \"description\": \"$description\",")
            appendLine("  \"requiredArguments\": [${requiredArguments.joinToString(", ") { "\"$it\"" }}],")
            appendLine("  \"optionalArguments\": [${optionalArguments.joinToString(", ") { "\"$it\"" }}]")
            appendLine("}")
        }
    }

    fun toDebugText(): String {
        return buildString {
            appendLine("- $name")
            appendLine("  operationId: $operationId")
            appendLine("  method: $method")
            appendLine("  path: $path")
            appendLine("  description: $description")
            appendLine("  required: ${requiredArguments.joinToString(", ").ifBlank { "none" }}")
            appendLine("  optional: ${optionalArguments.joinToString(", ").ifBlank { "none" }}")
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/ToolExecutionResult.kt

``kotlin
package com.brayan.erpagentlocal.agent

import org.json.JSONObject

data class ToolExecutionResult(
    val toolName: String,
    val success: Boolean,
    val message: String,
    val statusCode: Int? = null,
    val rawResponse: JSONObject? = null,
    val error: String? = null,
    val executedAt: Long = System.currentTimeMillis()
) {

    fun toExecutedTool(argumentsJson: String): ExecutedTool {
        return ExecutedTool(
            toolName = toolName,
            argumentsJson = argumentsJson,
            success = success,
            resultSummary = message,
            resultJson = rawResponse?.toString(2),
            createdAt = executedAt
        )
    }

    fun toPromptBlock(): String {
        return buildString {
            appendLine("{")
            appendLine("  \"toolName\": \"$toolName\",")
            appendLine("  \"success\": $success,")

            if (statusCode != null) {
                appendLine("  \"statusCode\": $statusCode,")
            }

            appendLine("  \"message\": \"${escape(message)}\"")

            if (rawResponse != null) {
                appendLine("  ,\"rawResponse\": ${rawResponse.toString(2)}")
            }

            if (!error.isNullOrBlank()) {
                appendLine("  ,\"error\": \"${escape(error)}\"")
            }

            appendLine("}")
        }
    }

    fun toDebugText(): String {
        return buildString {
            appendLine("Tool execution result")
            appendLine("Tool: $toolName")
            appendLine("Success: $success")

            if (statusCode != null) {
                appendLine("Status code: $statusCode")
            }

            appendLine("Message: $message")

            if (!error.isNullOrBlank()) {
                appendLine("Error: $error")
            }

            if (rawResponse != null) {
                appendLine("Raw response:")
                appendLine(rawResponse.toString(2))
            }
        }
    }

    fun toJsonObject(): JSONObject {
        return rawResponse ?: JSONObject()
            .put("success", success)
            .put("message", message)
            .put("data", JSONObject())
            .put("error", error ?: "")
            .put("statusCode", statusCode ?: 0)
    }

    private fun escape(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")
    }

    companion object {

        fun success(
            toolName: String,
            message: String,
            rawResponse: JSONObject,
            statusCode: Int? = null
        ): ToolExecutionResult {
            return ToolExecutionResult(
                toolName = toolName,
                success = true,
                message = message,
                statusCode = statusCode,
                rawResponse = rawResponse
            )
        }

        fun failure(
            toolName: String,
            message: String,
            error: String? = null,
            rawResponse: JSONObject? = null,
            statusCode: Int? = null
        ): ToolExecutionResult {
            return ToolExecutionResult(
                toolName = toolName,
                success = false,
                message = message,
                statusCode = statusCode,
                rawResponse = rawResponse,
                error = error
            )
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/ToolRegistry.kt

``kotlin
package com.brayan.erpagentlocal.agent

/*
 * Compatibilidad temporal:
 *
 * En fases posteriores AgentService usarÃ¡ ToolCatalog directamente.
 * Por ahora mantenemos ToolRegistry para no romper PromptProvider,
 * AgentService y ToolExecutor actuales.
 */
object ToolRegistry {

    private var catalog: ToolCatalog = ToolCatalog.default()

    val tools: List<ToolDefinition>
        get() = catalog.getAll()

    fun setCatalog(newCatalog: ToolCatalog) {
        catalog = newCatalog
    }

    fun getCatalog(): ToolCatalog {
        return catalog
    }

    fun exists(toolName: String): Boolean {
        return catalog.exists(toolName)
    }

    fun find(toolName: String): ToolDefinition? {
        return catalog.find(toolName)
    }

    fun describeTools(): String {
        return catalog.describeTools()
    }

    fun describeToolsForPrompt(): String {
        return catalog.describeToolsForPrompt()
    }

    fun count(): Int {
        return catalog.count()
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/agent/ValidationResult.kt

``kotlin
package com.brayan.erpagentlocal.agent

data class ValidationResult(
    val isValid: Boolean,
    val message: String = "",
    val shouldAskUser: Boolean = false,
    val askUserMessage: String? = null
) {

    companion object {

        fun valid(): ValidationResult {
            return ValidationResult(
                isValid = true,
                message = "Valid tool call"
            )
        }

        fun invalid(
            message: String
        ): ValidationResult {
            return ValidationResult(
                isValid = false,
                message = message,
                shouldAskUser = false,
                askUserMessage = null
            )
        }

        fun askUser(
            message: String
        ): ValidationResult {
            return ValidationResult(
                isValid = false,
                message = message,
                shouldAskUser = true,
                askUserMessage = message
            )
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/ai/AgentService.kt

``kotlin
package com.brayan.erpagentlocal.ai

import com.brayan.erpagentlocal.agent.AgentAction
import com.brayan.erpagentlocal.agent.AgentActionParser
import com.brayan.erpagentlocal.agent.AgentErrorMapper
import com.brayan.erpagentlocal.agent.AgentMemory
import com.brayan.erpagentlocal.agent.AgentState
import com.brayan.erpagentlocal.agent.AgentStateUpdater
import com.brayan.erpagentlocal.agent.ToolCallValidator
import com.brayan.erpagentlocal.agent.ToolRegistry
import com.brayan.erpagentlocal.data.ToolExecutor
import com.brayan.erpagentlocal.util.JsonUtils
import org.json.JSONArray
import org.json.JSONObject

class AgentService(
    private val parser: AgentActionParser = AgentActionParser(),
    private val toolExecutor: ToolExecutor = ToolExecutor(),
    private val memory: AgentMemory = AgentMemory(),
    private val stateUpdater: AgentStateUpdater = AgentStateUpdater(),
    private val toolCallValidator: ToolCallValidator = ToolCallValidator()
) {

    private var agentState: AgentState = AgentState.empty()

    suspend fun processUserMessage(input: String): String {
        val trimmed = input.trim()

        if (trimmed.isBlank()) {
            return "Escribe una instrucciÃ³n o un comando."
        }

        memory.addUser(trimmed)

        val response = when {
            trimmed == "/tools" -> ToolRegistry.describeTools()

            trimmed == "/prompt" -> PromptProvider.buildSystemPrompt()

            trimmed == "/memory" -> memory.asText().ifBlank {
                "La memoria estÃ¡ vacÃ­a."
            }

            trimmed == "/memory-short" -> memory.asShortSummary()

            trimmed == "/state" -> agentState.toDebugText()

            trimmed == "/clear" -> {
                memory.clear()
                agentState = AgentState.empty()
                "Memoria y estado limpiados."
            }

            trimmed == "/example-customer" -> exampleCustomer()

            trimmed == "/example-product" -> exampleProduct()

            trimmed == "/example-purchase" -> examplePurchase()

            trimmed == "/example-sale" -> exampleSale()

            trimmed == "/agent-demo" -> runFullDemo()

            trimmed.startsWith("{") -> executeParsedAction(trimmed)

            else -> {
                """
                Para lenguaje natural usa el modelo local inicializado.

                Ejemplos:
                - Crea un cliente llamado Ana LÃ³pez
                - Crea un producto llamado CafÃ© con precio de venta 25 y precio de compra 18
                - Compra 50 unidades de CafÃ© a 18
                - Crea un cliente Emanuel Blanco y vÃ©ndele 4 zapatos
                """.trimIndent()
            }
        }

        memory.addAssistant(response)
        return response
    }

    suspend fun processNaturalLanguageWithModel(
        userMessage: String,
        localModelService: LocalModelService,
        maxSteps: Int = 8
    ): String {
        return try {
            if (!localModelService.isInitialized()) {
                return AgentErrorMapper.fromModelNotReady().userMessage
            }

            val cleanUserMessage = userMessage.trim()

            if (cleanUserMessage.isBlank()) {
                return "Escribe una instrucciÃ³n para el agente."
            }

            memory.addUser(cleanUserMessage)

            val directFlowResult = tryExecuteDirectSpanishFlow(cleanUserMessage)

            if (directFlowResult != null) {
                memory.addAssistant(directFlowResult)
                return directFlowResult
            }

            val loopContextBuilder = StringBuilder()

            var consecutiveValidationErrors = 0
            var consecutiveToolErrors = 0

            for (step in 1..maxSteps) {
                val prompt = PromptProvider.buildAgentDecisionPrompt(
                    userMessage = cleanUserMessage,
                    memoryText = "",
                    loopContext = buildLoopContext(
                        loopContext = loopContextBuilder.toString(),
                        state = agentState
                    ),
                    step = step,
                    maxSteps = maxSteps
                )

                val rawModelResponse = localModelService.generateDecision(prompt)
                val cleanedJson = JsonUtils.extractJsonObject(rawModelResponse)

                val parsedAction = parseOrRepairDecision(
                    rawOutput = cleanedJson,
                    localModelService = localModelService,
                    originalUserMessage = cleanUserMessage
                )

                when (parsedAction) {
                    is AgentAction.ToolCall -> {
                        val validationResult = toolCallValidator.validate(
                            action = parsedAction,
                            state = agentState
                        )

                        if (!validationResult.isValid) {
                            consecutiveValidationErrors++

                            val validationMessage = if (validationResult.shouldAskUser) {
                                validationResult.askUserMessage ?: validationResult.message
                            } else {
                                validationResult.message
                            }

                            if (validationResult.shouldAskUser) {
                                agentState = agentState.withPendingQuestion(validationMessage)
                                memory.addAssistant(validationMessage)

                                return renderFinalResult(
                                    finalMessage = validationMessage,
                                    state = agentState
                                )
                            }

                            val validationContext = buildValidationErrorContext(
                                tool = parsedAction.tool,
                                arguments = parsedAction.arguments,
                                message = validationMessage
                            )

                            loopContextBuilder.appendLine(validationContext)

                            if (consecutiveValidationErrors >= 2) {
                                val finalMessage = """
                                    No pude ejecutar la acciÃ³n porque faltan datos o el modelo intentÃ³ usar informaciÃ³n invÃ¡lida.

                                    $validationMessage
                                """.trimIndent()

                                memory.addAssistant(finalMessage)

                                return renderFinalResult(
                                    finalMessage = finalMessage,
                                    state = agentState
                                )
                            }

                            continue
                        }

                        consecutiveValidationErrors = 0

                        val toolResponse = safeExecuteTool(parsedAction)
                        val toolSuccess = toolResponse.optBoolean("success", false)

                        if (AgentErrorMapper.isEmptySearchResult(parsedAction.tool, toolResponse)) {
                            val message = AgentErrorMapper.emptySearchMessage(
                                toolName = parsedAction.tool,
                                arguments = parsedAction.arguments
                            )

                            agentState = agentState.withPendingQuestion(message)
                            memory.addAssistant(message)

                            return renderFinalResult(
                                finalMessage = message,
                                state = agentState
                            )
                        }

                        storeToolResult(
                            toolName = parsedAction.tool,
                            arguments = parsedAction.arguments,
                            response = toolResponse
                        )

                        val toolContext = buildToolContext(
                            tool = parsedAction.tool,
                            arguments = parsedAction.arguments,
                            response = toolResponse,
                            state = agentState
                        )

                        loopContextBuilder.appendLine(toolContext)

                        if (!toolSuccess) {
                            consecutiveToolErrors++

                            if (shouldStopAfterToolError(toolResponse, consecutiveToolErrors)) {
                                val finalMessage = buildToolErrorFinalMessage(toolResponse)
                                memory.addAssistant(finalMessage)

                                return renderFinalResult(
                                    finalMessage = finalMessage,
                                    state = agentState
                                )
                            }
                        } else {
                            consecutiveToolErrors = 0
                        }
                    }

                    is AgentAction.AskUser -> {
                        val message = parsedAction.message

                        agentState = agentState.withPendingQuestion(message)
                        memory.addAssistant(message)

                        return renderFinalResult(
                            finalMessage = message,
                            state = agentState
                        )
                    }

                    is AgentAction.Final -> {
                        val message = parsedAction.message

                        agentState = agentState.clearPendingQuestion()
                        memory.addAssistant(message)

                        return renderFinalResult(
                            finalMessage = message,
                            state = agentState
                        )
                    }

                    is AgentAction.Invalid -> {
                        val message = parsedAction.error
                        memory.addAssistant(message)

                        return renderFinalResult(
                            finalMessage = message,
                            state = agentState
                        )
                    }
                }
            }

            finishAfterStepLimit(
                userMessage = cleanUserMessage,
                localModelService = localModelService,
                loopContext = loopContextBuilder.toString(),
                maxSteps = maxSteps
            )
        } catch (exception: Exception) {
            val message = """
                OcurriÃ³ un error durante la ejecuciÃ³n del agente.

                Detalle:
                ${exception.message ?: "Error desconocido"}
            """.trimIndent()

            memory.addAssistant(message)
            message
        }
    }

    suspend fun checkHealth(): String {
        val response = toolExecutor.checkHealth()
        memory.addTool("health", response)
        return response
    }

    suspend fun runFullDemo(): String {
        val response = toolExecutor.runFullErpDemo()
        memory.addTool("demo", response)
        return response
    }

    fun getStateText(): String {
        return agentState.toDebugText()
    }

    fun getMemoryText(): String {
        return memory.asText()
    }

    private suspend fun tryExecuteDirectSpanishFlow(
    userMessage: String
): String? {
    parseMultipleCreateCustomersCommand(userMessage)?.let { customerNames ->
        return executeCreateMultipleCustomersFlow(customerNames)
    }

    parseCreateCustomerAndSaleCommand(userMessage)?.let { command ->
        return executeCreateCustomerAndSaleFlow(command)
    }

    buildFallbackActionFromUserMessage(userMessage)?.let { action ->
        return executeValidatedToolCall(action)
    }

    return null
}
    private suspend fun executeCreateMultipleCustomersFlow(
    customerNames: List<String>
): String {
    if (customerNames.isEmpty()) {
        return "No encontrÃ© nombres de clientes para crear."
    }

    val created = mutableListOf<String>()
    val existing = mutableListOf<String>()
    val failed = mutableListOf<String>()

    customerNames.forEach { fullName ->
        val searchArguments = JSONObject()
            .put("name", fullName)

        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "searchCustomer",
                arguments = searchArguments
            )
        )

        val existingCustomer = findMatchingCustomer(
            response = searchResponse,
            fullName = fullName
        )

        if (existingCustomer != null) {
            storeToolResult(
                toolName = "searchCustomer",
                arguments = searchArguments,
                response = searchResponse
            )

            existing.add(fullName)
            return@forEach
        }

        val splitName = splitFullName(fullName)

        if (splitName.first.isBlank() || splitName.second.isBlank()) {
            failed.add("$fullName: falta nombre o apellido")
            return@forEach
        }

        val createArguments = JSONObject()
            .put("firstName", splitName.first)
            .put("lastName", splitName.second)

        val createResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "createCustomer",
                arguments = createArguments
            )
        )

        storeToolResult(
            toolName = "createCustomer",
            arguments = createArguments,
            response = createResponse
        )

        if (createResponse.optBoolean("success", false)) {
            created.add(fullName)
        } else {
            failed.add("$fullName: ${createResponse.optString("message", "no se pudo crear")}")
        }
    }

    return buildString {
        if (created.isNotEmpty()) {
            appendLine("Clientes creados correctamente:")
            created.forEach { name ->
                appendLine("- $name")
            }
        }

        if (existing.isNotEmpty()) {
            if (isNotEmpty()) appendLine()
            appendLine("Clientes que ya existÃ­an:")
            existing.forEach { name ->
                appendLine("- $name")
            }
        }

        if (failed.isNotEmpty()) {
            if (isNotEmpty()) appendLine()
            appendLine("No se pudieron procesar:")
            failed.forEach { item ->
                appendLine("- $item")
            }
        }
    }.trim()
}

private fun parseMultipleCreateCustomersCommand(
    userMessage: String
): List<String>? {
    val text = normalizeText(userMessage)

    val looksLikeCreateCustomer =
        text.contains("crea") &&
                (
                        text.contains("usuario") ||
                                text.contains("cliente") ||
                                text.contains("customer")
                        )

    if (!looksLikeCreateCustomer) {
        return null
    }

    val cleaned = userMessage
        .replace(Regex("(?i)crea\\s+"), "")
        .replace(Regex("(?i)crear\\s+"), "")
        .replace(Regex("(?i)registra\\s+"), "")
        .replace(Regex("(?i)registrar\\s+"), "")
        .replace(Regex("(?i)un\\s+"), "")
        .replace(Regex("(?i)una\\s+"), "")
        .replace(Regex("(?i)al\\s+"), "")
        .replace(Regex("(?i)a\\s+"), "")
        .replace(Regex("(?i)cliente\\s+llamado\\s+"), "")
        .replace(Regex("(?i)usuario\\s+llamado\\s+"), "")
        .replace(Regex("(?i)cliente\\s+"), "")
        .replace(Regex("(?i)usuario\\s+"), "")
        .replace(Regex("(?i)customer\\s+"), "")
        .trim()

    val parts = cleaned
        .split(
            Regex("(?i)\\s+y\\s+otro\\s+|\\s+y\\s+otra\\s+|\\s+y\\s+|\\s*,\\s*")
        )
        .map { raw ->
            raw
                .replace(Regex("(?i)^cliente\\s+llamado\\s+"), "")
                .replace(Regex("(?i)^usuario\\s+llamado\\s+"), "")
                .replace(Regex("(?i)^cliente\\s+"), "")
                .replace(Regex("(?i)^usuario\\s+"), "")
                .replace(Regex("[.,;:]$"), "")
                .trim()
        }
        .filter { name ->
            val words = name.split(Regex("\\s+")).filter { it.isNotBlank() }
            words.size >= 2
        }

    if (parts.isEmpty()) {
        return null
    }

    return parts.distinctBy { normalizeText(it) }
}

    private suspend fun executeCreateCustomerAndSaleFlow(
        command: CreateCustomerAndSaleCommand
    ): String {
        val customer = findOrCreateCustomer(command.customerFullName)

        if (customer == null || customer.customerId.isBlank()) {
            return "No pude crear o encontrar al cliente ${command.customerFullName}."
        }

        val product = findProductByName(command.productName)

        if (product == null || product.productId.isBlank()) {
            return "No encontrÃ© el producto \"${command.productName}\". CrÃ©alo primero antes de registrar la venta."
        }

        val saleItems = JSONArray()
            .put(
                JSONObject()
                    .put("productId", product.productId)
                    .put("quantity", command.quantity)
            )

        val saleArguments = JSONObject()
            .put("customerId", customer.customerId)
            .put("items", saleItems)

        val saleAction = AgentAction.ToolCall(
            tool = "createSale",
            arguments = saleArguments
        )

        val validationResult = toolCallValidator.validate(
            action = saleAction,
            state = agentState
        )

        if (!validationResult.isValid) {
            return validationResult.askUserMessage
                ?: validationResult.message
        }

        val saleResponse = safeExecuteTool(saleAction)

        storeToolResult(
            toolName = "createSale",
            arguments = saleArguments,
            response = saleResponse
        )

        if (!saleResponse.optBoolean("success", false)) {
            return buildToolErrorFinalMessage(saleResponse)
        }

        return renderFinalResult(
            finalMessage = "Listo. CreÃ© o encontrÃ© al cliente ${command.customerFullName} y registrÃ© la venta de ${command.quantity} unidades de ${command.productName}.",
            state = agentState
        )
    }

    private suspend fun findOrCreateCustomer(
        fullName: String
    ): CustomerRef? {
        val searchArguments = JSONObject()
            .put("name", fullName)

        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "searchCustomer",
                arguments = searchArguments
            )
        )

        val existingCustomer = findMatchingCustomer(
            response = searchResponse,
            fullName = fullName
        )

        if (existingCustomer != null) {
            storeToolResult(
                toolName = "searchCustomer",
                arguments = searchArguments,
                response = searchResponse
            )

            val customerId = existingCustomer.optString("customerId", "")

            if (customerId.isNotBlank()) {
                return CustomerRef(
                    customerId = customerId,
                    customerName = fullName
                )
            }
        }

        val splitName = splitFullName(fullName)

        val createArguments = JSONObject()
            .put("firstName", splitName.first)
            .put("lastName", splitName.second)

        val createResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "createCustomer",
                arguments = createArguments
            )
        )

        storeToolResult(
            toolName = "createCustomer",
            arguments = createArguments,
            response = createResponse
        )

        val customerObject = firstDataObject(createResponse)
        val customerId = customerObject?.optString("customerId", "").orEmpty()

        if (customerId.isBlank()) {
            return null
        }

        return CustomerRef(
            customerId = customerId,
            customerName = fullName
        )
    }

    private suspend fun findProductByName(
        productName: String
    ): ProductRef? {
        val searchArguments = JSONObject()
            .put("name", productName)

        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "searchProduct",
                arguments = searchArguments
            )
        )

        val productObject = findMatchingProduct(
            response = searchResponse,
            productName = productName
        )

        if (productObject == null) {
            return null
        }

        storeToolResult(
            toolName = "searchProduct",
            arguments = searchArguments,
            response = searchResponse
        )

        val productId = productObject.optString("productId", "")
        val name = productObject.optString("name", productName)

        if (productId.isBlank()) {
            return null
        }

        return ProductRef(
            productId = productId,
            productName = name
        )
    }

    private suspend fun executeParsedAction(rawText: String): String {
        return when (val action = parser.parse(rawText)) {
            is AgentAction.ToolCall -> executeValidatedToolCall(action)

            is AgentAction.AskUser -> {
                agentState = agentState.withPendingQuestion(action.message)
                action.message
            }

            is AgentAction.Final -> {
                agentState = agentState.clearPendingQuestion()
                action.message
            }

            is AgentAction.Invalid -> {
                action.error
            }
        }
    }

    private suspend fun executeValidatedToolCall(
        action: AgentAction.ToolCall
    ): String {
        val validationResult = toolCallValidator.validate(
            action = action,
            state = agentState
        )

        if (!validationResult.isValid) {
            val message = if (validationResult.shouldAskUser) {
                validationResult.askUserMessage ?: validationResult.message
            } else {
                "No ejecutÃ© la acciÃ³n porque no pasÃ³ la validaciÃ³n interna:\n${validationResult.message}"
            }

            if (validationResult.shouldAskUser) {
                agentState = agentState.withPendingQuestion(message)
            }

            return message
        }

        val existingEntityMessage = precheckBeforeCreate(action)

        if (existingEntityMessage != null) {
            return existingEntityMessage
        }

        val response = safeExecuteTool(action)

        storeToolResult(
            toolName = action.tool,
            arguments = action.arguments,
            response = response
        )

        if (!response.optBoolean("success", false)) {
            return buildToolErrorFinalMessage(response)
        }

        return renderSuccessMessageFromTool(
            tool = action.tool,
            response = response,
            state = agentState
        )
    }

    private suspend fun precheckBeforeCreate(
        action: AgentAction.ToolCall
    ): String? {
        return when (action.tool) {
            "createProduct" -> precheckProductBeforeCreate(action)
            "createCustomer" -> precheckCustomerBeforeCreate(action)
            else -> null
        }
    }

    private suspend fun precheckProductBeforeCreate(
        action: AgentAction.ToolCall
    ): String? {
        val productName = action.arguments.optString("name", "").trim()

        if (productName.isBlank()) {
            return null
        }

        val searchArguments = JSONObject()
            .put("name", productName)

        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "searchProduct",
                arguments = searchArguments
            )
        )

        val existingProduct = findMatchingProduct(
            response = searchResponse,
            productName = productName
        )

        if (existingProduct == null) {
            return null
        }

        storeToolResult(
            toolName = "searchProduct",
            arguments = searchArguments,
            response = searchResponse
        )

        val existingName = existingProduct.optString("name", productName)
        val existingId = existingProduct.optString("productId", "")

        return renderFinalResult(
            finalMessage = "El producto \"$existingName\" ya existe. No creÃ© un duplicado.",
            state = agentState
        ) + if (existingId.isNotBlank()) "\nID existente: $existingId" else ""
    }

    private suspend fun precheckCustomerBeforeCreate(
        action: AgentAction.ToolCall
    ): String? {
        val firstName = action.arguments.optString("firstName", "").trim()
        val lastName = action.arguments.optString("lastName", "").trim()
        val fullName = "$firstName $lastName".trim()

        if (fullName.isBlank()) {
            return null
        }

        val searchArguments = JSONObject()
            .put("name", fullName)

        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "searchCustomer",
                arguments = searchArguments
            )
        )

        val existingCustomer = findMatchingCustomer(
            response = searchResponse,
            fullName = fullName
        )

        if (existingCustomer == null) {
            return null
        }

        storeToolResult(
            toolName = "searchCustomer",
            arguments = searchArguments,
            response = searchResponse
        )

        val existingId = existingCustomer.optString("customerId", "")

        return renderFinalResult(
            finalMessage = "El cliente \"$fullName\" ya existe. No creÃ© un duplicado.",
            state = agentState
        ) + if (existingId.isNotBlank()) "\nID existente: $existingId" else ""
    }

    private suspend fun parseOrRepairDecision(
        rawOutput: String,
        localModelService: LocalModelService,
        originalUserMessage: String
    ): AgentAction {
        val parsed = parser.parse(rawOutput)

        if (parsed !is AgentAction.Invalid) {
            return parsed
        }

        val fallbackAction = buildFallbackActionFromUserMessage(originalUserMessage)

        if (fallbackAction != null) {
            return fallbackAction
        }

        val repairPrompt = PromptProvider.buildJsonRepairPrompt(
            invalidOutput = rawOutput,
            error = parsed.error
        )

        val repairedRawOutput = localModelService.generateDecision(repairPrompt)
        val repairedJson = JsonUtils.extractJsonObject(repairedRawOutput)

        return parser.parse(repairedJson)
    }

    private suspend fun finishAfterStepLimit(
        userMessage: String,
        localModelService: LocalModelService,
        loopContext: String,
        maxSteps: Int
    ): String {
        val summaryPrompt = PromptProvider.buildFinalSummaryPrompt(
            userMessage = userMessage,
            memoryText = "",
            loopContext = buildLoopContext(
                loopContext = loopContext,
                state = agentState
            )
        )

        val rawSummary = localModelService.generateDecision(summaryPrompt)
        val cleanSummaryJson = JsonUtils.extractJsonObject(rawSummary)

        val finalMessage = when (val action = parser.parse(cleanSummaryJson)) {
            is AgentAction.Final -> action.message
            else -> "El agente llegÃ³ al lÃ­mite de $maxSteps pasos. Intenta escribir la instrucciÃ³n de forma mÃ¡s especÃ­fica."
        }

        memory.addAssistant(finalMessage)

        return renderFinalResult(
            finalMessage = finalMessage,
            state = agentState
        )
    }

    private suspend fun safeExecuteTool(
        action: AgentAction.ToolCall
    ): JSONObject {
        return try {
            toolExecutor.executeRaw(action)
        } catch (exception: Exception) {
            JSONObject()
                .put("success", false)
                .put("message", exception.message ?: "Error ejecutando la acciÃ³n.")
                .put("statusCode", 0)
                .put("data", JSONObject())
                .put("error", exception.message ?: "Unknown error")
        }
    }

    private fun buildFallbackActionFromUserMessage(
        userMessage: String
    ): AgentAction.ToolCall? {
        val text = userMessage.trim()

        val createProductRegex = Regex(
            pattern = "(?i)crea\\s+(?:un\\s+)?producto\\s+llamado\\s+(.+?)\\s+con\\s+precio\\s+de\\s+venta\\s+(\\d+(?:\\.\\d+)?)\\s+y\\s+precio\\s+de\\s+compra\\s+(\\d+(?:\\.\\d+)?)"
        )

        createProductRegex.find(text)?.let { match ->
            val name = match.groupValues[1].trim()
            val salePrice = match.groupValues[2].toDoubleOrNull()
            val purchasePrice = match.groupValues[3].toDoubleOrNull()

            if (name.isNotBlank() && salePrice != null && purchasePrice != null) {
                return AgentAction.ToolCall(
                    tool = "createProduct",
                    arguments = JSONObject()
                        .put("name", name)
                        .put("description", "Producto creado desde agente local")
                        .put("unit", "unit")
                        .put("salePrice", salePrice)
                        .put("purchasePrice", purchasePrice)
                )
            }
        }

        val searchProductRegex = Regex(
            pattern = "(?i)(busca|buscar|buscame|bÃºscame|encuentra|consulta)\\s+(?:un\\s+)?producto(?:\\s+llamado)?\\s+(.+)"
        )

        searchProductRegex.find(text)?.let { match ->
            val name = cleanProductName(match.groupValues[2])

            if (name.isNotBlank()) {
                return AgentAction.ToolCall(
                    tool = "searchProduct",
                    arguments = JSONObject()
                        .put("name", name)
                )
            }
        }

        return null
    }

    private fun parseCreateCustomerAndSaleCommand(
        userMessage: String
    ): CreateCustomerAndSaleCommand? {
        val text = userMessage.trim()

        val saleRegex = Regex(
            pattern = "(?i)(v[eÃ©]ndele|vendele|vendel[eÃ©]|venderle|vende)\\s+(\\d+)\\s+(?:unidades?\\s+de\\s+)?(.+)$"
        )

        val saleMatch = saleRegex.find(text) ?: return null

        val quantity = saleMatch.groupValues[2].toIntOrNull() ?: return null
        val productName = cleanProductName(saleMatch.groupValues[3])

        if (productName.isBlank()) {
            return null
        }

        val beforeSale = text
            .substring(0, saleMatch.range.first)
            .replace(Regex("(?i)\\s+y\\s*$"), "")
            .trim()

        val customerRegex = Regex(
            pattern = "(?i)crea\\s+(?:al\\s+|a\\s+|un\\s+|una\\s+)?(?:cliente|usuario|customer)?\\s*(.+)$"
        )

        val customerMatch = customerRegex.find(beforeSale) ?: return null
        val customerFullName = cleanCustomerName(customerMatch.groupValues[1])

        if (customerFullName.isBlank()) {
            return null
        }

        return CreateCustomerAndSaleCommand(
            customerFullName = customerFullName,
            quantity = quantity,
            productName = productName
        )
    }

    private fun firstDataObject(
        response: JSONObject
    ): JSONObject? {
        val data = response.opt("data")

        return when (data) {
            is JSONArray -> data.optJSONObject(0)
            is JSONObject -> data
            else -> null
        }
    }

    private fun findMatchingProduct(
        response: JSONObject,
        productName: String
    ): JSONObject? {
        val data = response.opt("data")
        val target = normalizeText(productName)

        if (data is JSONObject) {
            val name = normalizeText(data.optString("name", ""))

            if (name == target) {
                return data
            }

            return null
        }

        if (data is JSONArray) {
            for (index in 0 until data.length()) {
                val item = data.optJSONObject(index) ?: continue
                val name = normalizeText(item.optString("name", ""))

                if (name == target) {
                    return item
                }
            }
        }

        return null
    }

    private fun findMatchingCustomer(
        response: JSONObject,
        fullName: String
    ): JSONObject? {
        val data = response.opt("data")
        val target = normalizeText(fullName)

        fun matches(customer: JSONObject): Boolean {
            val full = normalizeText(
                customer.optString(
                    "customerName",
                    "${customer.optString("firstName", "")} ${customer.optString("lastName", "")}"
                )
            )

            val name = normalizeText(customer.optString("name", ""))

            return full == target || name == target
        }

        if (data is JSONObject) {
            return if (matches(data)) data else null
        }

        if (data is JSONArray) {
            for (index in 0 until data.length()) {
                val item = data.optJSONObject(index) ?: continue

                if (matches(item)) {
                    return item
                }
            }
        }

        return null
    }

    private fun splitFullName(
        fullName: String
    ): Pair<String, String> {
        val parts = fullName
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        if (parts.isEmpty()) {
            return Pair("", "")
        }

        if (parts.size == 1) {
            return Pair(parts[0], "")
        }

        val lastName = parts.last()
        val firstName = parts.dropLast(1).joinToString(" ")

        return Pair(firstName, lastName)
    }

    private fun normalizeText(value: String): String {
        return value
            .trim()
            .lowercase()
            .replace(Regex("\\s+"), " ")
    }

    private fun cleanProductName(value: String): String {
        return value
            .trim()
            .replace(Regex("(?i)^de\\s+"), "")
            .replace(Regex("[.,;:]$"), "")
            .trim()
    }

    private fun cleanCustomerName(value: String): String {
        return value
            .trim()
            .replace(Regex("[.,;:]$"), "")
            .trim()
    }

    private fun storeToolResult(
        toolName: String,
        arguments: JSONObject,
        response: JSONObject
    ) {
        memory.addTool(
            toolName = toolName,
            content = response.toString()
        )

        agentState = stateUpdater.updateAndStoreExecution(
            toolName = toolName,
            arguments = arguments,
            response = response,
            currentState = agentState
        )
    }

    private fun shouldStopAfterToolError(
        toolResponse: JSONObject,
        consecutiveToolErrors: Int
    ): Boolean {
        val statusCode = toolResponse.optInt("statusCode", 0)
        val message = toolResponse.optString("message", "").lowercase()
        val userMessage = toolResponse.optString("userMessage", "").lowercase()

        if (statusCode == 400) return true
        if (statusCode == 404) return true
        if (statusCode == 409) return true
        if (message.contains("stock") || userMessage.contains("stock")) return true
        if (consecutiveToolErrors >= 2) return true

        return false
    }

    private fun buildToolErrorFinalMessage(
        toolResponse: JSONObject
    ): String {
        val mappedError = AgentErrorMapper.fromHttpResponse(toolResponse)
        return mappedError.userMessage
    }

    private fun renderFinalResult(
        finalMessage: String,
        state: AgentState
    ): String {
        return buildString {
            appendLine(finalMessage)

            if (
                !state.lastCustomerName.isNullOrBlank() ||
                !state.lastProductName.isNullOrBlank() ||
                !state.lastSaleId.isNullOrBlank() ||
                !state.lastPurchaseId.isNullOrBlank() ||
                state.lastInventoryStock != null
            ) {
                appendLine()
                appendLine("Resumen:")

                if (!state.lastCustomerName.isNullOrBlank()) {
                    appendLine("- Cliente: ${state.lastCustomerName}")
                }

                if (!state.lastProductName.isNullOrBlank()) {
                    appendLine("- Producto: ${state.lastProductName}")
                }

                if (!state.lastSaleId.isNullOrBlank()) {
                    appendLine("- Venta: ${state.lastSaleId}")
                }

                if (!state.lastPurchaseId.isNullOrBlank()) {
                    appendLine("- Compra: ${state.lastPurchaseId}")
                }

                if (state.lastInventoryStock != null) {
                    appendLine("- Stock actual: ${state.lastInventoryStock}")
                }
            }
        }.trim()
    }

    private fun renderSuccessMessageFromTool(
        tool: String,
        response: JSONObject,
        state: AgentState
    ): String {
        val backendMessage = response.optString("message", "")

        val baseMessage = when (tool) {
            "createCustomer" -> "Listo. El cliente fue creado correctamente."
            "createProduct" -> "Listo. El producto fue creado correctamente."
            "createPurchase" -> "Listo. La compra fue registrada correctamente."
            "createSale" -> "Listo. La venta fue registrada correctamente."
            "searchProduct" -> "Listo. EncontrÃ© la informaciÃ³n del producto."
            "searchCustomer" -> "Listo. EncontrÃ© la informaciÃ³n del cliente."
            "getInventory" -> "Listo. ConsultÃ© el inventario correctamente."
            else -> backendMessage.ifBlank { "Listo. La operaciÃ³n fue completada correctamente." }
        }

        return renderFinalResult(
            finalMessage = baseMessage,
            state = state
        )
    }

    private fun buildLoopContext(
        loopContext: String,
        state: AgentState
    ): String {
        return buildString {
            appendLine("State:")
            appendLine("lastCustomerId=${state.lastCustomerId ?: "none"}")
            appendLine("lastCustomerName=${state.lastCustomerName ?: "none"}")
            appendLine("lastProductId=${state.lastProductId ?: "none"}")
            appendLine("lastProductName=${state.lastProductName ?: "none"}")
            appendLine("lastSaleId=${state.lastSaleId ?: "none"}")
            appendLine("lastPurchaseId=${state.lastPurchaseId ?: "none"}")
            appendLine("lastInventoryStock=${state.lastInventoryStock ?: "none"}")

            if (loopContext.isNotBlank()) {
                appendLine()
                appendLine("Last tool result:")
                appendLine(loopContext.takeLast(1200))
            }
        }
    }

    private fun buildToolContext(
        tool: String,
        arguments: JSONObject,
        response: JSONObject,
        state: AgentState
    ): String {
        return buildString {
            appendLine("tool=$tool")
            appendLine("success=${response.optBoolean("success", false)}")
            appendLine("message=${response.optString("message", "")}")

            val data = response.opt("data")

            if (data != null) {
                appendLine("data=${data.toString().take(800)}")
            }

            appendLine("lastCustomerId=${state.lastCustomerId ?: "none"}")
            appendLine("lastProductId=${state.lastProductId ?: "none"}")
        }
    }

    private fun buildValidationErrorContext(
        tool: String,
        arguments: JSONObject,
        message: String
    ): String {
        return buildString {
            appendLine("validation_error")
            appendLine("tool=$tool")
            appendLine("arguments=${arguments.toString()}")
            appendLine("message=${escapeForJson(message)}")
        }
    }

    private fun escapeForJson(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")
    }

    private fun exampleCustomer(): String {
        return """
            {
              "type": "tool_call",
              "tool": "createCustomer",
              "arguments": {
                "firstName": "Carlos",
                "lastName": "Rojas",
                "phone": "70000000",
                "email": "carlos@email.com"
              }
            }
        """.trimIndent()
    }

    private fun exampleProduct(): String {
        return """
            {
              "type": "tool_call",
              "tool": "createProduct",
              "arguments": {
                "name": "Arroz",
                "description": "Bolsa de arroz 1kg",
                "unit": "unit",
                "salePrice": 18,
                "purchasePrice": 12
              }
            }
        """.trimIndent()
    }

    private fun examplePurchase(): String {
        return """
            {
              "type": "tool_call",
              "tool": "createPurchase",
              "arguments": {
                "productId": "PEGA_AQUI_EL_PRODUCT_ID",
                "quantity": 50,
                "unitCost": 12
              }
            }
        """.trimIndent()
    }

    private fun exampleSale(): String {
        return """
            {
              "type": "tool_call",
              "tool": "createSale",
              "arguments": {
                "customerId": "PEGA_AQUI_EL_CUSTOMER_ID",
                "items": [
                  {
                    "productId": "PEGA_AQUI_EL_PRODUCT_ID",
                    "quantity": 2
                  }
                ]
              }
            }
        """.trimIndent()
    }

    private data class CreateCustomerAndSaleCommand(
        val customerFullName: String,
        val quantity: Int,
        val productName: String
    )

    private data class CustomerRef(
        val customerId: String,
        val customerName: String
    )

    private data class ProductRef(
        val productId: String,
        val productName: String
    )
}
``

### app/src/main/java/com/brayan/erpagentlocal/ai/LocalModelService.kt

``kotlin
package com.brayan.erpagentlocal.ai

import android.content.Context
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.LogSeverity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import java.io.File

class LocalModelService {

    private var engine: Engine? = null
    private var conversation: Conversation? = null

    private var initialized: Boolean = false
    private var loadedModelPath: String? = null
    private var loadedModelName: String? = null
    private var lastError: String? = null

    private var currentSystemInstruction: String =
        PromptProvider.buildSystemPrompt()

    private val backendName: String = "CPU"

    suspend fun initialize(
        context: Context,
        modelFile: File
    ): String {
        return initialize(
            context = context,
            modelFile = modelFile,
            systemInstruction = PromptProvider.buildSystemPrompt()
        )
    }

    suspend fun initialize(
        context: Context,
        modelFile: File,
        systemInstruction: String = PromptProvider.buildSystemPrompt()
    ): String {
        return withContext(Dispatchers.IO) {
            try {
                validateModelFile(modelFile)

                close()

                currentSystemInstruction = systemInstruction
                lastError = null

                Engine.setNativeMinLogSeverity(LogSeverity.ERROR)

                val engineConfig = EngineConfig(
                    modelPath = modelFile.absolutePath,
                    backend = Backend.CPU(),
                    cacheDir = context.cacheDir.absolutePath
                )

                val newEngine = Engine(engineConfig)
                newEngine.initialize()

                val conversationConfig = ConversationConfig(
                    systemInstruction = Contents.of(systemInstruction)
                )

                val newConversation = newEngine.createConversation(conversationConfig)

                engine = newEngine
                conversation = newConversation
                initialized = true
                loadedModelPath = modelFile.absolutePath
                loadedModelName = modelFile.name

                buildString {
                    appendLine("Modelo inicializado correctamente.")
                    appendLine()
                    appendLine("Backend: $backendName")
                    appendLine()
                    appendLine("Archivo:")
                    appendLine(modelFile.absolutePath)
                    appendLine()
                    appendLine("Estado:")
                    appendLine(getModelStatus().toDebugText())
                }
            } catch (exception: Exception) {
                close()

                lastError = exception.message ?: "Error inicializando modelo."

                buildString {
                    appendLine("ERROR AL INICIALIZAR MODELO")
                    appendLine()
                    appendLine(lastError)
                }
            }
        }
    }

    suspend fun generate(prompt: String): String {
        return generateResult(prompt).getOrThrow()
    }

    suspend fun generateDecision(prompt: String): String {
        return generateResult(prompt).getOrThrow()
    }

    suspend fun generateResult(prompt: String): ModelGenerationResult {
        return withContext(Dispatchers.IO) {
            if (!initialized) {
                val message = "El modelo local todavÃ­a no estÃ¡ inicializado."
                lastError = message

                return@withContext ModelGenerationResult.failure(
                    error = message,
                    promptLength = prompt.length
                )
            }

            val activeConversation = conversation

            if (activeConversation == null) {
                val message = "No existe una conversaciÃ³n activa con el modelo."
                lastError = message

                return@withContext ModelGenerationResult.failure(
                    error = message,
                    promptLength = prompt.length
                )
            }

            try {
                val result = StringBuilder()

                activeConversation
                    .sendMessageAsync(prompt)
                    .catch { throwable ->
                        throw throwable
                    }
                    .collect { message ->
                        result.append(message.toString())
                    }

                val output = result.toString().trim()

                lastError = null

                ModelGenerationResult.success(
                    text = output,
                    promptLength = prompt.length
                )
            } catch (exception: Exception) {
                val message = exception.message ?: "Error generando respuesta del modelo."
                lastError = message

                ModelGenerationResult.failure(
                    error = message,
                    promptLength = prompt.length
                )
            }
        }
    }

    suspend fun testJsonResponse(): String {
        val prompt = """
            Respond only with valid JSON.
            Do not use markdown.
            Do not explain.

            Return exactly this structure:
            {
              "type": "final",
              "message": "model_ready"
            }
        """.trimIndent()

        return generateDecision(prompt)
    }

    fun isInitialized(): Boolean {
        return initialized
    }

    fun getLoadedModelPath(): String? {
        return loadedModelPath
    }

    fun getLoadedModelName(): String? {
        return loadedModelName
    }

    fun getSystemInstruction(): String {
        return currentSystemInstruction
    }

    fun getModelStatus(): ModelStatus {
        return ModelStatus(
            initialized = initialized,
            modelPath = loadedModelPath,
            modelName = loadedModelName,
            backend = backendName,
            lastError = lastError
        )
    }

    fun getStatus(): String {
        return getModelStatus().toDebugText()
    }

    fun close() {
        try {
            conversation?.close()
        } catch (_: Exception) {
        }

        try {
            engine?.close()
        } catch (_: Exception) {
        }

        conversation = null
        engine = null
        initialized = false
        loadedModelPath = null
        loadedModelName = null
        currentSystemInstruction = PromptProvider.buildSystemPrompt()
    }

    private fun validateModelFile(modelFile: File) {
        if (!modelFile.exists()) {
            throw IllegalStateException("No existe el modelo en: ${modelFile.absolutePath}")
        }

        if (!modelFile.isFile) {
            throw IllegalStateException("La ruta seleccionada no es un archivo vÃ¡lido: ${modelFile.absolutePath}")
        }

        if (modelFile.length() <= 0L) {
            throw IllegalStateException("El archivo del modelo estÃ¡ vacÃ­o: ${modelFile.absolutePath}")
        }

        if (!modelFile.name.endsWith(".litertlm", ignoreCase = true)) {
            throw IllegalStateException("El archivo seleccionado no parece ser un modelo .litertlm")
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/ai/ModelGenerationResult.kt

``kotlin
package com.brayan.erpagentlocal.ai

data class ModelGenerationResult(
    val success: Boolean,
    val text: String,
    val error: String? = null,
    val promptLength: Int = 0,
    val outputLength: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {

    fun getOrThrow(): String {
        if (!success) {
            throw IllegalStateException(error ?: "Error generando respuesta del modelo.")
        }

        return text
    }

    fun toDebugText(): String {
        val builder = StringBuilder()

        builder.appendLine("MODEL GENERATION RESULT")
        builder.appendLine()
        builder.appendLine("Success: $success")
        builder.appendLine("Prompt length: $promptLength")
        builder.appendLine("Output length: $outputLength")
        builder.appendLine("Created at: $createdAt")
        builder.appendLine()

        if (!error.isNullOrBlank()) {
            builder.appendLine("Error:")
            builder.appendLine(error)
            builder.appendLine()
        }

        builder.appendLine("Output:")
        builder.appendLine(text)

        return builder.toString()
    }

    companion object {

        fun success(
            text: String,
            promptLength: Int
        ): ModelGenerationResult {
            return ModelGenerationResult(
                success = true,
                text = text,
                promptLength = promptLength,
                outputLength = text.length
            )
        }

        fun failure(
            error: String,
            promptLength: Int = 0
        ): ModelGenerationResult {
            return ModelGenerationResult(
                success = false,
                text = "",
                error = error,
                promptLength = promptLength,
                outputLength = 0
            )
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/ai/ModelStatus.kt

``kotlin
package com.brayan.erpagentlocal.ai

data class ModelStatus(
    val initialized: Boolean,
    val modelPath: String?,
    val modelName: String?,
    val backend: String,
    val lastError: String? = null
) {

    fun toDebugText(): String {
        return buildString {
            appendLine("MODEL STATUS")
            appendLine()
            appendLine("Initialized: $initialized")
            appendLine("Backend: $backend")
            appendLine()

            appendLine("Model name:")
            appendLine(modelName ?: "none")
            appendLine()

            appendLine("Model path:")
            appendLine(modelPath ?: "none")
            appendLine()

            if (!lastError.isNullOrBlank()) {
                appendLine("Last error:")
                appendLine(lastError)
            }
        }
    }

    fun toUserText(): String {
        return if (initialized) {
            buildString {
                appendLine("Modelo cargado correctamente.")
                appendLine()
                appendLine("Archivo:")
                appendLine(modelName ?: "Modelo desconocido")
            }
        } else {
            buildString {
                appendLine("Modelo no inicializado.")

                if (!lastError.isNullOrBlank()) {
                    appendLine()
                    appendLine("Ãšltimo error:")
                    appendLine(lastError)
                }
            }
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/ai/PromptProvider.kt

``kotlin
package com.brayan.erpagentlocal.ai

object PromptProvider {

    fun buildSystemPrompt(): String {
        return """
            You are a local ERP assistant on Android.

            Your job:
            Convert Spanish user requests into ONE JSON action.

            Valid JSON actions:

            1. Tool call:
            {
              "type": "tool_call",
              "tool": "toolName",
              "arguments": {}
            }

            2. Ask user:
            {
              "type": "ask_user",
              "message": "Pregunta en espaÃ±ol"
            }

            3. Final:
            {
              "type": "final",
              "message": "Respuesta final en espaÃ±ol"
            }

            CRITICAL RULES:
            - Output only JSON.
            - No markdown.
            - No explanations outside JSON.
            - "type" must be only: "tool_call", "ask_user", or "final".
            - Never put the tool name inside "type".
            - Android executes the tool.
            - Never invent customerId or productId.
        """.trimIndent()
    }

    fun buildAgentDecisionPrompt(
        userMessage: String,
        memoryText: String,
        loopContext: String,
        step: Int,
        maxSteps: Int
    ): String {
        return """
            You are an ERP agent.

            Step: $step/$maxSteps

            User request:
            $userMessage

            Context:
            ${loopContext.ifBlank { "No context yet." }}

            Available tools:

            createCustomer:
            required: firstName, lastName
            optional: phone, email

            searchCustomer:
            required: name

            createProduct:
            required: name, salePrice, purchasePrice
            optional: description, unit

            searchProduct:
            required: name

            updateProduct:
            required: productId
            optional: name, description, unit, salePrice, purchasePrice

            getInventory:
            required: productId

            createPurchase:
            required: productId, quantity, unitCost

            createSale:
            required: customerId, items
            item fields: productId, quantity

            JSON FORMAT RULES:
            Return exactly one JSON object.
            The only valid type values are:
            - tool_call
            - ask_user
            - final

            Correct:
            {
              "type": "tool_call",
              "tool": "createProduct",
              "arguments": {
                "name": "zapatos",
                "salePrice": 200,
                "purchasePrice": 100
              }
            }

            Incorrect:
            {
              "type": "createProduct",
              "tool": "createProduct",
              "arguments": {}
            }

            DECISION RULES:

            If user asks to create a product:
            - Use createProduct.
            - Extract name, salePrice and purchasePrice.
            - If salePrice is missing, ask_user.
            - If purchasePrice is missing, ask_user.
            - If unit is missing, use "unit".
            - If description is missing, use "Producto creado desde agente local".

            If user asks to search a product:
            - Use searchProduct with name.

            If user asks to create a customer:
            - Use createCustomer.
            - Split full name into firstName and lastName.
            - If lastName is missing, ask_user.

            If user asks to register a purchase:
            - If only product name is given, first use searchProduct.
            - Then use createPurchase with productId, quantity and unitCost.

            If user asks to register a sale:
            - If only customer name is given, first use searchCustomer.
            - If only product name is given, first use searchProduct.
            - Then use createSale with real customerId and productId.

            Do it in steps:
1. createCustomer with firstName = "Emanuel" and lastName = "Blanco"
2. searchProduct with name = "zapatos"
3. createSale with the real customerId and productId
4. final response in Spanish

Important:
- "vendele", "vÃ©ndele", "vendelÃ©" and "venderle" mean createSale.
- Never use createPurchase when the user says sell/vendele.
- Before createProduct, searchProduct should be used to avoid duplicates.
- Before createCustomer, searchCustomer should be used to avoid duplicates.

            If the requested action is already completed:
            - Return final in Spanish.

            Now return only one JSON object.
        """.trimIndent()
    }

    fun buildJsonRepairPrompt(
        invalidOutput: String,
        error: String
    ): String {
        return """
            Fix this invalid model output.

            Error:
            $error

            Invalid output:
            $invalidOutput

            Return only one valid JSON object.

            Valid formats:

            Tool call:
            {
              "type": "tool_call",
              "tool": "toolName",
              "arguments": {}
            }

            Ask user:
            {
              "type": "ask_user",
              "message": "Pregunta en espaÃ±ol"
            }

            Final:
            {
              "type": "final",
              "message": "Respuesta final en espaÃ±ol"
            }

            Rules:
            - Only JSON.
            - No markdown.
            - "type" must be "tool_call", "ask_user", or "final".
            - Tool name must go in "tool", never in "type".
        """.trimIndent()
    }

    fun buildFinalSummaryPrompt(
        userMessage: String,
        memoryText: String,
        loopContext: String
    ): String {
        return """
            Write a final Spanish response.

            User request:
            $userMessage

            Context:
            ${loopContext.ifBlank { "No context." }}

            Return only:
            {
              "type": "final",
              "message": "Respuesta final breve en espaÃ±ol"
            }

            Do not include technical JSON in the message.
        """.trimIndent()
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/data/AgentApiClient.kt

``kotlin
package com.brayan.erpagentlocal.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class AgentApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(ApiConfig.DEFAULT_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(ApiConfig.DEFAULT_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(ApiConfig.DEFAULT_WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun request(
        method: HttpMethod,
        path: String,
        body: JSONObject? = null
    ): JSONObject {
        return when (method) {
            HttpMethod.GET -> get(path)
            HttpMethod.POST -> post(path, body ?: JSONObject())
            HttpMethod.PATCH -> patch(path, body ?: JSONObject())
            HttpMethod.DELETE -> delete(path)
        }
    }

    suspend fun get(path: String): JSONObject {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(ApiConfig.buildUrl(path))
                .get()
                .build()

            executeRequest(request)
        }
    }

    suspend fun post(path: String, body: JSONObject): JSONObject {
        return withContext(Dispatchers.IO) {
            val requestBody = body.toString().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(ApiConfig.buildUrl(path))
                .post(requestBody)
                .build()

            executeRequest(request)
        }
    }

    suspend fun patch(path: String, body: JSONObject): JSONObject {
        return withContext(Dispatchers.IO) {
            val requestBody = body.toString().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(ApiConfig.buildUrl(path))
                .patch(requestBody)
                .build()

            executeRequest(request)
        }
    }

    suspend fun delete(path: String): JSONObject {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(ApiConfig.buildUrl(path))
                .delete()
                .build()

            executeRequest(request)
        }
    }

    private fun executeRequest(request: Request): JSONObject {
        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                val parsedBody = parseResponseBody(responseBody)
                val statusCode = response.code

                if (!response.isSuccessful) {
                    return normalizeErrorResponse(
                        statusCode = statusCode,
                        parsedBody = parsedBody,
                        rawBody = responseBody
                    )
                }

                normalizeSuccessResponse(
                    statusCode = statusCode,
                    parsedBody = parsedBody
                )
            }
        } catch (exception: UnknownHostException) {
            networkError("No se pudo resolver el host del backend. Verifica tu internet o la URL del API Gateway.", exception)
        } catch (exception: SocketTimeoutException) {
            networkError("La conexiÃ³n con el backend tardÃ³ demasiado.", exception)
        } catch (exception: IOException) {
            networkError("No se pudo conectar con el backend.", exception)
        } catch (exception: Exception) {
            JSONObject()
                .put("success", false)
                .put("message", exception.message ?: "Error inesperado en la llamada HTTP.")
                .put("statusCode", 0)
                .put("data", JSONObject())
                .put("error", exception.message ?: "Unexpected error")
        }
    }

    private fun normalizeSuccessResponse(
        statusCode: Int,
        parsedBody: JSONObject
    ): JSONObject {
        if (parsedBody.length() == 0) {
            return JSONObject()
                .put("success", true)
                .put("message", "Empty response")
                .put("statusCode", statusCode)
                .put("data", JSONObject())
        }

        if (!parsedBody.has("success")) {
            parsedBody.put("success", true)
        }

        if (!parsedBody.has("message")) {
            parsedBody.put("message", "Operation completed")
        }

        if (!parsedBody.has("data")) {
            parsedBody.put("data", JSONObject())
        }

        parsedBody.put("statusCode", statusCode)

        return parsedBody
    }

    private fun normalizeErrorResponse(
        statusCode: Int,
        parsedBody: JSONObject,
        rawBody: String
    ): JSONObject {
        val message = extractErrorMessage(parsedBody, statusCode)

        return JSONObject()
            .put("success", false)
            .put("message", message)
            .put("statusCode", statusCode)
            .put("data", parsedBody.opt("data") ?: JSONObject())
            .put("error", rawBody.ifBlank { message })
    }

    private fun networkError(
        message: String,
        exception: Exception
    ): JSONObject {
        return JSONObject()
            .put("success", false)
            .put("message", message)
            .put("statusCode", 0)
            .put("data", JSONObject())
            .put("error", exception.message ?: message)
    }

    private fun parseResponseBody(responseBody: String): JSONObject {
        if (responseBody.isBlank()) {
            return JSONObject()
        }

        return try {
            JSONObject(responseBody)
        } catch (_: Exception) {
            JSONObject()
                .put("success", false)
                .put("message", "El backend devolviÃ³ una respuesta no JSON.")
                .put("rawBody", responseBody)
        }
    }

    private fun extractErrorMessage(
        body: JSONObject,
        statusCode: Int
    ): String {
        val message = body.optString("message", "")

        if (message.isNotBlank()) {
            return message
        }

        return when (statusCode) {
            400 -> "Solicitud invÃ¡lida."
            401 -> "No autorizado."
            403 -> "Acceso denegado."
            404 -> "Recurso no encontrado."
            409 -> "Conflicto de negocio."
            500 -> "Error interno del servidor."
            else -> "Error HTTP $statusCode."
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/data/ApiConfig.kt

``kotlin
package com.brayan.erpagentlocal.data

object ApiConfig {

    const val BASE_URL = "https://qnphwupj51.execute-api.us-east-1.amazonaws.com/Prod"

    const val CUSTOMERS = "/customers"
    const val PRODUCTS = "/products"
    const val PURCHASES = "/purchases"
    const val SALES = "/sales"
    const val INVENTORY = "/inventory"
    const val HEALTH = "/health"

    const val DEFAULT_CONNECT_TIMEOUT_SECONDS = 20L
    const val DEFAULT_READ_TIMEOUT_SECONDS = 30L
    const val DEFAULT_WRITE_TIMEOUT_SECONDS = 30L

    fun buildUrl(path: String): String {
        val cleanBaseUrl = BASE_URL.trimEnd('/')
        val cleanPath = path.trimStart('/')

        return "$cleanBaseUrl/$cleanPath"
    }

    fun getProductionBaseUrl(): String {
        return BASE_URL
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/data/HttpMethod.kt

``kotlin
package com.brayan.erpagentlocal.data

enum class HttpMethod {
    GET,
    POST,
    PATCH,
    DELETE;

    companion object {
        fun fromValue(value: String): HttpMethod {
            return entries.firstOrNull { method ->
                method.name.equals(value.trim(), ignoreCase = true)
            } ?: GET
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/data/ToolExecutor.kt

``kotlin
package com.brayan.erpagentlocal.data

import com.brayan.erpagentlocal.agent.AgentAction
import com.brayan.erpagentlocal.agent.AgentErrorMapper
import com.brayan.erpagentlocal.agent.ToolDefinition
import com.brayan.erpagentlocal.agent.ToolExecutionResult
import com.brayan.erpagentlocal.agent.ToolRegistry
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ToolExecutor(
    private val apiClient: AgentApiClient = AgentApiClient()
) {

    suspend fun execute(action: AgentAction.ToolCall): String {
        val response = executeRaw(action)
        return formatJson("TOOL EJECUTADA: ${action.tool}", response)
    }

    suspend fun executeRaw(action: AgentAction.ToolCall): JSONObject {
        return executeToolCall(action).toJsonObject()
    }

    suspend fun executeToolCall(action: AgentAction.ToolCall): ToolExecutionResult {
        val toolDefinition = ToolRegistry.find(action.tool)
            ?: return ToolExecutionResult.failure(
                toolName = action.tool,
                message = "Tool no registrada: ${action.tool}",
                error = "Tool not found in ToolRegistry"
            )

        return executeByDefinition(
            toolDefinition = toolDefinition,
            arguments = action.arguments
        )
    }

    suspend fun executeByDefinition(
        toolDefinition: ToolDefinition,
        arguments: JSONObject
    ): ToolExecutionResult {
        return try {
            val method = HttpMethod.fromValue(toolDefinition.method)

            val path = buildPath(
                pathTemplate = toolDefinition.path,
                arguments = arguments
            )

            val body = when (method) {
                HttpMethod.POST,
                HttpMethod.PATCH -> buildBody(
                    toolDefinition = toolDefinition,
                    arguments = arguments
                )

                HttpMethod.GET,
                HttpMethod.DELETE -> null
            }

            val response = apiClient.request(
                method = method,
                path = path,
                body = body
            )

            val success = response.optBoolean("success", false)

            val message = response.optString(
                "message",
                if (success) {
                    "Tool executed successfully"
                } else {
                    "Tool execution failed"
                }
            )

            val statusCode = response
                .optInt("statusCode", 0)
                .takeIf { it > 0 }

            if (success) {
                ToolExecutionResult.success(
                    toolName = toolDefinition.name,
                    message = message,
                    rawResponse = response,
                    statusCode = statusCode
                )
            } else {
                val mappedError = AgentErrorMapper.fromHttpResponse(
                    response = response,
                    toolName = toolDefinition.name
                )

                response.put("userMessage", mappedError.userMessage)

                ToolExecutionResult.failure(
                    toolName = toolDefinition.name,
                    message = mappedError.userMessage,
                    rawResponse = response,
                    statusCode = statusCode,
                    error = mappedError.technicalMessage
                )
            }
        } catch (exception: Exception) {
            val mappedError = AgentErrorMapper.fromException(exception)

            val errorResponse = JSONObject()
                .put("success", false)
                .put("message", mappedError.userMessage)
                .put("statusCode", 0)
                .put("data", JSONObject())
                .put("error", mappedError.technicalMessage)

            ToolExecutionResult.failure(
                toolName = toolDefinition.name,
                message = mappedError.userMessage,
                rawResponse = errorResponse,
                statusCode = 0,
                error = mappedError.technicalMessage
            )
        }
    }

    suspend fun checkHealth(): String {
        val response = apiClient.get(ApiConfig.HEALTH)
        return formatJson("HEALTH", response)
    }

    suspend fun runFullErpDemo(): String {
        val code = generateCode()

        val customerResponse = executeRaw(
            AgentAction.ToolCall(
                tool = "createCustomer",
                arguments = JSONObject()
                    .put("firstName", "Juan")
                    .put("lastName", "Perez $code")
                    .put("phone", "70000000")
                    .put("email", "juan$code@email.com")
            )
        )

        val customerId = customerResponse
            .optJSONObject("data")
            ?.optString("customerId")
            .orEmpty()

        if (customerId.isBlank()) {
            return buildString {
                appendLine("DEMO ERP DETENIDA")
                appendLine()
                appendLine("No se pudo crear el cliente.")
                appendLine(customerResponse.toString(2))
            }
        }

        val productResponse = executeRaw(
            AgentAction.ToolCall(
                tool = "createProduct",
                arguments = JSONObject()
                    .put("name", "Azucar Demo $code")
                    .put("description", "Bolsa de azucar 1kg")
                    .put("unit", "unit")
                    .put("salePrice", 20)
                    .put("purchasePrice", 15)
            )
        )

        val productId = productResponse
            .optJSONObject("data")
            ?.optString("productId")
            .orEmpty()

        if (productId.isBlank()) {
            return buildString {
                appendLine("DEMO ERP DETENIDA")
                appendLine()
                appendLine("Cliente creado: $customerId")
                appendLine("No se pudo crear el producto.")
                appendLine(productResponse.toString(2))
            }
        }

        val purchaseResponse = executeRaw(
            AgentAction.ToolCall(
                tool = "createPurchase",
                arguments = JSONObject()
                    .put("productId", productId)
                    .put("quantity", 50)
                    .put("unitCost", 15)
            )
        )

        val inventoryAfterPurchase = executeRaw(
            AgentAction.ToolCall(
                tool = "getInventory",
                arguments = JSONObject()
                    .put("productId", productId)
            )
        )

        val saleItems = JSONArray()
            .put(
                JSONObject()
                    .put("productId", productId)
                    .put("quantity", 2)
            )

        val saleResponse = executeRaw(
            AgentAction.ToolCall(
                tool = "createSale",
                arguments = JSONObject()
                    .put("customerId", customerId)
                    .put("items", saleItems)
            )
        )

        val inventoryAfterSale = executeRaw(
            AgentAction.ToolCall(
                tool = "getInventory",
                arguments = JSONObject()
                    .put("productId", productId)
            )
        )

        return buildString {
            appendLine("DEMO ERP COMPLETADA")
            appendLine()
            appendLine("1. Cliente creado:")
            appendLine("customerId: $customerId")
            appendLine()
            appendLine("2. Producto creado:")
            appendLine("productId: $productId")
            appendLine()
            appendLine("3. Compra registrada:")
            appendLine(purchaseResponse.toString(2))
            appendLine()
            appendLine("4. Inventario despuÃ©s de la compra:")
            appendLine(inventoryAfterPurchase.toString(2))
            appendLine()
            appendLine("5. Venta registrada:")
            appendLine(saleResponse.toString(2))
            appendLine()
            appendLine("6. Inventario despuÃ©s de la venta:")
            appendLine(inventoryAfterSale.toString(2))
        }
    }

    private fun buildPath(
        pathTemplate: String,
        arguments: JSONObject
    ): String {
        var path = pathTemplate

        val placeholderRegex = Regex("\\{([^}]+)\\}")
        val matches = placeholderRegex.findAll(pathTemplate).toList()

        matches.forEach { match ->
            val fieldName = match.groupValues[1]

            val rawValue = arguments.opt(fieldName)
                ?: throw IllegalArgumentException("Falta argumento para path/query: $fieldName")

            val encodedValue = encode(rawValue.toString())
            path = path.replace("{$fieldName}", encodedValue)
        }

        return path
    }

    private fun buildBody(
        toolDefinition: ToolDefinition,
        arguments: JSONObject
    ): JSONObject {
        val body = JSONObject()
        val pathArgumentNames = extractPlaceholders(toolDefinition.path)

        val keys = arguments.keys()

        while (keys.hasNext()) {
            val key = keys.next()

            if (!pathArgumentNames.contains(key)) {
                body.put(key, arguments.get(key))
            }
        }

        return body
    }

    private fun extractPlaceholders(pathTemplate: String): Set<String> {
        val placeholderRegex = Regex("\\{([^}]+)\\}")

        return placeholderRegex
            .findAll(pathTemplate)
            .map { match -> match.groupValues[1] }
            .toSet()
    }

    private fun encode(value: String): String {
        return URLEncoder.encode(value, "UTF-8")
            .replace("+", "%20")
    }

    private fun generateCode(): String {
        return SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
    }

    private fun formatJson(title: String, json: JSONObject): String {
        return buildString {
            appendLine(title)
            appendLine()
            appendLine(json.toString(2))
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/MainActivity.kt

``kotlin
package com.brayan.erpagentlocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.brayan.erpagentlocal.ui.screens.ChatScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen()
                }
            }
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/model/ApiResponse.kt

``kotlin
package com.brayan.erpagentlocal.model

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)
``

### app/src/main/java/com/brayan/erpagentlocal/model/CustomerDtos.kt

``kotlin
package com.brayan.erpagentlocal.model

data class CustomerRequest(
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
    val email: String? = null
)

data class CustomerDto(
    val customerId: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val email: String?,
    val isActive: Boolean
)
``

### app/src/main/java/com/brayan/erpagentlocal/model/InventoryDtos.kt

``kotlin
package com.brayan.erpagentlocal.model

data class InventoryDto(
    val productId: String,
    val stock: Int,
    val minStock: Int,
    val updatedAt: String?
)
``

### app/src/main/java/com/brayan/erpagentlocal/model/ProductDtos.kt

``kotlin
package com.brayan.erpagentlocal.model

data class ProductRequest(
    val name: String,
    val description: String? = null,
    val unit: String = "unit",
    val salePrice: Double,
    val purchasePrice: Double
)

data class ProductDto(
    val productId: String,
    val name: String,
    val description: String?,
    val unit: String,
    val salePrice: Double,
    val purchasePrice: Double,
    val isActive: Boolean
)
``

### app/src/main/java/com/brayan/erpagentlocal/model/PurchaseDtos.kt

``kotlin
package com.brayan.erpagentlocal.model

data class PurchaseRequest(
    val productId: String,
    val quantity: Int,
    val unitCost: Double
)
``

### app/src/main/java/com/brayan/erpagentlocal/model/SaleDtos.kt

``kotlin
package com.brayan.erpagentlocal.model

data class SaleItemRequest(
    val productId: String,
    val quantity: Int
)

data class SaleRequest(
    val customerId: String,
    val items: List<SaleItemRequest>
)
``

### app/src/main/java/com/brayan/erpagentlocal/ui/components/MessageBubble.kt

``kotlin
package com.brayan.erpagentlocal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brayan.erpagentlocal.ui.theme.ErpColors

data class ChatMessageUi(
    val role: String,
    val content: String
)

@Composable
fun MessageBubble(
    message: ChatMessageUi,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == "user"
    val isSystem = message.role == "system"

    val backgroundColor = when {
        isUser -> ErpColors.UserBubble
        isSystem -> ErpColors.ToolBubble
        else -> ErpColors.AssistantBubble
    }

    val horizontalAlignment = if (isUser) {
        Alignment.End
    } else {
        Alignment.Start
    }

    val label = when {
        isUser -> "TÃº"
        isSystem -> "Sistema"
        else -> "Agente"
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ErpColors.TextMuted,
            modifier = Modifier.padding(
                start = 4.dp,
                end = 4.dp,
                bottom = 4.dp
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.88f else 0.94f)
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    )
                )
                .padding(14.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = ErpColors.TextPrimary
            )
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/ui/components/StatusCard.kt

``kotlin
package com.brayan.erpagentlocal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brayan.erpagentlocal.ui.theme.ErpColors

@Composable
fun StatusCard(
    modelReady: Boolean,
    backendStatus: String,
    toolsCount: Int,
    modelName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ErpColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "ERP Agent Local",
                        style = MaterialTheme.typography.titleMedium,
                        color = ErpColors.TextPrimary
                    )

                    Text(
                        text = "Agente reactivo conectado al ERP serverless",
                        style = MaterialTheme.typography.bodySmall,
                        color = ErpColors.TextSecondary
                    )
                }

                StatusPill(
                    text = if (modelReady) "Modelo listo" else "Modelo no cargado",
                    positive = modelReady
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoBox(
                    title = "Backend",
                    value = backendStatus.ifBlank { "Sin verificar" },
                    modifier = Modifier.weight(1f)
                )

                InfoBox(
                    title = "Tools",
                    value = "$toolsCount",
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = if (modelName.isBlank()) {
                    "Modelo: todavÃ­a no seleccionado"
                } else {
                    "Modelo: $modelName"
                },
                style = MaterialTheme.typography.bodySmall,
                color = ErpColors.TextMuted
            )
        }
    }
}

@Composable
private fun StatusPill(
    text: String,
    positive: Boolean
) {
    val background = if (positive) {
        ErpColors.SuccessSoft
    } else {
        ErpColors.WarningSoft
    }

    val foreground = if (positive) {
        ErpColors.Success
    } else {
        ErpColors.Warning
    }

    Row(
        modifier = Modifier
            .background(
                color = background,
                shape = CircleShape
            )
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = foreground,
                    shape = CircleShape
                )
                .padding(4.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = foreground
        )
    }
}

@Composable
private fun InfoBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = ErpColors.SurfaceSoft,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = ErpColors.TextMuted
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = ErpColors.TextPrimary
        )
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/ui/components/TraceViewer.kt

``kotlin
package com.brayan.erpagentlocal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brayan.erpagentlocal.ui.theme.ErpColors

@Composable
fun TraceViewer(
    traceText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = ErpColors.SurfaceSoft,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Trazabilidad del agente",
            style = MaterialTheme.typography.titleSmall,
            color = ErpColors.TextPrimary
        )

        Text(
            text = "Observe â†’ Decide â†’ Validate â†’ Act â†’ Store â†’ Continue / Finish",
            style = MaterialTheme.typography.bodySmall,
            color = ErpColors.TextMuted
        )

        if (traceText.isBlank()) {
            Text(
                text = "TodavÃ­a no hay trazabilidad disponible. Ejecuta un caso de prueba para ver los pasos del agente.",
                style = MaterialTheme.typography.bodySmall,
                color = ErpColors.TextMuted
            )
        } else {
            TraceTextBlock(traceText = traceText)
        }
    }
}

@Composable
private fun TraceTextBlock(
    traceText: String
) {
    val highlightedText = traceText
        .replace("TRAZABILIDAD PARA DEFENSA", "ðŸ“Œ TRAZABILIDAD PARA DEFENSA")
        .replace("Paso ", "â–¶ Paso ")
        .replace("Observe", "Observe")
        .replace("Decide", "Decide")
        .replace("Validate", "Validate")
        .replace("Act", "Act")
        .replace("Store", "Store")
        .replace("Finish", "Finish")

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        text = highlightedText,
        style = MaterialTheme.typography.bodySmall,
        color = ErpColors.TextSecondary
    )
}
``

### app/src/main/java/com/brayan/erpagentlocal/ui/screens/ChatScreen.kt

``kotlin
package com.brayan.erpagentlocal.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.brayan.erpagentlocal.agent.ToolCatalogLoader
import com.brayan.erpagentlocal.agent.ToolRegistry
import com.brayan.erpagentlocal.ai.AgentService
import com.brayan.erpagentlocal.ai.LocalModelService
import com.brayan.erpagentlocal.ui.components.ChatMessageUi
import com.brayan.erpagentlocal.ui.components.MessageBubble
import com.brayan.erpagentlocal.ui.components.StatusCard
import com.brayan.erpagentlocal.ui.theme.ErpColors
import com.brayan.erpagentlocal.util.ModelFileManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val loadedCatalog = remember {
        val catalog = ToolCatalogLoader.loadFromAssets(context)
        ToolRegistry.setCatalog(catalog)
        catalog
    }

    val agentService = remember { AgentService() }
    val localModelService = remember { LocalModelService() }

    val messages = remember {
        mutableStateListOf(
            ChatMessageUi(
                role = "assistant",
                content = """
                    Hola. Soy tu agente ERP local.

                    Puedo ayudarte a trabajar con clientes, productos, compras, ventas e inventario usando lenguaje natural.

                    Primero selecciona e inicializa el modelo. Luego puedes escribir algo como:
                    â€œCrea un cliente llamado Ana LÃ³pezâ€
                """.trimIndent()
            )
        )
    }

    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var backendStatus by remember { mutableStateOf("Sin verificar") }
    var modelName by remember { mutableStateOf(localModelService.getLoadedModelName().orEmpty()) }
    var modelReady by remember { mutableStateOf(localModelService.isInitialized()) }

    fun refreshModelStatus() {
        modelReady = localModelService.isInitialized()
        modelName = localModelService.getLoadedModelName().orEmpty()
    }

    fun addMessage(role: String, content: String) {
        messages.add(
            ChatMessageUi(
                role = role,
                content = content
            )
        )
    }

    fun runAction(
    userText: String,
    showUserMessage: Boolean = true,
    action: suspend () -> String
) {
    scope.launch {
        try {
            loading = true

            if (showUserMessage) {
                addMessage("user", userText)
            }

            val result = action()

            addMessage(
                role = "assistant",
                content = result.ifBlank {
                    "No recibÃ­ una respuesta vÃ¡lida."
                }
            )

            refreshModelStatus()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            addMessage(
                role = "assistant",
                content = """
                    OcurriÃ³ un error, pero la app pudo recuperarse.

                    Detalle:
                    ${throwable.message ?: throwable::class.java.simpleName}
                """.trimIndent()
            )
        } finally {
            loading = false
        }
    }
}

    fun sendMessage(messageToSend: String) {
        val cleanMessage = messageToSend.trim()

        if (cleanMessage.isBlank()) {
            return
        }

        input = ""

        runAction(cleanMessage) {
            when {
                cleanMessage.startsWith("/") -> {
                    agentService.processUserMessage(cleanMessage)
                }

                cleanMessage.startsWith("{") -> {
                    agentService.processUserMessage(cleanMessage)
                }

                localModelService.isInitialized() -> {
                    agentService.processNaturalLanguageWithModel(
                        userMessage = cleanMessage,
                        localModelService = localModelService,
                        maxSteps = 8
                    )
                }

                else -> {
                    """
                    El modelo local no estÃ¡ inicializado.

                    Pasos:
                    1. Presiona â€œModeloâ€.
                    2. Selecciona tu archivo .litertlm.
                    3. Presiona â€œInicializarâ€.
                    4. Vuelve a enviar tu instrucciÃ³n.
                    """.trimIndent()
                }
            }
        }
    }

    fun clearChatAndAgent() {
        messages.clear()
        messages.add(
            ChatMessageUi(
                role = "assistant",
                content = "Chat limpiado. Puedes escribir una nueva instrucciÃ³n."
            )
        )

        input = ""
        backendStatus = "Sin verificar"

        scope.launch {
            try {
                agentService.processUserMessage("/clear")
            } catch (_: Exception) {
            }
        }
    }

    val modelPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            addMessage("assistant", "No se seleccionÃ³ ningÃºn modelo.")
            return@rememberLauncherForActivityResult
        }

        scope.launch {
            try {
                loading = true
                addMessage("user", "Seleccionar modelo")

                val file = ModelFileManager.copyModelToInternalStorage(
                    context = context,
                    uri = uri
                )

                refreshModelStatus()

                addMessage(
                    role = "assistant",
                    content = buildString {
                        appendLine("Modelo copiado correctamente.")
                        appendLine()
                        appendLine("Archivo:")
                        appendLine(file.absolutePath)
                        appendLine()
                        appendLine("Ahora presiona â€œInicializarâ€.")
                    }
                )
            } catch (exception: Exception) {
                addMessage(
                    role = "assistant",
                    content = "ERROR AL COPIAR MODELO:\n${exception.message}"
                )
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            localModelService.close()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ERP Agent Local",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ErpColors.Surface,
                    titleContentColor = ErpColors.TextPrimary
                )
            )
        },
        containerColor = ErpColors.SurfaceSoft
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
                .imePadding()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            StatusCard(
                modelReady = modelReady,
                backendStatus = backendStatus,
                toolsCount = loadedCatalog.count(),
                modelName = modelName
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ErpColors.Surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Acciones principales",
                        style = MaterialTheme.typography.titleSmall,
                        color = ErpColors.TextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            enabled = !loading,
                            onClick = {
                                modelPickerLauncher.launch(
                                    arrayOf(
                                        "application/octet-stream",
                                        "*/*"
                                    )
                                )
                            }
                        ) {
                            Text("Modelo")
                        }

                        Button(
                            modifier = Modifier.weight(1f),
                            enabled = !loading,
                            onClick = {
                                runAction("Inicializar modelo") {
                                    val modelFile = ModelFileManager.getDefaultModelFile(context)

                                    localModelService.initialize(
                                        context = context,
                                        modelFile = modelFile
                                    )
                                }
                            }
                        ) {
                            Text("Inicializar")
                        }

                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            enabled = !loading,
                            onClick = {
                                clearChatAndAgent()
                            }
                        ) {
                            Text("Limpiar")
                        }
                    }
                }
            }

            if (loading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ErpColors.Surface,
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = input,
                        onValueChange = { input = it },
                        label = {
                            Text("Escribe una instrucciÃ³n para el agente")
                        },
                        placeholder = {
                            Text("Ej: VÃ©ndele 2 unidades de CafÃ© a Ana LÃ³pez")
                        },
                        minLines = 2,
                        maxLines = 5,
                        enabled = !loading
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            enabled = !loading && input.isNotBlank(),
                            onClick = {
                                input = ""
                            }
                        ) {
                            Text("Borrar")
                        }

                        Button(
                            modifier = Modifier.weight(2f),
                            enabled = !loading && input.isNotBlank(),
                            onClick = {
                                sendMessage(input)
                            }
                        ) {
                            Text("Enviar")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/ui/theme/ErpColors.kt

``kotlin
package com.brayan.erpagentlocal.ui.theme

import androidx.compose.ui.graphics.Color

object ErpColors {
    val Primary = Color(0xFF2563EB)
    val PrimaryDark = Color(0xFF1E40AF)
    val PrimarySoft = Color(0xFFEFF6FF)

    val Success = Color(0xFF16A34A)
    val SuccessSoft = Color(0xFFDCFCE7)

    val Warning = Color(0xFFF59E0B)
    val WarningSoft = Color(0xFFFEF3C7)

    val Error = Color(0xFFDC2626)
    val ErrorSoft = Color(0xFFFEE2E2)

    val Surface = Color(0xFFFFFFFF)
    val SurfaceSoft = Color(0xFFF8FAFC)
    val Border = Color(0xFFE2E8F0)

    val TextPrimary = Color(0xFF0F172A)
    val TextSecondary = Color(0xFF475569)
    val TextMuted = Color(0xFF64748B)

    val UserBubble = Color(0xFFDBEAFE)
    val AssistantBubble = Color(0xFFF1F5F9)
    val ToolBubble = Color(0xFFECFDF5)
}
``

### app/src/main/java/com/brayan/erpagentlocal/util/JsonUtils.kt

``kotlin
package com.brayan.erpagentlocal.util

import org.json.JSONArray
import org.json.JSONObject

object JsonUtils {

    fun extractJsonObject(rawText: String): String {
        val trimmed = rawText.trim()

        val fencedRegex = Regex(
            pattern = "```(?:json)?\\s*([\\s\\S]*?)\\s*```",
            option = RegexOption.IGNORE_CASE
        )

        val fencedMatch = fencedRegex.find(trimmed)

        if (fencedMatch != null) {
            return fencedMatch.groupValues[1].trim()
        }

        val firstBrace = trimmed.indexOf('{')
        val lastBrace = trimmed.lastIndexOf('}')

        if (firstBrace >= 0 && lastBrace > firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1).trim()
        }

        return trimmed
    }

    fun isValidJsonObject(rawText: String): Boolean {
        return try {
            JSONObject(extractJsonObject(rawText))
            true
        } catch (_: Exception) {
            false
        }
    }

    fun toPrettyJson(rawText: String): String {
        return try {
            val json = JSONObject(extractJsonObject(rawText))
            json.toString(2)
        } catch (_: Exception) {
            rawText
        }
    }

    fun toPrettyJson(jsonObject: JSONObject): String {
        return jsonObject.toString(2)
    }

    fun toPrettyJson(jsonArray: JSONArray): String {
        return jsonArray.toString(2)
    }

    fun safeGetString(
        jsonObject: JSONObject?,
        key: String,
        defaultValue: String = ""
    ): String {
        if (jsonObject == null) return defaultValue
        return jsonObject.optString(key, defaultValue)
    }

    fun safeGetObject(
        jsonObject: JSONObject?,
        key: String
    ): JSONObject? {
        if (jsonObject == null) return null
        return jsonObject.optJSONObject(key)
    }

    fun safeGetArray(
        jsonObject: JSONObject?,
        key: String
    ): JSONArray? {
        if (jsonObject == null) return null
        return jsonObject.optJSONArray(key)
    }
}
``

### app/src/main/java/com/brayan/erpagentlocal/util/ModelFileManager.kt

``kotlin
package com.brayan.erpagentlocal.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ModelFileManager {

    private const val MODELS_DIR_NAME = "models"
    private const val DEFAULT_MODEL_NAME = "gemma-4-E2B-it.litertlm"

    suspend fun copyModelToInternalStorage(
        context: Context,
        uri: Uri
    ): File {
        return withContext(Dispatchers.IO) {
            val modelsDir = File(context.filesDir, MODELS_DIR_NAME)

            if (!modelsDir.exists()) {
                modelsDir.mkdirs()
            }

            val originalName = getFileNameFromUri(context, uri)
                ?: DEFAULT_MODEL_NAME

            val safeName = if (originalName.endsWith(".litertlm", ignoreCase = true)) {
                originalName
            } else {
                DEFAULT_MODEL_NAME
            }

            val destinationFile = File(modelsDir, safeName)

            context.contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input) {
                    "No se pudo abrir el archivo seleccionado."
                }

                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            destinationFile
        }
    }

    fun getModelsDir(context: Context): File {
        return File(context.filesDir, MODELS_DIR_NAME)
    }

    fun getDefaultModelFile(context: Context): File {
        val modelsDir = getModelsDir(context)

        if (!modelsDir.exists()) {
            modelsDir.mkdirs()
        }

        val existingModels = modelsDir
            .listFiles()
            ?.filter { it.isFile && it.name.endsWith(".litertlm", ignoreCase = true) }
            .orEmpty()

        return existingModels.firstOrNull()
            ?: File(modelsDir, DEFAULT_MODEL_NAME)
    }

    fun modelExists(context: Context): Boolean {
        val file = getDefaultModelFile(context)
        return file.exists() && file.length() > 0
    }

    fun getModelInfo(context: Context): String {
        val file = getDefaultModelFile(context)

        if (!file.exists()) {
            return "No hay modelo copiado todavÃ­a."
        }

        val sizeMb = file.length().toDouble() / (1024.0 * 1024.0)

        return buildString {
            appendLine("Modelo encontrado:")
            appendLine(file.absolutePath)
            appendLine("TamaÃ±o aproximado: ${"%.2f".format(sizeMb)} MB")
        }
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null

        return try {
            cursor = context.contentResolver.query(uri, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                if (nameIndex >= 0) {
                    cursor.getString(nameIndex)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        } finally {
            cursor?.close()
        }
    }
}
``

### app/src/main/res/drawable/ic_launcher_background.xml

``xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#3DDC84"
        android:pathData="M0,0h108v108h-108z" />
    <path
        android:fillColor="#00000000"
        android:pathData="M9,0L9,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,0L19,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M29,0L29,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M39,0L39,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M49,0L49,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M59,0L59,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M69,0L69,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M79,0L79,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M89,0L89,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M99,0L99,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,9L108,9"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,19L108,19"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,29L108,29"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,39L108,39"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,49L108,49"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,59L108,59"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,69L108,69"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,79L108,79"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,89L108,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,99L108,99"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,29L89,29"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,39L89,39"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,49L89,49"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,59L89,59"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,69L89,69"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,79L89,79"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M29,19L29,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M39,19L39,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M49,19L49,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M59,19L59,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M69,19L69,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M79,19L79,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
</vector>
``

### app/src/main/res/drawable/ic_launcher_foreground.xml

``xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:aapt="http://schemas.android.com/aapt"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path android:pathData="M31,63.928c0,0 6.4,-11 12.1,-13.1c7.2,-2.6 26,-1.4 26,-1.4l38.1,38.1L107,108.928l-32,-1L31,63.928z">
        <aapt:attr name="android:fillColor">
            <gradient
                android:endX="85.84757"
                android:endY="92.4963"
                android:startX="42.9492"
                android:startY="49.59793"
                android:type="linear">
                <item
                    android:color="#44000000"
                    android:offset="0.0" />
                <item
                    android:color="#00000000"
                    android:offset="1.0" />
            </gradient>
        </aapt:attr>
    </path>
    <path
        android:fillColor="#FFFFFF"
        android:fillType="nonZero"
        android:pathData="M65.3,45.828l3.8,-6.6c0.2,-0.4 0.1,-0.9 -0.3,-1.1c-0.4,-0.2 -0.9,-0.1 -1.1,0.3l-3.9,6.7c-6.3,-2.8 -13.4,-2.8 -19.7,0l-3.9,-6.7c-0.2,-0.4 -0.7,-0.5 -1.1,-0.3C38.8,38.328 38.7,38.828 38.9,39.228l3.8,6.6C36.2,49.428 31.7,56.028 31,63.928h46C76.3,56.028 71.8,49.428 65.3,45.828zM43.4,57.328c-0.8,0 -1.5,-0.5 -1.8,-1.2c-0.3,-0.7 -0.1,-1.5 0.4,-2.1c0.5,-0.5 1.4,-0.7 2.1,-0.4c0.7,0.3 1.2,1 1.2,1.8C45.3,56.528 44.5,57.328 43.4,57.328L43.4,57.328zM64.6,57.328c-0.8,0 -1.5,-0.5 -1.8,-1.2s-0.1,-1.5 0.4,-2.1c0.5,-0.5 1.4,-0.7 2.1,-0.4c0.7,0.3 1.2,1 1.2,1.8C66.5,56.528 65.6,57.328 64.6,57.328L64.6,57.328z"
        android:strokeWidth="1"
        android:strokeColor="#00000000" />
</vector>
``

### app/src/main/res/mipmap-anydpi/ic_launcher.xml

``xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
``

### app/src/main/res/mipmap-anydpi/ic_launcher_round.xml

``xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
``

### app/src/main/res/values/colors.xml

``xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
``

### app/src/main/res/values/strings.xml

``xml
<resources>
    <string name="app_name">ERPAgentLocal</string>
</resources>
``

### app/src/main/res/values/themes.xml

``xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <style name="Theme.ERPAgentLocal" parent="@android:style/Theme.Material.Light.NoActionBar">
        <item name="android:windowNoTitle">true</item>
        <item name="android:windowActionBar">false</item>
        <item name="android:windowLightStatusBar">true</item>
        <item name="android:statusBarColor">@android:color/white</item>
        <item name="android:navigationBarColor">@android:color/white</item>
        <item name="android:windowBackground">@android:color/white</item>
    </style>

</resources>
``

### app/src/main/res/values-night/themes.xml

``xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <style name="Theme.ERPAgentLocal" parent="@android:style/Theme.Material.NoActionBar">
        <item name="android:windowNoTitle">true</item>
        <item name="android:windowActionBar">false</item>
        <item name="android:windowLightStatusBar">false</item>
        <item name="android:statusBarColor">@android:color/black</item>
        <item name="android:navigationBarColor">@android:color/black</item>
        <item name="android:windowBackground">@android:color/black</item>
    </style>

</resources>
``

### app/src/main/res/xml/backup_rules.xml

``xml
<?xml version="1.0" encoding="utf-8"?><!--
   Sample backup rules file; uncomment and customize as necessary.
   See https://developer.android.com/guide/topics/data/autobackup
   for details.
   Note: This file is ignored for devices older than API 31
   See https://developer.android.com/about/versions/12/backup-restore
-->
<full-backup-content>
    <!--
   <include domain="sharedpref" path="."/>
   <exclude domain="sharedpref" path="device.xml"/>
-->
</full-backup-content>
``

### app/src/main/res/xml/data_extraction_rules.xml

``xml
<?xml version="1.0" encoding="utf-8"?><!--
   Sample data extraction rules file; uncomment and customize as necessary.
   See https://developer.android.com/about/versions/12/backup-restore#xml-changes
   for details.
-->
<data-extraction-rules>
    <cloud-backup>
        <!-- TODO: Use <include> and <exclude> to control what is backed up.
        <include .../>
        <exclude .../>
        -->
    </cloud-backup>
    <!--
    <device-transfer>
        <include .../>
        <exclude .../>
    </device-transfer>
    -->
</data-extraction-rules>
``

### app/src/test/java/com/brayan/erpagentlocal/ExampleUnitTest.kt

``kotlin
package com.brayan.erpagentlocal

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}
``

### build.gradle.kts

``kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
``

### gradle.properties

``properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8

android.useAndroidX=true
android.enableJetifier=true

kotlin.code.style=official
``

### gradle/gradle-daemon-jvm.properties

``properties
#This file is generated by updateDaemonJvm
toolchainUrl.FREE_BSD.AARCH64=https\://api.foojay.io/disco/v3.0/ids/ec7520a1e057cd116f9544c42142a16b/redirect
toolchainUrl.FREE_BSD.X86_64=https\://api.foojay.io/disco/v3.0/ids/4c4f879899012ff0a8b2e2117df03b0e/redirect
toolchainUrl.LINUX.AARCH64=https\://api.foojay.io/disco/v3.0/ids/ec7520a1e057cd116f9544c42142a16b/redirect
toolchainUrl.LINUX.X86_64=https\://api.foojay.io/disco/v3.0/ids/4c4f879899012ff0a8b2e2117df03b0e/redirect
toolchainUrl.MAC_OS.AARCH64=https\://api.foojay.io/disco/v3.0/ids/73bcfb608d1fde9fb62e462f834a3299/redirect
toolchainUrl.MAC_OS.X86_64=https\://api.foojay.io/disco/v3.0/ids/846ee0d876d26a26f37aa1ce8de73224/redirect
toolchainUrl.UNIX.AARCH64=https\://api.foojay.io/disco/v3.0/ids/ec7520a1e057cd116f9544c42142a16b/redirect
toolchainUrl.UNIX.X86_64=https\://api.foojay.io/disco/v3.0/ids/4c4f879899012ff0a8b2e2117df03b0e/redirect
toolchainUrl.WINDOWS.AARCH64=https\://api.foojay.io/disco/v3.0/ids/9482ddec596298c84656d31d16652665/redirect
toolchainUrl.WINDOWS.X86_64=https\://api.foojay.io/disco/v3.0/ids/39701d92e1756bb2f141eb67cd4c660e/redirect
toolchainVersion=21
``

### gradle/libs.versions.toml

``toml
[versions]
agp = "8.10.1"
kotlin = "2.3.21"
coreKtx = "1.16.0"
lifecycleRuntimeKtx = "2.9.0"
activityCompose = "1.10.1"
composeBom = "2025.05.01"
junit = "4.13.2"
junitVersion = "1.2.1"
espressoCore = "3.6.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }

androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }

androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-material3 = { group = "androidx.compose.material3", name = "material3" }

junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
``

### gradle/wrapper/gradle-wrapper.properties

``properties
#Sat May 16 19:34:36 BOT 2026
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionSha256Sum=2ab2958f2a1e51120c326cad6f385153bb11ee93b3c216c5fccebfdfbb7ec6cb
distributionUrl=https\://services.gradle.org/distributions/gradle-9.4.1-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
``

### local.properties

``properties
## This file is automatically generated by Android Studio.
# Do not modify this file -- YOUR CHANGES WILL BE ERASED!
#
# This file should *NOT* be checked into Version Control Systems,
# as it contains information specific to your local configuration.
#
# Location of the SDK. This is only used by Gradle.
# For customization when using a Version Control System, please read the
# header note.
sdk.dir=C\:\\Users\\braya\\AppData\\Local\\Android\\Sdk
``

### settings.gradle.kts

``kotlin
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ERPAgentLocal"
include(":app")
``

