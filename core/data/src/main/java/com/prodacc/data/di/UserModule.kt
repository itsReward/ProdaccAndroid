package com.prodacc.data.di

import com.prodacc.data.remote.ApiServiceContainer
import com.prodacc.data.repositories.EmployeeRepository
import com.prodacc.data.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        apiServiceContainer: ApiServiceContainer,
        dispatchers: CoroutineDispatchers
    ): UserRepository {
        return UserRepository(apiServiceContainer, dispatchers)
    }

    @Provides
    @Singleton
    fun provideEmployeeRepository(
        apiServiceContainer: ApiServiceContainer,
        dispatchers: CoroutineDispatchers
    ): EmployeeRepository {
        return EmployeeRepository(apiServiceContainer, dispatchers)
    }
}