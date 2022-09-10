package hr.algebra.boardgames

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hr.algebra.boardgames.databinding.ActivitySplashScreenBinding
import hr.algebra.boardgames.framework.*

private const val DELAY = 3000L
const val DATA_IMPORTED = "hr.algebra.nasa.data_imported"
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
                Intent(this, NasaService::class.java).apply {
                    NasaService.enqueue(
                        this@SplashScreenActivity,
                        this)
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