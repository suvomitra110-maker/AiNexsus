package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProficiencyTimePoint
import com.example.model.SubjectCategory
import com.example.ui.theme.CyanBright
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavySurfaceCard
import com.example.ui.theme.NavySurfaceVariant

/**
 * Native Jetpack Compose implementation of D3 / Recharts multi-line & area chart logic.
 * Features:
 * - D3-style linear and cubic bezier Bézier smoothing curves
 * - Semi-transparent gradient area fills beneath lines
 * - Interactive crosshair / hover probe revealing exact subject proficiency metrics
 * - Multi-subject color-coded legend toggles
 * - Smooth entrance interpolation animation
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProficiencyGrowthChart(
    history: List<ProficiencyTimePoint>,
    selectedSubject: SubjectCategory?,
    onSubjectToggled: (SubjectCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (history.isEmpty()) return

    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(history) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(1f, animationSpec = tween(durationMillis = 800))
    }

    var selectedIndex by remember { mutableStateOf<Int?>(history.size - 1) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("proficiency_growth_chart_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Chart Header with D3 / Recharts indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📈 Proficiency Growth Over Time",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Dynamic calibration curve (0-100 scale)",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                // Metric tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "D3 / Recharts Engine",
                        color = CyanBright,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subject Filter Chips (Legend)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // "All" pill
                val isAll = selectedSubject == null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isAll) CyanBright.copy(alpha = 0.2f) else Color(0xFF090D16))
                        .border(1.dp, if (isAll) CyanBright else Color(0xFF334155), RoundedCornerShape(10.dp))
                        .clickable { onSubjectToggled(null) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "All Subjects",
                        color = if (isAll) CyanBright else Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                SubjectCategory.entries.forEach { subject ->
                    val isSubjectSelected = selectedSubject == subject
                    val color = Color(subject.chartColorHex)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSubjectSelected) color.copy(alpha = 0.2f) else Color(0xFF090D16))
                            .border(1.dp, if (isSubjectSelected) color else Color(0xFF334155), RoundedCornerShape(10.dp))
                            .clickable { onSubjectToggled(if (isSubjectSelected) null else subject) }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = subject.shortName,
                                color = if (isSubjectSelected) color else Color(0xFFCBD5E1),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Chart Area with D3 curves
            val pointCount = history.size
            val activeSubjects = if (selectedSubject != null) listOf(selectedSubject) else SubjectCategory.entries

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0A0F1D))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .pointerInput(pointCount) {
                            detectTapGestures { offset ->
                                val stepX = size.width / (pointCount - 1).coerceAtLeast(1)
                                val tappedIndex = ((offset.x + (stepX / 2)) / stepX).toInt()
                                    .coerceIn(0, pointCount - 1)
                                selectedIndex = tappedIndex
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val bottomPadding = 24.dp.toPx()
                    val chartHeight = h - bottomPadding

                    // 1. Draw horizontal grid lines (Y-axis 25%, 50%, 75%, 100%)
                    val gridLines = listOf(0.25f, 0.5f, 0.75f, 1f)
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                    gridLines.forEach { frac ->
                        val y = chartHeight * (1f - frac)
                        drawLine(
                            color = Color(0xFF1E293B),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f,
                            pathEffect = dashEffect
                        )
                    }

                    if (pointCount < 2) return@Canvas

                    val stepX = w / (pointCount - 1)

                    // 2. Draw lines and gradient areas for each active subject
                    activeSubjects.forEach { subject ->
                        val subjectColor = Color(subject.chartColorHex)
                        val points = history.mapIndexed { idx, item ->
                            val score = item.scores[subject] ?: 30f
                            val animScore = score * animatedProgress.value
                            val x = idx * stepX
                            val y = chartHeight * (1f - (animScore / 100f).coerceIn(0f, 1f))
                            Offset(x, y)
                        }

                        // Path with cubic Bézier smoothing (Recharts style curveMonotoneX)
                        val path = Path()
                        val areaPath = Path()

                        path.moveTo(points.first().x, points.first().y)
                        areaPath.moveTo(points.first().x, chartHeight)
                        areaPath.lineTo(points.first().x, points.first().y)

                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2f, p0.y)
                            val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2f, p1.y)
                            path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                            areaPath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                        }

                        areaPath.lineTo(points.last().x, chartHeight)
                        areaPath.close()

                        // Draw shaded area
                        drawPath(
                            path = areaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(subjectColor.copy(alpha = 0.25f), subjectColor.copy(alpha = 0.0f)),
                                startY = 0f,
                                endY = chartHeight
                            )
                        )

                        // Draw smooth line
                        drawPath(
                            path = path,
                            color = subjectColor,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        // Draw data points
                        points.forEachIndexed { pIdx, pt ->
                            val isCurrentSelected = pIdx == selectedIndex
                            drawCircle(
                                color = NavyDark,
                                radius = (if (isCurrentSelected) 6.dp else 4.dp).toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = subjectColor,
                                radius = (if (isCurrentSelected) 4.5.dp else 2.5.dp).toPx(),
                                center = pt
                            )
                        }
                    }

                    // 3. Draw vertical probe line if index is selected
                    selectedIndex?.let { selIdx ->
                        if (selIdx in 0 until pointCount) {
                            val probeX = selIdx * stepX
                            drawLine(
                                color = CyanBright.copy(alpha = 0.6f),
                                start = Offset(probeX, 0f),
                                end = Offset(probeX, chartHeight),
                                strokeWidth = 1.5.dp.toPx(),
                                pathEffect = dashEffect
                            )
                        }
                    }
                }
            }

            // X-Axis Day Labels
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                history.forEachIndexed { idx, point ->
                    val isSelected = idx == selectedIndex
                    Text(
                        text = point.dayLabel,
                        color = if (isSelected) CyanBright else Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.clickable { selectedIndex = idx }
                    )
                }
            }

            // Interactive Tooltip Box (D3 hover tooltip preview)
            selectedIndex?.let { selIdx ->
                if (selIdx in history.indices) {
                    val currentPoint = history[selIdx]
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "📅 ${currentPoint.dayLabel} Proficiency Snapshot",
                                color = CyanBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                activeSubjects.forEach { subj ->
                                    val valScore = currentPoint.scores[subj] ?: 30f
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = subj.shortName,
                                            color = Color(subj.chartColorHex),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "${valScore.toInt()}%",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
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
