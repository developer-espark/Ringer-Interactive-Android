package com.ringer.interactive.contact

import android.content.Context
import android.os.Build
import android.util.Log
import java.util.*

fun DeviceData(context: Context) {



       val release = java.lang.Double.parseDouble(java.lang.String(Build.VERSION.RELEASE).replaceAll("(\\d+[.]\\d+)(.*)", "$1"))
       var codeName = "Unsupported"//below Jelly Bean
       if (release >= 4.1 && release < 4.4)  codeName = "Jelly Bean"
       else if (release < 5)   codeName = "Kit Kat"
       else if (release < 6)   codeName = "Lollipop"
       else if (release < 7)   codeName = "Marshmallow"
       else if (release < 8)   codeName = "Nougat"
       else if (release < 9)   codeName = "Oreo"
       else if (release < 10)  codeName = "Pie"
       else if (release >= 10) codeName = "Android "+(release.toInt())//since API 29 no more candy code names
       Log.e("Device Brand",""+Build.BRAND)
       Log.e("DeviceName",""+android.os.Build.MODEL)
       Log.e("Device Version Info",""+codeName + " v" + release + ", API Level: " + Build.VERSION.SDK_INT)
       val tz: TimeZone = TimeZone.getDefault()
       Log.e("DeviceTimeZone",""+"TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT)
              .toString() + " Timezone id :: " + tz.getID())

}