package com.demo.chillx

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
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

val Navy = Color(0xFF0B192C)
val NavyLight = Color(0xFF1E3E62)
val Green = Color(0xFF16A34A)
val GreenBg = Color(0xFFDCFCE7)
val RedAlert = Color(0xFFDC2626)
val RedBg = Color(0xFFFEE2E2)
val PageBg = Color(0xFFF4F6F9)
val TextDark = Color(0xFF0F172A)
val TextMuted = Color(0xFF64748B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Navy, background = PageBg, surface = Color.White)) {
                ChillXApp()
            }
        }
    }
}

data class MenuItem(val id: String, val en: String, val hi: String, val icon: ImageVector)

@Composable
fun ChillXApp() {
    val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("chillx_prefs", Context.MODE_PRIVATE) }

    var isHi by remember { mutableStateOf(prefs.getBoolean("isHi", false)) }
    var name by remember { mutableStateOf(prefs.getString("name", "Vikash") ?: "Vikash") }
    var city by remember { mutableStateOf(prefs.getString("city", "India") ?: "India") }
    var handle by remember { mutableStateOf(prefs.getString("handle", "@vikash_cx") ?: "@vikash_cx") }
    var balance by remember { mutableDoubleStateOf(prefs.getFloat("balance", 5000f).toDouble()) }
    var history by remember {
        mutableStateOf(prefs.getString("history", "₹1,000 • Sample Preview • Today")?.split("|")?.filter { it.isNotBlank() } ?: emptyList())
    }

    var screen by remember { mutableStateOf("HOME") }
    var amountStr by remember { mutableStateOf("1000") }
    var feedback by remember { mutableStateOf("") }
    var alertMsg by remember { mutableStateOf<String?>(null) }

    val amt = amountStr.toDoubleOrNull() ?: 0.0
    val bonus = amt * 0.50
    val total = amt + bonus

    fun inr(v: Double): String = "₹" + NumberFormat.getNumberInstance(Locale("en", "IN")).apply { maximumFractionDigits = 2 }.format(v)

    fun save() {
        prefs.edit().putBoolean("isHi", isHi).putString("name", name).putString("city", city)
            .putString("handle", handle).putFloat("balance", balance.toFloat())
            .putString("history", history.joinToString("|")).apply()
    }

    BackHandler(enabled = screen != "HOME") { screen = "HOME" }

    val menus = listOf(
        MenuItem("DASHBOARD", "Dashboard", "डैशबोर्ड", Icons.Default.Dashboard),
        MenuItem("TRACKER", "30 Days Tracker", "30 दिन ट्रैकर", Icons.Default.CalendarMonth),
        MenuItem("HISTORY", "Transaction History", "ट्रांजैक्शन हिस्ट्री", Icons.Default.History),
        MenuItem("NOTIFICATIONS", "Notifications", "नोटिफिकेशन", Icons.Default.Notifications),
        MenuItem("PROFILE", "My Profile", "मेरी प्रोफ़ाइल", Icons.Default.Person),
        MenuItem("HELP", "Help & Support", "सहायता और समर्थन", Icons.AutoMirrored.Filled.HelpOutline),
        MenuItem("FAQ", "FAQ", "अक्सर पूछे जाने वाले सवाल", Icons.Default.QuestionAnswer),
        MenuItem("LANGUAGE", "Hindi / English", "हिंदी / English बदलें", Icons.Default.Language),
        MenuItem("FEEDBACK", "Complaint / Feedback", "शिकायत / सुझाव", Icons.Default.Feedback),
        MenuItem("PRIVACY", "Privacy Policy", "गोपनीयता नीति", Icons.Default.PrivacyTip),
        MenuItem("TERMS", "Terms & Conditions", "नियम और शर्तें", Icons.Default.Description),
        MenuItem("ABOUT", "About Chill𝕏", "Chill𝕏 के बारे में", Icons.Default.Info),
        MenuItem("RESET", "Reset Demo Data", "डेमो डेटा रीसेट करें", Icons.Default.RestartAlt)
    )

    Scaffold(
        topBar = {
            Row(
                Modifier.fillMaxWidth().background(Navy).statusBarsPadding().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (screen != "HOME") {
                        IconButton(onClick = { screen = "HOME" }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    }
                    Text("Chill𝕏", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.background(Color(0xFF1E293B), RoundedCornerShape(6.dp)).border(1.dp, Color(0xFF475569), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                        Text("DEMO", color = Color(0xFF93C5FD), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    if (isHi) "हिंदी" else "EN", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(NavyLight).clickable { isHi = !isHi; save() }.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        },
        bottomBar = {
            if (screen == "QR") {
                Box(Modifier.fillMaxWidth().background(Color.White).navigationBarsPadding().padding(16.dp)) {
                    Button(
                        onClick = { screen = "HOME" },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Navy),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Home, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isHi) "HOME (होम पर जाएं)" else "HOME", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    ) { pad ->
        if (alertMsg != null) {
            AlertDialog(
                onDismissRequest = { alertMsg = null },
                confirmButton = { TextButton(onClick = { alertMsg = null }) { Text("OK", fontWeight = FontWeight.Bold) } },
                title = { Text("Chill𝕏") }, text = { Text(alertMsg ?: "") }
            )
        }

        when (screen) {
            "HOME" -> LazyColumn(
                Modifier.fillMaxSize().background(PageBg).padding(pad).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Column {
                        Text(if (isHi) "स्वागत है, $name" else "Welcome, $name", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(if (isHi) "इंटरैक्टिव कैलकुलेटर और डैशबोर्ड" else "Interactive Calculator & Overview", fontSize = 13.sp, color = TextMuted)
                    }
                }
                item {
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(16.dp)) {
                            Text(if (isHi) "राशि दर्ज करें (Enter Amount)" else "Enter Amount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = amountStr,
                                onValueChange = { if (it.length <= 9) amountStr = it.filter { c -> c.isDigit() } },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                                prefix = { Text("₹ ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy) },
                                placeholder = { Text("0") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true, shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
                item {
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.5.dp, Green)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(if (isHi) "आपकी गणना (Your Calculation)" else "Your Calculation", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                Box(Modifier.background(GreenBg, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text("+50% Math Example", color = Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Base", fontSize = 12.sp, color = TextMuted)
                                    Text(inr(amt), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("50% Value", fontSize = 12.sp, color = TextMuted)
                                    Text("+${inr(bonus)}", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Green)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Illustrative Total", fontSize = 12.sp, color = TextMuted)
                                    Text(inr(total), fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = Green)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                if (isHi) "केवल गणितीय उदाहरण (Illustrative Only): यह वास्तविक रिटर्न नहीं है।"
                                else "Illustrative mathematical 50% example only. Not a real return.",
                                fontSize = 12.sp, color = TextMuted
                            )
                        }
                    }
                }
                item {
                    val presets = listOf(100, 200, 300, 500, 1000, 5000, 10000)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(if (isHi) "त्वरित राशि चुनें" else "Select Preset Amount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        presets.chunked(4).forEach { row ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { p ->
                                    val sel = amountStr == p.toString()
                                    Surface(
                                        modifier = Modifier.weight(1f).clickable { amountStr = p.toString() },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (sel) Navy else Color.White,
                                        border = BorderStroke(1.dp, if (sel) Navy else Color(0xFFCBD5E1))
                                    ) {
                                        Text(
                                            "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(p)}",
                                            Modifier.padding(vertical = 12.dp), textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (sel) Color.White else TextDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Button(
                        onClick = {
                            if (amt > 0) {
                                val t = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                                history = listOf("${inr(amt)} • Sample Preview • $t") + history.take(19)
                                balance += amt; save(); screen = "QR"
                            } else alertMsg = if (isHi) "कृपया 0 से अधिक राशि दर्ज करें।" else "Enter an amount greater than 0."
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green), shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(if (isHi) "OK / आगे बढ़ें" else "OK / Continue", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Navy)) {
                            Column(Modifier.padding(14.dp)) {
                                Text("Demo Balance", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                Text(inr(balance), color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                                Text("Sample Counter", color = Color(0xFF38BDF8), fontSize = 11.sp)
                            }
                        }
                        Card(Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(Modifier.padding(14.dp)) {
                                Text("Demo Activity", color = TextMuted, fontSize = 12.sp)
                                Text("${history.size} Entries", color = TextDark, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                                Text("Local Session", color = Green, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                item { Text(if (isHi) "ऐप मेनू (App Menu)" else "App Menu", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark) }
                items(menus) { m ->
                    Card(
                        Modifier.fillMaxWidth().clickable {
                            when (m.id) {
                                "LANGUAGE" -> { isHi = !isHi; save() }
                                "RESET" -> {
                                    amountStr = "1000"; balance = 5000.0; history = listOf("₹1,000 • Sample Entry • Today"); save()
                                    alertMsg = if (isHi) "डेमो डेटा रीसेट हो गया है।" else "Sample data reset successfully."
                                }
                                else -> screen = m.id
                            }
                        },
                        shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(m.icon, null, tint = if (m.id == "RESET") RedAlert else Navy, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(if (isHi) m.hi else m.en, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = if (m.id == "RESET") RedAlert else TextDark)
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                        }
                    }
                }
            }

            "QR" -> LazyColumn(
                Modifier.fillMaxSize().background(PageBg).padding(pad).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(Modifier.fillMaxWidth().background(RedBg, RoundedCornerShape(12.dp)).border(1.5.dp, RedAlert, RoundedCornerShape(12.dp)).padding(12.dp), contentAlignment = Alignment.Center) {
                        Text("NOT FOR PAYMENT • NON-SCANNABLE SAMPLE", color = RedAlert, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, textAlign = TextAlign.Center)
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(if (isHi) "चयनित राशि" else "Selected Amount", fontSize = 14.sp, color = TextMuted)
                            Text(inr(amt), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Navy)
                            Text("Illustrative 50% Example: ${inr(total)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Green)
                            Spacer(Modifier.height(18.dp))
                            Box(Modifier.size(200.dp).border(3.dp, Navy, RoundedCornerShape(16.dp)).padding(14.dp), contentAlignment = Alignment.Center) {
                                Canvas(Modifier.fillMaxSize()) {
                                    val g = 9; val c = size.width / g
                                    for (r in 0 until g) for (col in 0 until g) {
                                        if ((r < 3 && col < 3) || (r < 3 && col > 5) || (r > 5 && col < 3) || ((r + col) % 2 == 0 && (r * col) % 3 != 0)) {
                                            drawRect(Navy, Offset(col * c + 2f, r * c + 2f),
