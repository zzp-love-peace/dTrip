package com.zzp.dtrip.data

class BigData {

    var title: String = ""
    var address: String = ""
    var _distance: Double = 0.0
    var location: Location = Location(0.0, 0.0)

    constructor(data: Data) {
        title = data.title
        address = data.address
        location = data.location
    }
    constructor(dataX: DataX) {
        title = dataX.title
        address = dataX.address
        _distance = dataX._distance
        location = dataX.location
    }
    constructor(dataXX: DataXX) {
        title = dataXX.title
        address = dataXX.address
        _distance = dataXX._distance
        location = dataXX.location
    }
}
