package com.macaron.blocktel

import android.app.Application

class BlockTelApp : Application() {

    lateinit var prefixRepository: PrefixRepository
        private set

    lateinit var blockedCallLog: BlockedCallLog
        private set

    override fun onCreate() {
        super.onCreate()
        prefixRepository = PrefixRepository(this)
        blockedCallLog = BlockedCallLog(this)
    }
}
