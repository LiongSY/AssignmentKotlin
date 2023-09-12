package com.example.assignment.DonationModule

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment.R

class DonationPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_page)

        val donateBtn:Button =findViewById(R.id.donate)

        donateBtn.setOnClickListener{
        val i = Intent(this, DonationForm::class.java)
            startActivity(i)
        }

        val viewRecord = findViewById<Button>(R.id.recordBtn)
        viewRecord.setOnClickListener{
            val i = Intent(this, DonationRecord::class.java)
            startActivity(i)
        }

    }
}