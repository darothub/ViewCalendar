package com.darothub.viewcalendar.ioc.services.remote
import com.darothub.viewcalendar.data.repository.local.EventLocalDataSource
import com.darothub.viewcalendar.data.repository.remote.EventRemoteDataSource
import com.darothub.viewcalendar.services.local.EventRealmDao
import com.darothub.viewcalendar.services.remote.EventServices
import com.darothub.viewcalendar.utils.Constant
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.RealmConfiguration
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkServiceModule {

    private val baseUrl = Constant.BASE_URL

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create(GsonBuilder().setLenient().create())
    /**
     * A function to provide okHttp logger
     */
    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    /**
     * A function to provide okHttp client
     */
    @Singleton
    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): okhttp3.OkHttpClient {
        return okhttp3.OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .retryOnConnectionFailure(false)
            .build()
    }
    /**
     * A function to provide retrofit
     *
     */
    @Singleton
    @Provides
    fun provideRetrofitInstance(
        gson: GsonConverterFactory,
        client: okhttp3.OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(gson)
            .client(client)
            .build()
    }

    @Provides
    fun provideAuthServices(retrofit: Retrofit): EventServices {
        return retrofit.create(EventServices::class.java)
    }

    @Singleton
    @Provides
    fun provideRemoteDataSource(eventServices: EventServices) = EventRemoteDataSource(eventServices)
}
