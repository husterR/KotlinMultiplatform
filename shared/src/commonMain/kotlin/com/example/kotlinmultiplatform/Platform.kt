package com.example.kotlinmultiplatform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform