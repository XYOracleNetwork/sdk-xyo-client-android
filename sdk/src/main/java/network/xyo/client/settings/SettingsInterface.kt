package network.xyo.client.settings

interface SettingsInterface {
    val accountPreferences: AccountPreferences
}

interface AccountPreferences {
    val fileName: String?
    val storagePath: String?
}