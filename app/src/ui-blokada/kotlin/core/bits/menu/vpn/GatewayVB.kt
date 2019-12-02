package core.bits.menu.vpn

import blocka.CurrentAccount
import blocka.CurrentLease
import com.github.salomonbrys.kodein.instance
import core.*
import core.bits.accountInactive
import gs.property.I18n
import org.blokada.R
import tunnel.showSnack
import java.util.*

class GatewayVB(
        private val ktx: AndroidKontext,
        private val gateway: Object,
        private val i18n: I18n = ktx.di().instance(),
        private val modal: ModalManager = modalManager,
        onTap: (SlotView) -> Unit
) : SlotVB(onTap) {

    private fun update() {
        val cfg = get(CurrentLease::class.java)
        val account = get(CurrentAccount::class.java)
    }

    private fun getLoad(usage: Int): String {
        return i18n.getString(when (usage) {
            in 0..50 -> R.string.slot_gateway_load_low
            else -> R.string.slot_gateway_load_high
        })
    }

    override fun attach(view: SlotView) {
        view.enableAlternativeBackground()
        view.type = Slot.Type.INFO
        on(CurrentLease::class.java, this::update)
        update()
    }

    override fun detach(view: SlotView) {
        cancel(CurrentLease::class.java, this::update)
    }
}
