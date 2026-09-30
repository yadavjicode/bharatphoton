package com.app.bharatphoton.common

import java.util.regex.Pattern

object Validator {
    fun String.isOtpValid(): Boolean {
        val otpRegex = Pattern.compile("^[0-9]{6}\$")
        return otpRegex.matcher(this).matches()
    }

    fun String.isPasswordValid(): Boolean {
        val regex =
            Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}")
        return regex.matcher(this).matches()
    }

    fun String.isPhoneNumberValid(): Boolean {
        val regexPattern = Pattern.compile("^[6-9][0-9]{9}\$")
        val phoneRegex = "^\\d{10}$"
        return regexPattern.matcher(this).matches()
    }

    fun String.isEmailValid(): Boolean {
        val emailRegex = Pattern.compile("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailRegex.matcher(this).matches()
    }
}