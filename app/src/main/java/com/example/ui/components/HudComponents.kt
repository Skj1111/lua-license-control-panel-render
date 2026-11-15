package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.LicenseStatus
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanDim
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.HudBorder
import com.example.ui.theme.HudCornerBracket
import com.example.ui.theme.InputSurface
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.PanelSurface
import com.example.ui.theme.PanelSurfaceHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CyberPanelBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .drawBehind {
                val step = 42.dp.toPx()
                val gridColor = Color(0x0A54E7FF)
                var x = 0f
                while (x <= size.width) {
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                    x += step
                }
                var y = 0f
                while (y <= size.height) {
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                    y += step
                }
            }
    ) {
        content()
    }
}

@Composable
fun HudPanelCard(
    modifier: Modifier = Modifier,
    borderColor: Color = HudBorder,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(listOf(PanelSurfaceHigh, PanelSurface)),
                shape = shape
            )
            .border(1.dp, borderColor, shape)
            .drawBehind {
                val bracket = 16.dp.toPx()
                val stroke = 2.dp.toPx()
                // Top-left bracket
                drawLine(HudCornerBracket, Offset(0f, 0f), Offset(bracket, 0f), stroke)
                drawLine(HudCornerBracket, Offset(0f, 0f), Offset(0f, bracket), stroke)
                // Bottom-right bracket
                drawLine(
                    HudCornerBracket,
                    Offset(size.width - bracket, size.height),
                    Offset(size.width, size.height),
                    stroke
                )
                drawLine(
                    HudCornerBracket,
                    Offset(size.width, size.height - bracket),
                    Offset(size.width, size.height),
                    stroke
                )
            }
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

@Composable
fun EyebrowText(
    text: String,
    color: Color = CyberCyan,
    modifier: Modifier = Modifier
) {
    Text(
        text = "// ${text.uppercase()}",
        color = color,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
    )
}

@Composable
fun BrandMark(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(38.dp)
            .background(
                Brush.linearGradient(
                    listOf(Color(0x2852ECFF), Color(0x14716CFF))
                ),
                RoundedCornerShape(6.dp)
            )
            .border(1.dp, Color(0x7364EDFF), RoundedCornerShape(6.dp))
    ) {
        Text(
            text = "LX",
            color = CyberCyan,
            fontFamily = DisplayFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun CyberPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CyberCyanDim,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF1A2538),
            disabledContentColor = TextMuted
        ),
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .background(
                brush = if (enabled) {
                    Brush.horizontalGradient(listOf(CyberCyanDim, CyberViolet))
                } else {
                    Brush.horizontalGradient(listOf(Color(0xFF1A2538), Color(0xFF1A2538)))
                },
                shape = RoundedCornerShape(6.dp)
            )
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text.uppercase(),
            fontFamily = MonoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.4.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CyberOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, if (enabled) Color(0x4D76E1FF) else Color(0x2276E1FF)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(0x0E71D0FF),
            contentColor = CyberCyan,
            disabledContentColor = TextMuted
        ),
        modifier = modifier.defaultMinSize(minHeight = 48.dp)
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text.uppercase(),
            fontFamily = MonoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            letterSpacing = 1.2.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CyberDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CyberDanger,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF3B152A),
            disabledContentColor = TextMuted
        ),
        modifier = modifier.defaultMinSize(minHeight = 48.dp)
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text.uppercase(),
            fontFamily = MonoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
fun CyberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            color = CyberCyan,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(placeholder, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                }
            },
            singleLine = singleLine,
            minLines = minLines,
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputSurface,
                unfocusedContainerColor = InputSurface,
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = Color(0x4D67E8F9),
                cursorColor = CyberCyan
            ),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun StatusPill(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Text(
            text = text.uppercase(),
            color = color,
            fontFamily = MonoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

fun licenseStatusColor(status: LicenseStatus): Color = when (status) {
    LicenseStatus.ACTIVE -> CyberEmerald
    LicenseStatus.REVOKED -> CyberPink
    LicenseStatus.EXPIRED -> CyberAmber
    LicenseStatus.UNUSED -> TextSecondary
}

@Composable
fun SectionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0x2800EEFF))
    )
}
