package com.example.part04_ch03_searchlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.example.part04_ch03_searchlocation.databinding.ActivityMapBinding
import com.example.part04_ch03_searchlocation.model.LocationLatLngEntity
import com.example.part04_ch03_searchlocation.model.SearchResultEntity
import com.example.part04_ch03_searchlocation.utillity.RetrofitUtill
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import kotlin.Exception
import kotlin.coroutines.CoroutineContext

class MapActivity: AppCompatActivity(), OnMapReadyCallback, CoroutineScope {

    // 코루틴
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivityMapBinding
    private lateinit var map: GoogleMap
    private var currentMarker: Marker? = null

    private lateinit var searchResult: SearchResultEntity

    private lateinit var locationManager: LocationManager    // 위치 정보를 불러올때 사용

    private lateinit var myLocationListener: MyLocationListener    // 내 위치 정보를 불러올 수 있는 리스너

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (::searchResult.isInitialized.not()) {
            intent?.let {
                // 검색 결과를 인텐트로 받아온다.
                searchResult = it.getParcelableExtra<SearchResultEntity>(SEARCH_RESULT_EXTRA_KEY) ?: throw  Exception("데이터가 존재하지 않습니다.")
                setupGoogleMap()
            }
        }

        bindViews()

    }

    private fun bindViews()  = with(binding) {
        myLocationBtn.setOnClickListener {
            getMyLocation()
        }
    }

    // 나의 위치를 지도에 표시
    private fun getMyLocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager    // LocationManager객체를 얻어 온다
        }

        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)    // GPS가 사용가능한가
        if (isGpsEnabled) {
            // 권한 확인
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 권한 승인 요청
                ActivityCompat.requestPermissions(
                    this , arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PERMISSION_REQUEST_CODE
                )
            } else {    // 권한이 모두 승인된 경우
                setMyLocationListener()
           }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocationListener() {
        val minTime = 1500L    // 현재 위치를 불러오는데 걸리는 최소 시간
        val minDistance = 100f    // 최소 거리 허용 단위
        // 초기화가 안돼있을 경우
        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }
        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                MyLocationListener()
            )

            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime,
                minDistance,
                MyLocationListener()
            )
        }

    }

    // 자신의 위치 업데이트
    private fun onCurrentLocationChanged(locationLatLngEntity: LocationLatLngEntity) {
        // 자신의 위치 좌표로 카메라 이동
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(locationLatLngEntity.longitude.toDouble() , locationLatLngEntity.longitude.toDouble()), CAMERA_ZOOM_LEVEL))

        loadReverseGeoInfomation(locationLatLngEntity)
        removeLocationListener()
    }

    private fun loadReverseGeoInfomation(locationLatLngEntity: LocationLatLngEntity) {
        launch(coroutineContext) {
            try {
                // IO 쓰레드에서 데이터를 받아오다가
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtill.apiService.getReverseGeoCode(
                        lat = locationLatLngEntity.latitude.toDouble(),
                        lon = locationLatLngEntity.longitude.toDouble()
                    )
                    if (response.isSuccessful) {
                        val body = response.body()

                        // Main 쓰레드로 바꿔준다.
                        withContext(Dispatchers.Main) {
                            body?.let {
                                currentMarker = setupMarker(
                                    SearchResultEntity(
                                        fullAdress = it.addressInfo.fullAddress ?: "주소 정보 없음",
                                        name = "내 위치",
                                        locationLatLng = locationLatLngEntity
                                    )
                                )
                                currentMarker?.showInfoWindow()
                            }
                        }
                    }

                }
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun removeLocationListener() {
        if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
            locationManager.removeUpdates(myLocationListener)    // locationManager안에 있는 리스너를 제거해준다.
        }
    }

    private fun setupGoogleMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        currentMarker = setupMarker(searchResult)

        currentMarker?.showInfoWindow()
    }

    private fun setupMarker(searchResult: SearchResultEntity): Marker {
        val positionLatLng = LatLng(searchResult.locationLatLng.latitude.toDouble() , searchResult.locationLatLng.longitude.toDouble())
        val markerOptions = MarkerOptions().apply {
            position(positionLatLng)
            title(searchResult.name)
            snippet(searchResult.fullAdress)
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, CAMERA_ZOOM_LEVEL))

        return map.addMarker(markerOptions)!!

    }

    // 권한 승인을 받았을 때 실행
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 모든 권한이 승인된 경우
                setMyLocationListener()
            } else {
                Toast.makeText(this, "권한을 받지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class MyLocationListener: LocationListener {
        // 현재 위치에 대한 콜백을 받는다
        override fun onLocationChanged(location: Location) {
            val locationLatLngEntity = LocationLatLngEntity(
                location.latitude.toFloat(),
                location.longitude.toFloat()
            )
            onCurrentLocationChanged(locationLatLngEntity)
        }

    }

    companion object {
        const val SEARCH_RESULT_EXTRA_KEY = "SEARCH_RESULT_EXTRA_KEY"
        const val CAMERA_ZOOM_LEVEL = 17f
        const val PERMISSION_REQUEST_CODE = 101
    }

}