package blocka

import core.PaperSource
import core.Register
import core.v
import java.io.InputStreamReader
import java.util.*

fun registerPersistenceForAccount() {
    v(">> setting persistence")
    val prop = Properties()
    /*
    val reader = InputStreamReader(BlockaVpnMain::class.java.getResource("res/wireguard.conf").openStream())
    reader.use {
        prop.load(reader)
    }
    v(">> load properties", prop.getProperty("publicKey"))
    */
    prop.put("PrivateKey", "")
    prop.put("publicKey", "")
    prop.put("Endpoint", "")
    prop.put("Address", "0.0.0.0/16")

    val account = CurrentAccount(
            privateKey = prop.getProperty("PrivateKey"),
            publicKey = prop.getProperty("publicKey")
    )
    v(">> setting account", account)
    Register.sourceFor(CurrentAccount::class.java, default = account, source = PaperSource("current-account"))
    Register.set(CurrentAccount::class.java, account)

    val lease = CurrentLease(
            gatewayId = prop.getProperty("publicKey"),
            gatewayIp = prop.getProperty("Endpoint").split(":")[0],
            gatewayPort = Integer.parseInt(prop.getProperty("Endpoint").split(":")[1]),
            vip4 = prop.getProperty("Address").split("/")[0]
    )
    v(">> setting lease", lease)
    Register.sourceFor(CurrentLease::class.java, default = lease,
            source = PaperSource("current-lease"))
    Register.set(CurrentLease::class.java, lease)
}
