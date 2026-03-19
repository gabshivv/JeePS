package com.example.jeeps.data.model

// Route
data class Route(
    val id: Int,
    val routeCode: String,
    val displayName: String,
    val origin: String,
    val destination: String,
    val routeType: RouteType,
    val status: RouteStatus = RouteStatus.ACTIVE,
    val stops: List<RouteStop> = emptyList(),
    val landmarks: List<Landmark> = emptyList(),
    val fareRules: List<FareRule> = emptyList(),
)

enum class RouteType   { BAYAN, NATIONAL_HIGHWAY }
enum class RouteStatus { ACTIVE, INACTIVE, REVIEW }

// RouteStop
data class RouteStop(
    val id: Int,
    val routeId: Int,
    val sequenceOrder: Int,
    val barangayName: String,
    val distanceFromOriginKm: Double,
)

// Landmark
data class Landmark(
    val id: Int,
    val routeStopId: Int,
    val name: String,
    val barangayName: String,
)

// FareRule
data class FareRule(
    val id: Int,
    val routeId: Int,
    val minKm: Double,
    val maxKm: Double,
    val amount: Double,
)

// FareResult
data class FareResult(
    val regularFare: Double,
    val discountedFare: Double,
    val distanceKm: Double,
    val stopCount: Int,
)

data class RouteSearchResult(
    val route: Route,
    val fare: FareResult,
    val isBestMatch: Boolean = false,
)

val sampleRoutes = listOf(
    RouteSearchResult(
        route = Route(
            id          = 1,
            routeCode   = "Line C",
            displayName = "Crossing → Cabuyao Bayan",
            origin      = "Crossing",
            destination = "Cabuyao Bayan",
            routeType   = RouteType.BAYAN,
            stops       = listOf(
                RouteStop(1, 1, 1, "Calamba City",  0.0),
                RouteStop(2, 1, 2, "Brgy. Mamatid", 1.2),
                RouteStop(3, 1, 3, "Brgy. Pulo",    3.5),
                RouteStop(4, 1, 4, "Brgy. Uno",     5.1),
                RouteStop(5, 1, 5, "Cabuyao City",  8.4),
            ),
            landmarks = listOf(
                Landmark(1, 2, "Mamatid Church",  "Brgy. Mamatid"),
                Landmark(2, 3, "Pulo Public Market", "Brgy. Pulo"),
            ),
            fareRules = listOf(
                FareRule(1, 1, 0.0, 4.0,  13.00),
                FareRule(2, 1, 4.0, 8.0,  17.00),
                FareRule(3, 1, 8.0, 99.0, 22.00),
            ),
        ),
        fare        = FareResult(22.0, 17.60, 8.4, 7),
        isBestMatch = true,
    ),
    RouteSearchResult(
        route = Route(
            id          = 2,
            routeCode   = "Line A",
            displayName = "Crossing → Complex",
            origin      = "Crossing",
            destination = "Complex",
            routeType   = RouteType.NATIONAL_HIGHWAY,
            stops       = listOf(
                RouteStop(6,  2, 1, "Calamba City",   0.0),
                RouteStop(7,  2, 2, "Brgy. Parian",   1.8),
                RouteStop(8,  2, 3, "Brgy. Barandal",  3.2),
                RouteStop(9,  2, 4, "Binan City",      5.0),
                RouteStop(10, 2, 5, "Complex",         6.1),
            ),
            landmarks = listOf(
                Landmark(3, 7, "Parian Church", "Brgy. Parian"),
            ),
            fareRules = listOf(
                FareRule(4, 2, 0.0, 4.0,  13.00),
                FareRule(5, 2, 4.0, 8.0,  17.00),
            ),
        ),
        fare = FareResult(18.0, 14.40, 6.1, 5),
    ),
    RouteSearchResult(
        route = Route(
            id          = 3,
            routeCode   = "Line B",
            displayName = "Crossing → San Pedro",
            origin      = "Crossing",
            destination = "San Pedro",
            routeType   = RouteType.NATIONAL_HIGHWAY,
            stops       = listOf(
                RouteStop(11, 3, 1, "Calamba City",  0.0),
                RouteStop(12, 3, 2, "Brgy. Pulo",   2.1),
                RouteStop(13, 3, 3, "Brgy. Cabuyao", 3.5),
                RouteStop(14, 3, 4, "San Pedro",     4.8),
            ),
            fareRules = listOf(
                FareRule(6, 3, 0.0, 4.0,  13.00),
                FareRule(7, 3, 4.0, 8.0,  17.00),
            ),
        ),
        fare = FareResult(15.0, 12.00, 4.8, 4),
    ),
)