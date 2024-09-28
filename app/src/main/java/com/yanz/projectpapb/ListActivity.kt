package com.yanz.projectpapb

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yanz.projectpapb.ui.theme.ProjectPapbTheme


data class Course(
    val hari: String = "",
    val mataKuliah: String = "",
    val jam: String = ""
)

class ListActivity : ComponentActivity() {
    private val database = FirebaseDatabase.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setContent {
            ProjectPapbTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CourseList(database)

                        // Tombol Logout
                        Button(
                            onClick = {
                                logoutUser(auth) { message ->
                                    // Tampilkan pesan logout
                                    Toast.makeText(this@ListActivity, message, Toast.LENGTH_SHORT).show()
                                    finish() // Kembali ke MainActivity
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }
}

// Fungsi untuk logout
fun logoutUser(auth: FirebaseAuth, callback: (String) -> Unit) {
    auth.signOut()
    callback("Logged out successfully!")
}

@Composable
fun CourseList(database: FirebaseDatabase) {
    var courses by remember { mutableStateOf(listOf<Course>()) }

    // Fetch data from Realtime Database
    LaunchedEffect(Unit) {
        val coursesRef = database.getReference("courses")
        coursesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courseList = mutableListOf<Course>()
                for (courseSnapshot in snapshot.children) {
                    val course = courseSnapshot.getValue(Course::class.java)
                    if (course != null) {
                        courseList.add(course)
                    }
                }
                courses = courseList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error here
            }
        })
    }

    // Display the list of course cards
    if (courses.isEmpty()) {
        Text("No courses available", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    } else {
        courses.forEach { course ->
            CourseCard(
                hari = course.hari,
                mataKuliah = course.mataKuliah,
                jam = course.jam
            )
        }
    }
}

@Composable
fun CourseCard(hari: String, mataKuliah: String, jam: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = hari,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mataKuliah,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = jam,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
    }
}
