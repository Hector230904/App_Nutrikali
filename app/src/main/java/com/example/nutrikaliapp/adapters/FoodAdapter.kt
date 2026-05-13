package com.example.nutrikaliapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrikaliapp.Food
import com.example.nutrikaliapp.databinding.ItemFoodBinding

class FoodAdapter(
    private var foods: List<Food>,
    private val onItemClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    override fun getItemCount(): Int = foods.size

    // Metodo opcional (si no se usa, puedes eliminarlo o mantenerlo comentado)
    // fun updateData(newFoods: List<Food>) {
    //     foods = newFoods
    //     notifyDataSetChanged()
    // }

    class FoodViewHolder(
        private val binding: ItemFoodBinding,
        private val onItemClick: (Food) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            binding.foodNameTextView.text = food.name
            // Usamos setText con String (no hay forma de evitar concatenación aquí sin recursos)
            binding.caloriesTextView.text = "${food.calories} kcal"
            binding.proteinTextView.text = "${food.protein} g"
            binding.carbsTextView.text = "${food.carbs} g"
            binding.fatTextView.text = "${food.fat} g"

            binding.root.setOnClickListener {
                onItemClick(food)
            }
        }
    }
}