package com.example.secrethitler.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.codelab.android.datastore.PlayerPreferences
import com.example.secrethitler.data.PlayersPreferencesRepository
import com.example.secrethitler.ui.game.GameViewModel
import com.example.secrethitler.ui.players.PlayersViewModel
import com.example.secrethitler.ui.playersPreferencesStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGameViewModel(playersPreferencesRepository: PlayersPreferencesRepository): GameViewModel =
        GameViewModel(playersPreferencesRepository)

    @Singleton
    @Provides
    fun providePlayersViewModel(playersPreferencesRepository: PlayersPreferencesRepository): PlayersViewModel =
        PlayersViewModel(playersPreferencesRepository)

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<PlayerPreferences> =
        context.playersPreferencesStore

    @Singleton
    @Provides
    fun providePlayersPreferencesRepository(playerPreferencesDataStore: DataStore<PlayerPreferences>): PlayersPreferencesRepository =
        PlayersPreferencesRepository(playerPreferencesDataStore)

}