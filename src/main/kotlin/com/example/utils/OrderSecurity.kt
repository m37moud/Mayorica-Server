package com.example.utils

import java.security.SecureRandom

fun generateOrderNumber(): String {
    /**
     * Shout out to: https://www.baeldung.com/kotlin/random-alphanumeric-string for
     * the great article.
     * [charPool] = letters and numbers to choose from to create a 15 char string
     */
//    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val charPool: List<Char> = ('0'..'9') + ('0'..'9')

    /**
     * [SecureRandom] generates a cryptographically strong random number generator (RNG).
     * To get a random number, you call `nextInt` and pass a range. In this function,
     * I'll use the [charPool] size, which will generate a random number from 0 to 62.
     */
    val random = SecureRandom()
    val bytes = ByteArray(9)

    /**
     * this gives me something like: charPool[5] = 'f' or charPool[59] = '7'
     */
    val apiKey = (0 until bytes.size - 1)
        .map { _ ->
            charPool[random.nextInt(charPool.size)]
        }.joinToString("")

    /**
     * this generates something like: u0LNanue58mApyD
     */
    println(apiKey)
    return apiKey

}

fun generateApiKey(): String {
    /**
     * Shout out to: https://www.baeldung.com/kotlin/random-alphanumeric-string for
     * the great article.
     * [charPool] = letters and numbers to choose from to create a 15 char string
     */
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    /**
     * [SecureRandom] generates a cryptographically strong random number generator (RNG).
     * To get a random number, you call `nextInt` and pass a range. In this function,
     * I'll use the [charPool] size, which will generate a random number from 0 to 62.
     */
    val random = SecureRandom()
    val bytes = ByteArray(16)

    /**
     * this gives me something like: charPool[5] = 'f' or charPool[59] = '7'
     */
    val apiKey = (0 until bytes.size - 1)
        .map { _ ->
            charPool[random.nextInt(charPool.size)]
        }.joinToString("")

    /**
     * this generates something like: u0LNanue58mApyD
     */
    println(apiKey)
    return apiKey

}
