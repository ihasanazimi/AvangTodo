package ir.ha.avanghtodo


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import ir.ha.avanghtodo.main.MainActivity
import ir.ha.avanghtodo.utils.Pref
import org.aviran.cookiebar2.CookieBar


class Splash : AppCompatActivity() {

    lateinit var pref: Pref
    lateinit var tvSplash: TextView
    lateinit var frameLOGO: View
    lateinit var imageViewLogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        init()


        val bio = BiometricManager.from(this)
        when (bio.canAuthenticate()) {

            // success mode
            BiometricManager.BIOMETRIC_SUCCESS -> {

                pref.setAuthSupport(true)
                Log.i("LOGTAG", "BIOMETRIC_SUCCESS: ")

                if (pref.getAuthAllow() && Build.VERSION.SDK_INT >= 23) {
                    imageViewLogo.setImageResource(R.drawable.ic_fingerprint)
                    tvSplash.text = resources.getString(R.string.auth_des)
                    val dialogInfo = bioDialogInfo()
                    biometricPromptCallBack().authenticate(dialogInfo)
                } else goToMainActivity(700)

            }


            // no hardware error
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.i("LOGTAG", "BIOMETRIC_ERROR_NO_HARDWARE: ")

                pref.setAuthAllow(false)
                pref.setAuthSupport(false)
                goToMainActivity(700)

            }


            // fingerPrint sensor not available
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.i("LOGTAG", "BIOMETRIC_ERROR_HW_UNAVAILABLE: ")
                pref.setAuthSupport(false)
                pref.setAuthSupport(false)
                goToMainActivity(700)
            }


            // not supported
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                pref.setAuthAllow(false)
                pref.setAuthSupport(false)
                Log.i("LOGTAG", "BIOMETRIC_ERROR_UNSUPPORTED: ")

                goToMainActivity(700)
            }


            // add fingerPrint From security setting please..
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.i("LOGTAG", "BIOMETRIC_ERROR_NONE_ENROLLED: ")
                pref.setAuthSupport(true)
                pref.setAuthAllow(false)
                goToMainActivity(800)
                Toast.makeText(this, "NONE_ENROLLED", Toast.LENGTH_SHORT).show()
            }


            // sensor state is not unknown
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                Log.i("LOGTAG", "BIOMETRIC_STATUS_UNKNOWN: ")
                pref.setAuthSupport(false)
                pref.setAuthAllow(false)
                goToMainActivity(800)
            }

            else -> {
                pref.setAuthAllow(false)
                pref.setAuthSupport(false)
                Toast.makeText(this@Splash, "Unknown Error!", Toast.LENGTH_LONG).show()
            }

        }

    }


    private fun init() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        tvSplash = findViewById(R.id.tvSplash)
        frameLOGO = findViewById(R.id.frameLogo)
        imageViewLogo = findViewById(R.id.logo)
        pref = Pref(this)
    }

    private fun bioDialogInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.authentication))
            .setNegativeButtonText(resources.getString(R.string.cancel))
            .setSubtitle(resources.getString(R.string.auth_subtitle))
            .build()
    }

    private fun goToMainActivity(delay: Long) {
        Handler(Looper.myLooper()!!).postDelayed({

            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }, delay)
    }

    private fun biometricPromptCallBack(): BiometricPrompt {

        return BiometricPrompt(this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {

                    imageViewLogo.setImageResource(R.drawable.icon)
                    tvSplash.text = resources.getString(R.string.app_name)

                    Toast.makeText(this@Splash, R.string.its_true, Toast.LENGTH_LONG).show()
                    goToMainActivity(500)

                    super.onAuthenticationSucceeded(result)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@Splash, R.string.not_confirm, Toast.LENGTH_LONG).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    CookieBar.build(this@Splash)
                        .setTitle(R.string.tip)
                        .setTitleColor(R.color.white)
                        .setBackgroundColor(R.color.grayDarker5)
                        .setIcon(R.drawable.ic_baseline_help_outline_24)
                        .setMessage("${(resources.getString(R.string.msggg))} \n \n" + errString)
                        .setAction(R.string.ok) {
                            CookieBar.dismiss(this@Splash)
                        }
                        .setEnableAutoDismiss(true)
                        .setDuration(11000) // 11 seconds
                        .show()
                }

            })
    }


}