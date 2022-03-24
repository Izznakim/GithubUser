package com.example.githubuser.data

import androidx.lifecycle.MediatorLiveData
import com.example.githubuser.data.local.entity.FavoriteEntity
import com.example.githubuser.data.local.room.FavoriteDao
import com.example.githubuser.data.remote.response.User
import com.example.githubuser.data.remote.retrofit.ApiService
import com.example.githubuser.utils.AppExecutors

class FavoriteRespository private constructor(
    private val apiService: ApiService,
    private val favoriteDao: FavoriteDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Result<List<FavoriteEntity>>>()

    fun setUserToFavorite(user: User) {
        val favorite = FavoriteEntity(user.username, user.avatarUrl)
        val favoriteList = ArrayList<FavoriteEntity>()
        appExecutors.diskIO.execute {
            favoriteList.add(favorite)
            favoriteDao.insertUserToFavorite(favoriteList)
        }
    }

    companion object {
        @Volatile
        private var instance: FavoriteRespository? = null
        fun getInstance(
            apiService: ApiService,
            favoriteDao: FavoriteDao,
            appExecutors: AppExecutors
        ): FavoriteRespository =
            instance ?: synchronized(this) {
                instance ?: FavoriteRespository(apiService, favoriteDao, appExecutors)
            }.also { instance = it }
    }
}