package com.example.reportemaltrato.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard as M3ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Fondo con gradiente usando la paleta de Material3.
 * Ahora el gradiente mezcla primary con un toque de secondary para un look más vivo.
 */
@Composable
fun GradientBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val gradient = Brush.verticalGradient(
        colors = listOf(
            cs.primary.copy(alpha = 0.95f),
            cs.secondary.copy(alpha = 0.75f),
            cs.background
        )
    )
    Box(modifier = modifier.background(gradient)) { content() }
}

/** Título de sección con tipografía Material3 y acento secundario. */
@Suppress("unused")
@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.secondary,
        modifier = modifier.padding(bottom = 6.dp)
    )
}

/** Tarjeta elevada Material3 reutilizable. */
@Composable
fun ElevatedCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    M3ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) { content() }
}
