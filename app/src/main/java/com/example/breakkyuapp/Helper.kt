package com.example.breakkyuapp

class Helper {

    fun validEmail(input: String): Boolean {
        if(input.isEmpty())
            return false
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return input.matches(emailRegex.toRegex())
    }

    fun validName(input: String): Boolean {
        if(input.isEmpty())
            return false
        val nameRegex = "^[A-Za-z-\\s]+$"
        return input.matches(nameRegex.toRegex())
    }

    fun validMobile(input: String): Boolean {
        if(input.length!=10)
            return false
//        val mobileRegex = "^[0-9]{10}$"
//        return input.matches(mobileRegex.toRegex())
        return true
    }

    fun validPassword(input: String): Boolean {
        if(input.length ==0)
            return false
        return true
    }

    fun validTextField(input: String): Boolean {
        if(input.length == 0)
            return false
        return true
    }

}