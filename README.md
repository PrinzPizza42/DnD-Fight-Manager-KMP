# DnD Fight Manager KMP

A Desktop application built with **Kotlin Multiplatform** and **Compose Multiplatform** to manage Dungeons & Dragons (DnD) encounters. It helps Game Masters (GMs) easily track combat rounds, manage fighter initiatives, and organize groups of monsters and players.

## Features

- **Encounter Management:** Track your encounters and switch between different fight setups.
- **Group Management:** Add and organize groups of fighters for quick setup.
- **Fighter Management:** Add individual fighters, track their stats, and automatically sort them by initiative.
- **Turn Tracking:** Easily navigate through the initiative order. The app keeps track of the current fighter and automatically increments the round counter when a full round is completed.
- **Save & Load:** Persist your encounter data by saving and loading it, so you never lose your progress during a session.
- **Notepad:** A built-in notepad for keeping track of encounter-specific notes or GM secrets.
- **Clear All Data:** Reset the entire application (without touching the saves) by clearing all fighters and groups.
- **Fighter Cloning:** Quickly copy existing fighters to manage swarms or groups of identical enemies.
- **Templates Management:** Create, manage, and utilize fighter templates to quickly add pre-configured fighters to any encounter.

## Technology Stack

- [Kotlin Multiplatform (KMP)](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- Desktop (JVM) Target

## Project Structure

This is a Kotlin Multiplatform project targeting Desktop (JVM).

* `composeApp/src` contains the application code.
  - `commonMain` is for code that’s common for all targets.
  - `jvmMain` is the Desktop (JVM) specific part.

## Build and Run

To build and run the development version of the desktop app, use the run configuration from the run widget in your IDE’s toolbar or run it directly from the terminal:

### On macOS/Linux
```shell
./gradlew :composeApp:run
```

### On Windows
```shell
.\gradlew.bat :composeApp:run
```

## Packaging

You can package the application into native distributions (like `.dmg` for macOS, `.msi` for Windows, and `.deb` for Linux).

```shell
./gradlew :composeApp:packageDistributionForCurrentOS
```
