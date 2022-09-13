package hr.algebra.boardgames.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hr.algebra.boardgames.R
import hr.algebra.boardgames.databinding.ActivitySplashScreenBinding
import hr.algebra.boardgames.framework.*
import hr.algebra.boardgames.services.BoardGamesService

private const val DELAY = 3000L
const val DATA_IMPORTED = "hr.algebra.boardgames.data_imported"

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimations()
        redirect()
    }

    private fun startAnimations() {
        binding.tvSplash.startAnimation(R.animator.blink)
        binding.ivSplash.startAnimation(R.animator.rotate)
    }

    private fun redirect() {

        //if data is loaded
        if (getBooleanProperty(DATA_IMPORTED)) {
            callDelayed(DELAY) { startActivity<HostActivity>() }
        } else {
            if (isOnline()) {
                Intent(this, BoardGamesService::class.java).apply {
                    BoardGamesService.enqueue(
                        this@SplashScreenActivity,
                        this
                    )
                }
            } else {
                binding.tvSplash.text = getString(R.string.no_internet)
                callDelayed(DELAY) { finish() }

            }
            // ako ima interneta, startaj service
            // else ispisi poruku i swedish()
        }
        // else start service
    }

}