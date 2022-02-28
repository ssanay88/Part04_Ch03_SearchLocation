package com.example.part04_ch03_searchlocation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.part04_ch03_searchlocation.databinding.ViewholderSearchResultItemBinding
import com.example.part04_ch03_searchlocation.model.SearchResultEntity

class SearchRecyclerViewAdapter : RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchResultItemViewHolder>() {

    private var searchResultList: List<SearchResultEntity> = listOf()
    private lateinit var searchResultClickListener: (SearchResultEntity) -> Unit

    // 뷰 홀더 선언, itemView : 리사이클러뷰의 아이템에 쓰일 뷰 , searchResultClickListener : 검색 버튼을 눌렀을 때 리스너
    class SearchResultItemViewHolder(val binding: ViewholderSearchResultItemBinding , val searchResultClickListener: (SearchResultEntity) -> Unit ) : RecyclerView.ViewHolder(binding.root) {

        // binding은 상위의 itemView , 우리는 하위의 itemView를 사용해야 한다.
        fun bind(data: SearchResultEntity) = with(binding) {
            ResultTitleTextView.text = data.name
            ResultSubTitleTextView.text = data.fullAdress
        }

        fun bindViews(data: SearchResultEntity) {
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }

    }

    // 뷰 홀더가 생성될 때 , SearchResultItemViewHolder 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultItemViewHolder {
        val view = ViewholderSearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return SearchResultItemViewHolder(view , searchResultClickListener)
    }

    // 뷰홀더에 데이터를 바인드
    override fun onBindViewHolder(holder: SearchResultItemViewHolder, position: Int) {
        holder.bind(searchResultList[position])
        holder.bindViews(searchResultList[position])
        // holder.bind(searchResultList[position])    // 각 포지션별 데이터들을 바인드
    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }

    fun setSearchResultList(searchResultList: List<SearchResultEntity> , searchResultClickListener: (SearchResultEntity) -> Unit) {
        this.searchResultList = searchResultList
        this.searchResultClickListener = searchResultClickListener
    }

}