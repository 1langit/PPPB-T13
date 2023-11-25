package com.example.pppb_t13

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

class TodoCollection {
    private val firestore = FirebaseFirestore.getInstance()
    val todoCollectionRef = firestore.collection("todo")
    val todoListLiveData: MutableLiveData<List<Todo>> by lazy {
        MutableLiveData<List<Todo>>()
    }

    fun getAllTodo() {
        todoCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening to budget changes")
            }
            if (snapshots != null) {
                val todos = snapshots?.toObjects(Todo::class.java)
                todoListLiveData.postValue(todos)
            }
        }
    }

    fun addTodo(todo: Todo) {
        todoCollectionRef.add(todo).addOnSuccessListener { documment ->
            todo.id = documment.id
            documment.set(todo).addOnFailureListener {
                Log.d("MainActivity", "Error updating todo id : ", it)
            }
        }.addOnFailureListener {
            Log.d("MainActivity", "Error adding todo id : ", it)
        }
    }

    fun updateTodo(todo: Todo) {
        todoCollectionRef.document(todo.id).set(todo).addOnFailureListener {
            Log.d("MainActivity", "Error updating todo", it)
        }
    }

    fun deleteTodo(todo: Todo) {
        if (todo.id.isEmpty()) {
            Log.d("MainActivity", "Error delete item!")
            return
        }
        todoCollectionRef.document(todo.id).delete().addOnFailureListener {
            Log.d("MainActivity", "Error deleting todo", it)
        }
    }
}