package com.example.hawkerpals.bottomnavbartutorial.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hawkerpals.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import androidx.appcompat.widget.SearchView
import kotlin.collections.ArrayList

//class HomeFragment : Fragment() {
//    private lateinit var CardRecyclerView: RecyclerView
////    private lateinit var hawkerName: TextView
////    private lateinit var hawkerAddress: TextView
////    private lateinit var hawkerPic: ImageView
//    private lateinit var cardAdapter: RecyclerAdapter
//    private lateinit var hawkerList: ArrayList<Hawkers>
//    private lateinit var mAuth:FirebaseAuth
//    private lateinit var mDbRef: DatabaseReference
//
//
//    val db = Firebase.database("https://hawkerpals-de16f-default-rtdb.asia-southeast1.firebasedatabase.app/")
//    val dbRef = db.getReference()
//
//    private var layoutManager: RecyclerView.LayoutManager? = null
//    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false)
//    }
//
//    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?){
//        super.onViewCreated(itemView, savedInstanceState)
//        mAuth = FirebaseAuth.getInstance()
//        hawkerList = ArrayList()
//        dbRef.child("hawkerCenter_info").addValueEventListener(object :ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                hawkerList.clear()
//                for(postSnapshot in snapshot.children){
//                    val currentHawkers = postSnapshot.getValue(Hawkers::class.java)
//                    hawkerList.add(currentHawkers!!)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//
//        recycler_view.apply{
//            // set a LinearLayoutManager to handle Android
//            // RecyclerView behavior
//            layoutManager = LinearLayoutManager(activity)
//            // set the custom adapter to the RecyclerView
//            adapter = RecyclerAdapter(context,hawkerList)
//        }
//
//
//
//    }
//
//}

class HomeFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    private lateinit var hawkerRecyclerView: RecyclerView
    private lateinit var hawkerList: ArrayList<Hawkers>
    lateinit var searchView: SearchView

    val db = Firebase.database("https://hawkerpals-de16f-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val dbRef = db.getReference().child("hawkerCenter_info")


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        // Inflate the layout for this fragment
        return v
    }


    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?){
        super.onViewCreated(itemView, savedInstanceState)
        hawkerRecyclerView = itemView.findViewById(R.id.recycler_view)
        searchView = itemView.findViewById(R.id.searchView)
        hawkerRecyclerView.layoutManager = LinearLayoutManager(context)

        hawkerRecyclerView.setHasFixedSize(true)

        hawkerList = arrayListOf<Hawkers>()
        getHawkerData(hawkerList)

    }
    private fun getHawkerData(arrayList: ArrayList<Hawkers>){
        dbRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (hawkerSnapShot in snapshot.children){
                        val hawkers = hawkerSnapShot.getValue(Hawkers::class.java)
                        arrayList.add(hawkers!!)
                    }
                    hawkerRecyclerView.adapter = RecyclerAdapter(hawkerList)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        if(searchView!= null)
        {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    search(newText)
                    return true
                }
            })
        }
    }

    private fun search(newText: String)
    {
        val myList = ArrayList<Hawkers>()
        for (Hawker in hawkerList)
        {
            if (Hawker.hawker_name?.toLowerCase()?.contains(newText.toLowerCase()) == true || Hawker.hawker_address?.toLowerCase()
                    ?.contains(newText.toLowerCase()) == true || Hawker.hawker_locate?.toLowerCase()?.contains(newText.toLowerCase()) == true
            )
            {
                myList.add(Hawker)
            }
        }
        hawkerRecyclerView.adapter = RecyclerAdapter(myList)
    }
}