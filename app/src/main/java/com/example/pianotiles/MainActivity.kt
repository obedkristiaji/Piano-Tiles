package com.example.pianotiles

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GestureDetectorCompat
import com.example.pianotiles.databinding.ActivityMainBinding
import com.example.pianotiles.model.Piano
import com.pppb.m06.PianoThread

class MainActivity: AppCompatActivity(), View.OnTouchListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var canvas: Canvas
    private lateinit var mDetector: GestureDetectorCompat
    private var start = false
    private var pause = false
    private var play = false
    private var thread = false
    private var piano: Piano = Piano()
    private lateinit var handler: ThreadHandler
    private lateinit var pianoThread: PianoThread
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.handler = ThreadHandler(this)

        this.mDetector = GestureDetectorCompat(this, MyCustomGestureListener())
        this.binding.ivCanvas.setOnTouchListener(this)

        this.binding.btnStart.setOnClickListener {
            if(!this.start) {
                this.initiateCanvas()
                this.start = true
            }

            if(!this.thread) {
                this.pianoThread = PianoThread(this.handler, Pair(this.canvas.width, this.canvas.height))
                this.thread = true
            }

            if(this.binding.btnStart.text.equals("START")) {
                this.binding.tvScore.setText(R.string.score)
                this.pianoThread.start()
                this.pianoThread.stop()
                this.play = true
            } else if(this.binding.btnStart.text.equals("PLAY")) {
                this.binding.btnStart.setText(R.string.pause)
                this.pianoThread.start()
            } else if(this.binding.btnStart.text.equals("PAUSE")) {
                this.pause = true
                this.pianoThread.stop()
            } else if(this.binding.btnStart.text.equals("RESUME")) {
                this.pause = false
                this.binding.btnStart.setText(R.string.pause)
                this.pianoThread.setPiano(this.piano, this.score)
                this.pianoThread.start()
            }
        }
    }

    fun lose() {
        this.play = false
    }

    fun resetThread() {
        this.thread = false
        if(pause) {
            this.binding.btnStart.setText(R.string.resume)
        } else {
            if(play){
                this.binding.btnStart.setText(R.string.play)
            }else {
                this.binding.btnStart.setText(R.string.start)
            }
        }
    }

    fun setPiano(piano: Piano, score: Int) {
        this.piano = piano
        this.score = score
    }

    fun setRect(piano: Piano, score: Int) {
        for (i in 0..piano.size-1) {
            for (j in 0..piano.tiles[i].getSize()-1) {
                this.drawRect(piano.tiles[i].getRect(j))
            }
        }
        this.binding.tvScore.setText(score.toString())
    }

    private fun drawRect(rect: Rect) {
        val paint = Paint()
        paint.color = ResourcesCompat.getColor(resources, R.color.black, null)

        canvas.drawRect(rect, paint)
    }

    private fun initiateCanvas() {
        val width = this.binding.ivCanvas.width
        val height = this.binding.ivCanvas.height

        val mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        this.binding.ivCanvas.setImageBitmap(mBitmap)

        this.canvas = Canvas(mBitmap)

        this.resetCanvas()
    }

    fun resetCanvas() {
        this.canvas.drawColor(ResourcesCompat.getColor(resources, R.color.purple_200, null))

        this.binding.ivCanvas.invalidate()
    }

    fun deleteRect(piano: Piano) {
        this.piano = piano
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        if(this.start && this.thread && !this.pause) {
            return this.mDetector.onTouchEvent(motionEvent)
        } else {
            return false
        }
    }

    private inner class MyCustomGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            if (e != null) {
                for (i in 0..piano.size-1) {
                    for (j in 0..piano.tiles[i].getSize()-1) {
                        if(piano.tiles[i].getRect(j).contains((e.x).toInt(), (e.y).toInt())) {
                            pianoThread.deleteTile(i, j)
                            break
                        }
                    }
                }
            }
            return super.onDown(e)
        }
    }
}