package io.composeflow.model.project.string

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringResourceHolderTest {
    @Test
    fun testEmptyStringResourceHolder() {
        val holder = StringResourceHolder()

        // Test default values
        assertTrue(holder.stringResources.isEmpty())

        // Test generation with no resources
        val resourceFiles = holder.generateStringResourceFiles()
        assertTrue(resourceFiles.isEmpty())
    }

    @Test
    fun testSingleStringResourceSingleLocale() {
        val holder = StringResourceHolder()
        holder.stringResources.add(
            stringResourceOf("hello_world", "en" to "Hello World", description = "Greeting message shown on the main screen"),
        )

        val resourceFiles = holder.generateStringResourceFiles()
        assertEquals(1, resourceFiles.size)

        val englishXml = resourceFiles["composeApp/src/commonMain/composeResources/values/strings.xml"]!!
        val expectedXml =
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="hello_world">Hello World</string>
            </resources>
            """.trimIndent() + "\n"
        assertEquals(expectedXml, englishXml)
    }

    @Test
    fun testMultipleStringResourcesMultipleLocales() {
        val holder = StringResourceHolder()
        val spanishLocale = StringResource.Locale("es")

        holder.supportedLocales.add(spanishLocale)

        holder.stringResources.add(
            stringResourceOf("hello", "en" to "Hello", "es" to "Hola", description = "Greeting message shown on the main screen"),
        )
        holder.stringResources.add(
            stringResourceOf("goodbye", "en" to "Goodbye", "es" to "Adiós", description = "Farewell message shown on the logout screen"),
        )

        val resourceFiles = holder.generateStringResourceFiles()
        assertEquals(2, resourceFiles.size)

        // Test English (default locale)
        val englishXml = resourceFiles["composeApp/src/commonMain/composeResources/values/strings.xml"]!!
        val expectedEnglishXml =
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="hello">Hello</string>
                <string name="goodbye">Goodbye</string>
            </resources>
            """.trimIndent() + "\n"
        assertEquals(expectedEnglishXml, englishXml)

        // Test Spanish
        val spanishXml = resourceFiles["composeApp/src/commonMain/composeResources/values-es/strings.xml"]!!
        val expectedSpanishXml =
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="hello">Hola</string>
                <string name="goodbye">Adiós</string>
            </resources>
            """.trimIndent() + "\n"
        assertEquals(expectedSpanishXml, spanishXml)
    }

    @Test
    fun testEmptyStringValues() {
        val holder = StringResourceHolder()
        holder.stringResources.add(stringResourceOf("empty_test", "en" to ""))

        val resourceFiles = holder.generateStringResourceFiles()
        val englishXml = resourceFiles["composeApp/src/commonMain/composeResources/values/strings.xml"]!!

        val expectedXml =
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="empty_test"></string>
            </resources>
            """.trimIndent() + "\n"
        assertEquals(expectedXml, englishXml)
    }

    @Test
    fun testXmlEscaping() {
        val holder = StringResourceHolder()
        holder.stringResources.add(stringResourceOf("special_chars", "en" to """Text with <special> & "quoted" 'chars'"""))

        val resourceFiles = holder.generateStringResourceFiles()
        val englishXml = resourceFiles["composeApp/src/commonMain/composeResources/values/strings.xml"]!!

        val expectedXml =
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="special_chars">Text with &lt;special&gt; &amp; &quot;quoted&quot; &apos;chars&apos;</string>
            </resources>
            """.trimIndent() + "\n"
        assertEquals(expectedXml, englishXml)
    }

    @Test
    fun testPartialTranslations() {
        val holder = StringResourceHolder()
        val germanLocale = StringResource.Locale("de")

        holder.supportedLocales.add(germanLocale)

        holder.stringResources.add(
            stringResourceOf("hello", "en" to "Hello", "de" to "Hallo", description = "Greeting message shown on the main screen"),
        )
        holder.stringResources.add(stringResourceOf("english_only", "en" to "English Only"))

        val resourceFiles = holder.generateStringResourceFiles()
        assertEquals(2, resourceFiles.size)

        // English should have both strings
        val englishXml = resourceFiles["composeApp/src/commonMain/composeResources/values/strings.xml"]!!
        val expectedEnglishXml =
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="hello">Hello</string>
                <string name="english_only">English Only</string>
            </resources>
            """.trimIndent() + "\n"
        assertEquals(expectedEnglishXml, englishXml)

        // German should only have the hello string
        val germanXml = resourceFiles["composeApp/src/commonMain/composeResources/values-de/strings.xml"]!!
        val expectedGermanXml =
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="hello">Hallo</string>
            </resources>
            """.trimIndent() + "\n"
        assertEquals(expectedGermanXml, germanXml)
    }

    @Test
    fun testNoTranslationsForLocale() {
        val holder = StringResourceHolder()
        val italianLocale = StringResource.Locale("it")

        holder.supportedLocales.add(italianLocale)

        holder.stringResources.add(stringResourceOf("test", "en" to "Test"))

        val resourceFiles = holder.generateStringResourceFiles()

        // Only English should be generated (1 file)
        assertEquals(1, resourceFiles.size)

        // English should be generated with correct content
        val englishXml = resourceFiles["composeApp/src/commonMain/composeResources/values/strings.xml"]!!
        val expectedEnglishXml =
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="test">Test</string>
            </resources>
            """.trimIndent() + "\n"
        assertEquals(expectedEnglishXml, englishXml)

        // Italian should not be generated since there are no translations
        assertFalse(resourceFiles.containsKey("composeApp/src/commonMain/composeResources/values-it/strings.xml"))
    }

    @Test
    fun testRegionalLocales() {
        val holder = StringResourceHolder()
        val usLocale = StringResource.Locale("en", "US")
        val gbLocale = StringResource.Locale("en", "GB")
        val frCaLocale = StringResource.Locale("fr", "CA")

        holder.supportedLocales.add(usLocale)
        holder.supportedLocales.add(gbLocale)
        holder.supportedLocales.add(frCaLocale)

        holder.stringResources.add(stringResourceOf("color", "en" to "Color", "en-rUS" to "Color", "en-rGB" to "Colour"))
        holder.stringResources.add(
            stringResourceOf("greeting", "en" to "Hello", "fr-rCA" to "Bonjour", description = "Greeting message shown on the main screen"),
        )

        val resourceFiles = holder.generateStringResourceFiles()
        assertEquals(4, resourceFiles.size) // en (default), en-rUS, en-rGB, fr-rCA

        // Test English (default locale)
        val englishXml = resourceFiles["composeApp/src/commonMain/composeResources/values/strings.xml"]!!
        val expectedEnglishXml =
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="color">Color</string>
                <string name="greeting">Hello</string>
            </resources>
            """.trimIndent() + "\n"
        assertEquals(expectedEnglishXml, englishXml)

        // Test US English
        val usXml = resourceFiles["composeApp/src/commonMain/composeResources/values-en-rUS/strings.xml"]!!
        val expectedUsXml =
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="color">Color</string>
            </resources>
            """.trimIndent() + "\n"
        assertEquals(expectedUsXml, usXml)

        // Test British English
        val gbXml = resourceFiles["composeApp/src/commonMain/composeResources/values-en-rGB/strings.xml"]!!
        val expectedGbXml =
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="color">Colour</string>
            </resources>
            """.trimIndent() + "\n"
        assertEquals(expectedGbXml, gbXml)

        // Test Canadian French
        val frCaXml = resourceFiles["composeApp/src/commonMain/composeResources/values-fr-rCA/strings.xml"]!!
        val expectedFrCaXml =
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="greeting">Bonjour</string>
            </resources>
            """.trimIndent() + "\n"
        assertEquals(expectedFrCaXml, frCaXml)
    }

    @Test
    fun testCopyContents() {
        val source = StringResourceHolder()
        source.defaultLocale = StringResource.Locale("fr")
        source.supportedLocales.clear()
        source.supportedLocales.add(StringResource.Locale("fr"))
        source.supportedLocales.add(StringResource.Locale("en"))

        source.stringResources.add(stringResourceOf("test", "fr" to "Test"))

        val target = StringResourceHolder()
        target.copyContents(source)

        assertEquals(source, target)
    }
}
