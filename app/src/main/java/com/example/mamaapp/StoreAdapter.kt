package com.example.mamaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mamaapp.data.StoreItem

class StoreAdapter(private val onBuy: (StoreItem) -> Unit)
    : ListAdapter<StoreItem, StoreAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<StoreItem>() {
            override fun areItemsTheSame(oldItem: StoreItem, newItem: StoreItem) = oldItem.name == newItem.name
            override fun areContentsTheSame(oldItem: StoreItem, newItem: StoreItem) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_store, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.findViewById<TextView>(R.id.tv_name)
        private val cost = view.findViewById<TextView>(R.id.tv_cost)
        private val buy = view.findViewById<Button>(R.id.btn_buy)

        fun bind(item: StoreItem) {
            name.text = item.name
            cost.text = "${item.cost} tokens"
            buy.setOnClickListener { onBuy(item) }
        }
    }
}
