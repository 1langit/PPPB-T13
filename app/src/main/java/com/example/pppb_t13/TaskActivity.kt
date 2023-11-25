package com.example.pppb_t13

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import com.example.pppb_t13.databinding.ActivityTaskBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val todoCollectionRef = firestore.collection("budgets")
    private val todoListLiveData: MutableLiveData<List<Todo>> by lazy {
        MutableLiveData<List<Todo>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            val id = intent.getStringExtra("id")
            if (id != null) {
                edtTitle.setText(intent.getStringExtra("title"))
                edtTag.setText(intent.getStringExtra("tag"))
                edtDesc.setText(intent.getStringExtra("description"))
                txtTitle.text = "Captured Task"
                btnSave.text = "Save"
                btnSave.setOnClickListener {
                    if (edtTitle.text.toString() == "") {
                        Toast.makeText(this@TaskActivity, "Please input title", Toast.LENGTH_SHORT).show()
                    } else {
                        updateTodo(
                            Todo(
                                id = id,
                                title = edtTitle.text.toString(),
                                tag = edtTag.text.toString(),
                                status = intent.getStringExtra("status")!!,
                                description = edtDesc.text.toString()
                            )
                        )
                        Toast.makeText(this@TaskActivity, "Task saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                btnSave.setOnClickListener {
                    if (edtTitle.text.toString() == "") {
                        Toast.makeText(this@TaskActivity, "Please input title", Toast.LENGTH_SHORT).show()
                    } else {
                        addTodo(
                            Todo(
                                title = edtTitle.text.toString(),
                                tag = edtTag.text.toString(),
                                status = "To do",
                                description = edtDesc.text.toString()
                            )
                        )
                        Toast.makeText(this@TaskActivity, "Task added", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun addTodo(todo: Todo) {
        todoCollectionRef.add(todo).addOnSuccessListener { documment ->
            todo.id = documment.id
            documment.set(todo).addOnFailureListener {
                Log.d("MainActivity", "Error updating budget id : ", it)
            }
        }.addOnFailureListener {
            Log.d("MainActivity", "Error adding budget id : ", it)
        }
    }

    private fun updateTodo(todo: Todo) {
        todoCollectionRef.document(todo.id).set(todo).addOnFailureListener {
            Log.d("MainActivity", "error updating budget", it)
        }
    }
}