import com.android.build.gradle.LibraryExtension
import com.dev.configureKotest
import com.dev.philo.configureCoroutineAndroid
import com.dev.philo.configureKotlinAndroid
import com.dev.philo.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("earalarm.android.hilt")
            }

            configureKotlinAndroid()
            configureCoroutineAndroid()
            configureKotest()

            extensions.configure<LibraryExtension> {
                // The resource prefix is derived from the module name,
                // so resources inside ":core:module1" must be prefixed with "core_module1_"
                resourcePrefix =
                    path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_")
                        .lowercase() + "_"
            }

            dependencies {
                add("testImplementation", kotlin("test"))
            }
        }
    }
}