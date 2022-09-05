package com.bignerdranch.android.photogallery.di

import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.bignerdranch.android.photogallery.Presentation.Fragments.PhotoGalleryFragment
import com.bignerdranch.android.photogallery.Repository.FlickrFetchr
import com.bignerdranch.android.photogallery.Repository.api.FlickrApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class AppModule {
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }
    @Provides
    @Singleton
    fun provideRetrofit(gson : Gson) : Retrofit {
       return Retrofit.Builder()
                .baseUrl("https://api.flickr.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
       }
    @Provides
    fun provideFlickrApi(retrofit: Retrofit) : FlickrApi =  retrofit.create(FlickrApi::class.java)
}

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun provideFlickrApi() : FlickrApi
    fun provideRetrofit(): Retrofit
    fun provideGson() : Gson

    fun inject(flickrFetchr: FlickrFetchr)
}