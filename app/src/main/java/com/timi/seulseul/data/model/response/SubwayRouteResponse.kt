package com.timi.seulseul.data.model.response

data class SubwayRouteResponse(
    val code: Int,
    val data: SubwayRouteData
)

data class SubwayRouteData(
    val bodyList: List<Body>,
    val totalTimeSection: List<TotalTimeSection>,
    val transferSection: List<TransferSection>
)


data class Body(
    val viewType: String,
    val data: BodyData
)

data class BodyData(
    val arriveTime: String,
    val departTime: String,
    val fastTrainDoor: String,
    val firstStation: String,
    val laneName: String,
    val lastStation: String,
    val wayName: String
)


data class TotalTimeSection(
    val viewType: String,
    val data: TotalTimeData
)

data class TotalTimeData(
    val arriveTime: String,
    val departTime: String,
    val totalTime: String
)


data class TransferSection(
    val viewType: String,
    val data: TransferData
)

data class TransferData(
    val exWalkTime: String
)