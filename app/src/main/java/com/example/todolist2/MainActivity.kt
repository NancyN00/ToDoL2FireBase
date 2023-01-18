package com.example.todolist2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() ,UpdateAndDelete{

    lateinit var database : DatabaseReference
    var toDoList : MutableList<ToDoModel>? = null
    lateinit var adapter: ToDoAdapter
    private var recyclerView: RecyclerView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        recyclerView = findViewById(R.id.item_recyclerview) as RecyclerView
        database = FirebaseDatabase.getInstance().reference

        fab.setOnClickListener {
            val alertDialog = AlertDialog.Builder (this)
            val textEditText = EditText(this)
            alertDialog.setMessage("Add TODO item")
            alertDialog.setTitle("Enter To Do Item")
            alertDialog.setView(textEditText)
            alertDialog.setPositiveButton("Add"){dialog, i ->
                val todoItemData = ToDoModel.createList()
                todoItemData.itemDataText =textEditText.text.toString()
                todoItemData.done = false
                val newItemData = database.child("todo").push()
                todoItemData.UID = newItemData.key

                newItemData.setValue(todoItemData)

                dialog.dismiss()
                Toast.makeText(this, "item saved", Toast.LENGTH_SHORT).show()
            }
            alertDialog.show()
        }

        toDoList = mutableListOf<ToDoModel>()
        adapter = ToDoAdapter(this, toDoList!!)
        recyclerView!!.adapter=adapter
        database.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoList!!.clear()
                addItemToList(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "No item added", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun addItemToList(snapshot: DataSnapshot) {
            val items=snapshot.children.iterator()
         if(items.hasNext()){
             val toDoIndexedValue=items.next()
             val itemsIterator = toDoIndexedValue.children.iterator()

             while (itemsIterator.hasNext()){
                 val currentItem = itemsIterator.next()
                 val toDoItemData = ToDoModel.createList()
                 val map = currentItem.getValue() as HashMap<String ,Any>

                 toDoItemData.UID = currentItem.key
                 toDoItemData.done=map.get("done") as Boolean?
                 toDoItemData.itemDataText= map.get("itemDatatext") as String?
                 toDoList!!.add(toDoItemData)
             }
         }

        adapter.notifyDataSetChanged()
    }

    override fun modifyItem(itemUID: String, idDone: Boolean) {
        val itemReference =database.child("todo").child(itemUID)
        itemReference.child("done").setValue(isDone)
    }

    override fun onItemDelete(itemUID: String) {
        val itemReference =database.child("todo").child(itemUID)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()
    }
}