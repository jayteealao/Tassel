package xyz.graphitenerd.tassel

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

//    @Inject lateinit var workerFactory: HiltWorkerFactory

//    override fun getWorkManagerConfiguration() =
//        Configuration.Builder()
//            .setWorkerFactory(workerFactory)
//            .build()
}
