package com.education.appcomposeretrofit

import android.app.Application

class AppComposeRetrofit : Application() {

  lateinit var repository: Repository

  override fun onCreate() {
    super.onCreate()
    repository = Repository()
  }

}
