package com.synergos.partner.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.synergos.partner.R
import com.synergos.partner.databinding.ActivityMainBinding
import com.synergos.partner.ui.dashboard.Dashboard
import com.synergos.partner.ui.home.Home
import com.synergos.partner.ui.setting.Setting
import com.synergos.partner.utils.CommonMethods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private var temp = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }



        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // This is Android 15 or above

        } else {
            // This is Android 14 or below
        }



        if (intent.getStringExtra("bottom_nav_pos")!= null) {
            val pos = intent.getStringExtra("bottom_nav_pos").toString()
            setFragment(pos.toInt())
        } else {
            setFragment(1)
        }


    }



    fun onClick(v: View?) {
        when (v?.id) {
            R.id.menu_home -> {
                if (temp != 1) {
                    setFragment(1)
                    CommonMethods.vibratePhone(this)
                }
            }

            R.id.menu_cart -> {
                    if (temp != 2) {
                        setFragment(2)
                        CommonMethods.vibratePhone(this)
                    }
            }

            R.id.menu_setting -> {
                    if (temp != 3) {
                        setFragment(3)
                        CommonMethods.vibratePhone(this)
                    }
            }

        }
    }


    @SuppressLint("NewApi")
    fun setFragment(pos: Int) {
        temp = pos

        binding.ivHome.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.medium_gray, null))

        binding.ivSetting.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.medium_gray, null))

        binding.ivCart.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.medium_gray, null))

        when (pos) {
            1 -> {
                binding.ivHome.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.color_primary, null))
                homeActivate()
            }

            2 -> {
                binding.ivCart.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.color_primary, null))
                notificationActivate()
            }

            3 -> {
                binding.ivSetting.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.color_primary, null))
                settingActivate()
            }
        }
    }



    private fun switchFragment(tag: String, fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        val current = supportFragmentManager.primaryNavigationFragment
        if (current != null) transaction.hide(current)

        var target = supportFragmentManager.findFragmentByTag(tag)
        if (target == null) {
            target = fragment
            transaction.add(R.id.fragment_container, target, tag)
        } else {
            transaction.show(target)
        }

        transaction.setPrimaryNavigationFragment(target)
        transaction.commit()
    }

    fun homeActivate(){
        switchFragment("Home", Home())
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.color_primary))
        binding.tvSetting.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        binding.tvCart.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
    }

    fun settingActivate(){
        switchFragment("Setting", Setting())
        binding.tvSetting.setTextColor(ContextCompat.getColor(this, R.color.color_primary))
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        binding.tvCart.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
    }

    fun notificationActivate(){
        switchFragment("Notification", Dashboard())
        binding.tvCart.setTextColor(ContextCompat.getColor(this, R.color.color_primary))
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        binding.tvSetting.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        CommonMethods.handleOverlayPermissionResult(this, requestCode)
    }

}