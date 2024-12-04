package network.xyo.client.settings

interface SettingsInterface {
    val accountPreferences: AccountPreferences
    val previousHashStorePreferences: PreviousHashStorePreferences
}

interface StorageLocationPreference {
    val fileName: String?
    val storagePath: String?
}

interface AccountPreferences: StorageLocationPreference

interface PreviousHashStorePreferences: StorageLocationPreference