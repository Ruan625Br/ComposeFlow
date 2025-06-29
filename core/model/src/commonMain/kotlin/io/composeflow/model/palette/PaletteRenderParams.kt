package io.composeflow.model.palette

data class PaletteRenderParams(
    /**
     *  Set to `true` for the items shown only in the canvas. For example, for
     *  items in `LazyColumn`, only the first item is editable, but some number of items are shown in
     *  the editor to preview how the rest of the items look like. In that case, this parameter is
     *  set to `true` for the rest of the items.
     */
    val isShadowNode: Boolean = false,
    /**
     *  isThumbnail Set to `true` if this node is rendered as a thumbnail (such as thumbnail
     *  in the new screen dialog). Certain features need to be disabled (such as scrolling in a
     *  LazyList)
     */
    val isThumbnail: Boolean = false,
    /**
     * Show borders for all the composables dropped in the canvas.
     */
    val showBorder: Boolean = false,
)
