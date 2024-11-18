package network.xyo.client.settings

import android.content.Context
import network.xyo.client.datastore.AccountPrefsRepository

class XyoSdk(val settings: SettingsInterface) {
    companion object {
        @Volatile
        private var INSTANCE: XyoSdk? = null

        fun getInstance(context: Context, settings: SettingsInterface = DefaultXyoSdkSettings()): XyoSdk {
            // Initialize the singleton with the users accountPreferences
            AccountPrefsRepository.getInstance(context.applicationContext, settings.accountPreferences)

            val newInstance = INSTANCE ?: synchronized(this) {
                INSTANCE ?: XyoSdk(settings).also { INSTANCE = it }
            }
            return newInstance
        }

        fun refresh(context: Context, settings: SettingsInterface = DefaultXyoSdkSettings()): XyoSdk {
            synchronized(this) {
                // Initialize the singleton with the users accountPreferences
                AccountPrefsRepository.getInstance(context.applicationContext, settings.accountPreferences)
                INSTANCE = XyoSdk(settings)
            }
            return INSTANCE!!
        }

        fun resetInstance() {
            INSTANCE = null
        }
    }
}