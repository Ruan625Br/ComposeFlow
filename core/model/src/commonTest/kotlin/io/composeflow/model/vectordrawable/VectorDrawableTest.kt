package io.composeflow.model.vectordrawable

import junit.framework.TestCase.assertEquals
import nl.adaptivity.xmlutil.serialization.XML
import kotlin.test.Test

class VectorDrawableTest {

    @Test
    fun testSerializeStandardVectorDrawable_verifyNoCrash() {
        val vectorDrawable = XML.decodeFromString<VectorDrawable>(standardVectorDrawable)
        XML.encodeToString(vectorDrawable)
    }

    @Test
    fun testSerializeVectorDrawableWithGroup() {
        val vectorDrawable = XML.decodeFromString<VectorDrawable>(vectorDrawableWithGroup)
        XML.encodeToString(vectorDrawable)
    }

    @Test
    fun testWrapVectorDrawableWithGroupForSplashScreen() {
        val vectorDrawable = XML.decodeFromString<VectorDrawable>(standardVectorDrawable)
        val wrappedVectorDrawable = vectorDrawable.wrapChildrenWithGroupForSplashScreen()
        XML.encodeToString(wrappedVectorDrawable)

        val wrappingGroup = wrappedVectorDrawable.groups[0]
        assertEquals(wrappingGroup.pivotX, vectorDrawable.viewportWidth / 2)
        assertEquals(wrappingGroup.pivotY, vectorDrawable.viewportHeight / 2)
        assertEquals(wrappingGroup.scaleX, 0.4f)
        assertEquals(wrappingGroup.scaleY, 0.4f)
    }
}

private const val standardVectorDrawable = """
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:height="24dp" android:tint="#000000" android:viewportHeight="24" android:viewportWidth="24" android:width="24dp">
    <path android:fillColor="#FF000000" android:pathData="M17.6,11.48 L19.44,8.3a0.63,0.63 0,0 0,-1.09 -0.63l-1.88,3.24a11.43,11.43 0,0 0,-8.94 0L5.65,7.67a0.63,0.63 0,0 0,-1.09 0.63L6.4,11.48A10.81,10.81 0,0 0,1 20L23,20A10.81,10.81 0,0 0,17.6 11.48ZM7,17.25A1.25,1.25 0,1 1,8.25 16,1.25 1.25,0 0,1 7,17.25ZM17,17.25A1.25,1.25 0,1 1,18.25 16,1.25 1.25,0 0,1 17,17.25Z"/>
</vector> 
"""

private const val vectorDrawableWithGroup = """
    <!-- res/drawable/battery_charging.xml -->
   <vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:height="24dp"
    android:width="24dp"
    android:viewportWidth="24.0"
    android:viewportHeight="24.0">
    <group
        android:pivotX="12"
        android:pivotY="12"
        android:scaleX="0.40"
        android:scaleY="0.40">
    <group
        android:name="rotationGroup"
        android:pivotX="10.0"
        android:pivotY="10.0"
        android:rotation="15.0" >
        <path
            android:name="vect"
            android:fillColor="#FF000000"
            android:pathData="M15.67,4H14V2h-4v2H8.33C7.6,4 7,4.6 7,5.33V9h4.93L13,7v2h4V5.33C17,4.6 16.4,4 15.67,4z"
            android:fillAlpha=".3"/>
        <path
            android:name="draw"
            android:fillColor="#FF000000"
            android:pathData="M13,12.5h2L11,20v-5.5H9L11.93,9H7v11.67C7,21.4 7.6,22 8.33,22h7.33c0.74,0 1.34,-0.6 1.34,-1.33V9h-4v3.5z"/>
    </group>
    </group>
</vector> 
"""
