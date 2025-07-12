@file:OptIn(ExperimentalSerializationApi::class)

package io.composeflow.model.vectordrawable

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@SerialName("vector")
data class VectorDrawable(
    @XmlSerialName(
        value = "height",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val height: String,

    @XmlSerialName(
        value = "width",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val width: String,

    @XmlSerialName(
        value = "viewportHeight",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val viewportHeight: Float,

    @XmlSerialName(
        value = "viewportWidth",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val viewportWidth: Float,

    @XmlSerialName(
        value = "tint",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val tint: String? = null, // Optional attribute

    @SerialName("group")
    @XmlElement
    val groups: List<Group> = emptyList(), // Groups are elements, not attributes

    @SerialName("path")
    @XmlElement
    val paths: List<Path> = emptyList()    // Paths are elements, not attributes
)

// Adjust the size of the vector drawable to fit
// https://proandroiddev.com/how-to-add-a-splash-screen-to-a-compose-multiplatform-app-ee4ef72dd845
fun VectorDrawable.wrapChildrenWithGroupForSplashScreen(): VectorDrawable {
    return copy(
        groups = listOf(
            Group(
                pivotX = viewportWidth / 2,
                pivotY = viewportHeight / 2,
                scaleX = 0.4f,
                scaleY = 0.4f,
                paths = this.paths, // Move all paths into the new group
                subGroups = this.groups // Move all existing groups into the new group
            )
        ),
        paths = emptyList() // Clear paths at the root level
    )
}

@Serializable
@SerialName("group")
@XmlElement
data class Group(
    @XmlSerialName(
        value = "name",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val name: String? = null,

    @XmlSerialName(
        value = "pivotX",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val pivotX: Float? = null,

    @XmlSerialName(
        value = "pivotY",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val pivotY: Float? = null,

    @XmlSerialName(
        value = "scaleX",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val scaleX: Float? = null,

    @XmlSerialName(
        value = "scaleY",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val scaleY: Float? = null,

    @XmlSerialName(
        value = "rotation",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val rotation: String? = null,

    @SerialName("group")
    @XmlElement
    val subGroups: List<Group> = emptyList(), // Nested groups

    @SerialName("path")
    @XmlElement
    val paths: List<Path> = emptyList() // Paths inside group
)

@Serializable
@SerialName("path")
@XmlElement
data class Path(
    @XmlSerialName(
        value = "name",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val name: String? = null,

    @XmlSerialName(
        value = "fillColor",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val fillColor: String? = null,

    @XmlSerialName(
        value = "fillAlpha",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val fillAlpha: String? = null,

    @XmlSerialName(
        value = "pathData",
        namespace = "http://schemas.android.com/apk/res/android",
        prefix = "android"
    )
    val pathData: String
)