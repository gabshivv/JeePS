package com.example.jeeps.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RouteType {
    NATIONAL_HIGHWAY,
    BAYAN
}

@Serializable
enum class RouteStatus {
    ACTIVE,
    INACTIVE
}

@Serializable
data class Route(
    @SerialName("route_id") val id: Int,
    @SerialName("route_name") val displayName: String,
    @SerialName("route_code") val routeCode: String,
    val origin: String,
    val destination: String,
    val routeType: RouteType,
    val status: RouteStatus = RouteStatus.ACTIVE,
    val stops: List<RouteSegment> = emptyList(),
    val landmarks: List<Landmark> = emptyList(),
    val waymarks: List<Waymark> = emptyList()
)

@Serializable
data class RouteSegment(
    @SerialName("route_id") val routeId: Int,
    @SerialName("barangay_id") val barangayId: Int,
    val barangayName: String = "",
    val distanceFromOriginKm: Double = 0.0
)

@Serializable
data class TerminalAssignment(
    @SerialName("assignment_id") val assignmentId: Int,
    @SerialName("route_id") val routeId: Int,
    @SerialName("terminal_id") val terminalId: Int,
    val type: String
)

@Serializable
data class Signage(
    @SerialName("signage_id") val signageId: Int,
    @SerialName("signage_name") val signageName: String
)

@Serializable
data class Waymark(
    @SerialName("route_id") val routeId: Int,
    @SerialName("signage_id") val signageId: Int,
    val signageName: String = ""
)

@Serializable
data class Landmark(
    @SerialName("landmark_id") val landmarkId: Int,
    @SerialName("landmark_name") val name: String,
    @SerialName("barangay_id") val barangayId: Int,
    val barangayName: String = ""
)

@Serializable
data class FareResult(
    val regularFare: Double,
    val discountedFare: Double,
    val distanceKm: Double,
    val stopCount: Int
)

@Serializable
data class RouteSearchResult(
    val route: Route,
    val fare: FareResult,
    val isBestMatch: Boolean = false
)