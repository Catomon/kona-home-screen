package com.github.catomon.moewpaper.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.moewpaper.desktopFolder
import com.github.catomon.moewpaper.userDataFolder
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@Composable
fun Options(
    viewModel: MoeViewModel,
    modifier: Modifier = Modifier,
    exitApplication: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val appSettings by viewModel.appSettings.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveSettings()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.background(color = Color(1593835520))
    ) {
        Column(modifier = Modifier.width(250.dp)) {
            Column {
                Text("Background alpha:", color = Color.White)

                Slider(appSettings.backgroundAlpha, onValueChange = {
                    viewModel.updateSettings(appSettings.copy(backgroundAlpha = it))
                }, modifier = Modifier.width(250.dp))
            }

            Column {
                Text("Background:", color = Color.White)

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.width(250.dp)
                ) {
                    Button(onClick = {
                        coroutineScope.launch {
                            val selectedFile = FileKit.openFilePicker(
                                type = FileKitType.Image,
                                mode = FileKitMode.Single,
                                title = "Choose image:",
                                directory = PlatformFile(desktopFolder)
                            )?.file
                            if (selectedFile?.exists() == true) {
                                Files.copy(
                                    selectedFile.toPath(),
                                    userDataFolder.toPath().resolve("custom_background.image"),
                                    StandardCopyOption.REPLACE_EXISTING
                                )
                            }

                            viewModel.updateSettings(
                                appSettings.copy(
                                    customBackground = false
                                )
                            )
                            viewModel.updateBackgroundImage()
                        }

                    }) {
                        Text("Change")
                    }

                    Button(onClick = {
                        userDataFolder.resolve("custom_background.image").delete()

                        viewModel.updateSettings(appSettings.copy(customBackground = false))
                        viewModel.updateBackgroundImage()
                    }) {
                        Text("Default")
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Effects", color = Color.White)

                Checkbox(appSettings.backgroundEffect, {
                   viewModel.updateSettings(appSettings.copy(backgroundEffect = it))
                })
            }

            Button(onClick = {
                viewModel.saveSettings()
                exitApplication()
            }) {
                Text("Exit App")
            }
        }

//        Text(
//            "Options",
//            fontSize = 16.sp,
//            color = Color.Black,
//            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()
//                .background(Color.White),
//            textAlign = TextAlign.Center,
//        )
    }
}