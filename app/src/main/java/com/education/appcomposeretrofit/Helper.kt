package com.education.appcomposeretrofit

import android.util.Log
    fun log(message: String?){
        message?.let{
            Log.v("appcompose", message)
        }
    }