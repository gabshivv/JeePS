package com.example.jeeps.data.model

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
enum class RouteType {
    @SerialName("NATIONAL_HIGHWAY") NATIONAL_HIGHWAY,
    @SerialName("BAYAN") BAYAN
}

@Serializable
enum class RouteStatus {
    @SerialName("ACTIVE") ACTIVE,
    @SerialName("INACTIVE") INACTIVE
}

@Serializable
data class Route(
    @SerialName("route_id") val id: Int,
    @SerialName("route_name") val displayName: String,
    @SerialName("route_code") val routeCode: String = "N/A",
    @SerialName("route_type") val routeType: RouteType = RouteType.NATIONAL_HIGHWAY,
    @SerialName("status") val status: RouteStatus = RouteStatus.ACTIVE,
    @SerialName("route_segment") val stops: List<RouteSegment> = emptyList(),
    @SerialName("terminal_assignment") val terminalAssignments: List<TerminalAssignment> = emptyList(),
    val landmarks: List<Landmark> = emptyList(),
    val waymarks: List<Waymark> = emptyList(),
    @Transient
    val path: List<LatLng> = emptyList()
) {
    // Dynamically get origin from terminal assignments
    val originName: String
        get() = terminalAssignments.find { it.type == "start" }?.terminal?.name ?: "Unknown Origin"

    // Dynamically get destination from terminal assignments
    val destinationName: String
        get() = terminalAssignments.find { it.type == "end" }?.terminal?.name ?: "Unknown Destination"
}

@Serializable
data class RouteSegment(
    @SerialName("route_id") val routeId: Int,
    @SerialName("barangay_id") val barangayId: Int,
    @SerialName("barangay") val barangay: Barangay? = null,
    val distanceFromOriginKm: Double = 0.0
)

val RouteSegment.barangayName: String
    get() = barangay?.name ?: "Unknown"

@Serializable
data class TerminalAssignment(
    @SerialName("assignment_id") val assignmentId: Int? = null,
    @SerialName("route_id") val routeId: Int? = null,
    @SerialName("terminal_id") val terminalId: Int? = null,
    @SerialName("terminal") val terminal: Terminal? = null,
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