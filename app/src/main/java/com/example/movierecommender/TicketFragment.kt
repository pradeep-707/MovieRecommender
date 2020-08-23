package com.example.movierecommender

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_ticket.*
import java.text.DateFormat
import java.util.*

class TicketFragment : Fragment() {

    lateinit var mParentLinearLayout: LinearLayout
    lateinit var mRowsLinearLayout: Array<LinearLayout?>
    lateinit var mSeats: Array<Array<ImageView?>>

    var mUsername = ""
    var mMovieName = ""
    private val mRowsNumber = 5
    private val mColumnsNumber = 10

    private var mSelectedSeats = mutableListOf<Pair<Int, Int>>()

    lateinit var mSeatInformation: MutableList<MutableList<String>>
    lateinit var mConfirmSeatsButton: Button

    var selectedDate = ""
    var possibleDates = mutableListOf<String>()
    lateinit var mDateButton1: RadioButton
    lateinit var mDateButton2: RadioButton
    lateinit var mDateButton3: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUsername = arguments!!.getString("username")!!
        mMovieName = arguments!!.getString("movieName")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ticket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mConfirmSeatsButton = view.findViewById(R.id.confirm_seats_button)
        mConfirmSeatsButton.setOnClickListener { bookSeats() }
        initializeDateButton()
        createGrid()
        getSeatBookedInfo()
    }

    private fun onClickRadio(view: View) {
        mSelectedSeats.clear()
        selectedDate = when (view) {
            mDateButton1 -> {
                possibleDates[0]
            }
            mDateButton2 -> {
                possibleDates[1]
            }
            else -> {
                possibleDates[2]
            }
        }
        getSeatBookedInfo()
    }

    private fun initializeDateButton() {
        calculateDates()
        mDateButton1 = view!!.findViewById(R.id.radio_date_one)
        mDateButton2 = view!!.findViewById(R.id.radio_date_two)
        mDateButton3 = view!!.findViewById(R.id.radio_date_three)
        mDateButton1.text = possibleDates[0]
        mDateButton2.text = possibleDates[1]
        mDateButton3.text = possibleDates[2]
        mDateButton1.setOnClickListener {
            onClickRadio(it)
        }
        mDateButton2.setOnClickListener {
            onClickRadio(it)
        }
        mDateButton3.setOnClickListener {
            onClickRadio(it)
        }
    }

    private fun calculateDates() {
        val calendar: Calendar = Calendar.getInstance()
        for (i in 0..2) {
            calendar.add(Calendar.DATE, 1)
            val date = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.time)
            possibleDates.add(date)
        }
        selectedDate = possibleDates[0]
    }

    private fun bookSeats() {
        if (mSelectedSeats.size == 0) {
            Toast.makeText(activity!!, "No seats selected", Toast.LENGTH_SHORT).show()
            return
        }
        val reference = FirebaseDatabase.getInstance().reference.child("movies")
            .child(mMovieName).child("tickets").child(selectedDate)
        for (pair in mSelectedSeats) {
            reference.child(pair.first.toString()).child(pair.second.toString()).setValue("booked")
        }


        val userTicketReference = FirebaseDatabase.getInstance().reference.child("users")
            .child(mUsername).child("tickets").push()
        var seats = ""
        for (pair in mSelectedSeats) {
            seats += "${pair.first}${pair.second}, "
        }
        val newTicket = Ticket(
            mMovieName,
            selectedDate,
            seats,
            mUsername
        )
        userTicketReference.setValue(newTicket).addOnCompleteListener {
            Toast.makeText(activity!!, "Ticket(s) booked successfully!", Toast.LENGTH_SHORT).show()
            activity!!.onBackPressed()
        }
    }

    private fun onClickSeat(view: View) {
        for (i in 0 until mRowsNumber) {
            for (j in 0 until mColumnsNumber) {
                if (view == mSeats[i][j]) {
                    when (mSeatInformation[i][j]) {
                        "unbooked" -> {
                            mSeatInformation[i][j] = "selected"
                            mSeats[i][j]?.setImageResource(R.drawable.ic_seat_green)
                            mSelectedSeats.add(Pair(i, j))
                        }
                        "booked" -> {
                            return
                        }
                        "selected" -> {
                            mSeatInformation[i][j] = "unbooked"
                            mSeats[i][j]?.setImageResource(R.drawable.ic_seat_white)
                            mSelectedSeats.remove(Pair(i, j))
                        }
                    }
                }
            }
        }
    }

    private fun setSeatBookedInfo() {
        for (i in 0 until mRowsNumber) {
            for (j in 0 until mColumnsNumber) {
                mSeats[i][j]?.setImageResource(when(mSeatInformation[i][j]){
                    "unbooked" -> R.drawable.ic_seat_white
                    "booked" -> R.drawable.ic_seat_red
                    else -> R.drawable.ic_seat_green
                })
            }
        }
    }

    private fun getSeatBookedInfo() {
        val reference = FirebaseDatabase.getInstance().reference.child("movies")
            .child(mMovieName).child("tickets").child(selectedDate)
        val valueEventListener = object: ValueEventListener{
            override fun onDataChange(ds: DataSnapshot) {
                val seatInformation = MutableList(mRowsNumber) {
                    MutableList(mColumnsNumber) {
                        "unbooked"
                    }
                }
                if (!ds.exists()) {
                    reference.setValue(seatInformation)
                } else {
                    for (rowsDs in ds.children.withIndex()) {
                        for (seatDs in rowsDs.value.children.withIndex()) {
                            seatInformation[rowsDs.index][seatDs.index] = seatDs.value.getValue(String::class.java)!!
                        }
                    }
                }
                mSeatInformation = seatInformation
                setSeatBookedInfo()
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }
        reference.addListenerForSingleValueEvent(valueEventListener)
    }

    private fun createGrid() {

        mParentLinearLayout = view!!.findViewById(R.id.tickets_parent_linear_layout)
        mRowsLinearLayout = arrayOfNulls(mRowsNumber)
        mSeats = Array(mRowsNumber) {
            arrayOfNulls<ImageView>(
                mColumnsNumber
            )
        }

        val rowParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        rowParams.setMargins(0, ROW_SPACE, 0, ROW_SPACE)
        rowParams.gravity = Gravity.CENTER
        val seatParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        seatParams.setMargins(SEAT_SPACE, 0, SEAT_SPACE, 0)


        for (i in 0 until mRowsNumber) {
            mRowsLinearLayout[i] = LinearLayout(activity!!)
            mRowsLinearLayout[i]?.layoutParams = rowParams
            for (j in 0 until mColumnsNumber) {
                mSeats[i][j] = ImageView(activity!!)
                mSeats[i][j]?.layoutParams = seatParams
                mSeats[i][j]?.setOnClickListener {
                    onClickSeat(it)
                }
                mRowsLinearLayout[i]?.addView(mSeats[i][j])
            }
            mParentLinearLayout.addView(mRowsLinearLayout[i])
        }
        val screenImageView = ImageView(activity!!)
        screenImageView.setImageResource(R.drawable.ic_screen)
        mParentLinearLayout.addView(screenImageView)
    }

    companion object {
        private const val ROW_SPACE = 32
        private const val SEAT_SPACE = 16
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TicketFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}