package io.composeflow.testing

fun isTest() =
    try {
        Class.forName("androidx.compose.ui.test.junit4.ComposeTestRule")
        true
    } catch (e: ClassNotFoundException) {
        false
    }
