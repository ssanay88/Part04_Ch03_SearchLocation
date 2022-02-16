package com.example.part04_ch03_searchlocation

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.part04_ch03_searchlocation.databinding.ViewholderSearchResultItemBinding

class SearchRecyclerViewAdapter : RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchResultItemViewHolder> {

    class SearchResultItemViewHolder(private val view: View , val searchResultClickListener: (Any) -> Unit ) : RecyclerView.ViewHolder(view) {

        fun bind(data: Any) = with(view) {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultItemViewHolder {
        val view = ViewholderSearchResultItemBinding.bind(parent)
        return SearchResultItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultItemViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}