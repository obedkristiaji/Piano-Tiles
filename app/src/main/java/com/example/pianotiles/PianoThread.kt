package com.pppb.m06

import android.graphics.Rect
import android.util.Log
import com.example.pianotiles.ThreadHandler
import com.example.pianotiles.model.Piano

class PianoThread(private val handler: ThreadHandler, private val canvas: Pair<Int, Int>) : Runnable {
    private var thread: Thread = Thread(this)
    private var start: Boolean = false
    private var initiate: Boolean = false
    private var piano: Piano = Piano()
    private var fill: Boolean = true
    private var exc = 0
    private var score = 0

    override fun run() {
        try {
            while(start) {
                Thread.sleep(10)

                for((i, tile) in piano.tiles.withIndex()) {
                    var j = 0
                    for(rect in piano.tiles[i].tile) {
                        val newPos = this.setPos(rect)
                        this.piano.tiles[i].setRect(j, newPos)

                        if(rect.top < 0) {
                            this.fill = false
                            break
                        }

                        if(rect.top == 0) {
                            this.fill = true
                            this.exc = i
                        }

                        if(newPos.top > this.canvas.second) {
                            this.start = false;
                            this.handler.lose()
                            this.handler.reset()
                            this.handler.stopThread()
                            break
                        }

                        j++
                    }
                }

                this.handler.setRect(Pair(this.piano, this.score))


                if(this.fill) {
                    this.randomTiles(exc)
                }
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun initiate() {
        for(i in 1..this.piano.size) {
            val top = (((i-1)/4f)*this.canvas.second).toInt()
            this.piano.tiles[i-1].addTile(Rect(((((i-1)/4f)*this.canvas.first)).toInt(), top, (((i/4f)*this.canvas.first)).toInt(), top+500))
        }
        this.randomTiles(exc)
    }

    fun start() {
        if(!start) {
            this.start = true
            this.thread.start()
        }

        if(!initiate) {
            this.initiate()
        }
    }

    fun stop() {
        this.start = false
        this.handler.setPos(Pair(this.piano, this.score))
        this.handler.stopThread()
    }

    fun setPiano(piano: Piano, score: Int) {
        this.piano = piano
        this.score = score
        this.initiate = true
    }

    private fun setPos(rect: Rect): Rect {
        val rect = Rect(rect.left, rect.top+25, rect.right, rect.bottom+25)
        return rect
    }

    fun randomTiles(exc: Int) {
        var pos = (1..this.piano.size).random()
        while((pos-1) == exc) {
            pos = (1..this.piano.size).random()
        }
        this.piano.tiles[pos-1].addTile(Rect(((((pos-1)/4f)*this.canvas.first)).toInt(), (-500), (((pos/4f)*this.canvas.first)).toInt(), 0))
        this.fill = false
    }

    fun deleteTile(pos: Int, index: Int) {
        this.piano.tiles[pos].click(index)
        this.handler.deleteRect(this.piano)
        this.score += 1000
    }
}