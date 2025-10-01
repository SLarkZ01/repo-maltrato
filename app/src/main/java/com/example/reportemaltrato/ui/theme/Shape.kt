package com.example.reportemaltrato.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

/**
 * Definición centralizada de esquinas/redondeos usados en la app.
 * - small: Controles pequeños (chips, badges, etc.)
 * - medium: Tarjetas generales o contenedores intermedios.
 * - large: Contenedores principales o paneles destacados.
 */
val AppShapes = Shapes(
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(24.dp)
)
