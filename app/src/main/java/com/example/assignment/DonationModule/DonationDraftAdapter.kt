package com.example.assignment.DonationModule

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class DonationDraftAdapter(private val donationList : ArrayList<Donation>, private val recyclerView: RecyclerView) : RecyclerView.Adapter<DonationDraftAdapter.MyViewHolder>() {

    val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.donation_draft_item,
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

            val c = holder.itemView.context // Assuming itemView is a valid View
            AlertDialog.Builder(c)
                .setTitle("Delete")
                .setIcon(R.drawable.ic_warning)
                .setMessage("Are you sure delete this Information")
                .setPositiveButton("Yes"){
                        dialog,_->
                    // Call a function to delete the item from Firestore
                    deleteItemFromFirestore(userID, position, currentitem,userEmail,c) // You need to define this function
                    notifyDataSetChanged()
                    Toast.makeText(c,"Record Deleted", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                }
                .setNegativeButton("No"){
                        dialog,_->
                    dialog.dismiss()
                }
                .create()
                .show()


        }

        holder.payBtn.setOnClickListener(View.OnClickListener {

            val donationData = hashMapOf(
                "name" to currentitem.name,
                "email" to currentitem.email,
                "contact" to currentitem.contact,
                "amount" to currentitem.amount
            )

            val currentID = currentitem.id

            val i = Intent(holder.itemView.context,DonationPayment::class.java)
            i.putExtra("donationData",donationData)
            i.putExtra("draftID",currentID)
            holder.itemView.context.startActivity(i)

        })

        holder.editBtn.setOnClickListener(View.OnClickListener {
            val c = holder.itemView.context // Assuming itemView is a valid View
            val v = LayoutInflater.from(c).inflate(R.layout.edit_item, null)
            val nameEditText = v.findViewById<EditText>(R.id.nameET)
            val amountEditText = v.findViewById<EditText>(R.id.amountET)
            val contactEditText = v.findViewById<EditText>(R.id.contactET)

            nameEditText.hint = currentitem.name
            amountEditText.hint = currentitem.amount
            contactEditText.hint = currentitem.contact

            AlertDialog.Builder(c)
                .setView(v)
                .setPositiveButton("Ok") { dialog, _ ->
                    // Get the edited values from the EditText fields
                    val newName = nameEditText.text.toString()
                    val newAmount = amountEditText.text.toString()
                    val newContact = contactEditText.text.toString()
                    if (newName.isNotBlank() && newAmount != null) {
                        // Update Firestore data with the new values
                        editItemFromFirestore(userID, position, currentitem, userEmail, newName, newAmount,newContact)
                        notifyDataSetChanged()
                        Toast.makeText(c, "Donation Draft Record Information is Edited", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(c, "Please enter valid name and amount ", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        })


    }

    private fun editItemFromFirestore(
        userID: String?,
        position: Int,
        currentitem: Donation,
        userEmail: String?,
        newName: String,
        newAmount: String,
        newContact: String
    ) {

        val db = FirebaseFirestore.getInstance()
        val donationsCollection = db.collection("donations_draft")

        // Build a query to find the document with the matching 'id' field
        val query = donationsCollection
            .whereEqualTo("email", userEmail)
            .whereEqualTo("id", userID)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    // Update the document with the new data
                    document.reference.update(
                        "name", newName,
                        "amount", newAmount,
                        "contact",newContact
                    )
                }
            }
    }

    private fun deleteItemFromFirestore(
        id: String?,
        position: Int,
        currentitem: Donation,
        email: String?,
        c: Context
    ) {
        val db = FirebaseFirestore.getInstance()
        val donationsCollection = db.collection("donations_draft")

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
                    .setAction("Undo") {

                        addCurrentItemToFirestore(currentitem,c)
                    }.show()

                }
            }
            .addOnFailureListener { e ->
                // Handle any errors that occur during the query or delete operation
                Log.e("Firestore Error", "Error deleting documents: ${e.message}")
            }
    }

    private fun addCurrentItemToFirestore(currentitem: Donation, context: Context) {
        db.collection("donations_draft")
            .add(currentitem)
            .addOnFailureListener {
                // Handle the error
                Toast.makeText(context,"Error Occur when Undo.", Toast.LENGTH_LONG)
            }



    }


    override fun getItemCount(): Int {

        return donationList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val id : TextView = itemView.findViewById(R.id.idTv)
        val date : TextView = itemView.findViewById(R.id.dateText)
        val amount : TextView = itemView.findViewById(R.id.amountText)



        val dltBtn : Button = itemView.findViewById(R.id.delete_btn)
        val editBtn :Button = itemView.findViewById(R.id.edit_btn)
        val payBtn : Button = itemView.findViewById(R.id.ctnPayBtn)
    }

}

