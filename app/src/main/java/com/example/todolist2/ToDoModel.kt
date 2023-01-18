package com.example.todolist2

class ToDoModel {

    companion object Factory {
        fun createList (): ToDoModel = ToDoModel()
    }

    var UID : String? = null
    var itemDataText : String? = null
    var done : Boolean? = false
}