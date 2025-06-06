package com.example.fecalguard.view.analyze.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fecalguard.data.local.database.DiseaseImage
import com.example.fecalguard.databinding.ItemDetailImageBinding

class DiseaseImageAdapter (private val sectionList: List<DiseaseImage>) : RecyclerView.Adapter<DiseaseImageAdapter.SectionViewHolder>() {

    class SectionViewHolder(private val binding: ItemDetailImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(section: DiseaseImage) {
            binding.contentImage.text = section.imageText
            binding.ivImage.visibility = View.VISIBLE
            val imageName = section.imageResId.removePrefix("R.drawable.")
            val resId = binding.ivImage.context.resources.getIdentifier(
                imageName,
                "drawable",
                binding.ivImage.context.packageName
            )

            Glide.with(binding.ivImage.context)
                .load(resId)
                .into(binding.ivImage)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val binding = ItemDetailImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sectionList[position]
        holder.bind(section)
    }

    override fun getItemCount(): Int = sectionList.size
}
