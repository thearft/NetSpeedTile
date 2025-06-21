package com.example.netspeedtile

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ───── views ─────
        drawer  = findViewById(R.id.drawer_layout)

        val navView = findViewById<NavigationView>(R.id.nav_view)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        // put the toolbar under AppCompat’s control
        setSupportActionBar(toolbar)

        /*  Hamburger-toggle **constructor that includes the Toolbar**.
            (The 4-arg constructor you used earlier only sets a static
             icon and does not hook into the Toolbar ⇒ no hamburger.) */
        toggle = ActionBarDrawerToggle(
            this,                   // host Activity
            drawer,                 // DrawerLayout
            toolbar,                // ← IMPORTANT!
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()          // show the icon

        navView.setNavigationItemSelectedListener(this)

        // first launch -> show Home fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
            navView.setCheckedItem(R.id.nav_home)
        }
    }

    /* optional – lets the toggle eat toolbar-item clicks
       (still harmless to keep even with the Toolbar constructor) */
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (toggle.onOptionsItemSelected(item)) true
        else super.onOptionsItemSelected(item)

    /* drawer menu taps */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
            }
            R.id.nav_settings -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SettingsFragment())   // ← now a fragment
                    .commit()
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    /* back button should close drawer first */
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
