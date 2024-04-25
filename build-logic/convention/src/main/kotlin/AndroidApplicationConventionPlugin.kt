
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import shared.configureKotlinAndroid
import shared.libs

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.run {
                    targetSdk = 34
                    compileSdk = 34
                }
                buildFeatures {
                    buildConfig = true
                    compose = true
                }
                composeOptions {
                    kotlinCompilerExtensionVersion = libs.findVersion("androidx-compose-compiler").get().toString()
                }
                lint {
                    checkDependencies = true
                    ignoreTestSources = true
                    htmlReport = true
                }
            }
        }
    }
}
