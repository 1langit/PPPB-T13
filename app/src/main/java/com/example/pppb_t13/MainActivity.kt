package com.example.pppb_t13

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pppb_t13.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val todoCollectionRef = firestore.collection("budgets")
    private val todoListLiveData: MutableLiveData<List<Todo>> by lazy {
        MutableLiveData<List<Todo>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllTodo()

        with(binding) {
            btnAdd.setOnClickListener {
                val intent = Intent(this@MainActivity, TaskActivity::class.java)
                startActivity(intent)
            }

            val currentDate = Calendar.getInstance()
            val formatter = SimpleDateFormat("E, d MMM", Locale.getDefault())
            txtDate.text =  formatter.format(currentDate.time)
        }
    }

    private fun getAllTodo() {
        todoCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening to budget changes")
            }
            if (snapshots != null) {
                val todos = snapshots?.toObjects(Todo::class.java)
                todoListLiveData.postValue(todos)
                showTodoCount(snapshots.size())
                showAllTodo()
            }
        }
    }

    private fun addTodo(todo: Todo) {
        todoCollectionRef.add(todo).addOnSuccessListener { documment ->
            todo.id = documment.id
            documment.set(todo).addOnFailureListener {
                Log.d("MainActivity", "Error updating todo id : ", it)
            }
        }.addOnFailureListener {
            Log.d("MainActivity", "Error adding todo id : ", it)
        }
    }

    private fun updateTodo(todo: Todo) {
        todoCollectionRef.document(todo.id).set(todo).addOnFailureListener {
            Log.d("MainActivity", "Error updating todo", it)
        }
    }

    private fun deleteTodo(todo: Todo) {
        if (todo.id.isEmpty()) {
            Log.d("MainActivity", "Error delete item!")
            return
        }
        todoCollectionRef.document(todo.id).delete().addOnFailureListener {
            Log.d("MainActivity", "Error deleting todo", it)
        }
    }

    private fun showTodoCount(count: Int) {
        with(binding) {
            txtTaskCount.text = "$count pending tasks"
            if (count == 0) linNoTask.visibility = View.VISIBLE
            else linNoTask.visibility = View.GONE
        }
    }

    private fun showAllTodo() {
        todoListLiveData.observe(this) { todos ->
            val adapterTodo = TodoAdapter(todos,
                { todo ->
                    val intent = Intent(this@MainActivity, TaskActivity::class.java)
                    intent.putExtra("id", todo.id)
                    intent.putExtra("title", todo.title)
                    intent.putExtra("tag", todo.tag)
                    intent.putExtra("status", todo.status)
                    intent.putExtra("description", todo.description)
                    startActivity(intent)
                },
                { todo ->
                    Toast.makeText(this@MainActivity, "Task deleted", Toast.LENGTH_SHORT).show()
                    deleteTodo(todo)
                },
                { todo ->
                    val newStatus = when(todo.status) {
                        "To do" -> "Doing"
                        "Doing" -> "Done"
                        "Done" -> "To do"
                        else -> ""
                    }
                    updateTodo(
                        Todo(
                            id = todo.id,
                            title = todo.title,
                            tag = todo.tag,
                            status = newStatus,
                            description = todo.description
                        )
                    )
                }
            )
            binding.rvTodo.apply {
                adapter = adapterTodo
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }
}