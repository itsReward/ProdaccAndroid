package com.prodacc.data.di

import android.content.Context
import com.prodacc.data.NetworkManager
import com.prodacc.data.NotificationManager
import com.prodacc.data.remote.ApiServiceContainer
import com.prodacc.data.remote.TokenManager
import com.prodacc.data.remote.WebSocketInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WebSocketModule {

    @Provides
    @Singleton
    fun provideWebSocketInstance(
        @ApplicationContext context: Context,
        networkManager: NetworkManager,
        tokenManager: TokenManager,
        notificationManager: NotificationManager,
        apiServiceContainer: ApiServiceContainer
    ): WebSocketInstance {
        return WebSocketInstance(context, networkManager, tokenManager, apiServiceContainer, notificationManager)
    }
}