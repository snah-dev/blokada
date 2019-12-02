package ui.bits.menu.vpn

import android.os.Build
import blocka.CurrentAccount
import blocka.blockaVpnMain
import com.github.salomonbrys.kodein.instance
import core.*
import gs.property.I18n
import org.blokada.R

class LeaseVB(
        val ktx: AndroidKontext,
        private val lease: Object = Object(),
        val i18n: I18n = ktx.di().instance(),
        val onRemoved: (LeaseVB) -> Unit = {},
        onTap: (SlotView) -> Unit
) : SlotVB(onTap) {

    private fun update() {
        val cfg = get(CurrentAccount::class.java)
        view?.apply {
            content = Slot.Content(
                    label =
                        i18n.getString(R.string.slot_lease_name_current, "%s-%s".format(
                                Build.MANUFACTURER, Build.DEVICE
                        )),
                    icon = ktx.ctx.getDrawable(R.drawable.ic_device),
                    description = " ",
                    action1 = null
            )

            onRemove = {
                onRemoved(this@LeaseVB)
            }
        }
    }

    override fun attach(view: SlotView) {
        view.enableAlternativeBackground()
        view.type = Slot.Type.INFO
        on(CurrentAccount::class.java, this::update)
        update()
    }

    override fun detach(view: SlotView) {
        cancel(CurrentAccount::class.java, this::update)
    }
}
