package com.zilla

import com.zilla.network.SearchHandler
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(FunkyModule::class))
interface DaggerComponent {

    fun inject(app: Application)
    fun inject(chartsFetcher: ChartsFetcher)
    fun inject(searchHandler: SearchHandler)

    fun inject(mainActivity: MainActivity)


}
