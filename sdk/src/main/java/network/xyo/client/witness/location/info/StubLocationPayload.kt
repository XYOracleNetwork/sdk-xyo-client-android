package network.xyo.client.witness.location.info

val stubCoordinates = Coordinates(
    accuracy = 0.0,
    altitude = 0.0,
    altitudeAccuracy = null,
    heading = 0.0,
    latitude = 0.0,
    longitude = 0.0,
    speed = null
)
val stubCurrentLocation = CurrentLocation(
    coords = stubCoordinates,
    timestamp = System.currentTimeMillis()
)