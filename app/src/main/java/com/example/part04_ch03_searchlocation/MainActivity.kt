package com.example.part04_ch03_searchlocation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.part04_ch03_searchlocation.databinding.ActivityMainBinding
import com.example.part04_ch03_searchlocation.model.LocationLatLngEntity
import com.example.part04_ch03_searchlocation.model.SearchResultEntity
import com.example.part04_ch03_searchlocation.response.search.Poi
import com.example.part04_ch03_searchlocation.response.search.Pois
import com.example.part04_ch03_searchlocation.utillity.RetrofitUtill
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

// CoroutineScope : 비동기로 코드 실행
class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding : ActivityMainBinding

    private lateinit var adapter: SearchRecyclerViewAdapter

    // coroutines 관련
    private lateinit var job: Job

    /*
    코루틴 컨텍스트의 세가지 Main , IO , Default
    Main : 메인 스레드에 대한 Context, UI갱신이나 Toast등의 View 작업에 사용
    IO : 네트워킹이나 내부 DB접근 등 백그라운드에서 필요한 작업을 수행
    Default : 크기가 큰 리스트를 다루거나 필터링을 수행하는 등 무거운 연산이 필요한 작업에 사용
     */
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job    // 어떤 스레드에서 동작할지 명시 , 메인 스레드에서 기본 동작


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        initAdapter()    // 리사이클러뷰 어댑터 초기화
        initViews()    // 뷰 객체 초기화
        bindViews()
        initData()


    }

    private fun initViews() = with(binding){
        EmptySearchResultTextView.isVisible = false    // 검색 결과가 없는 경우는 우선 숨겨둔다.
        SearchRecyclerView.adapter = adapter
    }

    private fun bindViews() = with(binding){
        // 검색 버튼 클릭 시 입력된 키워트로 결과 검색
        SearchBtn.setOnClickListener {
            searchKeyword(SearchEditText.text.toString())
        }
    }

    private fun initAdapter() {
        adapter = SearchRecyclerViewAdapter()
    }

    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun setData(pois: Pois) {
        val dataList = pois.poi.map {
            SearchResultEntity(
                name = it.name ?: "빌딩명 없음",
                fullAdress = makeMainAdress(it),
                locationLatLng = LocationLatLngEntity(it.noorLat,it.noorLon)
            )
        }
        // 어댑터에 새로운 dataList를 넣어준다.
        adapter.setSearchResultList(dataList) {
            Log.d("로그","메인 액티비티 클릭")
            // Toast.makeText(this,"빌딩 이름 : ${it.name} , 주소 : ${it.fullAdress} , 위도/경도 : ${it.locationLatLng}",Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this,MapActivity::class.java)
            )

        }
    }

    // 입력 받은 키워드로 검색하여 리사이클러뷰 초기화
    private fun searchKeyword(keywordString: String) {

        // 비동기 프로그래밍 진행 , 검색 시 비동기로 IO스레드에서 메인스레드로 변경
        // coroutineContext를 통해 메인 스레드에서 시작하는 것을 알린다.
        launch(coroutineContext) {
            try {
                withContext(Dispatchers.IO) {
                    // 키워드로 위치 API를 불러와준다.
                    val response = RetrofitUtill.apiService.getSearchLocation(
                        keyword = keywordString
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            Log.d("response","$body")
                            // 결과값으로 리사이클러뷰를 바인딩해준다.
                            body?.let { searchResponse ->
                                setData(searchResponse.searchPoiInfo.pois)
                            }
                        }
                    }
                }
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun makeMainAdress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }


}

/*

위치를 검색하여 결과 표시
지도에 해당 위치를 표시
지도에 현재 내 위치 표시 기능
1. 검색화면
 - POI (Point Of Interest)
 - Retrofit
 - Gson
 - RecyclerView
 - Coroutines

2. 지도 위치 화면
 - Google Map
 - Intent

3. 현재 내 위치 기능
 - Google Map
 - POI Geo Reverse
 - Retrofit
 - Gson

1) RecyclerView 구현하여 Mocking 데이터 뿌리기
2) Tmap POI 데이터 소개 및 데이터 뿌리기
3) GoogleMap 소개 및 데이터 보여주기
4) 추가 기능 구현 및 코드 정리하기기


*/