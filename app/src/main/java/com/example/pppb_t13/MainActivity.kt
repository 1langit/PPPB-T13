package com.example.pppb_t13

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pppb_t13.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val todoCollection = TodoCollection()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        todoCollection.getAllTodo()
        showAllTodo()

        with(binding) {
            btnAdd.setOnClickListener {
                val intent = Intent(this@MainActivity, TaskActivity::class.java)
                startActivity(intent)
            }

            val currentDate = Calendar.getInstance()
            val formatter = SimpleDateFormat("E, d MMM", Locale.getDefault())
            txtDate.text =  formatter.format(currentDate.time)

            todoCollection.todoCollectionRef.addSnapshotListener { snapshots, error ->
                if (snapshots != null) {
                    val count = snapshots.size()
                    txtTaskCount.text = "$count pending tasks"
                    if (count == 0) linNoTask.visibility = View.VISIBLE
                    else linNoTask.visibility = View.GONE
                }
            }
        }
    }

    private fun showAllTodo() {
        todoCollection.todoListLiveData.observe(this) { todos ->
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
                    todoCollection.deleteTodo(todo)
                },
                { todo ->
                    val newStatus = when(todo.status) {
                        "To do" -> "Doing"
                        "Doing" -> "Done"
                        "Done" -> "To do"
                        else -> ""
                    }
                    todoCollection.updateTodo(
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