package com.zzp.dtrip.data

data class AddressReference(
    val business_area: BusinessArea,
    val crossroad: BusinessArea,
    val famous_area: BusinessArea,
    val landmark_l2: BusinessArea,
    val street: BusinessArea,
    val street_number: BusinessArea,
    val town: BusinessArea
)