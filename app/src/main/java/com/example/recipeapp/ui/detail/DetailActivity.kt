package com.example.recipeapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.example.core.data.Resource
import com.example.core.domain.model.Recipe
import com.example.core.utils.htmlParser
import com.example.core.utils.setImageViewTint
import com.example.core.utils.setTextViewColor
import com.example.recipeapp.R
import com.example.recipeapp.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()
    private val detailViewModel: DetailViewModel by viewModels()
    private var statusFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val recipeId: Int = args.id

        detailViewModel.getDetailRecipe(recipeId).observe(this) {
            when (it) {
                is Resource.Success -> {
                    with(binding) {
                        viewError.root.visibility = View.GONE
                        pbDetail.visibility = View.GONE
                        frameLayout.visibility = View.VISIBLE
                        constraintLayout.visibility = View.VISIBLE
                        setData(it.data)
                    }
                }
                is Resource.Loading -> {
                    with(binding) {
                        pbDetail.visibility = View.VISIBLE
                        frameLayout.visibility = View.GONE
                        constraintLayout.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    with(binding) {
                        viewError.root.visibility = View.VISIBLE
                        viewError.tvError.text = it.message ?: getString(R.string.something_wrong)
                        pbDetail.visibility = View.GONE
                        clDetail.visibility = View.GONE
                        frameLayout.visibility = View.GONE
                    }
                }
                else -> Unit
            }
        }

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setData(data: Recipe?) {
        data?.let {
            Glide.with(this)
                .load(data.image)
                .into(binding.ivDetailImage)
            binding.apply {
                tvDetailTitle.text = data.title
                setImageViewTint(applicationContext, ivDetailVegetarian, data.vegetarian.toString())
                setTextViewColor(applicationContext, tvDetailVegetarian, data.vegetarian.toString())
                setImageViewTint(applicationContext, ivDetailVegan, data.vegan.toString())
                setTextViewColor(applicationContext, tvDetailVegan, data.vegan.toString())
                setImageViewTint(applicationContext, ivDetailGlutenFree, data.glutenFree.toString())
                setTextViewColor(applicationContext, tvDetailGlutenFree, data.glutenFree.toString())
                setImageViewTint(applicationContext, ivDetailDairyFree, data.dairyFree.toString())
                setTextViewColor(applicationContext, tvDetailDairyFree, data.dairyFree.toString())
                setImageViewTint(applicationContext, ivDetailHealthy, data.veryHealthy.toString())
                setTextViewColor(applicationContext, tvDetailHealthy, data.veryHealthy.toString())
                setImageViewTint(applicationContext, ivDetailCheap, data.cheap.toString())
                setTextViewColor(applicationContext, tvDetailCheap, data.cheap.toString())
                tvDetailSummary.text = htmlParser(data.summary)
            }
            detailViewModel.checkId(data.recipeId).observe(this@DetailActivity) {
                statusFavorite = it.isFavorite == true
                setStatusFavorite(statusFavorite)
            }
            btnFavoriteClicked(data)
        }

    }

    private fun btnFavoriteClicked(data: Recipe) {
        binding.favoriteButton.setOnClickListener {
            statusFavorite = !statusFavorite
            detailViewModel.setFavoriteRecipe(data, statusFavorite)
            setStatusFavorite(statusFavorite)
        }
    }

    private fun setStatusFavorite(statusFavorite: Boolean) {
        if (statusFavorite) {
            binding.favoriteButton.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.baseline_bookmarked_border_24
                )
            )
        } else {
            binding.favoriteButton.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.baseline_bookmark_border_24
                )
            )
        }
    }
}