package com.example.pianotiles.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.example.pianotiles.R
import com.example.pianotiles.databinding.FragmentGameBinding
import com.example.pianotiles.model.Piano
import com.example.pianotiles.presenter.IMainPresenter

class GameFragment : Fragment(), View.OnTouchListener {
    private lateinit var binding: FragmentGameBinding
    private lateinit var listener: IMainActivity
    private lateinit var canvas: Canvas
    private lateinit var mDetector: GestureDetectorCompat
    private lateinit var handler: ThreadHandler
    private lateinit var presenter: IMainPresenter
    lateinit var pianoThread: PianoThread

    init {

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentGameBinding.inflate(inflater, container, false)

        this.mDetector = GestureDetectorCompat(this.activity, MyCustomGestureListener())

        this.binding.ivCanvas.setOnTouchListener(this)

        this.binding.btnPlay.setOnClickListener {
            if(this.presenter.isThread() == false) {
                this.pianoThread = PianoThread(this.handler, Pair(this.canvas.width, this.canvas.height), this.presenter.getPL())
                this.presenter.setThread(true)
            }

            if(this.binding.btnPlay.text.equals("PLAY")) {
                this.binding.btnPlay.setText(R.string.pause)
                this.listener.showCountdown()
            } else if(this.binding.btnPlay.text.equals("PAUSE")) {
                this.presenter.setPause(true)
                this.binding.btnPlay.setText(R.string.resume)
                this.pianoThread.stop()
            } else if(this.binding.btnPlay.text.equals("RESUME")) {
                this.presenter.setPause(false)
                this.binding.btnPlay.setText(R.string.pause)
                this.pianoThread.setPiano(this.presenter.getPiano(), this.presenter.getScore())
                this.listener.showCountdown()
            }
        }

        return this.binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is IMainActivity) {
            this.listener = context as IMainActivity
        } else {
            throw ClassCastException(context.toString()
                    + " must implement FragmentListener")
        }
    }

    companion object {
        fun newInstance(presenter: IMainPresenter, handler: ThreadHandler): GameFragment {
            val fragment: GameFragment = GameFragment()
            fragment.handler = handler
            fragment.presenter = presenter
            return fragment
        }
    }

    fun drawRect(piano: Piano) {
        val paint = Paint()
        paint.color = ResourcesCompat.getColor(resources, R.color.black, null)

        for (i in 0..piano.size-1) {
            for (j in 0..piano.tiles[i].getSize()-1) {
                this.canvas.drawRect(piano.tiles[i].getRect(j), paint)
            }
        }
    }

    fun setScore(score: Int) {
        this.binding.tvScore.setText(score.toString())
    }

    fun initiateGame() {
        if(this.presenter.isPlay() == false) {
            this.initiatePage()
            this.presenter.setPlay(true)
        }

        if(this.presenter.isLose() == true) {
            this.binding.btnPlay.setText(R.string.play)
            this.presenter.setLose(false)
        }
    }

    fun initiatePage() {
        this.initiateCanvas()
        this.binding.tvScore.setText(R.string.score)
        this.pianoThread = PianoThread(this.handler, Pair(this.canvas.width, this.canvas.height), this.presenter.getPL())
        this.pianoThread.start()
        this.pianoThread.stop()
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
        this.canvas.drawColor(ResourcesCompat.getColor(resources, this.presenter.getPC(), null))

        this.binding.ivCanvas.invalidate()
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        if(this.presenter.isPlay() == true && this.presenter.isThread() == true && this.presenter.isPause() == false) {
            return this.mDetector.onTouchEvent(motionEvent)
        } else {
            return false
        }
    }

    private inner class MyCustomGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            if (e != null) {
                for (i in 0..presenter.getPiano().size-1) {
                    for (j in 0..presenter.getPiano().tiles[i].getSize()-1) {
                        if(presenter.getPiano().tiles[i].getRect(j).contains((e.x).toInt(), (e.y).toInt())) {
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