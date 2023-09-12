package com.example.assignment.DonationModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.assignment.R
import com.example.assignment.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date



class DonationPayment : AppCompatActivity() {
    private lateinit var payBtn: Button
    private lateinit var payLater: Button
    private lateinit var payMethod: Spinner
    private val db = FirebaseFirestore.getInstance()
    private val calendar: Calendar = Calendar.getInstance()
    private val currentDate: Date = calendar.time
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val formattedDate: String = dateFormat.format(currentDate)
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_payment)

        val donationData = intent.getSerializableExtra("donationData") as? HashMap<String, String>
        payMethod = findViewById(R.id.method_spn)

        val method = payMethod.selectedItem.toString()
        // Check if the donation data is not null
        val draftID = intent.getStringExtra("draftID")

        donationData?.let {
            val name = it["name"]
            val email = it["email"]
            val contact = it["contact"]
            val amount = it["amount"]

            val amountText = findViewById<TextView>(R.id.amountText)
            amountText.text = amount

            val donationData = hashMapOf(
                "id" to generateRandomID(),
                "name" to name,
                "email" to email,
                "contact" to contact,
                "amount" to amount,
                "method" to method,
                "date" to formattedDate
            )


            payBtn = findViewById(R.id.payBtn)
            payBtn.setOnClickListener(View.OnClickListener {
                // Add the donation data to the Firestore collection
                db.collection("donations")
                    .add(donationData)
                    .addOnSuccessListener {
                        // Donation data added successfully

                        //check id value
                        if(draftID != null) {
                            //Delete the record if the draft record is exist
                            val dbCollection = db.collection("donations_draft")
                            val query = dbCollection.whereEqualTo("id", draftID)

                            query.get()
                                .addOnSuccessListener { querySnapshot ->
                                    for (document in querySnapshot.documents) {
                                        // Delete each document that matches the query
                                        document.reference.delete()
                                        // Add your logic here after deletion if needed
                                    }
                                }.addOnFailureListener { e ->
                                    // Handle any errors that occur during the query or delete operation
                                    Log.e(
                                        "Firestore Error",
                                        "Error deleting documents: ${e.message}"
                                    )
                                }
                        }else{
                            Toast.makeText(this, "ID is null",Toast.LENGTH_LONG)
                        }
                        showToast("Donation submitted successfully!")
                        val intent = Intent(this, DonationCompleted::class.java)
                        intent.putExtra("donationData", donationData)
                        startActivity(intent)


                    }
                    .addOnFailureListener {
                        // Handle the error

                        showToast("Error submitting donation. Please try again.")
                    }

            })

            payLater =findViewById(R.id.payLater_btn)
            payLater.setOnClickListener(View.OnClickListener{
                // Add the donation data to the Firestore collection
                db.collection("donations_draft")
                    .add(donationData)
                    .addOnSuccessListener {
                        // Donation data added successfully
                        showToast("Donation has been save into draft!")
//                        val intent = Intent(this, DonationDraft::class.java)
//                        startActivity(intent)

                        val recipientEmail = "lawtj-wm20@student.tarc.edu.my"
                        val emailSubject = "Payment Confirmation"
                        val emailMessage =
                            "Thank you for your payment. Your payment of $amount has been confirmed."

                        /*             runBlocking {
                                         try {
                                             sendConfirmationEmail(recipientEmail, emailSubject, emailMessage)
                                         } catch (e: Exception) {
                                             e.printStackTrace()
                                         }
                                     }*/


                    }
                    .addOnFailureListener {
                        // Handle the error

                        showToast("Error submitting donation. Please try again.")
                    }

            })




        }
    }

    private fun generateRandomID(): String {
        // Reference to a Firestore collection, but without specifying a document ID
        val collectionReference = db.collection("donations")

        // Use the document() method to generate a new unique ID
        val newDocumentReference = collectionReference.document()

        // Retrieve the newly generated document ID as a string
        return newDocumentReference.id
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }





}
