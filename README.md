# AndroidComposePatternLock
Using a single `Canvas` of Jetpack Compose to interact with `pointerInput`
It calculates a square at the center of `Canvas` thus it supports Horizontally or Vertically.

# Introduction
Demonstrating a pattern lock functionality with the use of Android Compose. This activity allows the user to create a pattern by dragging on the screen across multiple dots. Each selected dot will be a part of the pattern, represented by a number.

# Implementation
It's all in the `@Composable fun PatternLock()` 

# Components
Here is a brief overview of the main components:

1. PatternLockActivity: This is the main activity. It sets the content to be PatternLockScreen.

1. PatternLockScreen: This composable function provides the main UI for the activity. It displays the pattern the user has drawn on the screen as a sequence of numbers.

1. SelectedNumbersDisplay: This composable function displays the sequence of numbers corresponding to the pattern the user has drawn on the screen.

1. PatternLock: This composable function enables the pattern lock functionality. It takes a callback onUpdates which is called when the pattern changes.

1. vibrate: This function triggers the device to vibrate, giving tactile feedback to the user.

1. PatternLockPreview and PatternLockPreviewForAutomotive1024p: These are preview functions that allow you to see how the PatternLockScreen looks without needing to run it on a device or emulator.

# Usage
The user interacts with the application by touching and dragging their finger across the screen, forming a pattern. The current pattern will be displayed at the top of the screen as a sequence of numbers.
 
# Note
The pattern lock is a simple and intuitive way for users to interact with your application. It can be used for any situation where you want to ask the user to input a simple sequence, like a gesture-based login, simple game control, or any other type of user interaction that can be mapped to a pattern of dots.

# Preview
https://github.com/Wesely/AndroidComposePatternLock/assets/5109822/d0994b9f-c6a4-4cf1-93a3-26c906c07835



https://github.com/Wesely/AndroidComposePatternLock/assets/5109822/cf592934-0bd8-48dc-a527-fb4a0a1bc871

