package org.blokada.ui.app.android

import android.app.AlertDialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.WindowManager
import com.github.salomonbrys.kodein.instance
import nl.komponents.kovenant.task
import nl.komponents.kovenant.ui.failUi
import nl.komponents.kovenant.ui.successUi
import org.blokada.framework.android.AActivityContext
import org.blokada.app.android.FilterSourceLink
import org.blokada.app.android.FilterSourceUri
import org.blokada.app.Filter
import org.blokada.lib.ui.R
import org.blokada.framework.nullIfEmpty
import org.blokada.app.FilterSourceSingle
import org.blokada.app.IFilterSource
import org.blokada.app.LocalisedFilter
import org.blokada.framework.android.di

/**
 * TODO: This poor thing needs love (like me)
 */
class AFilterAddDialog(
        private val ctx: Context,
        var sourceProvider: (String) -> IFilterSource
) {
    var onSave = { filter: Filter -> }

    private val activity by lazy { ctx.di().instance<AActivityContext<MainActivity>>().getActivity() }
    private val themedContext by lazy { ContextThemeWrapper(ctx, R.style.BlokadaColors_Dialog) }
    private val view = LayoutInflater.from(themedContext)
            .inflate(R.layout.view_filtersadd, null, false) as AFiltersAddView
    private val dialog: AlertDialog

    init {
        val d = AlertDialog.Builder(activity)
        d.setView(view)
        d.setPositiveButton(R.string.filter_edit_save, { dia, int -> })
        d.setNegativeButton(R.string.filter_edit_cancel, { dia, int -> })
        dialog = d.create()
    }

    fun show(filter: Filter?) {
        view.forceType = when {
            filter?.source is FilterSourceLink -> AFiltersAddView.Tab.LINK
            filter?.source is FilterSourceUri -> AFiltersAddView.Tab.FILE
            filter?.source is FilterSourceSingle -> AFiltersAddView.Tab.SINGLE
            else -> null
        }

        if (filter != null) when (view.forceType) {
            AFiltersAddView.Tab.SINGLE -> {
                view.singleView.text = filter.source.toUserInput()
                view.singleView.comment = filter.localised?.comment ?: ""
            }
            AFiltersAddView.Tab.LINK -> {
                view.linkView.text = filter.source.toUserInput()
                view.linkView.correct = true
                view.linkView.comment = filter.localised?.comment ?: ""
                view.linkView.filters = filter.hosts
            }
            AFiltersAddView.Tab.FILE -> {
                val source = filter.source as FilterSourceUri
                view.fileView.uri = source.source
                view.fileView.correct = true
                view.fileView.comment = filter.localised?.comment ?: ""
                view.fileView.filters = filter.hosts
            }
        }

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { handleSave(filter) }
        dialog.window.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        )
    }

    private fun handleSave(filter: Filter?) {
        when (view.currentTab) {
            AFiltersAddView.Tab.SINGLE -> {
                if (!view.singleView.correct) view.singleView.showError = true
                else {
                    dialog.dismiss()
                    onSave(Filter(
                            id = filter?.id ?: view.singleView.text,
                            source = FilterSourceSingle(view.singleView.text),
                            active = true,
                            localised = LocalisedFilter(view.singleView.text,
                            view.singleView.comment.nullIfEmpty())
                    ))
                }
            }
            AFiltersAddView.Tab.LINK -> {
                if (!view.linkView.correct) view.linkView.showError = true
                else {
                    task {
                        val source = sourceProvider("link")
                        if (!source.fromUserInput(view.linkView.text))
                            throw Exception("invalid source")
                        val hosts = source.fetch()
                        if (hosts.isEmpty()) throw Exception("source with no hosts")
                        source to hosts
                    } successUi {
                        dialog.dismiss()
                        onSave(Filter(
                                id = filter?.id ?: it.first.serialize(),
                                source = it.first,
                                hosts = it.second,
                                active = true,
                                localised = LocalisedFilter(sourceToName(ctx, it.first),
                                view.linkView.comment.nullIfEmpty())
                        ))
                        view.linkView.correct = true
                    } failUi {
                        view.linkView.correct = false
                        view.linkView.showError = true
                    }
                }
            }
            AFiltersAddView.Tab.FILE -> {
                if (!view.fileView.correct) view.fileView.showError = true
                else {
                    task {
                        val source = sourceProvider("file") as FilterSourceUri
                        source.source = view.fileView.uri
                        source.flags = view.fileView.flags
                        val hosts = source.fetch()
                        if (hosts.isEmpty()) throw Exception("source with no hosts")
                        source to hosts
                    } successUi {
                        dialog.dismiss()
                        onSave(Filter(
                                id = filter?.id ?: it.first.serialize(),
                                source = it.first,
                                hosts = it.second,
                                active = true,
                                localised = LocalisedFilter(sourceToName(ctx, it.first),
                                view.fileView.comment.nullIfEmpty())
                        ))
                        view.fileView.correct = true
                    } failUi {
                        view.fileView.correct = false
                        view.fileView.showError = true
                    }
                }
            }
        }
    }

}

