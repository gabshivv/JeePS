package com.example.jeeps.data.model

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LatLngSerializer : KSerializer<LatLng> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LatLng", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LatLng) = encoder.encodeString("${value.latitude},${value.longitude}")
    override fun deserialize(decoder: Decoder): LatLng {
        val parts = decoder.decodeString().split(",")
        if (parts.size < 2) return LatLng(0.0, 0.0)
        return LatLng(parts[0].toDouble(), parts[1].toDouble())
    }
}

object LatLngListSerializer : KSerializer<List<LatLng>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LatLngList", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: List<LatLng>) {
        val str = value.joinToString(";") { "${it.latitude},${it.longitude}" }
        encoder.encodeString(str)
    }
    override fun deserialize(decoder: Decoder): List<LatLng> {
        val str = decoder.decodeString()
        if (str.isEmpty()) return emptyList()
        return str.split(";").mapNotNull {
            val parts = it.split(",")
            if (parts.size >= 2) LatLng(parts[0].toDouble(), parts[1].toDouble()) else null
        }
    }
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
    @SerialName("route_code") val routeCode: String = "SP-CAL",
    @SerialName("type") val routeType: String,
    @SerialName("status") val status: RouteStatus = RouteStatus.ACTIVE,
    @SerialName("route_segment") val stops: List<RouteSegment> = emptyList(),
    @SerialName("terminal_assignment") val terminalAssignments: List<TerminalAssignment> = emptyList(),
    val landmarks: List<Landmark> = emptyList(),
    val waymarks: List<Waymark> = emptyList(),
    @Serializable(with = LatLngListSerializer::class)
    @SerialName("path_geometry") val path: List<LatLng> = emptyList()
) {
    val originName: String
        get() = terminalAssignments.find { it.type == "start" }?.terminal?.name ?: "Unknown Origin"

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
