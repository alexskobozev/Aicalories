package com.wishnewjam.aicalories

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

