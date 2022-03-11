package com.example.part04_ch03_searchlocation.utillity


import com.example.part04_ch03_searchlocation.Key
import com.example.part04_ch03_searchlocation.Url
import com.example.part04_ch03_searchlocation.response.search.SearchResponse
import com.example.part04_ch03_searchlocation.response.search.address.AddressInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


// API를 통해 위치에 대한 정보들을 받아오는 인터페이스
interface ApiService {

    // 키워드를 통해 위치를 검색
    @GET(Url.GET_TMAP_LOCATION)
    suspend fun getSearchLocation(
        @Header("appKey") appKey: String = Key.TMAP_API,
        @Query("version") version: Int = 1,
        @Query("callback") callback: String? = null,
        @Query("count") count: Int = 20,
        @Query("searchKeyword") keyword: String,
        @Query("areaLLCode") areaLLCode: String? = null,
        @Query("areaLMCode") areaLMCode: String? = null,
        @Query("resCoordType") resCoordType: String? = null,
        @Query("searchType") searchType: String? = null,
        @Query("multiPoint") multiPoint: String? = null,
        @Query("searchtypCd") searchtypCd: String? = null,
        @Query("radius") radius: String? = null,
        @Query("reqCoordType") reqCoordType: String? = null,
        @Query("centerLon") centerLon: String? = null,
        @Query("centerLat") centerLat: String? = null
    ): Response<SearchResponse>

    // 자신의 위치 좌표를 통해 해당 지점 건물을 리버스 지오코딩
    @GET(Url.GET_TMAP_REVERSE_GEO_CODE)
    suspend fun getReverseGeoCode(
        @Header("appKey") appKey: String = Key.TMAP_API,
        @Query("version") version: Int = 1,
        @Query("callback") callback: String? = null,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("coordType") coordType: String? = null,
        @Query("addressType") addressType: String? = null
    ): Response<AddressInfoResponse>
}