package com.example.part04_ch03_searchlocation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.part04_ch03_searchlocation.MapActivity.Companion.SEARCH_RESULT_EXTRA_KEY
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

    private lateinit var binding : ActivityMainBinding    // 뷰바인딩

    private lateinit var adapter: SearchRecyclerViewAdapter    // 어댑터 객체

    // coroutines 관련
    private lateinit var job: Job

    /*
    코루틴 컨텍스트의 세가지 Main , IO , Default
    Main : 메인 스레드에 대한 Context, UI갱신이나 Toast등의 View 작업에 사용
    IO : 네트워킹이나 내부 DB접근 등 백그라운드에서 필요한 작업을 수행
    Default : 크기가 큰 리스트를 다루거나 필터링을 수행하는 등 무거운 연산이 필요한 작업에 사용
     */
    // 검색 결과를 찾는 과정에 코루틴 사용
    // Main 스레드에서 IO스레드로 넘어가서 검색 결과를 찾은 후, 다시 Main스레드로 돌아와서 보여준다.
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job    // 어떤 스레드에서 동작할지 명시 , 메인 스레드에서 기본 동작


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        initAdapter()    // 리사이클러뷰 어댑터 객체 생성
        initViews()    // 뷰 객체 초기화
        bindViews()    // 버튼에 대한 클릭리스너 설정
        initData()


    }

    private fun initViews() = with(binding){
        EmptySearchResultTextView.isVisible = false    // 검색 결과가 없는 경우는 우선 숨겨둔다.
        SearchRecyclerView.adapter = adapter    // 리사이클러뷰에 생성해둔 어댑터 객체를 연결
    }

    private fun bindViews() = with(binding){
        // 검색 버튼 클릭 시 입력된 키워트로 결과 검색
        SearchBtn.setOnClickListener {
            searchKeyword(SearchEditText.text.toString())    // 입력한 키워드를 통해 검색 결과를 출력
        }
    }

    private fun initAdapter() {
        adapter = SearchRecyclerViewAdapter()    // 어댑터 클래스에서 객체 생성
    }

    private fun initData() {
        adapter.notifyDataSetChanged()    // 어댑터에 데이터 변경을 알림
    }

    // 받아온 데이터를 리사이클러뷰 아이템에 넣어준다.
    private fun setData(pois: Pois) {
        // 받아온 데이터를 SearchResultEntity형태로 리스트로 만들어준다.
        val dataList = pois.poi.map {
            SearchResultEntity(
                name = it.name ?: "빌딩명 없음",
                fullAdress = makeMainAdress(it),    // Poi데이터 형태를 이용해 주소로 생성
                locationLatLng = LocationLatLngEntity(it.noorLat,it.noorLon)    // 좌표
            )
        }
        // 어댑터에 새로운 dataList를 넣어준다. 어댑터에서 선언한 메서드 실행
        adapter.setSearchResultList(dataList) {
            // 지도 액티비티로 이동
            startActivity(
                Intent(this,MapActivity::class.java).apply{
                    putExtra(SEARCH_RESULT_EXTRA_KEY, it)    // SearchResultEntity를 같이 보내준다.
                }
            )

        }
    }

    // 입력 받은 키워드로 검색하여 리사이클러뷰 초기화
    private fun searchKeyword(keywordString: String) {

        // 비동기 프로그래밍 진행 , 검색 시 비동기로 IO스레드에서 메인스레드로 변경
        // coroutineContext를 통해 메인 스레드에서 시작하는 것을 알린다.
        launch(coroutineContext) {
            try {
                // IO스레드로 넘어가서 네트워킹 작업을 통해 검색 결과 가져온다.
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