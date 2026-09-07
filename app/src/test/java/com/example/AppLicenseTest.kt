package com.example

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AppLicenseTest {

    @Test
    fun testRootLicenseFileExistsAndIsValid() {
        val licenseFile = File("../../LICENSE").takeIf { it.exists() }
            ?: File("../LICENSE").takeIf { it.exists() }
            ?: File("LICENSE").takeIf { it.exists() }
            ?: File("/LICENSE")

        assertTrue("Root LICENSE file must exist", licenseFile.exists())

        val content = licenseFile.readText()
        assertTrue("License must specify MFG Global AI", content.contains("MFG GLOBAL AI SOFTWARE LICENSE"))
        assertTrue("License must specify Certificate ID MFG-AI-2026-LIC-MH69", content.contains("MFG-AI-2026-LIC-MH69"))
        assertTrue("License must attribute creator Mojimul Hoque", content.contains("MOJIMUL HOQUE"))
        assertTrue("License must specify organization MFG EDUCATION", content.contains("MFG EDUCATION"))
        assertTrue("License must acknowledge Apache 2.0 open source libraries", content.contains("Apache License 2.0"))
    }
}
