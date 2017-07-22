package org.blokada.ui.app.android

import com.github.salomonbrys.kodein.instance
import org.blokada.app.Events
import org.blokada.framework.IJournal
import org.blokada.framework.android.di
import org.blokada.ui.app.Dash
import org.blokada.ui.app.InfoType
import org.blokada.ui.app.Info
import org.blokada.ui.app.UiState

class ADashActor(
        initialDash: Dash,
        private val v: ADashView,
        private val ui: UiState,
        private val contentActor: ContentActor
) {
    private val j by lazy { v.context.di().instance<IJournal>() }

    var dash = initialDash
        set(value) {
            field = value
            dash.onUpdate.add { update() }
            update()
        }

    init {
        update()
        v.onChecked = { checked -> dash.checked = checked }
        v.onClick = {
            if (dash.onClick?.invoke(v) ?: true) defaultClick()
            j.event(Events.Companion.CLICK_DASH(dash.id))
        }
        v.onLongClick = {
            ui.infoQueue %= ui.infoQueue() + Info(InfoType.CUSTOM, dash.description)
            j.event(Events.Companion.CLICK_LONG_DASH(dash.id))
        }

        dash.onUpdate.add { update() }
    }

    private fun defaultClick() {
        if (ui.editUi()) {
            dash.active = !dash.active
            if (dash.active) j.event(Events.Companion.SHOW_DASH(dash.id))
            else j.event(Events.Companion.HIDE_DASH(dash.id))
        } else {
            contentActor.reveal(dash,
                    x = v.x.toInt() + v.measuredWidth / 2,
                    y = v.y.toInt() + v.measuredHeight / 2
            )
        }
    }

    private fun update() {
        if (dash.isSwitch) {
            v.checked = dash.checked
        } else {
            if (dash.icon is Int) {
                v.iconRes = dash.icon as Int
            }
        }

        v.text = dash.text
        v.active = dash.active
        v.emphasized = dash.emphasized
    }

}