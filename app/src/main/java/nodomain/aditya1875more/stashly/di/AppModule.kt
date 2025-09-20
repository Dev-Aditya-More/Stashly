package nodomain.aditya1875more.stashly.di

import androidx.room.Room
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import nodomain.aditya1875more.stashly.data.local.AppDatabase
import nodomain.aditya1875more.stashly.data.remote.ItemRepository
import nodomain.aditya1875more.stashly.data.remote.ItemRepositoryImpl
import nodomain.aditya1875more.stashly.ui.viewmodels.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module{

    single{

        Room.databaseBuilder(
                get(),
                AppDatabase::class.java,
                "app_database"
            ).fallbackToDestructiveMigration(true).build()
    }

    single{
        get<AppDatabase>().itemDao()
    }

    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    single<ItemRepository> { ItemRepositoryImpl(get()) }

    viewModel { MainViewModel(get()) }

}

