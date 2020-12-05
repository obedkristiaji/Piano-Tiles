package com.example.pianotiles.presenter

import android.util.Log
import com.example.pianotiles.model.Piano
import com.example.pianotiles.storage.GameStorage
import com.example.pianotiles.view.MainActivity

class MainPresenter(private val view: MainActivity): IMainPresenter {
    private val db: GameStorage = GameStorage(this.view)
    private var piano: Piano = Piano()
    private var start = false
    private var thread = false
    private var play = false
    private var pause = false
    private var lose = false
    private var score = 0

    override fun lose(score: Int) {
        if(score > this.db.getScore()) {
            this.db.saveScore(score)
        }
        this.lose = true
        this.play = false
        this.view.showLoseDialog()
    }

    override fun resetThread() {
        this.thread = false
    }

    override fun setPiano(piano: Piano, score: Int) {
        this.piano = piano
        this.score = score
    }

    override fun setRect(piano: Piano, score: Int) {
        this.view.drawPiano(piano)
        this.setScore(score)
    }

    override fun deleteRect(piano: Piano) {
        this.piano = piano
    }

    override fun setScore(score: Int) {
        this.view.setScore(score)
    }

    override fun getPiano(): Piano {
        return this.piano
    }

    override fun getScore(): Int {
        return this.score
    }

    override fun isStart(): Boolean {
        return this.start
    }

    override fun isThread(): Boolean {
        return this.thread
    }

    override fun isPlay(): Boolean {
        return this.play
    }

    override fun isPause(): Boolean {
        return this.pause
    }

    override fun isLose(): Boolean {
        return this.lose
    }

    override fun setStart(start: Boolean) {
        this.start = start
    }

    override fun setThread(thread: Boolean) {
        this.thread = thread
    }

    override fun setPlay(play: Boolean) {
        this.play = play
    }

    override fun setPause(pause: Boolean) {
        this.pause = pause
    }

    override fun setLose(lose: Boolean) {
        this.lose = lose
    }

    override fun getHS(): Int {
        return this.db.getScore()
    }

    override fun getPC(): Int {
        return this.db.getColor()
    }

    override fun getPL(): Int {
        return this.db.getLevel()
    }

    override fun setColor(color: Int) {
        this.db.saveColor(color)
    }

    override fun setLevel(level: Int) {
        this.db.saveLevel(level)
    }
}