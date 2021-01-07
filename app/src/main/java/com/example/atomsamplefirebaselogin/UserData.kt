package com.example.atomsamplefirebaselogin

class UserData {
    companion object{
        private lateinit var currentUser: UserModel

        fun setUser(user: UserModel){
            currentUser = user
        }

        fun getUser():UserModel{
            return currentUser
        }
    }
}