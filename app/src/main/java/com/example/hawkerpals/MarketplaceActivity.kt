package com.example.hawkerpals

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hawkerpals.models.Post
import com.example.hawkerpals.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_marketplace.*
import kotlinx.android.synthetic.main.activity_upload_market_product.*
import java.util.ArrayList

class MarketplaceActivity : AppCompatActivity() {

    private var layoutManager : RecyclerView.LayoutManager? = null
    private lateinit var mName: TextView
    private lateinit var listingList: ArrayList<Listing>
    private lateinit var marketRecyclerView: RecyclerView


    //new code
    private var signedInUser:User? = null
    private lateinit var firebaseDb: FirebaseFirestore
    private lateinit var posts:MutableList<Post>
    private lateinit var adapter:MarketplaceRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marketplace)

        val intent = getIntent()
        val groupname = intent.getStringExtra("GroupName")
        mName = findViewById(R.id.mpName)
        mName.setText(groupname)

        posts = mutableListOf()

        adapter = MarketplaceRecyclerAdapter(this, posts)
        marketrecycler_view.adapter = adapter
        marketrecycler_view.layoutManager = LinearLayoutManager(this)
        //new code
        firebaseDb = FirebaseFirestore.getInstance()


        firebaseDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG,"signed in user: ${signedInUser}")
            }
        val postsReference = firebaseDb
            .collection("posts")
            .limit(20)
            .orderBy("creationtime", Query.Direction.DESCENDING)
            .whereEqualTo("marketname", groupname)
        postsReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG, "Got some error", exception)
                return@addSnapshotListener
            }

            val postList = snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postList)
            adapter.notifyDataSetChanged()
            for (post in postList) {
                Log.i(TAG, "Post ${post}")
            }


        }
        fabCreate.setOnClickListener{
            val intent = Intent(this,uploadMarketProduct::class.java)
            intent.putExtra("GroupName",groupname)
            startActivity(intent)
        }
        reviewBtn.setOnClickListener {
            val intent = Intent(this,ReviewActivity::class.java)
            intent.putExtra("GroupName",groupname)
            startActivity(intent)
        }

    }
}