package com.example.model

import com.example.R

enum class LandmarkTheme(
    val id: String,
    val title: String,
    val subtitle: String,
    val drawableRes: Int
) {
    HOWRAH_BRIDGE(
        id = "howrah",
        title = "Howrah Bridge",
        subtitle = "Kolkata • Hooghly River Cantilever Wonder",
        drawableRes = R.drawable.img_howrah_bridge
    ),
    KOLKATA_VICTORIA(
        id = "victoria",
        title = "Victoria Memorial",
        subtitle = "Kolkata • Marble Heritage & Serene Ponds",
        drawableRes = R.drawable.img_kolkata_victoria
    ),
    BENGAL_HILLS(
        id = "bengal_hills",
        title = "Himalayan Foothills",
        subtitle = "Bengal Mountain Ranges & Misty Tea Valleys",
        drawableRes = R.drawable.img_bengal_hills
    )
}
