package com.example.jeeps.data.model

// ERD: route_id (PK), route_name
data class Route(
    val id          : Int,
    val routeName   : String,      // ERD: route_name
    // Frontend display helpers (not in ERD — derived/computed by backend)
    val routeCode   : String,
    val displayName : String,
    val routeType   : RouteType,
    val status      : RouteStatus = RouteStatus.ACTIVE,
    // Related data (populated by backend joins)
    val stops       : List<RouteSegment>         = emptyList(),
    val landmarks   : List<Landmark>             = emptyList(),
    val fareRules   : List<FareRule>             = emptyList(),
    val waymarks    : List<Waymark>              = emptyList(),
    val assignments : List<TerminalAssignment>   = emptyList(),
)

enum class RouteType   { BAYAN, NATIONAL_HIGHWAY }
enum class RouteStatus { ACTIVE, INACTIVE, REVIEW }

// ERD: route_id (PK, FK), barangay_id (PK, FK)
// Composite PK junction — one row per barangay stop on a route.
data class RouteSegment(
    val routeId             : Int,
    val barangayId          : Int,
    // Frontend display helpers (resolved by backend join to barangay table)
    val barangayName        : String,
    val sequenceOrder       : Int,
    val distanceFromOriginKm: Double,
)

// ERD: landmark_id (PK), barangay_id (FK), landmark_name
data class Landmark(
    val id         : Int,
    val barangayId : Int,          // ERD FK — links to barangay, not routeStop
    val name       : String,
    // Frontend helper — resolved by backend
    val barangayName: String,
)

// ERD: signage_id (PK), signage_name
// The physical sign displayed on the jeepney. Core to the app's "find your jeep" feature.
data class Signage(
    val id   : Int,
    val name : String,
)

// ERD: route_id (PK, FK), signage_id (PK, FK)
// Junction: resolves which signage belongs to which route.
// Used to populate the SignboardCard with the correct sign name.
data class Waymark(
    val routeId   : Int,
    val signageId : Int,
    // Frontend helper — resolved by backend join
    val signageName: String,
)

// ERD: route_id (PK, FK), terminal_id (FK), type
// Tells us which terminal is the ORIGIN and which is the DESTINATION for a route.
data class TerminalAssignment(
    val routeId    : Int,
    val terminalId : Int,
    val type       : TerminalType,
    // Frontend helper — resolved by backend join
    val terminalName: String,
)

enum class TerminalType { ORIGIN, DESTINATION }

// Not a direct ERD table — this is the LTFRB fare matrix.
// Backend will compute this; frontend just displays the result.
data class FareRule(
    val id      : Int,
    val routeId : Int,
    val minKm   : Double,
    val maxKm   : Double,
    val amount  : Double,
)

data class FareResult(
    val regularFare    : Double,
    val discountedFare : Double,   // 80% of regular (PWD / Senior — LTFRB rule)
    val distanceKm     : Double,
    val stopCount      : Int,
)

// What the backend returns for a Find Routes query.
// Wraps Route + computed FareResult + best-match flag.
data class RouteSearchResult(
    val route       : Route,
    val fare        : FareResult,
    val isBestMatch : Boolean = false,
)

val Route.origin: String
    get() = assignments
        .firstOrNull { it.type == TerminalType.ORIGIN }
        ?.terminalName
        ?: displayName.substringBefore("→").trim()   // fallback: parse displayName

val Route.destination: String
    get() = assignments
        .firstOrNull { it.type == TerminalType.DESTINATION }
        ?.terminalName
        ?: displayName.substringAfter("→").trim()    // fallback: parse displayName

// PLACEHOLDER — replace with repository/API response when backend is ready.

val sampleSignage = listOf(
    Signage(id = 1, name = "Crossing - Cabuyao Bayan"),
    Signage(id = 2, name = "Crossing - Complex"),
    Signage(id = 3, name = "Crossing - San Pedro"),
)

