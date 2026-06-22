package com.example.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun MarkdownText(text: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        val lines = text.split("\n")
        for (line in lines) {
            when {
                line.startsWith("# ") -> {
                    Text(
                        text = parseInlineMarkdown(line.substring(2)),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
                line.startsWith("## ") -> {
                    Text(
                        text = parseInlineMarkdown(line.substring(3)),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                line.startsWith("### ") -> {
                    Text(
                        text = parseInlineMarkdown(line.substring(4)),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                line.startsWith("- ") || line.startsWith("* ") -> {
                    Row(modifier = Modifier.padding(start = 12.dp, top = 2.dp, bottom = 2.dp)) {
                        Text(
                            text = "• ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = parseInlineMarkdown(line.substring(2)),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                line.startsWith("---") -> {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                else -> {
                    Text(
                        text = parseInlineMarkdown(line),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

private fun parseInlineMarkdown(inlineText: String) = buildAnnotatedString {
    var i = 0
    while (i < inlineText.length) {
        when {
            inlineText.startsWith("**", i) -> {
                val end = inlineText.indexOf("**", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(inlineText.substring(i + 2, end))
                    }
                    i = end + 2
                } else {
                    append(inlineText[i])
                    i++
                }
            }
            inlineText.startsWith("*", i) -> {
                val end = inlineText.indexOf("*", i + 1)
                if (end != -1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(inlineText.substring(i + 1, end))
                    }
                    i = end + 1
                } else {
                    append(inlineText[i])
                    i++
                }
            }
            else -> {
                append(inlineText[i])
                i++
            }
        }
    }
}
