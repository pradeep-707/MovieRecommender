package com.example.movierecommender

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserTicketsFragment: Fragment() {

    lateinit var mTicketsRecyclerView: RecyclerView
    lateinit var mTicketsAdapter: TicketsAdapter
    lateinit var mTicketsLayoutManager: LinearLayoutManager
    lateinit var mTicketsList: MutableList<Ticket>

    var mUsername = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mUsername = arguments!!.getString("username")!!
        return inflater.inflate(R.layout.fragment_users_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTicketsRecyclerView = view.findViewById(R.id.tickets_recycler_view)
        createRecyclerView()
        getTickets()
    }

    private fun getTickets() {
        val reference = FirebaseDatabase.getInstance().reference.child("users").
                child(mUsername).child("tickets")
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(ds: DataSnapshot) {
                if (!ds.exists()) {
                    return
                }
                for (ticketDs in ds.children) {
                    val ticket = ticketDs.getValue(Ticket::class.java)!!
                    mTicketsList.add(ticket)
                }
                mTicketsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun createRecyclerView() {
        mTicketsList = mutableListOf()
        mTicketsRecyclerView = view!!.findViewById(R.id.tickets_recycler_view)
        mTicketsAdapter = TicketsAdapter(mTicketsList)
        mTicketsLayoutManager = LinearLayoutManager(activity!!)
        mTicketsRecyclerView.adapter = mTicketsAdapter
        mTicketsRecyclerView.layoutManager = mTicketsLayoutManager
    }
}