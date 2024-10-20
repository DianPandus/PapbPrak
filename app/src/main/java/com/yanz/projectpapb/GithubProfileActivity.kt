package com.yanz.projectpapb

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.yanz.projectpapb.ui.theme.ProjectPapbTheme
import kotlinx.coroutines.launch

class GithubProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectPapbTheme {
                // Make sure the whole screen is filled, including the bottom bar
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween, // Ensures items are spaced correctly
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile screen content
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GithubProfileScreen()
                        }

                        // Bottom Navigation Bar
                        BottomNavigationBar(
                            onProfileClick = {},
                            onTasksClick = {
                                // Navigate back to ListActivity
                                val intent = Intent(this@GithubProfileActivity, ListActivity::class.java)
                                startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GithubProfileScreen() {
    var profile by remember { mutableStateOf<GithubProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val apiService = GithubApiService.create()
            try {
                profile = apiService.getProfile("DianPandus")
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Failed to load profile: ${e.localizedMessage}"
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else if (errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize()
        )
    } else if (profile != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val imagePainter = rememberAsyncImagePainter(model = profile!!.avatar_url)
            Image(
                painter = imagePainter,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Username: ${profile!!.login}", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Name: ${profile!!.name ?: "Unknown"}", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Followers: ${profile!!.followers}", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Following: ${profile!!.following}", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}
