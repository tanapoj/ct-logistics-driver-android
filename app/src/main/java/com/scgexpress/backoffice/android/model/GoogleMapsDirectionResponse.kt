package com.scgexpress.backoffice.android.model

data class GoogleMapsDirectionResponse (
    val routes: List<GoogleMapsRoute>
)

data class GoogleMapsRoute(
    val legs: List<GoogleMapsRouteLeg>
)

data class GoogleMapsRouteLeg(
    val distance: GoogleMapsRouteLegDistance
)

data class GoogleMapsRouteLegDistance(
    val text:String,
    val value:Double
)

