package com.zzp.dtrip.data

data class Result(
    val ad_info: AdInfoX,
    val address: String,
    val address_component: AddressComponent,
    val address_reference: AddressReference,
    val formatted_addresses: FormattedAddresses,
    val location: Location,
    val poi_count: Int,
    val pois: List<Poi>
)