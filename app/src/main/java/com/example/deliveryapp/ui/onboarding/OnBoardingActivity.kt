package com.example.deliveryapp.ui.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.deliveryapp.R
import com.ramotion.paperonboarding.PaperOnboardingPage
import android.graphics.Color
import com.ramotion.paperonboarding.PaperOnboardingFragment

class OnBoardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        val page1 = PaperOnboardingPage(
            getString(R.string.on_boarding_page1_title),
            getString(R.string.on_boarding_page1_description),
            Color.parseColor("#FFFFFF"), R.drawable.sonic_logo_no_bg, R.drawable.ic_marker_complete
        )
        val page2 = PaperOnboardingPage(
            getString(R.string.on_boarding_page2_title),
            getString(R.string.on_boarding_page2_description),
            Color.parseColor("#FFFFFF"), R.drawable.idelivery_completed, R.drawable.ic_marker_complete
        )
        val page3 = PaperOnboardingPage(
            getString(R.string.on_boarding_page3_title),
            getString(R.string.on_boarding_page3_description),
            Color.parseColor("#FFFFFF"), R.drawable.page3_image, R.drawable.ic_marker_complete
        )

        val pages = arrayListOf(page1,page2,page3)

        val onBoardingFragment = PaperOnboardingFragment.newInstance(pages)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.on_boarding_frame, onBoardingFragment)
        fragmentTransaction.commit()

        onBoardingFragment.setOnRightOutListener {
            val getStartedFragmentTransaction = supportFragmentManager.beginTransaction()
            val bf = GettingStartedFragment()
            getStartedFragmentTransaction.replace(R.id.on_boarding_frame, bf)
            getStartedFragmentTransaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
            getStartedFragmentTransaction.commit()
        }

    }
}
