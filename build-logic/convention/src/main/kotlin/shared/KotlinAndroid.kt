package shared

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    applyKotlinOptions: Boolean = true
) {
    commonExtension.apply {

        compileSdk = 34

        defaultConfig {
            minSdk = 28
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            isCoreLibraryDesugaringEnabled = true
        }

        if (applyKotlinOptions) {
            kotlinOptions {
                configure()
            }
        }
    }

    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("corelibrarydesugaring").get())
        add("implementation", libs.findLibrary("androidx-compose-runtime").get())
        add("api", libs.findLibrary("kotlinx-coroutines-core").get())
        add("api", libs.findLibrary("napier").get())
        if (!path.startsWith(":utils")) {
            add("implementation", project(":utils:async"))
            add("implementation", project(":utils:buildinfo"))
            add("implementation", project(":utils:coroutines"))
            add("implementation", project(":utils:di"))
            add("implementation", project(":utils:misc"))
            add("api", project(":utils:time"))

            add("testImplementation", project(":utils:testing"))
            add("testImplementation", libs.findLibrary("kotlin-test").get())
            add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
        }
    }
}

private fun CommonExtension<*, *, *, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}

internal fun KotlinJvmOptions.configure() {
    jvmTarget = JavaVersion.VERSION_17.toString()
    freeCompilerArgs = freeCompilerArgs + listOf(
        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-opt-in=kotlinx.coroutines.FlowPreview",
        "-opt-in=kotlin.ExperimentalStdlibApi",
        "-opt-in=kotlin.time.ExperimentalTime",
        "-opt-in=kotlin.experimental.ExperimentalObjCRefinement",
    )
}
