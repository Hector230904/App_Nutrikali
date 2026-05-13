package com.example.nutrikaliapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrikaliapp.R

data class Recommendation(val title: String, val subtitle: String, val imageRes: Int)

class RecommendationAdapter(private val recommendations: List<Recommendation>) :
    RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommendation, parent, false)
        return RecommendationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        holder.bind(recommendations[position])
    }

    override fun getItemCount(): Int = recommendations.size

    class RecommendationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.recommendationTitle)
        private val subtitleTextView: TextView = itemView.findViewById(R.id.recommendationSubtitle)
        private val imageView: ImageView = itemView.findViewById(R.id.recommendationImage)

        fun bind(rec: Recommendation) {
            titleTextView.text = rec.title
            subtitleTextView.text = rec.subtitle
            imageView.setImageResource(rec.imageRes)
        }
    }
}