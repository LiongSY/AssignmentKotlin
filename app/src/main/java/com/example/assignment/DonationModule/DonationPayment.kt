package com.example.assignment.DonationModule

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationId = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_payment)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
                        createNotificationChannel(amount)
                        sendNotification(amount)
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

    private fun createNotificationChannel(amount: String?) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Donation Payment Confirmation"
            val descriptionText = "Donate Successfully : Amount : $amount has been received by us"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(amount: String?) {
        val intent  = Intent(this,DonationRecord::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent:PendingIntent = PendingIntent.getActivity(this,0, intent,0)
        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.notification_icon)
        val bitmapLargeIcon = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ic_warning)

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Payment Confirmation")
            .setContentText("Donate Successfully : Amount : $amount ")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("has been received by us"))
            .setLargeIcon(bitmapLargeIcon)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)){
            if (ActivityCompat.checkSelfPermission(this@DonationPayment,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(notificationId, builder.build())
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle the Up button click here
                onBackPressed() // or navigate to the parent activity
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
