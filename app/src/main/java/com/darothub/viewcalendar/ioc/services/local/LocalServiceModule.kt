package com.darothub.viewcalendar.ioc.services.local
import com.darothub.viewcalendar.data.repository.EventRepository
import com.darothub.viewcalendar.data.repository.local.EventLocalDataSource
import com.darothub.viewcalendar.data.repository.remote.EventRemoteDataSource
import com.darothub.viewcalendar.services.local.EventRealmDao
import com.darothub.viewcalendar.services.remote.EventServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import io.realm.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
object LocalServiceModule {

    @Provides
    @ActivityRetainedScoped
    fun provideRealmConfiguration(): RealmConfiguration = RealmConfiguration.Builder()
        .deleteRealmIfMigrationNeeded()
        .build()

    @Provides
    @ActivityRetainedScoped
    fun provideRealmDao(realmConfiguration: RealmConfiguration): EventRealmDao = EventLocalDataSource(realmConfiguration)

    @Provides
    @ActivityRetainedScoped
    fun provideRepository(
        localDataSource: EventRealmDao,
        remoteDataSource: EventRemoteDataSource
    ) = EventRepository(localDataSource, remoteDataSource)
}
