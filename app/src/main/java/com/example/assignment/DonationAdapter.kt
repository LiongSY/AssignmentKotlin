package com.example.assignment

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class DonationAdapter(private val donationList : ArrayList<Donation>, private val recyclerView: RecyclerView) : RecyclerView.Adapter<DonationAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.donation_item,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = donationList[position]

        holder.id.text = currentitem.id
        holder.date.text = currentitem.date
        holder.amount.text = currentitem.amount

        val userID = currentitem.id
        val userEmail = currentitem.email
        // Add a click listener to the delete button
        holder.dltBtn.setOnClickListener {
            // Call a function to delete the item from Firestore
            deleteItemFromFirestore(userID, position, currentitem,userEmail) // You need to define this function
        }

        holder.record.setOnClickListener(View.OnClickListener {
            val intent = Intent(holder.itemView.context, DonationRecordDetails::class.java)
            intent.putExtra("id",userID)
            holder.itemView.context.startActivity(intent)
        })


    }

    private fun deleteItemFromFirestore(id: String?,position: Int, currentitem: Donation,email:String?) {
        val db = FirebaseFirestore.getInstance()
        val donationsCollection = db.collection("donations")

        // Build a query to find the document with the matching 'id' field
        val query = donationsCollection
            .whereEqualTo("email",email)
            .whereEqualTo("id",id)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    // Delete each document that matches the query
                    document.reference.delete()
                    Snackbar.make(recyclerView, "Record Deleted", Snackbar.LENGTH_SHORT)
//                        .setAction("Undo") {
//                            // Code to handle the "Undo" action goes here
//                            // Restore the deleted item or take other appropriate action
//
//                            // For example, you can re-add the item to the list at its original position
//                            donationList.add(position, currentitem)
//                            notifyItemInserted(position)
//                        }
//                        .addCallback(object : Snackbar.Callback() {
//                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
//                                if (event != DISMISS_EVENT_ACTION) {
//                                    // This code will execute when the Snackbar is dismissed without using the "Undo" action
//                                    // Implement any cleanup or permanent deletion logic here
//                                }
//                            }
//                        })
//                        .show()

                }
            }
            .addOnFailureListener { e ->
                // Handle any errors that occur during the query or delete operation
                Log.e("Firestore Error", "Error deleting documents: ${e.message}")
            }
    }


    override fun getItemCount(): Int {

        return donationList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val id : TextView = itemView.findViewById(R.id.idTv)
        val date : TextView = itemView.findViewById(R.id.dateText)
        val amount : TextView = itemView.findViewById(R.id.amountText)

        val dltBtn : Button = itemView.findViewById(R.id.dlt_btn)
        val record : CardView = itemView.findViewById(R.id.record_cardView)

    }

}
