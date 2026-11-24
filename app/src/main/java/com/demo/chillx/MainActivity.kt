package com.demo.chillx

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

val NavyPrimary = Color(0xFF0B192C)
val NavyLight = Color(0xFF1E3E62)
val GreenHighlight = Color(0xFF16A34A)
val GreenBg = Color(0xFFDCFCE7)
val RedAlert = Color(0xFFDC2626)
val RedBg = Color(0xFFFEE2E2)
val SurfaceWhite = Color(0xFFFFFFFF)
val PageBg = Color(0xFFF4F6F9)
val TextDark = Color(0xFF0F172A)
val TextMuted = Color(0xFF64748B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = NavyPrimary,
                    background = PageBg,
                    surface = SurfaceWhite
                )
            ) {
                ChillXApp()
            }
        }
    }
}

data class MenuOption(val id: String, val titleEn: String, val titleHi: String, val icon: ImageVector)

@Composable
fun ChillXApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("chillx_local_prefs", Context.MODE_PRIVATE) }

    var isHindi by remember { mutableStateOf(prefs.getBoolean("isHindi", false)) }
    var userName by remember { mutableStateOf(prefs.getString("userName", "Vikash") ?: "Vikash") }
    var userCity by remember { mutableStateOf(prefs.getString("userCity", "India") ?: "India") }
    var userHandle by remember { mutableStateOf(prefs.getString("userHandle", "@vikash_cx") ?: "@vikash_cx") }
    var demoBalance by remember { mutableDoubleStateOf(prefs.getFloat("demoBalance", 5000f).toDouble()) }
    var historyList by remember {
        mutableStateOf(
            prefs.getString("history", "₹1,000 • Sample Entry • Today|₹500 • Sample Entry • Yesterday")
                ?.split("|")?.filter { it.isNotBlank() } ?: emptyList()
        )
    }

    var currentScreen by remember { mutableStateOf("HOME") }
    var amountInput by remember { mutableStateOf("1000") }
    var feedbackText by remember { mutableStateOf("") }
    var toastMsg by remember { mutableStateOf<String?>(null) }

    val numericAmount = amountInput.toDoubleOrNull() ?: 0.0
    val fiftyPercentBonus = numericAmount * 0.50
    val illustrativeTotal = numericAmount + fiftyPercentBonus

    fun formatINR(value: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        formatter.maximumFractionDigits = 2
        return "₹" + formatter.format(value)
    }

    fun saveAll() {
        prefs.edit()
            .putBoolean("isHindi", isHindi)
            .putString("userName", userName)
            .putString("userCity", userCity)
            .putString("userHandle", userHandle)
            .putFloat("demoBalance", demoBalance.toFloat())
            .putString("history", historyList.joinToString("|"))
            .apply()
    }

    BackHandler(enabled = currentScreen != "HOME") {
        currentScreen = "HOME"
    }

    val menuItems = listOf(
        MenuOption("DASHBOARD", "Dashboard", "डैशबोर्ड", Icons.Default.Dashboard),
        MenuOption("TRACKER", "30 Days Tracker", "30 दिन ट्रैकर", Icons.Default.CalendarMonth),
        MenuOption("HISTORY", "Transaction History", "ट्रांजैक्शन हिस्ट्री", Icons.Default.History),
        MenuOption("NOTIFICATIONS", "Notifications", "नोटिफिकेशन", Icons.Default.Notifications),
        MenuOption("PROFILE", "My Profile", "मेरी प्रोफ़ाइल", Icons.Default.Person),
        MenuOption("HELP", "Help & Support", "सहायता और समर्थन", Icons.AutoMirrored.Filled.HelpOutline),
        MenuOption("FAQ", "FAQ", "अक्सर पूछे जाने वाले सवाल", Icons.Default.QuestionAnswer),
        MenuOption("LANGUAGE", "Hindi / English", "हिंदी / English बदलें", Icons.Default.Language),
        MenuOption("FEEDBACK", "Complaint / Feedback", "शिकायत / सुझाव", Icons.Default.Feedback),
        MenuOption("PRIVACY", "Privacy Policy", "गोपनीयता नीति", Icons.Default.PrivacyTip),
        MenuOption("TERMS", "Terms & Conditions", "नियम और शर्तें", Icons.Default.Description),
        MenuOption("ABOUT", "About Chill𝕏", "Chill𝕏 के बारे में", Icons.Default.Info),
        MenuOption("RESET", "Reset Demo Data", "डेमो डेटा रीसेट करें", Icons.Default.RestartAlt)
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyPrimary)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (currentScreen != "HOME") {
                        IconButton(onClick = { currentScreen = "HOME" }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                    Text(
                        text = "Chill𝕏",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFF475569), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("DEMO", color = Color(0xFF93C5FD), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = if (isHindi) "हिंदी" else "EN",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NavyLight)
                        .clickable {
                            isHindi = !isHindi
                            saveAll()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        },
        bottomBar = {
            if (currentScreen == "QR") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceWhite)
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { currentScreen = "HOME" },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "HOME (होम पर जाएं)" else "HOME",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (toastMsg != null) {
            AlertDialog(
                onDismissRequest = { toastMsg = null },
                confirmButton = {
                    TextButton(onClick = { toastMsg = null }) { Text("OK", fontWeight = FontWeight.Bold) }
                },
                title = { Text("Chill𝕏") },
                text = { Text(toastMsg ?: "") }
            )
        }

        when (currentScreen) {
            "HOME" -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PageBg)
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Welcome Header
                    item {
                        Column {
                            Text(
                                text = if (isHindi) "स्वागत है, $userName" else "Welcome, $userName",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = if (isHindi) "इंटरैक्टिव कैलकुलेटर और डैशबोर्ड" else "Interactive Calculator & Overview",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Enter Amount Input at the top
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isHindi) "राशि दर्ज करें (Enter Amount)" else "Enter Amount",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = amountInput,
                                    onValueChange = { if (it.length <= 9) amountInput = it.filter { ch -> ch.isDigit() } },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                                    prefix = { Text("₹ ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NavyPrimary) },
                                    placeholder = { Text("0") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    // Your Calculation immediately below input
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            border = BorderStroke(1.5.dp, GreenHighlight),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isHindi) "आपकी गणना (Your Calculation)" else "Your Calculation",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(GreenBg, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("+50% Math Example", color = GreenHighlight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Base Value", fontSize = 12.sp, color = TextMuted)
                                        Text(formatINR(numericAmount), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("50% Value", fontSize = 12.sp, color = TextMuted)
                                        Text("+${formatINR(fiftyPercentBonus)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GreenHighlight)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Illustrative Total", fontSize = 12.sp, color = TextMuted)
                                        Text(formatINR(illustrativeTotal), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = GreenHighlight)
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (isHindi)
                                        "केवल गणितीय उदाहरण (Illustrative Only): यह कोई वास्तविक निवेश या गारंटीड रिटर्न नहीं है।"
                                    else
                                        "Illustrative mathematical 50% example only. Not a real financial return or guarantee.",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    // Preset Amount Cards
                    item {
                        val presets = listOf(100, 200, 300, 500, 1000, 5000, 10000)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (isHindi) "त्वरित राशि चुनें (Preset Amounts)" else "Select Preset Amount",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            presets.chunked(4).forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowItems.forEach { amt ->
                                        val isSelected = amountInput == amt.toString()
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { amountInput = amt.toString() },
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isSelected) NavyPrimary else SurfaceWhite,
                                            border = BorderStroke(1.dp, if (isSelected) NavyPrimary else Color(0xFFCBD5E1))
                                        ) {
                                            Text(
                                                text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(amt)}",
                                                modifier = Modifier.padding(vertical = 12.dp),
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = if (isSelected) Color.White else TextDark
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // OK / Continue Button
                    item {
                        Button(
                            onClick = {
                                if (numericAmount > 0) {
                                    val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                                    val newEntry = "${formatINR(numericAmount)} • Sample Preview • $time"
                                    historyList = listOf(newEntry) + historyList.take(19)
                                    demoBalance += numericAmount
                                    saveAll()
                                    currentScreen = "QR"
                                } else {
                                    toastMsg = if (isHindi) "कृपया 0 से अधिक राशि दर्ज करें।" else "Please enter an amount greater than 0."
                                }
                            },
            
