package com.example.pianotiles.presenter

import com.example.pianotiles.model.Piano

interface IMainPresenter {
    fun lose(score: Int)
    fun resetThread()
    fun setPiano(piano: Piano, score: Int)
    fun setRect(piano: Piano, score: Int)
    fun deleteRect(piano: Piano)
    fun setScore(score: Int)
    fun getPiano(): Piano
    fun getScore(): Int
    fun isStart(): Boolean
    fun isThread(): Boolean
    fun isPlay(): Boolean
    fun isPause(): Boolean
    fun isLose(): Boolean
    fun setStart(start: Boolean)
    fun setThread(thread: Boolean)
    fun setPlay(play: Boolean)
    fun setPause(pause: Boolean)
    fun setLose(lose: Boolean)
    fun getHS(): Int
    fun getPC(): Int
    fun getPL(): Int
    fun setColor(color: Int)
    fun setLevel(level: Int)
}