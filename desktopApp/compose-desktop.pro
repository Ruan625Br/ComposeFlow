-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.coroutines.** { *; }
-keep class kotlinx.coroutines.Dispatchers.** { *; }
-keep class kotlinx.coroutines.Dispatchers.Main.** { *; }
-keep class com.jetbrains.JBR$Metadata { *; }
-keep class com.sun.jna.** { *; }
-keep class ch.qos.logback.** { *; }
-keep class io.ktor.client.** { *; }
-keep class io.ktor.serialization.** { *; }
-keep class io.netty.channel.** { *; }
-keep class javax.inject.** { *; }
-keep class org.http4k.** { *; }
-keep class org.gradle.** { *; }
-keep class org.jetbrains.kotlin.** { *; }

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Ignore warnings about missing superclasses or interfaces related to Jakarta Servlet
-dontwarn jakarta.servlet.**

-dontwarn ch.qos.logback.classic.**
-dontwarn org.checkerframework.checker.**

-dontwarn kotlin.annotations.**
-dontwarn org.jetbrains.kotlin.**
-dontwarn org.gradle.**
-dontwarn org.codehaus.**
-dontwarn jakarta.mail.**
-dontwarn com.aayushatharva.brotli4j.**
-dontwarn com.jcraft.jzlib.**
-dontwarn com.aayushatharva.brotli4j.**
-dontwarn com.github.luben.**
-dontwarn com.google.protobuf.**
-dontwarn com.jcraft.jzlib.**
-dontwarn com.ning.compress.**
-dontwarn com.oracle.svm.**
-dontwarn dev.forkhandles.result4k.**
-dontwarn dev.forkhandles.values.**
-dontwarn io.netty.internal.**
-dontwarn jakarta.mail.**
-dontwarn javax.servlet.ServletOutputStream.**
-dontwarn javax.servlet.http.**
-dontwarn lzma.sdk.lzma.**
-dontwarn net.jpountz.lz4.**
-dontwarn net.jpountz.xxhash.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.logging.**
-dontwarn org.codehaus.commons.**
-dontwarn org.codehaus.janino.**
-dontwarn org.eclipse.jetty.**
-dontwarn org.jboss.marshalling.**
-dontwarn reactor.blockhound.BlockHound.**
-dontwarn reactor.blockhound.BlockHound$Builder.**
-dontwarn reactor.blockhound.integration.**

-dontwarn reactor.blockhound.**

# Suppress warnings for javax.servlet classes
-dontwarn javax.servlet.**

# Suppress warnings for specific methods in io.netty.util.internal.logging.Log4J2Logger
-dontwarn io.netty.util.internal.logging.Log4J2Logger

# Suppress warnings for the missing method in java.util.concurrent.Executors
-dontwarn java.util.concurrent.Executors

-keep class org.jetbrains.org.objectweb.asm.** { *; }
-keep class io.netty.** { *; }
-keep class org.gradle.internal.watch.registry.** { *; }
-keep class org.http4k.server.** { *; }
-keep class org.jetbrains.kotlin.** { *; }
-keep class javaslang.** { *; }
-keep class gnu.trove.** { *; }
-keep class io.ktor.network.sockets.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class org.slf4j.Logger { *; }

-keep class kotlinx.serialization.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class io.ktor.** { *; }
-keep class kotlin.coroutines.** { *; }
-keep class org.slf4j.** { *; }
-keep class com.google.protobuf.** { *; }
-keep class io.netty.handler.codec.protobuf.** { *; }
-keep class io.netty.util.internal.logging.** { *; }

# Suppress warnings for specific packages
-dontwarn ch.qos.logback.**
-dontwarn com.google.**
-dontwarn com.fasterxml.jackson.core.**
-dontwarn com.sun.jna.**
-dontwarn io.grpc.**
-dontwarn io.netty.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.conscrypt.**
-dontwarn sun.security.x509.**

# Keep necessary classes and their members
-keep class io.grpc.netty.shaded.io.netty.** { *; }
-keep class io.netty.** { *; }
-keep class io.grpc.** { *; }
-keep class io.perfmark.** { *; }
-keep class io.sentry.** { *; }
-keep class io.ktor.** { *; }
-keep class it.unimi.dsi.fastutil.io.** { *; }
-keep class kotlinx.serialization.** { *; }
-keep class okhttp3.** { *; }
-keep class org.apache.commons.logging.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.conscrypt.** { *; }
-keep class org.gradle.** { *; }
-keep class org.jetbrains.** { *; }
-keep class org.slf4j.** { *; }
-keep class com.google.protobuf.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.common.** { *; }
-keep class sun.security.x509.** { *; }

-keep class androidx.compose.** { *; }
-keep class org.jetbrains.jewel.window.** { *; }
-keep class com.jetbrains.WindowDecorations$CustomTitleBar { *; }
-keep class org.jetbrains.jewel.window.styling.** { *; }
-keep class org.jetbrains.jewel.foundation.theme.** { *; }
-keep class org.jetbrains.jewel.ui.** { *; }
-keep class java.awt.Window { *; }
-keep class sun.security.x509.** { *; }
-keep class it.unimi.dsi.fastutil.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

-keepclassmembers class androidx.compose.foundation.gestures.AnchoredDraggableKt {
    public static androidx.compose.ui.Modifier anchoredDraggable$default(...);
}
-keep class androidx.compose.foundation.gestures.AnchoredDraggableState { *; }

-keep class androidx.compose.animation.core.SeekableTransitionState { *; }
-keep class androidx.compose.ui.res.ResourceLoader { *; }
-keep class org.koin.core.error.KoinAppAlreadyStartedException { *; }

-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.animation.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class moe.tlaster.precompose.** { *; }
-keep class org.jetbrains.jewel.ui.component.** { *; }
-keep class org.koin.core.error.** { *; }
-keep class com.google.api.client.util.** { *; }

-dontwarn moe.tlaster.precompose.**
-dontwarn org.jetbrains.jewel.**
-dontwarn org.koin.core.error.**
-dontwarn androidx.compose.**
