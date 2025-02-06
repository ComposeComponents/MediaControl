# Media Control
[![Build](https://github.com/ComposeComponents/MediaControl/actions/workflows/build-library.yml/badge.svg)](https://github.com/ComposeComponents/MediaControl/actions/workflows/build-library.yml)
[![Lint](https://github.com/ComposeComponents/MediaControl/actions/workflows/lint.yml/badge.svg)](https://github.com/ComposeComponents/MediaControl/actions/workflows/lint.yml)

A library to provide customisable media control components.

## Installation
![Stable](https://img.shields.io/github/v/release/ComposeComponents/MediaControl?label=Stable)
![Preview](https://img.shields.io/github/v/release/ComposeComponents/MediaControl?label=Preview&include_prereleases)

```
implementation "cl.emilym.compose:mediacontrol:<latest>"
```

## Usage
### ProgressBar
```kotlin
var mediaProgress by mutableStateOf(0f)

ProgressBar(
    mediaProgress,
    onSeek = {
        mediaProgress = it
    },
    modifier = Modifier.fillMaxWidth().padding(16.dp)
)
```