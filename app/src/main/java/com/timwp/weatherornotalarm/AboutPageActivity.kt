package com.timwp.weatherornotalarm

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.net.Uri


class AboutPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_page)
    }

    fun openDarkSkyWebpage(view: View) {
        openWebpage(getString(R.string.dark_sky_credit_url))
    }


    fun openLauncherIconWebpage(view: View) {
        openWebpage(getString(R.string.launcher_icon_credit_url))
    }


    fun openWeatherIconWebpage(view: View) {
        openWebpage(getString(R.string.weather_icon_credit_url))
    }

    private fun openWebpage(urlString: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.data = Uri.parse(urlString)
        startActivity(intent)
    }
}
