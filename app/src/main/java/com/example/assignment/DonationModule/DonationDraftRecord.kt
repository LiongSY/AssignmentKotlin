package com.example.assignment.DonationModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class DonationDraftRecord : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var donationRecyclerview : RecyclerView
    private lateinit var donationArrayList : ArrayList<Donation>
    private lateinit var myAdapter: DonationDraftAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_draft_record)


        donationRecyclerview = findViewById(R.id.userList)
        donationRecyclerview.layoutManager = LinearLayoutManager(this)
        donationRecyclerview.setHasFixedSize(true)

        donationArrayList = arrayListOf()

        myAdapter = DonationDraftAdapter(donationArrayList,donationRecyclerview)
        val email:String = "tengjie419@gmail.com"

        EventChangeListener(email)
        donationRecyclerview.adapter = myAdapter

    }


    private fun EventChangeListener(email: String) {
        db = FirebaseFirestore.getInstance()

        db.collection("donations_draft")
            .whereEqualTo("email", email) // Change "emaill" to match your field name
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    donationArrayList.clear() // Clear the previous data

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            donationArrayList.add(dc.document.toObject(Donation::class.java))
                        }
                    }

                    myAdapter.notifyDataSetChanged()
                }
            })

    }
}