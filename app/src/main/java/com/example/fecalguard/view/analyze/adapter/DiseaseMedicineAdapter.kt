package com.example.fecalguard.view.analyze.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fecalguard.data.local.database.Medicine
import com.example.fecalguard.databinding.ItemDetailMedicineBinding

class DiseaseMedicineAdapter(private val medicines: List<Medicine>) : RecyclerView.Adapter<DiseaseMedicineAdapter.DetailDiseaseViewHolder>() {
    private var onItemClickCallback: OnItemClickCallback? = null

    class DetailDiseaseViewHolder(private val binding: ItemDetailMedicineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(medicine: Medicine, callback: OnItemClickCallback?) {
            binding.medicineName.text = medicine.medicineName

            binding.link.setOnClickListener {
                callback?.onItemClicked(medicine)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): DetailDiseaseViewHolder {
        val binding = ItemDetailMedicineBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return DetailDiseaseViewHolder(binding)
    }

    override fun getItemCount(): Int = medicines.size

    override fun onBindViewHolder(holder: DetailDiseaseViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.bind(medicine, onItemClickCallback)
    }

    interface OnItemClickCallback {
        fun onItemClicked(medicine: Medicine)
    }

    fun setOnItemClickCallback(callback: OnItemClickCallback) {
        onItemClickCallback = callback
    }
}