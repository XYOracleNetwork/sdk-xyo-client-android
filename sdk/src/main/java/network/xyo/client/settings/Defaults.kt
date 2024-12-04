package network.xyo.client.settings

const val DefaultStoragePath = "__xyo-client-sdk__"
const val DefaultFileName = "network-xyo-sdk-prefs"

open class DefaultXyoSdkSettings : SettingsInterface {
    override val accountPreferences: AccountPreferences = DefaultAccountPreferences()
    override val previousHashStorePreferences: PreviousHashStorePreferences = DefaultPreviousHashStorePreferences()
}

open class DefaultStorageLocationPreferences: StorageLocationPreference {
    override val storagePath = DefaultStoragePath
    override val fileName = DefaultFileName
}

open class DefaultAccountPreferences: DefaultStorageLocationPreferences(), AccountPreferences {
    override val fileName = "network-xyo-sdk-account-prefs"
}
open class DefaultPreviousHashStorePreferences: DefaultStorageLocationPreferences(), PreviousHashStorePreferences {
    override val fileName = "network-xyo-sdk-previous-hash-store-prefs"
}

val defaultXyoSdkSettings = DefaultXyoSdkSettings()