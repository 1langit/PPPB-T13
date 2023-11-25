package com.example.pppb_t13

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pppb_t13.databinding.ActivityTaskBinding

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    private val todoCollection = TodoCollection()

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
                        todoCollection.updateTodo(
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
                        todoCollection.addTodo(
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
}