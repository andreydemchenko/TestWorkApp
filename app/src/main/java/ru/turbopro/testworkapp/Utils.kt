package ru.turbopro.testworkapp

import java.util.*

const val ERR_UPLOAD = "UploadErrorException"

internal fun getRandomString(length: Int, uNum: String, endLength: Int): String {
	val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
	fun getStr(l: Int): String = (1..l).map { allowedChars.random() }.joinToString("")
	return getStr(length) + uNum + getStr(endLength)
}

internal fun getFruitId(): String {
	val uniqueId = UUID.randomUUID().toString()
	return "fruit-by-$uniqueId"
}


