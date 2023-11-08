package com.example.secrethitler.di

import androidx.datastore.core.DataStore
import com.codelab.android.datastore.PlayerPreferences
import com.example.secrethitler.data.PlayersPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object Module {


    @Provides
    fun providePlayersPreferencesRepository(playerPreferencesDataStore: DataStore<PlayerPreferences>) : PlayersPreferencesRepository =
        PlayersPreferencesRepository(playerPreferencesDataStore)

}