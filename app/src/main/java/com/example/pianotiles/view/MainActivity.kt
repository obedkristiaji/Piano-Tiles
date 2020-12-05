package com.example.pianotiles.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.pianotiles.databinding.ActivityMainBinding
import com.example.pianotiles.model.Piano
import com.example.pianotiles.presenter.MainPresenter

class MainActivity: AppCompatActivity(), IMainActivity {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentH: HomeFragment
    private lateinit var fragmentG: GameFragment
    private lateinit var fragmentC: CountdownFragment
    private lateinit var fragmentL: LoseFragment
    private lateinit var fragmentS: SettingFragment
    private lateinit var handler: ThreadHandler
    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.presenter = MainPresenter(this)
        this.handler = ThreadHandler(this.presenter)

        this.fragmentG = GameFragment.newInstance(this.presenter, this.handler)
        this.fragmentH = HomeFragment.newInstance(this.presenter)
        this.fragmentL = LoseFragment.newInstance(this.presenter)
        this.fragmentS = SettingFragment.newInstance(this.presenter)
        this.fragmentC = CountdownFragment()
        this.fragmentManager = this.supportFragmentManager

        this.changePage(2)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus) {
            this.fragmentG.initiateGame()
        }
    }

    override fun changePage(page: Int) {
        val ft: FragmentTransaction = this.fragmentManager.beginTransaction()
        if(page == 1) {
            if(this.fragmentH.isAdded()) {
                ft.show(this.fragmentH)
            } else {
                ft.add(binding.fragmentContainer.id, this.fragmentH)
            }
            if(this.fragmentG.isAdded()) {
                ft.hide(this.fragmentG)
            }
        } else if(page == 2) {
            if(this.fragmentG.isAdded()) {
                ft.show(this.fragmentG)
            } else {
                ft.add(binding.fragmentContainer.id, this.fragmentG)
            }
            if(this.fragmentH.isAdded()) {
                ft.hide(this.fragmentH)
            }
        }
        ft.commit()
    }

    override fun showCountdown() {
        this.fragmentC.show(fragmentManager, "countdown")
        this.fragmentC.setCancelable(false)
    }

    override fun showLoseDialog() {
        this.fragmentL.show(fragmentManager, "loseDialog")
        this.fragmentL.setCancelable(false)
    }

    override fun showSetting() {
        this.fragmentS.show(fragmentManager, "setting")
        this.fragmentS.setCancelable(false)
    }

    override fun closeApplication() {
        this.moveTaskToBack(true)
        this.finish()
    }

    override fun drawPiano(piano: Piano) {
        this.fragmentG.resetCanvas()
        this.fragmentG.drawRect(piano)
    }

    override fun setScore(score: Int) {
        this.fragmentG.setScore(score)
    }

    override fun startThread() {
        this.fragmentG.pianoThread.start()
    }

    override fun updateHighScore(score: Int) {
        this.fragmentH.setScore(score)
    }

    override fun initialize() {
        this.fragmentG.initiatePage()
    }
}