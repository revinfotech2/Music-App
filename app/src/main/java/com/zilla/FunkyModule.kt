package com.zilla

import android.support.v4.app.NotificationManagerCompat
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FunkyModule(private val app: Application) {

    @Provides
    @Singleton
    fun getVolleyQueue() = Volley.newRequestQueue(app)


    @Provides
    @Singleton
    fun getNotificationManager() =  NotificationManagerCompat.from(app)

    @Provides
    fun getChartsFetcher() = ChartsFetcher(app)


}
