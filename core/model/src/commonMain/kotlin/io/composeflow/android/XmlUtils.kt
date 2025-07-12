package io.composeflow.android

import androidx.compose.ui.graphics.Color
import io.composeflow.model.project.appassets.ANDROID_IC_SPLASH_IMAGE
import io.composeflow.serializer.asAndroidXmlString

fun generateSplashThemeXml(backgroundColor: Color?): String {
    val backgroundColorElement =
        if (backgroundColor != null) {
            """
            |<item name="windowSplashScreenBackground">
            |    ${backgroundColor.asAndroidXmlString()}
            |</item>
            """.trimMargin()
        } else {
            ""
        }

    return """
        |<?xml version="1.0" encoding="utf-8"?>
        |<resources>
        |    <style name="Theme.App.Splash" parent="Theme.SplashScreen">
        |        $backgroundColorElement
        |        <item name="windowSplashScreenAnimatedIcon">
        |            @drawable/${ANDROID_IC_SPLASH_IMAGE}
        |        </item>
        |        <item name="postSplashScreenTheme">
        |            @android:style/Theme.Material.NoActionBar
        |        </item>
        |    </style>
        |</resources>
        """.trimMargin()
}

fun generateAndroidManifestXml(isSplashScreenEnabled: Boolean): String {
    val applicationTheme =
        if (isSplashScreenEnabled) {
            "android:theme=\"@style/Theme.App.Splash\""
        } else {
            "android:theme=\"@style/Theme.AppCompat.Light.NoActionBar\""
        }

    val activityTheme =
        if (isSplashScreenEnabled) {
            "android:theme=\"@style/Theme.App.Splash\""
        } else {
            ""
        }

    return """
        <?xml version="1.0" encoding="utf-8"?>
        <manifest xmlns:android="http://schemas.android.com/apk/res/android">

            <uses-permission android:name="android.permission.INTERNET" />
            <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

            <application
                android:allowBackup="true"
                android:icon="@mipmap/ic_launcher"
                android:label="@string/app_name"
                android:roundIcon="@mipmap/ic_launcher_round"
                android:supportsRtl="true"
                $applicationTheme
                android:name=".MainApplication">

                <activity
                    android:exported="true"
                    $activityTheme
                    android:name=".MainActivity">
                    <intent-filter>
                        <action android:name="android.intent.action.MAIN" />

                        <category android:name="android.intent.category.LAUNCHER" />
                    </intent-filter>
                </activity>
            </application>
        </manifest>
        """.trimIndent()
}

fun generateIOSInfoPlistXml(isSplashScreenEnabled: Boolean): String {
    val launchScreenDict =
        if (isSplashScreenEnabled) {
            """
        |    <key>UILaunchScreen</key>
        |    <dict>
        |        <key>UIImageName</key>
        |        <string>SplashImage</string>
        |        <key>UIImageRespectsSafeAreaInsets</key>
        |        <true/>
        |        <key>UILaunchScreenMaintainAspectRatio</key>
        |        <true/>
        |    </dict>
            """.trimMargin()
        } else {
            """
        |    <key>UILaunchScreen</key>
        |    <dict/>
            """.trimMargin()
        }

    return """
        |<?xml version="1.0" encoding="UTF-8"?>
        |<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
        |<plist version="1.0">
        |<dict>
        |    <key>CFBundleDevelopmentRegion</key>
        |    <string>${'$'}(DEVELOPMENT_LANGUAGE)</string>
        |    <key>CFBundleExecutable</key>
        |    <string>${'$'}(EXECUTABLE_NAME)</string>
        |    <key>CFBundleIdentifier</key>
        |    <string>${'$'}(PRODUCT_BUNDLE_IDENTIFIER)</string>
        |    <key>CFBundleInfoDictionaryVersion</key>
        |    <string>6.0</string>
        |    <key>CFBundleName</key>
        |    <string>${'$'}(PRODUCT_NAME)</string>
        |    <key>CFBundlePackageType</key>
        |    <string>${'$'}(PRODUCT_BUNDLE_PACKAGE_TYPE)</string>
        |    <key>CFBundleShortVersionString</key>
        |    <string>1.0</string>
        |    <key>CFBundleVersion</key>
        |    <string>1</string>
        |    <key>LSRequiresIPhoneOS</key>
        |    <true/>
        |    <key>CADisableMinimumFrameDurationOnPhone</key>
        |    <true/>
        |    <key>UIApplicationSceneManifest</key>
        |    <dict>
        |        <key>UIApplicationSupportsMultipleScenes</key>
        |        <false/>
        |    </dict>
        |$launchScreenDict
        |    <key>UIRequiredDeviceCapabilities</key>
        |    <array>
        |        <string>armv7</string>
        |    </array>
        |    <key>UISupportedInterfaceOrientations</key>
        |    <array>
        |        <string>UIInterfaceOrientationPortrait</string>
        |        <string>UIInterfaceOrientationLandscapeLeft</string>
        |        <string>UIInterfaceOrientationLandscapeRight</string>
        |    </array>
        |    <key>UISupportedInterfaceOrientations~ipad</key>
        |    <array>
        |        <string>UIInterfaceOrientationPortrait</string>
        |        <string>UIInterfaceOrientationPortraitUpsideDown</string>
        |        <string>UIInterfaceOrientationLandscapeLeft</string>
        |        <string>UIInterfaceOrientationLandscapeRight</string>
        |    </array>
        |    <key>firebasePlaceholder</key><string>firebasePlaceholder</string>
        |</dict>
        |</plist>
        """.trimMargin()
}
