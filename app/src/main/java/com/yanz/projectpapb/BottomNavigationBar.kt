package com.yanz.projectpapb

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(onProfileClick: () -> Unit, onTasksClick: () -> Unit) {
    val context: Context = LocalContext.current

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.search3),
                    contentDescription = "Search",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Schedule") },
            selected = false,
            onClick = {
                // Navigate to ListActivity when "Search" is clicked
                val intent = Intent(context, ListActivity::class.java)
                context.startActivity(intent)
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.love4),
                    contentDescription = "Tasks",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Tasks") },
            selected = false,
            onClick = onTasksClick
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.profile2),
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Profile") },
            selected = false,
            onClick = onProfileClick
        )
    }
}
