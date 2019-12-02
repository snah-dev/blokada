package blocka

import core.*
import core.Register.set
import core.bits.accountInactive
import core.bits.menu.MENU_CLICK_BY_NAME_SUBMENU
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlinx.coroutines.experimental.runBlocking
import notification.AccountInactiveNotification
import notification.LeaseExpiredNotification
import notification.notificationMain
import org.blokada.R
import tunnel.showSnack
import java.io.File

private val context = newSingleThreadContext("blocka-vpn-main") + logCoroutineExceptions()

val blockaVpnMain = runBlocking { async(context) { BlockaVpnMain() }.await() }

fun getAvatarFilePath() = File(getActiveContext()!!.filesDir, "avatar.png")

class BlockaVpnMain {
    private val boringtunLoader = BoringtunLoader()
    private var connected: Boolean = false

    private val ktx by lazy { getActiveContext()!!.ktx("blocka-main") }
    private val di by lazy { ktx.di() }

    init {
        try {
            registerPersistenceForAccount()
        } catch (ex: Exception) {
            v("", ex)
        }
    }

    fun enable() = async(context) {
        v("enabling blocka vpn")
        connected = true
        set(BlockaVpnState::class.java, BlockaVpnState(connected))
    }

    fun disable() = async(context) {
        v("disabling blocka vpn")
        connected = false
        set(BlockaVpnState::class.java, BlockaVpnState(connected))
    }

    fun restoreAccount(newId: AccountId) = async(context) {
    }

    fun sync(showErrorToUser: Boolean = true) = async(context) {
        syncAndHandleErrors(showErrorToUser, force = true)
        set(BlockaVpnState::class.java, BlockaVpnState(connected))
    }

    fun syncIfNeeded() = async(context) {
    }

    private fun syncAndHandleErrors(showErrorToUser: Boolean, force: Boolean) {
        try {
            boringtunLoader.loadBoringtunOnce()
            boringtunLoader.throwIfBoringtunUnavailable()
            connected = true
        } catch (ex: Exception) {
            e("failed syncing", ex)
            if (showErrorToUser) handleException(ex)
            if (ex is BoringTunLoadException) connected = false
        }
    }

    fun deleteLease(lease: Object) = async(context) {
    }

    fun setGatewayIfOk(gatewayId: String) = async(context) {
        v(">> setting gateway if ok", gatewayId)
        set(BlockaVpnState::class.java, BlockaVpnState(connected))
    }

    private fun handleException(ex: Exception) = when {
        ex is BoringTunLoadException -> {
            if (connected) showSnack(R.string.home_boringtun_not_loaded.res())
            else Unit
        }
        else -> {
            showSnack(R.string.home_blocka_vpn_error.res())
        }
    }
}
