package com.kuvuni.examplesqlite.db.repo

import com.kuvuni.examplesqlite.db.dao.UserDao
import com.kuvuni.examplesqlite.db.entity.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun update(user: User) {
        userDao.update(user)
    }

    suspend fun delete(user: User) {
        userDao.delete(user)
    }

    fun getUserById(uid: Int): Flow<User> {
        return userDao.getUserById(uid)
    }

    fun getAdultUsers(): Flow<List<User>> {
        return userDao.getAdultUsers()
    }
}