val sampleRoutes = listOf(
    RouteSearchResult(
        route = Route(
            id          = 1,
            routeName   = "Crossing - Cabuyao Bayan",
            routeCode   = "Line C",
            displayName = "Crossing → Cabuyao Bayan",
            routeType   = RouteType.BAYAN,
            stops       = listOf(
                RouteSegment(1, 1, "Calamba City",   1, 0.0),
                RouteSegment(1, 2, "Brgy. Mamatid",  2, 1.2),
                RouteSegment(1, 3, "Brgy. Pulo",     3, 3.5),
                RouteSegment(1, 4, "Brgy. Uno",      4, 5.1),
                RouteSegment(1, 5, "Cabuyao City",   5, 8.4),
            ),
            landmarks   = listOf(
                Landmark(1, 2, "Mamatid Church",     "Brgy. Mamatid"),
                Landmark(2, 3, "Pulo Public Market", "Brgy. Pulo"),
            ),
            fareRules   = listOf(
                FareRule(1, 1, 0.0, 4.0,  13.00),
                FareRule(2, 1, 4.0, 8.0,  17.00),
                FareRule(3, 1, 8.0, 99.0, 22.00),
            ),
            waymarks    = listOf(Waymark(1, 1, "Crossing - Cabuyao Bayan")),
            assignments = listOf(
                TerminalAssignment(1, 1, TerminalType.ORIGIN,      "Crossing"),
                TerminalAssignment(1, 5, TerminalType.DESTINATION,  "Cabuyao Bayan"),
            ),
        ),
        fare        = FareResult(22.0, 17.60, 8.4, 7),
        isBestMatch = true,
    ),
    RouteSearchResult(
        route = Route(
            id          = 2,
            routeName   = "Crossing - Complex",
            routeCode   = "Line A",
            displayName = "Crossing → Complex",
            routeType   = RouteType.NATIONAL_HIGHWAY,
            stops       = listOf(
                RouteSegment(2, 1,  "Calamba City",   1, 0.0),
                RouteSegment(2, 6,  "Brgy. Parian",   2, 1.8),
                RouteSegment(2, 7,  "Brgy. Barandal", 3, 3.2),
                RouteSegment(2, 8,  "Binan City",     4, 5.0),
                RouteSegment(2, 9,  "Complex",         5, 6.1),
            ),
            landmarks   = listOf(
                Landmark(3, 6, "Parian Church", "Brgy. Parian"),
            ),
            fareRules   = listOf(
                FareRule(4, 2, 0.0, 4.0, 13.00),
                FareRule(5, 2, 4.0, 8.0, 17.00),
            ),
            waymarks    = listOf(Waymark(2, 2, "Crossing - Complex")),
            assignments = listOf(
                TerminalAssignment(2, 1, TerminalType.ORIGIN,      "Crossing"),
                TerminalAssignment(2, 2, TerminalType.DESTINATION,  "Complex"),
            ),
        ),
        fare = FareResult(18.0, 14.40, 6.1, 5),
    ),
    RouteSearchResult(
        route = Route(
            id          = 3,
            routeName   = "Crossing - San Pedro",
            routeCode   = "Line B",
            displayName = "Crossing → San Pedro",
            routeType   = RouteType.NATIONAL_HIGHWAY,
            stops       = listOf(
                RouteSegment(3, 1,  "Calamba City",   1, 0.0),
                RouteSegment(3, 3,  "Brgy. Pulo",     2, 2.1),
                RouteSegment(3, 10, "Brgy. Cabuyao",  3, 3.5),
                RouteSegment(3, 11, "San Pedro",      4, 4.8),
            ),
            fareRules   = listOf(
                FareRule(6, 3, 0.0, 4.0, 13.00),
                FareRule(7, 3, 4.0, 8.0, 17.00),
            ),
            waymarks    = listOf(Waymark(3, 3, "Crossing - San Pedro")),
            assignments = listOf(
                TerminalAssignment(3, 1, TerminalType.ORIGIN,      "Crossing"),
                TerminalAssignment(3, 3, TerminalType.DESTINATION,  "San Pedro"),
            ),
        ),
        fare = FareResult(15.0, 12.00, 4.8, 4),
    ),
)