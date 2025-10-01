package com.example.reportemaltrato.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Fondo con gradiente vertical reutilizable para darle profundidad y
 * cohesión visual a pantallas principales. Recibe contenido Compose.
 */
@Composable
fun GradientBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colors.primary.copy(alpha = 0.90f),
            MaterialTheme.colors.primary.copy(alpha = 0.75f),
            MaterialTheme.colors.background
        )
    )
    Box(modifier = modifier.background(gradient)) {
        content()
    }
}

/** Título de sección estándar con énfasis en color primario. */
@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colors.primary,
        modifier = modifier.padding(bottom = 4.dp)
    )
}

/**
 * Tarjeta elevada con esquinas redondeadas y elevación uniforme.
 * Envuelve contenido arbitrario.
 */
@Composable
fun ElevatedCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        elevation = 6.dp,
        shape = RoundedCornerShape(18.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) { content() }
}

/**
 * Pequeña insignia (badge) para mostrar estados, etiquetas o contadores.
 */
@Composable
fun Badge(text: String, background: Color, contentColor: Color = Color.White, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(50)),
        color = background,
        contentColor = contentColor,
        elevation = 2.dp
    ) {
        Text(text = text, style = MaterialTheme.typography.caption, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}
