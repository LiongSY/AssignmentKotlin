package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.assignment.DonationModule.DonationPage
import com.example.assignment.DonationModule.DonationReport
import com.example.assignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val donateBtn:Button = findViewById(R.id.donate_btn)
        val reportBtn:Button = findViewById(R.id.report_btn)
        donateBtn.setOnClickListener {
            val i = Intent(this, DonationPage::class.java)
            startActivity(i)
        }

        reportBtn.setOnClickListener(View.OnClickListener {
            val i = Intent(this, DonationReport::class.java)
            startActivity(i)
        })

    }
}