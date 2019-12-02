package ui.bits.menu.vpn

import android.os.Handler
import blocka.CurrentAccount
import blocka.MAX_RETRIES
import com.github.salomonbrys.kodein.instance
import core.*
import core.bits.menu.adblocking.SlotMutex
import gs.presentation.ListViewBinder
import gs.presentation.NamedViewBinder
import gs.presentation.ViewBinder
import kotlinx.coroutines.experimental.async
import org.blokada.R
import retrofit2.Call
import retrofit2.Response

class LeasesDashboardSectionVB(
        val ktx: AndroidKontext,
        val api: Object = Object(),
        override val name: Resource = R.string.menu_vpn_leases.res()
) : ListViewBinder(), NamedViewBinder {

    private val slotMutex = SlotMutex()

    private var items = listOf<ViewBinder>(
            LabelVB(ktx, label = R.string.slot_leases_info.res())
    )

    private val request = Handler {
        async { populate() }
        true
    }

    override fun attach(view: VBListView) {
        view.enableAlternativeMode()
        view.set(items)
        request.sendEmptyMessage(0)
    }

    override fun detach(view: VBListView) {
        slotMutex.detach()
        request.removeMessages(0)
    }

    private fun populate(retry: Int = 0) {
    }
}
