package com.novanesttech.beedocs

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.novanesttech.beedocs.network.VolleyConnection

class BeeDocsApplication : Application() {
	
	val TAG = BeeDocsApplication::class.java.simpleName
	
	private lateinit var volleyConnection: VolleyConnection
	
	override fun onCreate() {
		super.onCreate()
		instance = this
		volleyConnection = VolleyConnection(getContext())
	}
	
	fun getVolleyConnection() : VolleyConnection {
		return volleyConnection
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