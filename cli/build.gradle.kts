
plugins {
    java
    application
}

application.mainClass.set("up.visulog.cli.CLILauncher")

dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.4.2.201908231537-r")
    implementation("commons-io:commons-io:2.6")
    implementation("org.slf4j", "slf4j-nop","1.7.29")
    implementation(project(":analyzer"))
    implementation(project(":config"))
    implementation(project(":gitrawdata"))
    testImplementation("junit:junit:4.+")
}


