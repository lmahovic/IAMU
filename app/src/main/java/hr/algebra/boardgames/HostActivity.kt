package hr.algebra.boardgames

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import hr.algebra.boardgames.databinding.ActivityHostBinding

class HostActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()
    }

    private fun initNavigation() {

//        cannot use viewBinding for compatibility purposes
        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.nav_host_frag) as NavHostFragment
        val navController = navHostFrag.navController
//        --define top level fragments (the ones where the hamburger menu is shown
        appBarConfiguration =
            AppBarConfiguration(setOf(R.id.menuFavourite), binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navigationView.setupWithNavController(navController)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.host_menu, menu)
        return true
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    //    detect icon changes for actionbar according to navigation
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_frag)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuExit -> {
                exitApp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exitApp() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.exit)
            setMessage(getString(R.string.really))
            setIcon(R.drawable.exit)
            setCancelable(true)
            setNegativeButton(getString(R.string.cancel), null)
            setPositiveButton("Ok") { _, _ -> finish() }
            show()
        }
    }
}