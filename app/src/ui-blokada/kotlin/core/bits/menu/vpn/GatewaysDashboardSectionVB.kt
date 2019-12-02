package core.bits.menu.vpn

import android.content.Intent
import android.os.Handler
import blocka.MAX_RETRIES
import com.github.salomonbrys.kodein.instance
import core.*
import core.bits.menu.SimpleMenuItemVB
import core.bits.menu.adblocking.SlotMutex
import gs.presentation.ListViewBinder
import gs.presentation.NamedViewBinder
import gs.presentation.ViewBinder
import kotlinx.coroutines.experimental.async
import org.blokada.R
import retrofit2.Call
import retrofit2.Response
import ui.StaticUrlWebActivity

class GatewaysDashboardSectionVB(
        val ktx: AndroidKontext,
        val api: Object = Object(),
        override val name: Resource = R.string.menu_vpn_gateways.res()
) : ListViewBinder(), NamedViewBinder {

    private val slotMutex = SlotMutex()

    private var items = listOf<ViewBinder>(
            LabelVB(ktx, label = R.string.menu_vpn_gateways_label.res())
    )

    private val gatewaysRequest = Handler {
        async { populateGateways() }
        true
    }

    private fun update() {
        gatewaysRequest.removeMessages(0)
        gatewaysRequest.sendEmptyMessage(0)
    }

    override fun attach(view: VBListView) {
        view.enableAlternativeMode()
        view.set(items)
        update()
    }

    override fun detach(view: VBListView) {
        slotMutex.detach()
        gatewaysRequest.removeMessages(0)
    }

    private fun populateGateways(retry: Int = 0) {
    }
}

fun createPartnerGatewaysMenuItem(ktx: AndroidKontext): NamedViewBinder {
    val page = ktx.di().instance<Pages>().vpn_partner
    return SimpleMenuItemVB(ktx,
            label = R.string.slot_gateway_info_partner.res(),
            icon = R.drawable.ic_info.res(),
            arrow = false,
            action = {
                modalManager.openModal()
                ktx.ctx.startActivity(Intent(ktx.ctx, StaticUrlWebActivity::class.java).apply {
                    putExtra(WebViewActivity.EXTRA_URL, page().toExternalForm())
                })
            }
    )
}
