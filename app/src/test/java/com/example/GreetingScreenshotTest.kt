package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent { 
      MyApplicationTheme(dynamicColor = false) { 
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color(0xFF1E1035),
                  Color(0xFF0F061E),
                  Color(0xFF05010B)
                )
              )
            )
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          MockXNewsDigestCard()
        }
      } 
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

@Composable
fun MockXNewsDigestCard() {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
      .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Author details
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Circle Avatar Placeholder
        Box(
          modifier = Modifier
            .size(40.dp)
            .background(Color(0xFFFFD700).copy(alpha = 0.15f), RoundedCornerShape(50))
            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.3f), RoundedCornerShape(50)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "N",
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "NASA",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Verified",
              tint = Color(0xFF1D9BF0),
              modifier = Modifier.size(14.dp)
            )
          }
          Text(
            text = "@NASA",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Title
      Text(
        text = "Voyager 1 Sends Historic Space Telemetry Data",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Content (tweet body)
      Text(
        text = "Voyager 1 has successfully resumed normal science data transmission after a ground-breaking deep space coding update. The probe continues to sail into interstellar space, sending back invaluable information about the galaxy's final frontiers.",
        color = Color.White.copy(alpha = 0.8f),
        fontSize = 13.sp,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Attached Folklore Joke section
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700).copy(alpha = 0.03f))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Folk Joke Commentary",
              color = Color(0xFFFFD700),
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
            Text(
              text = "Jewish Folklore",
              color = Color(0xFFFFD700).copy(alpha = 0.7f),
              fontWeight = FontWeight.Medium,
              fontSize = 10.sp
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "An elder is asked: 'Why is voyager sending data from so far?'\nThe elder replies: 'Because the farther away you are from your relatives, the better the communication is!'",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp,
            lineHeight = 16.sp
          )
        }
      }
    }
  }
}

