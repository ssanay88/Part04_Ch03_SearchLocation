package com.example.part04_ch03_searchlocation.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize    // TODO 뭐지???
// 위치의 좌표
data class LocationLatLngEntity(
    val latitude: Float,
    val longitude: Float
):Parcelable
