package com.ringer.interactive.ui.base

import android.annotation.SuppressLint
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels

import com.ringer.interactive.adapter.MenuAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.menu.*
import javax.inject.Inject

@SuppressLint("RestrictedApi")
@AndroidEntryPoint
open class BaseMenuFragment @Inject constructor() : BaseFragment<BaseViewState>() {
    override val contentView by lazy { binding.root }
    override val viewState: BaseViewState by viewModels()

    private var _onMenuItemClickListener: (MenuItem) -> Unit = {}
    private val binding by lazy { com.ringer.interactive.databinding.MenuBinding.inflate(layoutInflater) }

    protected open val title by lazy { getString(args.getInt(ARG_TITLE_RES)) }
    protected open val subtitle by lazy {
        val subtitleRes = args.getInt(ARG_SUBTITLE_RES, -1)
        if (subtitleRes == -1) {
            null
        } else {
            getString(subtitleRes)
        }
    }

    @Inject lateinit var adapter: MenuAdapter


    override fun onSetup() {
        adapter.setOnItemClickListener {
            _onMenuItemClickListener.invoke(it)
            onMenuItemClick(it)
        }

        binding.apply {
            menuTitle.text = title
            menuSubtitle.text = subtitle
            menu_Data.adapter = adapter
            menuSubtitle.isVisible = subtitle != null
        }
    }


    private fun setMenu(menu: MenuBuilder) {
        menu.visibleItems.toList().let { adapter.items = it }
    }

    fun setMenuResList(menuResList: List<Int>) {
        setMenu(MenuBuilder(baseActivity).apply {
            menuResList.forEach { MenuInflater(baseActivity).inflate(it, this) }
        })
    }

    fun setOnMenuItemClickListener(onMenuItemClickListener: (menuItem: MenuItem) -> Unit) {
        _onMenuItemClickListener = onMenuItemClickListener
    }

    protected open fun onMenuItemClick(menuItem: MenuItem) {}


    companion object {
        const val ARG_TITLE_RES = "title_res"
        const val ARG_SUBTITLE_RES = "subtitle_res"
    }
}