package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class DonationDraftRecord : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var userArrayList : ArrayList<Donation>
    private lateinit var myAdapter: DonationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_draft_record)

        userRecyclerview = findViewById(R.id.userList)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.setHasFixedSize(true)

        userArrayList = arrayListOf()

        myAdapter = DonationAdapter(userArrayList,userRecyclerview)

        userRecyclerview.adapter = myAdapter

        val email:String = "tengjie419@gmail.com"

        EventChangeListener(email)


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

                    userArrayList.clear() // Clear the previous data

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            userArrayList.add(dc.document.toObject(Donation::class.java))
                        }
                    }

                    myAdapter.notifyDataSetChanged()
                }
            })

    }
}