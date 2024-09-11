package com.yanz.projectpapb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.yanz.projectpapb.ui.theme.ProjectPapbTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person // Import this icon
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.draw.shadow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectPapbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MyScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen() {
    var text by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }
    var inputText by remember { mutableStateOf("") }
    var inputText2 by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Output Text
        AnimatedVisibility(visible = isVisible) {
            Box(
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().background(Color.Gray)
                    )
                    Text(
                        text = text2,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().background(Color.Gray)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TextField with Icon
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter Name", color = Color.Black, fontWeight = FontWeight.Bold) },
            modifier = Modifier
                .fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "User Icon") }, // Added icon
        )

        Spacer(modifier = Modifier.height(10.dp))

        // TextField Number
        OutlinedTextField(
            value = inputText2,
            onValueChange = { inputText2 = it },
            label = { Text("Enter NIM", color = Color.Black,fontWeight = FontWeight.Bold) },
            modifier = Modifier
                .fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = "Number Icon") }, // Added icon
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Button Submit
        Button(
            onClick = {
                text = inputText
                text2 = inputText2
                isVisible = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray,
                contentColor = Color.White
            ),
        ) {
            Text("Submit", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProjectPapbTheme {
        MyScreen()
    }
}
