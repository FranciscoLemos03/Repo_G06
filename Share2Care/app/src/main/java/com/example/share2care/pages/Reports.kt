package com.example.share2care.pages

import android.graphics.Typeface
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.CircleButton
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun ReportsScreen(navController: NavController, authViewModel: AuthViewModel) {
    val firestoreViewModel = FirestoreViewModel()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 20.dp,  bottom = 80.dp),
        color = MaterialTheme.colorScheme.background
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Beneficiários Report",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground)

        }
    }

        Spacer(modifier = Modifier.height(50.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            CircleButton (
                onClick = {
                    navController.navigate("home"){
                    }
                },
                R.drawable.back
            )
        }


            Spacer(modifier = Modifier.height(20.dp))
            PieChartComposable(firestoreViewModel)
    }
}

@Composable
fun PieChartComposable(firestoreViewModel : FirestoreViewModel) {
    val entries = remember { mutableListOf<PieEntry>() }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch data from Firestore when the composable is first displayed
    LaunchedEffect(Unit) {
        try {
            val data = withContext(Dispatchers.IO) { firestoreViewModel.fetchBeneficiariosGroupedByNacionalidade() }
            entries.clear()
            entries.addAll(data)
        } catch (_: Exception) {

        } finally {
            isLoading = false
        }
    }

    if (isLoading) {

        CircularProgressIndicator()
    } else {
        val dataSet = PieDataSet(entries, "")

        val pieData = PieData(dataSet)

        dataSet.colors = listOf(
            Color(179, 0, 255, 255).toArgb(),
            Color(135, 0, 255, 255).toArgb(),
            Color(90, 0, 255, 255).toArgb(),
            Color(0, 5, 255, 255).toArgb(),
        )

        dataSet.valueTextColor = Color.Black.hashCode()

        dataSet.valueTextSize = 30f

        dataSet.valueTypeface = Typeface.DEFAULT_BOLD

        dataSet.sliceSpace = 8.49898f

        dataSet.selectionShift = 20f

        AndroidView(
            factory = { context ->
                PieChart(context).apply {
                    this.data = pieData
                    isDrawHoleEnabled = true
                    setHoleColor(Color.Transparent.hashCode())
                    animateY(1400)

                    setPadding(20, 60, 20, 0)

                    legend.apply {
                        // Increase text size for legend labels
                        textSize = 18f // Increase legend label size (change as needed)

                        textColor = Color.White.hashCode()


                        // Set form size for the legend items
                        formSize = 20f // Set the size of the legend indicator (colored circle)

                        // Position the legend closer to the chart
                        //verticalAlignment = Legend.LegendVerticalAlignment.TOP // Move the legend to the top
                        horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT // Align to the left
                    }
                }
            },
            update = { chart ->
                chart.data = pieData
                chart.invalidate()
            }
        )
    }
}
