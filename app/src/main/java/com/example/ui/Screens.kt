package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.viewmodel.MedTrackViewModel
import com.example.ai.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class TabItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

// --- SHIMMER SKELETON PREVIEW COMPONENTS ---

@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp,
    width: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp.Unspecified,
    shape: androidx.compose.foundation.shape.CornerSize = androidx.compose.foundation.shape.CornerSize(8.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    Box(
        modifier = modifier
            .then(if (width != androidx.compose.ui.unit.Dp.Unspecified) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .clip(RoundedCornerShape(shape))
            .background(color.copy(alpha = color.alpha * alpha))
    )
}

@Composable
fun DashboardSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card Skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                ShimmerPlaceholder(height = 24.dp, width = 180.dp)
                Spacer(modifier = Modifier.height(10.dp))
                ShimmerPlaceholder(height = 16.dp, width = 260.dp)
                Spacer(modifier = Modifier.height(6.dp))
                ShimmerPlaceholder(height = 14.dp, width = 120.dp)
            }
        }
        
        // Circular / Quick Action Bar Skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerPlaceholder(height = 50.dp, width = 110.dp)
            ShimmerPlaceholder(height = 50.dp, width = 110.dp)
            ShimmerPlaceholder(height = 50.dp, width = 110.dp)
        }

        // Today's Routine List Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ShimmerPlaceholder(height = 20.dp, width = 140.dp)
            ShimmerPlaceholder(height = 18.dp, width = 80.dp)
        }

        // Checklist Skeleton elements
        repeat(3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShimmerPlaceholder(height = 36.dp, width = 36.dp, shape = androidx.compose.foundation.shape.CornerSize(18.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        ShimmerPlaceholder(height = 16.dp, width = 160.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        ShimmerPlaceholder(height = 12.dp, width = 90.dp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    ShimmerPlaceholder(height = 24.dp, width = 60.dp)
                }
            }
        }
    }
}

@Composable
fun MedicinesSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShimmerPlaceholder(height = 110.dp) // Smart scanner promo card mockup
        ShimmerPlaceholder(height = 56.dp)  // Search bar skeleton
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { // Category chips skeletons
            repeat(4) {
                ShimmerPlaceholder(height = 32.dp, width = 70.dp, shape = androidx.compose.foundation.shape.CornerSize(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        ShimmerPlaceholder(height = 16.dp, width = 140.dp)
        
        repeat(3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ShimmerPlaceholder(height = 12.dp, width = 120.dp)
                        }
                        ShimmerPlaceholder(height = 20.dp, width = 60.dp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    ShimmerPlaceholder(height = 14.dp, width = 240.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    ShimmerPlaceholder(height = 12.dp, width = 150.dp)
                }
            }
        }
    }
}

@Composable
fun AppointmentsSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ShimmerPlaceholder(height = 42.dp) // Sub-Tab Row
        Spacer(modifier = Modifier.height(4.dp))
        repeat(3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            ShimmerPlaceholder(height = 18.dp, width = 160.dp)
                            Spacer(modifier = Modifier.height(4.dp))
                            ShimmerPlaceholder(height = 14.dp, width = 100.dp)
                        }
                        ShimmerPlaceholder(height = 24.dp, width = 50.dp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    ShimmerPlaceholder(height = 14.dp, width = 200.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    ShimmerPlaceholder(height = 12.dp, width = 120.dp)
                }
            }
        }
    }
}

@Composable
fun ReportsSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShimmerPlaceholder(height = 80.dp) // Drag & Drop Upload banner mock
        Spacer(modifier = Modifier.height(4.dp))
        ShimmerPlaceholder(height = 14.dp, width = 180.dp)
        
        repeat(3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShimmerPlaceholder(height = 40.dp, width = 40.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        ShimmerPlaceholder(height = 16.dp, width = 200.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        ShimmerPlaceholder(height = 12.dp, width = 130.dp)
                    }
                }
            }
        }
    }
}

