package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.assignment.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class DonationForm : AppCompatActivity() {


    private lateinit var name: EditText
    private lateinit var contact: EditText
    private lateinit var amount: EditText
    private lateinit var donateBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_donation_form)

        name = findViewById(R.id.name_field)
        contact = findViewById(R.id.contact_field)
        amount = findViewById(R.id.amount_field)

        val donateBtn = findViewById<Button>(R.id.donate_button)

       donateBtn.setOnClickListener(View.OnClickListener {
            val sName = name.text.toString().trim()
            //after done change to get email from session
            val sEmail = "tengjie419@gmail.com"
            val sContact = contact.text.toString().trim()
            val sAmount = amount.text.toString().trim()

            val donationData = hashMapOf(
                "name" to sName,
                "email" to sEmail,
                "contact" to sContact,
                "amount" to sAmount
            )

            val intent = Intent(this, DonationPayment::class.java)
            intent.putExtra("donationData", donationData)
            startActivity(intent)

        })
    }
}