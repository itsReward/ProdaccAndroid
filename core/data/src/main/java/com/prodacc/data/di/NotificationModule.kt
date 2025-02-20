package com.prodacc.data.di

import android.content.Context
import com.prodacc.data.NotificationManager
import com.prodacc.data.SignedInUserManager
import com.prodacc.data.repositories.JobCardTechnicianRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        jobCardTechnicianRepository: JobCardTechnicianRepository,
        signedInUserManager: SignedInUserManager
    ): NotificationManager {
        return NotificationManager(context, jobCardTechnicianRepository, signedInUserManager)
    }

}