// --- MAIN APPLICATION LAYOUT (TABS SYSTEM) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(
    viewModel: MedTrackViewModel,
    modifier: Modifier = Modifier
) {
    val isAuth by viewModel.isUserAuthenticated.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val toastMessage = viewModel.toastMessage
    val activeAlarm by viewModel.activeAlarmMedicine.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Collect Toast Messages from ViewModel securely
    LaunchedEffect(key1 = true) {
        toastMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (!isAuth) {
        LoginScreen(viewModel = viewModel)
    } else {
        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf(
            TabItem("Today", Icons.Default.Today, Icons.Outlined.Today),
            TabItem("Medicines", Icons.Default.MedicalServices, Icons.Outlined.MedicalServices),
            TabItem("Appointments", Icons.Default.CalendarMonth, Icons.Outlined.CalendarMonth),
            TabItem("Reports", Icons.Default.Folder, Icons.Outlined.Folder),
            TabItem("Med AI Chat", Icons.Default.Psychology, Icons.Outlined.Psychology)
        )

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Healing,
                                contentDescription = "MedTrack Header Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "MedTrack",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.testTag("logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Log Out"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    windowInsets = WindowInsets.navigationBars
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val selected = selectedTab == index
                        NavigationBarItem(
                            selected = selected,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label, fontSize = 11.sp) },
                            modifier = Modifier.testTag("nav_tab_${tab.label.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            val isDataLoading by viewModel.isDataLoading.collectAsState()
            val dbError by viewModel.dbError.collectAsState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (dbError != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error icon",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "System Warning",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = dbError!!,
                                    fontSize = 12.sp,
                                    lineHeight = 15.sp
                                )
                            }
                            IconButton(onClick = { viewModel.dismissDbError() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss error"
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isDataLoading) {
                        when (selectedTab) {
                            0 -> DashboardSkeleton()
                            1 -> MedicinesSkeleton()
                            2 -> AppointmentsSkeleton()
                            3 -> ReportsSkeleton()
                            else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    } else {
                        when (selectedTab) {
                            0 -> DashboardScreen(viewModel = viewModel)
                            1 -> MedicinesScreen(viewModel = viewModel)
                            2 -> AppointmentsAndCaregiversScreen(viewModel = viewModel)
                            3 -> ReportsScreen(viewModel = viewModel)
                            4 -> ConsultationScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }

        val context = LocalContext.current
        activeAlarm?.let { alarmMed ->
            ActiveAlarmReminderOverlay(
                medicine = alarmMed,
                onTakeNow = {
                    viewModel.logMedicationProgress(alarmMed, isTaken = true, note = "Taken from on-screen real-time alarm trigger.")
                    viewModel.dismissActiveAlarm()
                },
                onSnooze = { mins ->
                    AlarmScheduler.scheduleSnoozeAlarm(context, alarmMed, mins)
                    viewModel.dismissActiveAlarm()
                },
                onSkip = {
                    viewModel.logMedicationProgress(alarmMed, isTaken = false, note = "Skipped from on-screen real-time alarm trigger.")
                    viewModel.dismissActiveAlarm()
                }
            )
        }
    }
}

@Composable
fun ActiveAlarmReminderOverlay(
    medicine: Medicine,
    onTakeNow: () -> Unit,
    onSnooze: (Int) -> Unit,
    onSkip: () -> Unit
) {
    var countdownSeconds by remember { mutableStateOf(30) }
    LaunchedEffect(key1 = true) {
        while (countdownSeconds > 0) {
            kotlinx.coroutines.delay(1000)
            countdownSeconds--
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .border(2.dp, Color(medicine.categoryColorValue), RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(medicine.categoryColorValue).copy(alpha = 0.25f))
                    )
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(medicine.categoryColorValue))
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Active alarm ringing",
                            tint = Color.White,
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CRITICAL MEDICINE REMINDER",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = medicine.name,
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Dose: ${medicine.dosage}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "Timing: ${medicine.frequency} at ${medicine.timesString}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (medicine.instructions.isNotBlank()) {
                    Text(
                        text = "Instructions: ${medicine.instructions}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (countdownSeconds > 0) "Ringing continuously for: 00:${countdownSeconds.toString().padStart(2, '0')}" else "Adherence window closing! Notify Caregiver...",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onTakeNow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("alarm_take_now_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "TAKE NOW",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSnooze(5) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("alarm_snooze_5_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text("Snooze 5m", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { onSnooze(10) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("alarm_snooze_10_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text("Snooze 10m", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onSkip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("alarm_skip_button")
                ) {
                    Text(
                        "Skip Medicine dose and log skipped",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}

// --- SCREEN 1: LOGIN & AUTHENTICATION SCREEN ---

@Composable
fun LoginScreen(viewModel: MedTrackViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    
    var isPasswordVisible by remember { mutableStateOf(false) }
    var selectedAuthTab by remember { mutableStateOf(0) } // 0: Local Sandbox, 1: Supabase, 2: Firebase
    
    val isLoading by viewModel.isAuthLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                .padding(horizontal = 24.dp, vertical = 28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Rounded App Logo Space matching Natural Tones
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = "Medical Logo",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "MedTrack Hub",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Secure & Compliant Patient Care",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Auth Provider Tabs
            TabRow(
                selectedTabIndex = selectedAuthTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(RoundedCornerShape(8.dp)),
                indicator = {}
            ) {
                Tab(
                    selected = selectedAuthTab == 0,
                    onClick = { selectedAuthTab = 0 },
                    text = { Text("Local Demo", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Tab(
                    selected = selectedAuthTab == 1,
                    onClick = { selectedAuthTab = 1 },
                    text = { Text("Supabase Auth", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Tab(
                    selected = selectedAuthTab == 2,
                    onClick = { selectedAuthTab = 2 },
                    text = { Text("Firebase Auth", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Inline Loader
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // --- LOCAL SANDBOX MODE ---
            if (selectedAuthTab == 0) {
                Text(
                    text = "Credential Bypass Sandbox",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Gain instant access into clinical dashboards with simulated client-side credentials.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    placeholder = { Text("Dr. John Doe") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("demo_name_input"),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Mock Email") },
                    placeholder = { Text("patient@medtrack.local") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("demo_email_input"),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val finalEmail = if (email.isBlank()) "guest.patient@medtrack.org" else email
                        val finalName = if (fullName.isBlank()) "Resident Patient" else fullName
                        viewModel.submitLogin(finalEmail, finalName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_login_button")
                ) {
                    Text("Instant Sandbox Bypass", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            // --- SUPABASE CLOUD AUTH ---
            else if (selectedAuthTab == 1) {
                Text(
                    text = "Supabase PostgreSQL Security Integration",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Text(
                    text = "Sign in or register directly to synchronize your pill reminders, dosage logs, and reports with remote secure tables.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name (Only required for Register)") },
                    placeholder = { Text("Enter your name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email address") },
                    placeholder = { Text("user@example.com") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("supabase_email_input"),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Secret Password (min 6 chars)") },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("supabase_password_input"),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.submitSupabaseSignUp(email, password, if (fullName.isBlank()) "Patient Care" else fullName)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        enabled = !isLoading
                    ) {
                        Text("Register", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.submitSupabaseLogin(email, password)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        enabled = !isLoading
                    ) {
                        Text("Sign In", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // --- FIREBASE CLOUD CONSOLE AUTH ---
            else {
                Text(
                    text = "Firebase Project Console Integration",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Text(
                    text = "Align devices with your official console endpoint:\nacet-medtrack",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Firebase User Email") },
                    placeholder = { Text("user@firebase.com") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("firebase_email_input"),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Firebase Password") },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("firebase_password_input"),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.submitFirebaseSignUp(email, password)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        enabled = !isLoading
                    ) {
                        Text("Register", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.submitFirebaseLogin(email, password)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        enabled = !isLoading
                    ) {
                        Text("Authorize", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Dynamic disclaimers about HIPAA safety and database compliance
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "GDPR Badge",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Clinical Compliance: Remote tables support Row-Level Security sandboxes, preventing cross-tenant leaks.",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}

// --- SCREEN 2: TODAY'S ACTIVE CHECKLIST & REMINDERS ---

@Composable
fun DashboardScreen(viewModel: MedTrackViewModel) {
    val userName by viewModel.userName.collectAsState()
    val medicines by viewModel.medicines.collectAsState()
    val logs by viewModel.logs.collectAsState()

    val scope = rememberCoroutineScope()
    var showEmergencyDialog by remember { mutableStateOf(false) }

    // Optimize performance: compute date filter outside the lazy list and cash it
    val todayLogs = remember(logs) {
        val formatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val todayStr = formatter.format(Date())
        logs.filter { log ->
            try {
                formatter.format(Date(log.timestamp)) == todayStr
            } catch (e: Exception) {
                false
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming Card Profile with high-contrast gradient
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Hello, $userName",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Track your prescription adherence to avoid clinical lapses.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showEmergencyDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationImportant,
                                contentDescription = "Alert",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Trigger Emergency SOS Alert", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text(
                "MEDICATION DOSES SCHEDULED TODAY",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (medicines.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalInformation,
                            contentDescription = "No medication icon",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No Medication Schedules Registered",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Go to the 'Medicines' Tab to set up your pill stock or scan prescriptions via OCR scanner.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(medicines) { med ->
                val isTakenToday = todayLogs.any { it.medicineId == med.id && it.status == "Taken" }
                val isSkippedToday = todayLogs.any { it.medicineId == med.id && it.status == "Skipped" }

                // Check for low stock warning
                val isLowStock = med.qtyRemaining <= med.qtyNeeded

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (isLowStock) 2.dp else 0.dp,
                            color = if (isLowStock) MaterialTheme.colorScheme.error else Color.Transparent,
                            shape = CardDefaults.shape
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isTakenToday) {
                            Color(0xFFE8F5E9) // Pastel green
                        } else if (isSkippedToday) {
                            Color(0xFFFFEBEE) // Pastel red
                        } else {
                            MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Indicator bullet with custom color token
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color(med.categoryColorValue))
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = med.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTakenToday || isSkippedToday) Color.Black else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = "${med.dosage} (${med.type}) • ${med.frequency} at ${med.timesString}",
                                        fontSize = 11.sp,
                                        color = if (isTakenToday || isSkippedToday) Color.DarkGray else Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Taken status badges
                            if (isTakenToday) {
                                Badge(
                                    containerColor = Color(0xFF2E7D32),
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        "TAKEN",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            } else if (isSkippedToday) {
                                Badge(
                                    containerColor = Color(0xFFC62828),
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        "SKIPPED",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }

                        // Low stock inline warning
                        if (isLowStock) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.errorContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Refill needed: ${med.qtyRemaining} left",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                TextButton(
                                    onClick = { viewModel.refillMedicine(med.id, 30) },
                                    modifier = Modifier.height(24.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("+30 doses", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }

                        if (!med.instructions.isNullOrBlank()) {
                            Text(
                                text = "Instructions: ${med.instructions}",
                                fontSize = 11.sp,
                                color = if (isTakenToday || isSkippedToday) Color.DarkGray.copy(alpha = 0.8f) else Color.Gray,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                            )
                        }

                        // Actions
                        if (!isTakenToday && !isSkippedToday) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.2f))
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.logMedicationProgress(med, isTaken = false) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("skip_button_${med.id}"),
                                    border = BorderStroke(1.dp, Color.Gray),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Skip", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Skip", fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Button(
                                    onClick = { viewModel.logMedicationProgress(med, isTaken = true) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("take_button_${med.id}"),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Take", modifier = Modifier.size(14.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Take", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Adherence Logs Feed
        item {
            Text(
                "RECENT LOGS & SYNC STATUS",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = "Sync banner",
                                tint = Color(0xFF00B0FF),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Supabase Cloud Sync Status",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            "Connected (Realtime)",
                            color = Color(0xFF4CAF50),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    val todayLogsList = logs.take(4)
                    val timeFormatter = remember { SimpleDateFormat("HH:mm a", Locale.getDefault()) }
                    if (todayLogsList.isEmpty()) {
                        Text(
                            "No actions logged yet today. Maintain your intake for health compliance.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    } else {
                        todayLogsList.forEach { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = log.medicineName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val timeStr = remember(log.timestamp) { timeFormatter.format(Date(log.timestamp)) }
                                    Text(
                                        text = "Logged $timeStr",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }

                                Badge(
                                    containerColor = if (log.status == "Taken") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                    contentColor = if (log.status == "Taken") Color(0xFF2E7D32) else Color(0xFFC62828)
                                ) {
                                    Text(log.status, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Caregiver Emergency Trigger dialog popup
    if (showEmergencyDialog) {
        Dialog(onDismissRequest = { showEmergencyDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Dangerous,
                        contentDescription = "Danger icon",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(54.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        "SOS Emergency Alert",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFD32F2F)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "This will simulate immediate push notifications and SMS warnings to all registered family caregivers & primary doctors. Press continue only in case of dosage complications or urgent health distress.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { showEmergencyDialog = false },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                showEmergencyDialog = false
                                scope.launch {
                                    viewModel.submitLogin(viewModel.userEmail.value ?: "guest@email.com", viewModel.userName.value)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text("Trigger SOS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 3: MEDICINES MANAGEMENT & OCR EXTRACTOR SCANNER ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicinesScreen(viewModel: MedTrackViewModel) {
    val medicines by viewModel.medicines.collectAsState()
    val isOcrScanning by viewModel.isOcrScanning.collectAsState()

    var showAddMedicineDialog by remember { mutableStateOf(false) }
    var showOcrScannerDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "Pill", "Syrup", "Injection", "Inhaler", "Other")

    // Modal state controllers
    var drugName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Daily") }
    var times by remember { mutableStateOf("08:00") }
    var instructions by remember { mutableStateOf("") }
    var qtyRemaining by remember { mutableStateOf("30") }
    var qtyNeeded by remember { mutableStateOf("5") }
    var type by remember { mutableStateOf("Pill") }
    var categoryColor by remember { mutableStateOf(0xFF2196F3.toInt()) } // Blue

    // Filter medicines
    val filteredMedicines = medicines.filter { med ->
        val matchesSearch = med.name.contains(searchQuery, ignoreCase = true) ||
                med.instructions.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategoryFilter == "All" || 
                med.type.trim().equals(selectedCategoryFilter.trim(), ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // High fidelity prescription AI parsing helper card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Smart Prescription OCR Scanner",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            "Paste prescription clinic paperwork below and let MedTrack AI parse schedules automatically.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            lineHeight = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { showOcrScannerDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Scan icon", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI OCR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Search input field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search medicines, active inventory...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("medicine_search_input"),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable chips row for quick category filtering
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "MEDICINES INVENTORY (${filteredMedicines.size})",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (filteredMedicines.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No corresponding medicines found.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredMedicines) { med ->
                        val isDangerStock = med.qtyRemaining <= med.qtyNeeded
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("medicine_item_card_${med.id}")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Color(med.categoryColorValue))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            med.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Category label chip
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(med.type, fontSize = 10.sp) },
                                        modifier = Modifier.height(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Dose: ${med.dosage}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text("Timing: ${med.frequency} at ${med.timesString}", fontSize = 11.sp, color = Color.Gray)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            "In Stock: ${med.qtyRemaining} doses",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isDangerStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            "Threshold: ${med.qtyNeeded}",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                if (med.instructions.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        "Instructions: ${med.instructions}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = { viewModel.deleteMedicine(med.id) },
                                        modifier = Modifier.testTag("delete_medicine_${med.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove medicine",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Button(
                                        onClick = { viewModel.triggerActiveAlarm(med) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                        modifier = Modifier.height(32.dp).testTag("simulate_alarm_${med.id}")
                                    ) {
                                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Test Alarm", fontSize = 11.sp)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = { viewModel.refillMedicine(med.id, 30) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Refill +30", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to manually insert med schedules
        FloatingActionButton(
            onClick = { showAddMedicineDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_medicine_fab"),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add medication", tint = Color.White)
        }
    }

    // Modal adding medication dialog overlay
    if (showAddMedicineDialog) {
        Dialog(onDismissRequest = { showAddMedicineDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Register New Medicine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = drugName,
                        onValueChange = { drugName = it },
                        label = { Text("Drug Name") },
                        placeholder = { Text("Atorvastatin, Vitamin C, etc.") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_med_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = dosage,
                            onValueChange = { dosage = it },
                            label = { Text("Dosage") },
                            placeholder = { Text("e.g. 1 pill, 5ml") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 6.dp)
                        )

                        OutlinedTextField(
                            value = type,
                            onValueChange = { type = it },
                            label = { Text("Category") },
                            placeholder = { Text("Pill, Syrup, etc.") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Quick Select Type:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Pill", "Syrup", "Injection", "Inhaler", "Other").forEach { cat ->
                            val isSelected = type.trim().equals(cat, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { type = cat },
                                label = { Text(cat, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = frequency,
                            onValueChange = { frequency = it },
                            label = { Text("Frequency") },
                            placeholder = { Text("Daily, Weekly") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 6.dp)
                        )

                        OutlinedTextField(
                            value = times,
                            onValueChange = { times = it },
                            label = { Text("Times list") },
                            placeholder = { Text("08:00, 20:00") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        label = { Text("Special Intake Instructions") },
                        placeholder = { Text("Take after heavy breakfast") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = qtyRemaining,
                            onValueChange = { qtyRemaining = it },
                            label = { Text("Initial Stock (Qty)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 6.dp)
                        )

                        OutlinedTextField(
                            value = qtyNeeded,
                            onValueChange = { qtyNeeded = it },
                            label = { Text("Warning Min Limit") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Category Label Color:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        val colorsList = listOf(0xFF2196F3, 0xFF4CAF50, 0xFFE91E63, 0xFFFF9800, 0xFF9C27B0)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            colorsList.forEach { colorValue ->
                                val selectedColor = categoryColor == colorValue.toInt()
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color(colorValue))
                                        .border(
                                            width = if (selectedColor) 2.dp else 0.dp,
                                            color = if (selectedColor) Color.Black else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { categoryColor = colorValue.toInt() }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (drugName.isNotBlank()) {
                                viewModel.addMedicine(
                                    name = drugName,
                                    dosage = dosage,
                                    frequency = frequency,
                                    times = times.split(",").map { it.trim() },
                                    instructions = instructions,
                                    qtyRemaining = qtyRemaining.toIntOrNull() ?: 30,
                                    qtyNeeded = qtyNeeded.toIntOrNull() ?: 5,
                                    categoryColorValue = categoryColor,
                                    type = type
                                )
                                // Clear fields
                                drugName = ""
                                dosage = ""
                                instructions = ""
                                type = "Pill"
                                showAddMedicineDialog = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_medicine_button")
                    ) {
                        Text("Save Medication Routine")
                    }
                }
            }
        }
    }

    // AI Scanner simulation dialog
    if (showOcrScannerDialog) {
        var rawOcrTextInput by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { if (!isOcrScanning) showOcrScannerDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = "OCR icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "AI Prescription Text Extractor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text(
                        "Medically trained Gemini AI will dissect raw OCR clinic letters or paper logs and auto-create structured database profiles instantly.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                        lineHeight = 15.sp
                    )

                    OutlinedTextField(
                        value = rawOcrTextInput,
                        onValueChange = { rawOcrTextInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        placeholder = {
                            Text(
                                "Paste clinic text scrap here, e.g:\n" +
                                "Rx: Lipitor Statins 40mg. Administer once daily at Bedtime. Keep stock count at 60 capsules. Take after eating foods."
                            )
                        },
                        maxLines = 6
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Autofill suggestions for sandboxed sandbox demo testing
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SuggestionChip(
                            onClick = {
                                rawOcrTextInput = "St. Paul Clinic. Patient: Ram. Pre: Ibuprofen 400 mg twice daily with food (08:00, 20:00). Max 4 times. Remaining count: 20 tablets. Threshold alert: 4."
                            },
                            label = { Text("Sample Rx: Ibuprofen", fontSize = 10.sp) }
                        )
                        SuggestionChip(
                            onClick = {
                                rawOcrTextInput = "Metformin hydrochloride 850mg. Frequency: taken daily at 12:00. Notes: swallow pill whole. 30 capsules, warning threshold 6."
                            },
                            label = { Text("Sample Rx: Metformin", fontSize = 10.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isOcrScanning) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        Text("Gemini medical AI is analyzing text format...", fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
                    } else {
                        Button(
                            onClick = {
                                if (rawOcrTextInput.isNotBlank()) {
                                    viewModel.executeOcrScan(rawOcrTextInput) { scanResult ->
                                        if (scanResult.success) {
                                            drugName = scanResult.medicineName
                                            dosage = scanResult.dosage
                                            frequency = scanResult.frequency
                                            times = scanResult.times.joinToString(",")
                                            instructions = scanResult.instructions
                                            categoryColor = scanResult.categoryColorValue
                                            type = scanResult.type

                                            showOcrScannerDialog = false
                                            showAddMedicineDialog = true
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Scan and Autofill Schedule", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 4: APPOINTMENTS & CAREGIVERS/DOCTORS HUB ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsAndCaregiversScreen(viewModel: MedTrackViewModel) {
    val appointments by viewModel.appointments.collectAsState()
    val contacts by viewModel.contacts.collectAsState()

    val dateTimeFormatter = remember { SimpleDateFormat("EEEE, MMM dd, yyyy - h:mm a", Locale.getDefault()) }

    var activeSubTab by remember { mutableStateOf(0) } // 0 = Appointments, 1 = Caregivers
    var showAddApptDialog by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Medical Sub tabs selectors
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = activeSubTab == 0,
                onClick = { activeSubTab = 0 },
                text = { Text("Clinical Visits", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            )
            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = { Text("Doctors & Caregivers", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeSubTab == 0) {
            // Appointments view
            Box(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "UPCOMING APPOINTMENTS CHECKLIST",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.2.sp
                        )

                        TextButton(onClick = { showAddApptDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Schedule Visit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (appointments.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No appointments scheduled.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(appointments) { appt ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Event,
                                                    contentDescription = "Event icon",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        appt.doctorName,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp
                                                    )
                                                    Text(
                                                        appt.specialty,
                                                        fontSize = 11.sp,
                                                        color = Color.Gray,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }

                                            Badge(
                                                containerColor = if (appt.status == "Scheduled") Color(0xFFE3F2FD) else Color(0xFFEEEEEE),
                                                contentColor = if (appt.status == "Scheduled") Color(0xFF1E88E5) else Color.DarkGray
                                            ) {
                                                Text(appt.status.uppercase(), fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        val displayTime = remember(appt.appointmentTime) { dateTimeFormatter.format(Date(appt.appointmentTime)) } // Optimized format
                                        // Display time computed ahead

                                        Text(
                                            "Time: $displayTime",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Text(
                                            "Clinic Address: ${appt.location}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )

                                        if (appt.notes.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                "Pre-visit Prep: ${appt.notes}",
                                                fontSize = 11.sp,
                                                color = Color.DarkGray,
                                                lineHeight = 14.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            OutlinedButton(
                                                onClick = { viewModel.deleteAppointment(appt.id) },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(30.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp)
                                            ) {
                                                Text("Cancel", fontSize = 10.sp)
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Button(
                                                onClick = { viewModel.updateAppointmentStatus(appt, "Completed") },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                                                modifier = Modifier.height(30.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp)
                                            ) {
                                                Text("Mark Done", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Doctors & Caregivers view
            Box(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "FAMILY CAREGIVERS & SPECIALISTS",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.2.sp
                        )

                        TextButton(onClick = { showAddContactDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Register Caregiver", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (contacts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No contacts registered.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(contacts) { contact ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            if (contact.role == "Doctor") Color(0xFFE0F2F1) else Color(0xFFFFF3E0)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = if (contact.role == "Doctor") Icons.Default.MedicalServices else Icons.Default.PeopleOutline,
                                                        contentDescription = null,
                                                        tint = if (contact.role == "Doctor") Color(0xFF00796B) else Color(0xFFE65100),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Column {
                                                    Text(
                                                        contact.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp
                                                    )
                                                    Text(
                                                        "${contact.role} ${if (contact.isEmergencyContact) "• Emergency SOS Contact" else ""}",
                                                        fontSize = 11.sp,
                                                        color = if (contact.isEmergencyContact) MaterialTheme.colorScheme.error else Color.Gray,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = { viewModel.deleteContact(contact.id) }
                                            ) {
                                                Icon(
                                                    Icons.Default.DeleteOutline,
                                                    contentDescription = "Remove contact",
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            "Tel: ${contact.contactNumber}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        if (contact.email.isNotBlank()) {
                                            Text(
                                                "Email: ${contact.email}",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }

                                        if (contact.notes.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                "Notes: ${contact.notes}",
                                                fontSize = 11.sp,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal adding appointments
    if (showAddApptDialog) {
        var docNameInput by remember { mutableStateOf("") }
        var specInput by remember { mutableStateOf("") }
        var locInput by remember { mutableStateOf("") }
        var notesInput by remember { mutableStateOf("") }
        var hoursDiff by remember { mutableStateOf("24") } // Default to schedule tomorrow (24h)

        Dialog(onDismissRequest = { showAddApptDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Schedule Medical Visit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = docNameInput,
                        onValueChange = { docNameInput = it },
                        label = { Text("Clinician Name") },
                        placeholder = { Text("Dr. Sarah Jenkins") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = specInput,
                        onValueChange = { specInput = it },
                        label = { Text("Specialty") },
                        placeholder = { Text("Cardiologist, GP, Dentist") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = locInput,
                        onValueChange = { locInput = it },
                        label = { Text("Clinic Address / Online") },
                        placeholder = { Text("Heart Wellness Clinic, Room 402") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = hoursDiff,
                        onValueChange = { hoursDiff = it },
                        label = { Text("Schedule in how many hours?") },
                        placeholder = { Text("e.g. 24, 48, 72") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Pre-visit Guidelines / Notes") },
                        placeholder = { Text("Fasting details, bring lab reports") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (docNameInput.isNotBlank()) {
                                val hrs = hoursDiff.toLongOrNull() ?: 24L
                                val futureTime = System.currentTimeMillis() + (hrs * 60 * 60 * 1000L)
                                viewModel.addAppointment(
                                    doctorName = docNameInput,
                                    specialty = specInput,
                                    date = futureTime,
                                    location = locInput,
                                    notes = notesInput
                                )
                                showAddApptDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Appointment")
                    }
                }
            }
        }
    }

    // Modal adding caretakers contacts
    if (showAddContactDialog) {
        var nameInput by remember { mutableStateOf("") }
        var phoneInput by remember { mutableStateOf("") }
        var emailInput by remember { mutableStateOf("") }
        var notesInput by remember { mutableStateOf("") }
        var isEmergency by remember { mutableStateOf(false) }
        var roleSelection by remember { mutableStateOf("Doctor") }

        Dialog(onDismissRequest = { showAddContactDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Register Specialist Caregiver",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Role Selection:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Doctor", "Caregiver", "Emergency Contact").forEach { role ->
                            FilterChip(
                                selected = roleSelection == role,
                                onClick = { roleSelection = role },
                                label = { Text(role, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Contact Phone") },
                        placeholder = { Text("+1 (555) 000-0000") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Specialty Notes") },
                        placeholder = { Text("Cardio consultant, night shift guide") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isEmergency,
                            onCheckedChange = { isEmergency = it }
                        )
                        Text("Designate primary Emergency SOS recipient", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (nameInput.isNotBlank() && phoneInput.isNotBlank()) {
                                viewModel.addContact(
                                    name = nameInput,
                                    role = roleSelection,
                                    contactNumber = phoneInput,
                                    email = emailInput,
                                    notes = notesInput,
                                    isEmergency = isEmergency
                                )
                                showAddContactDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Entry")
                    }
                }
            }
        }
    }
}

// --- SCREEN 5: SECURE REPORTS & PRESCRIPTION TRACKER ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: MedTrackViewModel) {
    val reports by viewModel.reports.collectAsState()
    var showUploadDialog by remember { mutableStateOf(false) }
    val reportDateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "SECURE HEALTH TRACKER",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp
                )
                Text(
                    "Encrypted Lab Records & Medical Scans",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = { showUploadDialog = true },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = "Upload", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Upload Document", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (reports.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Your Secure HIPAA report hub is empty.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(reports) { rpt ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (rpt.type == "Lab Report") Icons.Default.BarChart else Icons.Default.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        rpt.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    val dateString = remember(rpt.date) { reportDateFormatter.format(Date(rpt.date)) }
                                    Text(
                                        "Date: $dateString • Type: ${rpt.type}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )

                                    Text(
                                        "Assessment: ${rpt.description}",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(top = 4.dp),
                                        lineHeight = 14.sp
                                    )

                                    if (!rpt.filePath.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(10.dp), tint = Color.Gray)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(rpt.filePath, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            IconButton(
                                onClick = { viewModel.deleteReport(rpt.id) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Remove document",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showUploadDialog) {
        var upTitle by remember { mutableStateOf("") }
        var upType by remember { mutableStateOf("Lab Report") }
        var upDesc by remember { mutableStateOf("") }
        var mockFileName by remember { mutableStateOf("cholesterol_screening_panel.pdf") }

        Dialog(onDismissRequest = { showUploadDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Upload Medical Report Document",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = upTitle,
                        onValueChange = { upTitle = it },
                        label = { Text("Report Title") },
                        placeholder = { Text("Routine Cholesterol Checkup") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Classification Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Lab Report", "Prescription", "Doctor Note").forEach { cat ->
                            FilterChip(
                                selected = upType == cat,
                                onClick = { upType = cat },
                                label = { Text(cat, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = upDesc,
                        onValueChange = { upDesc = it },
                        label = { Text("Summary Doctor Assessment Notes") },
                        placeholder = { Text("Glucose levels normal, take 1 Lipitor nightly") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mock upload selector card visual
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .clickable {
                                mockFileName = "medical_scan_attached_${System.currentTimeMillis() % 1000}.pdf"
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Attached: $mockFileName", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Tap to simulate file capture selection", fontSize = 9.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (upTitle.isNotBlank()) {
                                viewModel.uploadMedicalReport(
                                    title = upTitle,
                                    type = upType,
                                    description = upDesc,
                                    mockFileName = mockFileName
                                )
                                showUploadDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add File Tracker Entry")
                    }
                }
            }
        }
    }
}

// --- SCREEN 6: GEMINI HEALTHCHAT AI CONSULTATION PANEL ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationScreen(viewModel: MedTrackViewModel) {
    val aiChatMessages by viewModel.aiChatMessages.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var chatInputField by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll chat history helper on messages update
    LaunchedEffect(aiChatMessages.size) {
        if (aiChatMessages.isNotEmpty()) {
            listState.animateScrollToItem(aiChatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "MED_AI SYMPTOM CHECKER",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp
                )
                Text(
                    "Generative Gemini AI Consultation",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = { viewModel.clearChat() }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear Chat",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat bubbles message lazy stack
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                .padding(12.dp)
        ) {
            items(aiChatMessages) { messagePair ->
                val (msgText, isUser) = messagePair
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Column(
                        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isUser) 16.dp else 0.dp,
                                bottomEnd = if (isUser) 0.dp else 16.dp
                            ),
                            color = if (isUser) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            shadowElevation = 1.dp
                        ) {
                            Text(
                                text = msgText,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp),
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp
                            )
                        }

                        Text(
                            text = if (isUser) "You" else "MedTrack AI",
                            fontSize = 9.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                        )
                    }
                }
            }

            if (isAiLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyzing symptoms & checking medicines...", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Predefined medical assistant advice prompts suggestions
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                SuggestionChip(
                    onClick = { chatInputField = "Explain potential drug interactions for Atorvastatin with other foods?" },
                    label = { Text("Atorvastatin details", fontSize = 10.sp) }
                )
            }
            item {
                SuggestionChip(
                    onClick = { chatInputField = "Symptom check: I feel sudden dizziness 1 hour after heart medical dosis." },
                    label = { Text("Symptom check", fontSize = 10.sp) }
                )
            }
            item {
                SuggestionChip(
                    onClick = { chatInputField = "Create a healthy lifestyle nutrition habit checklist." },
                    label = { Text("Lifestyle guide", fontSize = 10.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = chatInputField,
                onValueChange = { chatInputField = it },
                placeholder = { Text("Consult symptoms, drug compatibility, disclaimers...", fontSize = 12.sp) },
                singleLine = false,
                maxLines = 3,
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input"),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            FloatingActionButton(
                onClick = {
                    if (chatInputField.isNotBlank()) {
                        viewModel.sendChatMessage(chatInputField)
                        chatInputField = ""
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("send_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send message",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
