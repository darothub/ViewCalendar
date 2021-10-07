package com.darothub.viewcalendar
object Keys {

    init {
        System.loadLibrary("native-lib")
    }

    external fun apiKey(): String
}