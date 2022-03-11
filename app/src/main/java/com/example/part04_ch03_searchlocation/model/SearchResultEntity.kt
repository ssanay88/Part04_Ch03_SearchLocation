package com.example.part04_ch03_searchlocation.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchResultEntity(
    val fullAdress: String,    // 전체 주소
    val name: String,    // 지역 이름
    val locationLatLng: LocationLatLngEntity    // 좌표
): Parcelable
