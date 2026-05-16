// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
}

allprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("-Xlint:-processing"))
    }
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.plugin.compose") {
        extensions.configure<org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension>("composeCompiler") {
            stabilityConfigurationFiles.add(
                rootProject.layout.projectDirectory.file("compose_stability.conf")
            )
        }
    }
}

/*
moduleGraphConfig {
    readmePath.set("./README.md")
    heading = "### Module Graph"
    setStyleByModuleType.set(true)
    theme.set(Theme.DARK)
}
*/
