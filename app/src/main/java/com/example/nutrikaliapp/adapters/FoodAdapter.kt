package com.example.nutrikaliapp.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
//import coil.load     --- clase sin funcion
import com.example.nutrikaliapp.Food
import com.example.nutrikaliapp.databinding.ItemFoodBinding
import com.example.nutrikaliapp.utils.ImageUrlCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FoodAdapter(
    private var foods: List<Food>,
    private val onItemClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FoodViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    override fun getItemCount(): Int = foods.size

    fun updateData(newFoods: List<Food>) {
        foods = newFoods
        notifyDataSetChanged()
    }

    class FoodViewHolder(
        private val binding: ItemFoodBinding,
        private val onItemClick: (Food) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        // Scope propio para tareas asíncronas (no afecta a la actividad)
        private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        fun bind(food: Food) {
            // ---- Texto (igual que antes) ----
            binding.foodNameTextView.text = food.name
            binding.caloriesTextView.text = "${food.calories} kcal"
            binding.proteinTextView.text = "${food.protein} g"
            binding.carbsTextView.text = "${food.carbs} g"
            binding.fatTextView.text = "${food.fat} g"

            // ---- Imagen (nuevo) ---- colocar el codigo de whatsapp

        }
    }
}