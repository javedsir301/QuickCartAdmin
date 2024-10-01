package com.example.quickcartadmin.activity

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.quickcartadmin.R
import com.example.quickcartadmin.databinding.ActivityAdminMainBinding

@Suppress("DEPRECATION")
class AdminMainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAdminMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarColor()
        NavigationUI.setupWithNavController(binding.bottomMenu,Navigation.findNavController(this,R.id.fragmentContainerView2))
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors = ContextCompat.getColor(context,R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}