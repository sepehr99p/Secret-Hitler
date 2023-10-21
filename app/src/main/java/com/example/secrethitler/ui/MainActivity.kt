package com.example.secrethitler.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.codelab.android.datastore.PlayerPreferences
import com.example.secrethitler.R
import com.example.secrethitler.data.PlayerPreferencesSerializer
import com.example.secrethitler.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

val Context.playersPreferencesStore: DataStore<PlayerPreferences> by dataStore(
    fileName = "players.pb",
    serializer = PlayerPreferencesSerializer
)

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var analytics: FirebaseAnalytics

    companion object {
        var rolesAreWatched = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        actionBar?.hide()
        supportActionBar?.hide()
        setContentView(binding.root)

        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics
        analytics.logEvent("LaunchApp",null)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


}