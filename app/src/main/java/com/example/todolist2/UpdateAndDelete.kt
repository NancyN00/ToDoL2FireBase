package com.example.todolist2

interface UpdateAndDelete{

    fun modifyItem (itemUID : String ,idDone :Boolean)
    fun onItemDelete(itemUID: String)
}