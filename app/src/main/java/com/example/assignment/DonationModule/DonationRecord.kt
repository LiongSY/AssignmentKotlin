package com.example.assignment.DonationModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.DonationModule.DonationModuleAdapter.DonationAdapter
import com.example.assignment.R
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class DonationRecord : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var userArrayList: ArrayList<Donation>
    private lateinit var myAdapter: DonationAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_record)
        setSupportActionBar(findViewById(R.id.toolbar2))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userRecyclerview = findViewById(R.id.userList)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.setHasFixedSize(true)

        val draftBtn = findViewById<Button>(R.id.draft_btn)

        draftBtn.setOnClickListener(View.OnClickListener {
            val i = Intent(this, DonationDraftRecord::class.java)
            startActivity(i)
        })

        userArrayList = arrayListOf()

        myAdapter = DonationAdapter(userArrayList,userRecyclerview)

        userRecyclerview.adapter = myAdapter

        val email:String = "tengjie419@gmail.com"

        EventChangeListener(email)

    }

    private fun EventChangeListener(email: String) {
        db = FirebaseFirestore.getInstance()

        db.collection("donations")
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