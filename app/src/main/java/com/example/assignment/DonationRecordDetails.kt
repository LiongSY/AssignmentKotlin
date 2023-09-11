package com.example.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.io.Serializable

class DonationRecordDetails : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_record_details)

        val donationData = intent.getSerializableExtra("id")

        getRecordDetails(donationData as String?)


    }

    private fun getRecordDetails(id: String?) {
        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()

// Reference a Firestore collection
        val collectionRef = db.collection("donations")

// Create a query (optional)
        val query = collectionRef.whereEqualTo("id", id)

// Retrieve data using a listener (real-time updates)
        query.addSnapshotListener { querySnapshot, firestoreException ->
            if (firestoreException != null) {
                // Handle the error
                return@addSnapshotListener
            }

            // Process the querySnapshot to access the documents and data
            for (document in querySnapshot!!) {
                val data = document.data // Access document data as a map
                val name = data["name"] // Access a specific field
                val email = data["email"]
                val amount = data["amount"]
                val method = data["method"]
                val date = data["date"]

                // Perform your desired actions with the data here

                val nameTv = findViewById<TextView>(R.id.name_tv)
                val emailTv = findViewById<TextView>(R.id.email_tv)
                val amountTv = findViewById<TextView>(R.id.amount_tv)
                val dateTv = findViewById<TextView>(R.id.date_tv)
                val methodTv = findViewById<TextView>(R.id.method_tv)

                nameTv.text = name.toString()
                emailTv.text = email.toString()
                amountTv.text = amount.toString()
                methodTv.text = method.toString()
                dateTv.text = date.toString()

            }
        }

    }

}