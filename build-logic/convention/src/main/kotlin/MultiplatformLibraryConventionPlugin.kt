
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import shared.configure
import shared.configureKotlinAndroid
import shared.libs

@OptIn(ExperimentalKotlinGradlePluginApi::class)
class MultiplatformLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.library")
            }
            extensions.configure<KotlinMultiplatformExtension> {
                targetHierarchy.default()
                androidTarget {
                    compilations.all {
                        kotlinOptions {
                            configure()
                        }
                    }
                }
                iosX64()
                iosArm64()
                iosSimulatorArm64()

                sourceSets {
                    commonMain {
                        dependencies {
                            api(libs.findLibrary("kotlinx-coroutines-core").get())
                            api(libs.findLibrary("napier").get())
                            if (!path.startsWith(":utils")) {
                                implementation(project(":utils:async"))
                                implementation(project(":utils:buildinfo"))
                                implementation(project(":utils:coroutines"))
                                implementation(project(":utils:di"))
                                implementation(project(":utils:misc"))
                                api(project(":utils:time"))
                            }
                        }
                    }
                    commonTest {
                        dependencies {
                            if (!path.startsWith(":utils")) {
                                implementation(project(":utils:testing"))
                                implementation(libs.findLibrary("kotlin-test").get())
                                implementation(libs.findLibrary("kotlinx-coroutines-test").get())
                            }
                        }
                    }
                    all {
                        if (name.startsWith("android"))
                            languageSettings.optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                        languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                        languageSettings.optIn("kotlinx.coroutines.FlowPreview")
                        languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
                        languageSettings.optIn("kotlin.experimental.ExperimentalObjCRefinement")
                        languageSettings.optIn("kotlin.ExperimentalStdlibApi")
                        languageSettings.optIn("kotlin.time.ExperimentalTime")
                    }
                }
            }
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this, applyKotlinOptions = false)

                defaultConfig.run {
                    targetSdk = 34
                    compileSdk = 34
                }
                buildFeatures {
                    compose = true
                }
                composeOptions {
                    kotlinCompilerExtensionVersion = libs.findVersion("androidx-compose-compiler").get().toString()
                }
            }
        }
    }
}

// Somehow the already existing extension from the Kotlin Gradle Plugin/DSL
// is not available to us here so this is an exact copy of that extension.
private fun KotlinMultiplatformExtension.sourceSets(configure: Action<NamedDomainObjectContainer<KotlinSourceSet>>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("sourceSets", configure)
