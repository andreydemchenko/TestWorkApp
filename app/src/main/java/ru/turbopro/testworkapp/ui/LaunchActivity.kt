package ru.turbopro.testworkapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import ru.turbopro.testworkapp.R

class LaunchActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_launch)
		setLaunchScreenTimeOut()
	}

	private fun setLaunchScreenTimeOut() {
		Looper.myLooper()?.let {
			Handler(it).postDelayed({
				startPreferredActivity()
			}, TIME_OUT)
		}
	}

	private fun startPreferredActivity() {
		val intent = Intent(this, MainActivity::class.java)
		startActivity(intent)
		finish()
	}

	companion object {
		private const val TIME_OUT: Long = 1500
	}
}