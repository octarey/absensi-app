package com.example.bismillah.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bismillah.R
import com.example.bismillah.UserIn
import com.example.bismillah.UserOut
import com.example.bismillah.databinding.AbsentItemBinding

class AbsentAdapter: RecyclerView.Adapter<AbsentAdapter.ViewHolder>(){
    private var absentIn = ArrayList<UserIn>()
    private var absentOut = ArrayList<UserOut>()
    fun setAbsentList(a: List<UserIn>, b:List<UserOut>){
        this.absentIn = a as ArrayList<UserIn> /* = java.util.ArrayList<com.example.bismillah.UserIn> */
        this.absentOut = b as ArrayList<UserOut> /* = java.util.ArrayList<com.example.bismillah.UserOut> */
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: AbsentItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AbsentItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val time = absentIn[position].timeIn
        val loc = absentIn[position].locIn
        val time2 = absentOut[position].timeOut
        val loc2 = absentOut[position].locOut

        val status = if(loc == 0) "Di Kantor" else "Di Luar kantor"
        val status2 = if(loc2 == 0) "Di Kantor" else "Di Luar kantor"

        holder.binding.itemDate.text = "Tanggal : " + absentIn[position].date

        holder.binding.itemTime.text = "$time WIB"
        holder.binding.itemLoc.text = status

        holder.binding.itemTimeOut.text = "$time2 WIB"
        holder.binding.itemLocOut.text = status2

        if (absentIn[position].statusIn == "NOK"){
            holder.binding.itemContainer.setBackgroundColor(holder.binding.itemContainer.resources.getColor(R.color.error))
            holder.binding.itemLabel.visibility = View.VISIBLE
        }else {
            holder.binding.itemContainer.setBackgroundColor(holder.binding.itemContainer.resources.getColor(R.color.white))
            holder.binding.itemLabel.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return absentOut.size
    }
}