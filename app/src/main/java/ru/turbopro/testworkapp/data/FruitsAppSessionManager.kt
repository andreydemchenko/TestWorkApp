package ru.turbopro.testworkapp.data

import android.content.Context
import android.content.SharedPreferences

class FruitsAppSessionManager(context: Context) {

	var userSession: SharedPreferences =
		context.getSharedPreferences("userSessionData", Context.MODE_PRIVATE)
	var editor: SharedPreferences.Editor = userSession.edit()


	fun createLoginSession(
		id: String,
		name: String,
	) {
		editor.putBoolean(IS_LOGIN, true)
		editor.putString(KEY_NAME, name)

		editor.commit()
	}

	fun getUserDataFromSession(): HashMap<String, String?> {
		return hashMapOf(
			KEY_NAME to userSession.getString(KEY_NAME, null)
		)
	}

	fun isLoggedIn(): Boolean = userSession.getBoolean(IS_LOGIN, false)

	fun logoutFromSession() {
		editor.clear()
		editor.commit()
	}

	companion object {
		private const val IS_LOGIN = "isLoggedIn"
		private const val KEY_NAME = "userName"
	}
}