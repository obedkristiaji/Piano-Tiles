package com.example.pianotiles.view

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.pianotiles.databinding.ActivityMainBinding
import com.example.pianotiles.model.Piano
import com.example.pianotiles.presenter.MainPresenter

class MainActivity: AppCompatActivity(), SensorEventListener, IMainActivity {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentH: HomeFragment
    private lateinit var fragmentG: GameFragment
    private lateinit var fragmentC: CountdownFragment
    private lateinit var fragmentL: LoseFragment
    private lateinit var fragmentS: SettingFragment
    private lateinit var handler: ThreadHandler
    private lateinit var presenter: MainPresenter
    private lateinit var mSensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var magnetometer: Sensor
    private var accelerometerReading: FloatArray = FloatArray(3)
    private var magnetometerReading: FloatArray = FloatArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        this.accelerometer = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        this.magnetometer = this.mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

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

    override fun onResume() {
        super.onResume()
        if(this.accelerometer != null) {
            this.mSensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }

        if(this.magnetometer != null) {
            this.mSensorManager.registerListener(this, this.magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        this.mSensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(this.presenter.isPlay() == true) {
            val sensorType: Int = event!!.sensor.getType()
            when (sensorType) {
                Sensor.TYPE_ACCELEROMETER -> this.accelerometerReading = event.values.clone()
                Sensor.TYPE_MAGNETIC_FIELD -> this.magnetometerReading = event.values.clone()
            }

            val rotationMatrix = FloatArray(9)
            val rotation = SensorManager.getRotationMatrix(rotationMatrix, null, this.accelerometerReading, this.magnetometerReading)

            val orientationAngles = FloatArray(3)
            if (rotation) {
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
            }

            if (Math.abs(orientationAngles[1]) > 1 || Math.abs(orientationAngles[1]) < -1) {
                this.fragmentG.addShake()
            }

            if (Math.abs(orientationAngles[2]) > 1 || Math.abs(orientationAngles[2]) < -1) {
                this.fragmentG.addShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //
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

    override fun setLoseScore(score: Int) {
        this.fragmentL.setScore(score)
    }
}