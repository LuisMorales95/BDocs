package com.beedocs

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.beedocs.network.VolleyConnection

class BeeDocsApplication : Application() {
	
	val TAG = this.javaClass.simpleName
	
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