package network.xyo.client.settings

open class DefaultXyoSdkSettings(): SettingsInterface {
    override val accountPreferences: AccountPreferences = DefaultAccountPreferences()
}

open class DefaultAccountPreferences: AccountPreferences {
    override val fileName = "network-xyo-sdk-prefs"
    override val storagePath = "__xyo-client-sdk__"
}