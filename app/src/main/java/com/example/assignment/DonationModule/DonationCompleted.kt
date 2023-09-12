package com.example.assignment.DonationModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.assignment.R

class DonationCompleted : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_completed)

        val donatedAmount = intent.getSerializableExtra("donationData") as? HashMap<String, String>

        // Check if the donation data is not null
        donatedAmount?.let {
            val amount = it["amount"]


            val amountText = findViewById<TextView>(R.id.amount)
            amountText.text = amount

        }

        val viewRecordBtn:Button = findViewById(R.id.view_record)

        viewRecordBtn.setOnClickListener{

            val i=Intent(this, DonationRecord::class.java)
            startActivity(i)
        }

    }
}