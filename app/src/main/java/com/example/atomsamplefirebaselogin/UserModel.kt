package com.example.atomsamplefirebaselogin

class UserModel{
    lateinit var id : String
    lateinit var emailId : String
    lateinit var profileName : String

    constructor(id: String, emailId: String, profileName: String){
        this.id = id
        this.emailId = emailId
        this.profileName = profileName
    }

    constructor(){}
}