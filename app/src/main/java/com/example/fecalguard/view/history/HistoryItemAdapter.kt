package com.example.fecalguard.view.history

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fecalguard.R
import com.example.fecalguard.data.remote.response.DiagnosisResponse
import com.example.fecalguard.databinding.ItemRiwayatBinding
import com.example.fecalguard.utils.parseDate

class HistoryItemAdapter(private val listhistory: List<DiagnosisResponse>) : RecyclerView.Adapter<HistoryItemAdapter.HistoryItemViewHolder>() {
    private var onItemClickCallback: OnItemClickCallback? = null

    class HistoryItemViewHolder(private val binding: ItemRiwayatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiagnosisResponse, callback: OnItemClickCallback?) {
            binding.tvDiseaseName.text = item.predictedClass
            binding.tvConfidenceScore.text = String.format("Confidence score: ${item.confidence}")
            binding.tvDate.text = parseDate(item.datetime)

            Glide.with(binding.ivIcon.context)
                .load(item.imageUrl)
                .error(R.drawable.ic_place_holder)
                .into(binding.ivIcon)

            binding.root.setOnClickListener {
                callback?.onItemClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val binding = ItemRiwayatBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return HistoryItemViewHolder(binding)
    }

    override fun getItemCount(): Int = listhistory.size

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        Log.d("Adapter", "Binding item at $position: ${listhistory[position]}")
        val item = listhistory[position]
        holder.bind(item, onItemClickCallback)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: DiagnosisResponse)
    }

    fun setOnItemClickCallback(callback: OnItemClickCallback) {
        onItemClickCallback = callback
    }
}