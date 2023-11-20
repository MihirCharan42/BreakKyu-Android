package com.example.breakkyuapp.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.breakkyuapp.R
import com.example.breakkyuapp.databinding.BreakItemBinding
import com.example.breakkyuapp.models.Break
import java.security.AccessController.getContext
import java.text.SimpleDateFormat


class BreakAdapter(): ListAdapter<Break, BreakAdapter.BreakViewHolder>(ComparatorDiffUtil()) {


    class ComparatorDiffUtil : DiffUtil.ItemCallback<Break>() {

        override fun areContentsTheSame(oldItem: Break, newItem: Break): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: Break, newItem: Break): Boolean {
            return oldItem.id == newItem.id
        }
    }

    inner class BreakViewHolder(private val binding: BreakItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(breakObj: Break) {
            binding.breakName.text = breakObj.name
            val date = breakObj.time.toDate()
            val sdf = SimpleDateFormat("hh:mm aaa")
            binding.breakTime.text = sdf.format(date)
            val drawableResId = when (breakObj.note) {
                "tea" -> R.drawable.baseline_coffee_24
                "sick" -> R.drawable.baseline_sick_24
                "lunch" -> R.drawable.baseline_dinner_dining_24
                "phone" -> R.drawable.baseline_local_phone_24
                else -> 0
            }
            if (drawableResId != 0) {
                val drawable = ContextCompat.getDrawable(binding.root.context, drawableResId)
                // Set the left drawable with a null top, right, and bottom drawable
                binding.breakName.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            } else {
                // Clear any existing drawables
                binding.breakName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreakViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BreakItemBinding.inflate(inflater, parent, false)
        return BreakViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BreakViewHolder, position: Int) {
        val breakObj = getItem(position)
        breakObj.let {
            holder.bind(it)
        }
    }
}