package com.example.homeworkcanvas

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.homeworkcanvas.CustomVIew.ClockView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "MAINACTIVITY"
    lateinit var ttt: ClockView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ttt = findViewById(R.id.clock)
        //ttt.setTime(11, 59, 50)
    }

    override fun onStart() {
        Log.d(TAG, "onStart()")
        super.onStart()
        ttt.start()
        ttt.setOnClickListener {
            if (ttt.isRun)
                ttt.stop()
            else
                ttt.start()
        }
    }
}
