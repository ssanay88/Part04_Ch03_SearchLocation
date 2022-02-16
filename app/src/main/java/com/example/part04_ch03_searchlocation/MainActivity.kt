package com.example.part04_ch03_searchlocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.example.part04_ch03_searchlocation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

    }

    private fun initViews() = with(binding){
        EmptySearchResultTextView.isVisible = false
        SearchRecyclerView.adpater =
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