package com.ringer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupClickListener
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.ringer.interactive.InitializeToken
import com.techatmosphere.expandablenavigation.model.ChildModel
import com.techatmosphere.expandablenavigation.model.HeaderModel
import com.techatmosphere.expandablenavigation.view.ExpandableNavigationListView


class EditScreenActivity : AppCompatActivity() {

    lateinit var btn_allow_setting: Button
    lateinit var switch_noti: SwitchCompat
    lateinit var switch_contact: SwitchCompat
    lateinit var switch_default_phone: SwitchCompat
    lateinit var switch_appear_on_top: SwitchCompat

    lateinit var txt_privacy1: TextView
    lateinit var txt_terms_condition: TextView

    lateinit var txt_contact_us: TextView

    lateinit var drawer_layout: DrawerLayout
    lateinit var nv: NavigationView
    private var t: ActionBarDrawerToggle? = null

    lateinit var expandable_navigation: ExpandableNavigationListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_screen)

        initialize()
        InitializeToken(
            this,
            resources.getString(R.string.ringer_user_name),
            resources.getString(R.string.ringer_password),
            resources.getString(R.string.app_name)
        )

        onClick()


    }

    private fun onClick() {
        txt_terms_condition.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.terms_url)))
            startActivity(browserIntent)
        }

        txt_privacy1.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.privacy_url)))
            startActivity(browserIntent)
        }
        txt_contact_us.setOnClickListener {

            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:") // only email apps should handle this

            intent.putExtra(Intent.EXTRA_EMAIL, "info@flashappllc.com")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }

        }
    }

    private fun initialize() {
        btn_allow_setting = findViewById(R.id.btn_allow_setting)
        switch_noti = findViewById(R.id.switch_noti)
        switch_contact = findViewById(R.id.switch_contact)
        switch_default_phone = findViewById(R.id.switch_default_phone)
        switch_appear_on_top = findViewById(R.id.switch_appear_on_top)
        txt_privacy1 = findViewById(R.id.txt_privacy1)
        txt_terms_condition = findViewById(R.id.txt_terms_condition)
        txt_contact_us = findViewById(R.id.txt_contact_us)

        drawer_layout = findViewById(R.id.drawer_layout)
        nv = findViewById(R.id.nv)

        t = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)

        drawer_layout.addDrawerListener(t!!)
        t!!.syncState()


        expandable_navigation = findViewById(R.id.expandable_navigation)


        expandable_navigation
            .init(this)
            .addHeaderModel(HeaderModel("Beranda"))
            .addHeaderModel(
                HeaderModel("Kotak Masuk")
                    .addChildModel(ChildModel("Chat"))
                    .addChildModel(ChildModel("Diskusi Produk"))
            )
            .addHeaderModel(HeaderModel("Keluar"))
            .build()
            .addOnGroupClickListener(OnGroupClickListener { parent, v, groupPosition, id ->
                expandable_navigation.setSelected(groupPosition)

                false
            })
            .addOnChildClickListener(OnChildClickListener { parent, v, groupPosition, childPosition, id ->
                expandable_navigation.setSelected(groupPosition, childPosition)

                false
            })

        expandable_navigation.setSelected(0)


        PreferencesApp().setNotification(this, false)
        PreferencesApp().setContact(this, false)
        PreferencesApp().setDefaultApp(this, false)
        PreferencesApp().setAppearOnTop(this, false)


    }
}