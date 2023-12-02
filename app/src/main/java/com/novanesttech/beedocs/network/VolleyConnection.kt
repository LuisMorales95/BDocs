package com.novanesttech.beedocs.network

import android.content.Context
import android.text.TextUtils
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import com.novanesttech.beedocs.model.LruBitmapCache

class VolleyConnection(val context: Context) {
	
	private val tag = this.javaClass.simpleName
	
	private var requestQueue: RequestQueue? = null
	private var imageLoader: ImageLoader? = null
	
	private fun getRequestQueue(): RequestQueue? {
		if (requestQueue == null) {
			requestQueue = Volley.newRequestQueue(context)
		}
		return requestQueue
	}
	
	fun getImageLoader(): ImageLoader? {
		if (imageLoader == null) {
			imageLoader = ImageLoader(getRequestQueue(), LruBitmapCache())
		}
		return imageLoader
	}
	
	fun <T> addToRequestQueue(req: Request<T>, tag: String?) {
		// set the default tag if tag is empty
		req.tag = if (TextUtils.isEmpty(tag)) this.tag else tag
		getRequestQueue()!!.add(req)
	}
	
	fun <T> addToRequestQueue(req: Request<T>) {
		req.tag = tag
		getRequestQueue()!!.add(req)
	}
	
	fun cancelPendingRequests(tag: Any?) {
		if (requestQueue != null) {
			requestQueue?.cancelAll(tag)
		}
	}
	
}