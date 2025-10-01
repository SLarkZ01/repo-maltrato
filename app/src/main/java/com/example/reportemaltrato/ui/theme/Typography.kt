package com.example.reportemaltrato.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

/**
 * Sistema tipográfico base de la app. Se apoya en la tipografía por defecto SansSerif.
 * Convenciones:
 * - h4/h5/h6: Encabezados jerárquicos en pantallas y tarjetas destacadas.
 * - subtitle1: Títulos de secciones o encabezados secundarios.
 * - body1: Texto base (párrafos / descripciones principales).
 * - body2: Texto complementario o aclaraciones.
 * - button: Estilo para texto de botones primarios.
 * - caption: Notas, metadatos o textos de soporte.
 */
val AppTypography = Typography(
    h4 = TextStyle(
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold
    ),
    h5 = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold
    ),
    h6 = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium
    ),
    subtitle1 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    ),
    body1 = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = FontFamily.SansSerif
    ),
    body2 = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Light
    ),
    button = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    ),
    caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
)
