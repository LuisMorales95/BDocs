package com.novanesttech.beedocs

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.novanesttech.beedocs.network.VolleyConnection

class BeeDocsApplication : Application() {
	
	val TAG = this.javaClass.simpleName
	
	private var volleyConnection: VolleyConnection? = null
	
	override fun onCreate() {
		super.onCreate()
		instance = this
	}
	
	fun getVolleyConnection() : VolleyConnection {
		if (volleyConnection == null) {
			volleyConnection = VolleyConnection(getContext())
		}
		return volleyConnection as VolleyConnection
	}
	
	companion object {
		@get:Synchronized
		var instance: BeeDocsApplication? = null
			private set
		
		@JvmStatic
		fun getContext(): Context {
			return instance!!.applicationContext
		}
	}
}