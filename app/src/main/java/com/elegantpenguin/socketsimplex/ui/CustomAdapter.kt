package com.elegantpenguin.socketsimplex.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elegantpenguin.socketsimplex.R
import java.util.ArrayList
import java.util.Stack

class CustomAdapter(private val dataSet: Stack<ArrayList<String>>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstLine : TextView
        val secondLine : TextView
        var thirdLine : TextView

        init {
            // Define click listener for the ViewHolder's View
            firstLine = view.findViewById(R.id.first_line)
            secondLine = view.findViewById(R.id.second_line)
            thirdLine = view.findViewById(R.id.third_line)
            //textView = view.findViewById(this.meow.)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_message, viewGroup, false)
        //val view = LayoutInflater.from(viewGroup.context).inflate(this.meow.messageList, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.firstLine.text = dataSet[position]?.get(0)
        viewHolder.secondLine.text = dataSet[position]?.get(1)
        viewHolder.thirdLine.text = dataSet[position]?.get(2)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}