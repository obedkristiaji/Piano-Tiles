package com.example.pianotiles

import android.os.Handler
import android.os.Message
import com.example.pianotiles.model.Piano

class ThreadHandler(private val mainActivity: MainActivity): Handler() {
    private val MSG_SET_RECT: Int = 0
    private val MSG_RESET_CANVAS: Int = 1
    private val MSG_RESET_THREAD: Int = 2
    private val MSG_SET_POS: Int = 3
    private val MSG_DELETE_RECT: Int = 4
    private val MSG_SET_PLAY: Int = 5

    override fun handleMessage(msg: Message) {
        when(msg.what) {
            this.MSG_SET_RECT -> {
                val attr: Pair<Piano, Int> = msg.obj as Pair<Piano, Int>
                this.mainActivity.resetCanvas()
                this.mainActivity.setRect(attr.first, attr.second)
            }
            this.MSG_RESET_CANVAS -> {
                this.mainActivity.resetCanvas()
            }
            this.MSG_RESET_THREAD -> {
                this.mainActivity.resetThread()
            }
            this.MSG_SET_POS -> {
                val attr: Pair<Piano, Int> = msg.obj as Pair<Piano, Int>
                this.mainActivity.setPiano(attr.first, attr.second)
            }
            this.MSG_DELETE_RECT -> {
                val piano: Piano = msg.obj as Piano
                this.mainActivity.deleteRect(piano)
            }
            this.MSG_SET_PLAY -> {
                this.mainActivity.lose()
            }
        }
    }

    fun setRect(attr: Pair<Piano, Int>) {
        val msg = Message()
        msg.what = MSG_SET_RECT
        msg.obj = attr

        this.sendMessage(msg)
    }

    fun reset() {
        val msg = Message()
        msg.what = MSG_RESET_CANVAS

        this.sendMessage(msg)
    }

    fun stopThread() {
        val msg = Message()
        msg.what = MSG_RESET_THREAD

        this.sendMessage(msg)
    }

    fun setPos(attr: Pair<Piano, Int>) {
        val msg = Message()
        msg.what = MSG_SET_POS
        msg.obj = attr

        this.sendMessage(msg)
    }

    fun deleteRect(piano: Piano) {
        val msg = Message()
        msg.what = MSG_DELETE_RECT
        msg.obj = piano

        this.sendMessage(msg)
    }

    fun lose() {
        val msg = Message()
        msg.what = MSG_SET_PLAY

        this.sendMessage(msg)
    }
}