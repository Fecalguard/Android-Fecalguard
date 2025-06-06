package com.example.fecalguard.view.analyze.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fecalguard.R
import com.example.fecalguard.databinding.ItemDetailTextBinding
import com.example.fecalguard.utils.cleanHTML
import com.example.fecalguard.utils.extractContent
import com.example.fecalguard.utils.extractPText

class DiseaseDetailTextAdapter (private val sectionList: List<String>) : RecyclerView.Adapter<DiseaseDetailTextAdapter.SectionViewHolder>() {

    class SectionViewHolder(private val binding: ItemDetailTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(section: String) {
            binding.titleText.text = extractPText(section)
            val desc = extractContent(section)
            binding.content.text = cleanHTML(desc)

//            section.img?.let {
//                Glide.with(binding.ivImage.context)
//                    .load(it)
//                    .into(binding.ivImage)
//                binding.ivImage.visibility = View.VISIBLE
//            } ?: run {
//                binding.ivImage.visibility = View.GONE
//            }


            var appear = false
            binding.hiddenLayout.visibility = View.GONE

            binding.chevron.setOnClickListener {
                if(appear){
                    appear = false
                    binding.chevron.setImageResource(R.drawable.baseline_expand_more_24)
                    binding.hiddenLayout.visibility = View.GONE
                }else{
                    appear = true
                    binding.chevron.setImageResource(R.drawable.baseline_expand_less_24)
                    binding.hiddenLayout.visibility = View.VISIBLE
                }
            }

            binding.titleText.setOnClickListener {
                if(appear){
                    appear = false
                    binding.chevron.setImageResource(R.drawable.baseline_expand_more_24)
                    binding.hiddenLayout.visibility = View.GONE
                }else{
                    appear = true
                    binding.chevron.setImageResource(R.drawable.baseline_expand_less_24)
                    binding.hiddenLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val binding = ItemDetailTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sectionList[position]
        holder.bind(section)
    }

    override fun getItemCount(): Int = sectionList.size
}
