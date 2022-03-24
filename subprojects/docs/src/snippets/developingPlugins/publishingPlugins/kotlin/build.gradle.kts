// tag::plugins_block[]
plugins {
    id("com.gradle.plugin-publish") version "0.20.0"
}
// end::plugins_block[]

// tag::gradle-plugin[]
gradlePlugin {
    plugins { // <1>
        create("greetingsPlugin") { // <2>
            id = "<your plugin identifier>" // <3>
            displayName = "<short displayable name for plugin>" // <4>
            description = "<human-readable description of what your plugin is about>" // <5>
            tags = listOf("tags", "for", "your", "plugins") // <6>
            implementationClass = "<your plugin class>"
        }
    }
}
// end::gradle-plugin[]

// tag::plugin_bundle[]
group = "io.github.johndoe" // <1>
version = "1.0" // <2>

pluginBundle {
    website = "<substitute your project website>" // <3>
    vcsUrl = "<uri to project source repository>" // <4>
    description = "<human-readable description of what your plugins are about>" // <5>
    tags = listOf("tags", "for", "your", "plugins") // <6>
}
// end::plugin_bundle[]

// tag::local_repository[]
publishing {
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri("../local-plugin-repository")
        }
    }
}
// end::local_repository[]
