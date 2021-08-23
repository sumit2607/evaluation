package com.sumit.musicplayer

import android.icu.number.NumberRangeFormatter.with
import android.provider.ContactsContract
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_layout.view.*

class PostViewholder(private val view:View):RecyclerView.ViewHolder(view) {

    fun setdata(datalist: ContactsContract.Contacts.Data){
        view.apply {
            Picasso.get().load(datalist.avatar).into(ivimage)
            tvname.text=datalist.first_name
            tvemail.text=datalist.email
        }
    }
}