package com.example.pianotiles

import android.graphics.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.pianotiles.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var canvas: Canvas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.binding.btnStart.setOnClickListener {
            this.initiateCanvas()
        }
    }

    private fun initiateCanvas() {
        val width = this.binding.ivCanvas.width
        val height = this.binding.ivCanvas.height

        val mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        this.binding.ivCanvas.setImageBitmap(mBitmap)

        this.canvas = Canvas(mBitmap)

        this.canvas.drawColor(ResourcesCompat.getColor(resources, R.color.design_default_color_background, null))
    }

    fun resetCanvas() {
        this.canvas.drawColor(ResourcesCompat.getColor(resources, R.color.design_default_color_background, null))

        this.binding.ivCanvas.invalidate()
    }
}