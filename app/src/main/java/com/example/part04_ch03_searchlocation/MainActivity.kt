package com.example.part04_ch03_searchlocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.part04_ch03_searchlocation.databinding.ActivityMainBinding
import com.example.part04_ch03_searchlocation.model.LocationLatLngEntity
import com.example.part04_ch03_searchlocation.model.SearchResultEntity

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private lateinit var adapter: SearchRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()    // 리사이클러뷰 어댑터 초기화
        initViews()    // 뷰 객체 초기화
        initData()
        setData()

    }

    private fun initViews() = with(binding){
        EmptySearchResultTextView.isVisible = false    // 검색 결과가 없는 경우는 우선 숨겨둔다.
        SearchRecyclerView.adapter = adapter
    }

    private fun initAdapter() {
        adapter = SearchRecyclerViewAdapter()
    }

    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun setData() {
        val dataList = (0..10).map {
            SearchResultEntity(
                name = "빌딩 $it",
                fullAdress = "주소 $it",
                locationLatLng = LocationLatLngEntity(it.toFloat(),it.toFloat())
            )
        }
        adapter.setSearchResultList(dataList) {
            Log.d("로그","메인 액티비티 클릭")
            Toast.makeText(this,"빌딩 이름 : ${it.name} , 주소 : ${it.fullAdress}",Toast.LENGTH_SHORT).show()
        }
    }

}

/*

위치를 검색하여 결과 표시
지도에 해당 위치를 표시
지도에 현재 내 위치 표시 기능
1. 검색화면
 - POI
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