package com.novanesttech.beedocs

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.novanesttech.beedocs.network.VolleyConnection
import com.novanesttech.beedocs.utils.UserInfoPreference

class BeeDocsApplication : Application() {
	
	val TAG = this.javaClass.simpleName
	
	private var volleyConnection: VolleyConnection? = null

	override fun onCreate() {
		super.onCreate()
		instance = this
		preference = UserInfoPreference(this)
	}

	fun getVolleyConnection() : VolleyConnection {
		if (volleyConnection == null) {
			volleyConnection = VolleyConnection(getContext())
		}
		return volleyConnection as VolleyConnection
	}
	
	companion object {

		@JvmStatic
		@get:Synchronized
		var instance: BeeDocsApplication? = null
			private set

		@JvmStatic
		@get:Synchronized
		var preference: UserInfoPreference? = null
			private set
		
		@JvmStatic
		fun getContext(): Context {
			return instance!!.applicationContext
		}
	}
}