package com.example.anchor.di

import androidx.room.Room
import com.example.anchor.data.local.AppDatabase
import com.example.anchor.data.remote.LinkRepository
import com.example.anchor.data.remote.LinkRepositoryImpl
import com.example.anchor.ui.viewmodels.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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

    single<LinkRepository> { LinkRepositoryImpl(get()) }

    viewModel { MainViewModel(get()) }

}

