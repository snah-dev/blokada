package blocka

import android.os.Build
import java.util.*

data class CurrentAccount(
        var id : String = "1",
        val privateKey: String = "", // wireguard private key
        val publicKey: String = "", // wireguard public key
        val migration: Int = 0,
        val unsupportedForVersionCode: Int = 0
) {
    override fun toString(): String {
        // No account ID and private key
        return "CurrentAccount(publicKey='$publicKey', migration=$migration)"
    }
}

data class CurrentLease(
        val gatewayId: String = "", // wireguard public key
        val gatewayIp: String = "", // wireguard endpoint
        val gatewayPort: Int = 0, // wireguard endpoint port
        val gatewayNiceName: String = "Nice", // just alias
        val vip4: String = "", // wireguard Address
        val vip6: String = "",
        val leaseOk: Boolean = true,
        val migration: Int = 0
)

data class BlockaVpnState(
        val enabled: Boolean
)


typealias AccountId = String
typealias ActiveUntil = Date

fun ActiveUntil.expired() = this.before(Date())

internal val defaultDeviceAlias = "%s-%s".format(Build.MANUFACTURER, Build.DEVICE)
