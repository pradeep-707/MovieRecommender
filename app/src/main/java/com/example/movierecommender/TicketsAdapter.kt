package com.example.movierecommender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TicketsAdapter(ticketsList: MutableList<Ticket>): RecyclerView.Adapter<TicketsAdapter.TicketsViewHolder>() {

    private val mTicketsList = ticketsList

    class TicketsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var mMovieName: TextView = view.findViewById(R.id.ticket_movie_name)
        var mDate: TextView = view.findViewById(R.id.ticket_date)
        var mSeats: TextView = view.findViewById(R.id.ticket_seats)
        var mBookedBy: TextView = view.findViewById(R.id.ticket_booked_by)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketsViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.ticket_item, parent, false)
        return TicketsViewHolder(v)
    }

    override fun onBindViewHolder(holder: TicketsViewHolder, position: Int) {
        holder.mMovieName.text = mTicketsList[position].movieName
        holder.mDate.text = "Date: ${mTicketsList[position].date}"
        holder.mSeats.text = "Seats: ${mTicketsList[position].seats}"
        holder.mBookedBy.text = "Booked By: ${mTicketsList[position].bookedBy}"
    }

    override fun getItemCount(): Int {
        return mTicketsList.size
    }
}