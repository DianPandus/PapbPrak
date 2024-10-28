package com.yanz.projectpapb

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yanz.projectpapb.ui.theme.ProjectPapbTheme
import com.yanz.projectpapb.R

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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CourseList(database)

                        Button(
                            onClick = {
                                // Call the logout function and handle navigation
                                logoutUser(auth) { message ->
                                    Toast.makeText(this@ListActivity, message, Toast.LENGTH_SHORT).show()

                                    // Navigate to MainActivity (Login Screen) after logout
                                    val intent = Intent(this@ListActivity, MainActivity::class.java)
                                    startActivity(intent)

                                    // Finish the current activity to prevent going back
                                    finish()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Logout")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        BottomNavigationBar(
                            onProfileClick = {
                                val intent = Intent(this@ListActivity, GithubProfileActivity::class.java)
                                startActivity(intent)
                            },
                            onTasksClick = {
                                val intent = Intent(this@ListActivity, TugasScreen::class.java)
                                startActivity(intent)
                            }
                        )

                    }
                }
            }
        }
    }
}

// Function for logging out
fun logoutUser(auth: FirebaseAuth, callback: (String) -> Unit) {
    auth.signOut()
    callback("Logged out successfully!")
}

@Composable
fun CourseList(database: FirebaseDatabase) {
    var courses by remember { mutableStateOf(listOf<Course>()) }

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
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mataKuliah,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = jam,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
