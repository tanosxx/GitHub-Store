package zed.rainxch.core.domain.model

enum class InstallerCategory(val sortPriority: Int) {
    SIDE_STORE(0),
    SIDELOADED(1),
    VENDOR_STORE(2),
    PLAY_STORE(3),
    SYSTEM_UPDATE(4),
    ;

    companion object {
        private val SIDE_STORE_INSTALLERS =
            setOf(
                "org.fdroid.fdroid",
                "org.fdroid.basic",
                "dev.imranr.obtainium",
                "dev.imranr.obtainium.fdroid",
                "com.aurora.store",
                "com.looker.droidify",
                "com.machiav3lli.fdroid",
                "nya.kitsunyan.foxydroid",
                "zed.rainxch.githubstore",
            )

        private val VENDOR_STORE_INSTALLERS =
            setOf(
                "com.sec.android.app.samsungapps",
                "com.huawei.appmarket",
                "com.xiaomi.market",
                "com.heytap.market",
                "com.oppo.market",
                "com.bbk.appstore",
                "com.hihonor.appmarket",
                "com.amazon.venezia",
            )

        private const val PLAY_STORE_INSTALLER = "com.android.vending"

        fun classify(
            installerPackageName: String?,
            isUpdatedSystemApp: Boolean,
        ): InstallerCategory {
            if (isUpdatedSystemApp) return SYSTEM_UPDATE
            return when (installerPackageName) {
                null -> SIDELOADED
                PLAY_STORE_INSTALLER -> PLAY_STORE
                in SIDE_STORE_INSTALLERS -> SIDE_STORE
                in VENDOR_STORE_INSTALLERS -> VENDOR_STORE
                else -> SIDELOADED
            }
        }
    }
}
