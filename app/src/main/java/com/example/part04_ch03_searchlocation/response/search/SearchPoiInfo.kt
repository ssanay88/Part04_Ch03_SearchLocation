package com.example.part04_ch03_searchlocation.response.search

data class SearchPoiInfo (
    val totalCount: String,
    val count: String,
    val page: String,
    val pois: Pois
    )