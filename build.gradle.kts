plugins {
    java
}

version = "0.0.1"
group = "up"


allprojects {
    repositories {
        mavenCentral()
    }



    plugins.apply("java")

    java.sourceCompatibility = JavaVersion.VERSION_1_10
    dependencies{

        implementation("net.sf.cssbox:pdf2dom:2.0.1")
        implementation("org.apache.pdfbox:pdfbox-tools:2.0.24")
        implementation("com.itextpdf:itextpdf:5.5.13.2")
        implementation("com.itextpdf.tool:xmlworker:5.5.13.2")
        implementation("org.apache.poi:poi-ooxml-schemas:4.1.2")
        implementation("org.apache.poi:poi-scratchpad:5.0.0")
    }

}

