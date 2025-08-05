package com.synergos.partner.utils

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.synergos.partner.R

fun setupToolbar(view: View, title: String, activity: AppCompatActivity) {
    val ivBack = view.findViewById<MaterialCardView>(R.id.ivBack)
    val tvTitle = view.findViewById<TextView>(R.id.tvTitle)

    tvTitle.text = title
    ivBack.setOnClickListener {
        activity.onBackPressedDispatcher.onBackPressed()
    }
}