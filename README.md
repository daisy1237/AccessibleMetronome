# Accessible Metronome 

An accessible desktop metronome application developed in **Java**, using **Swing** and the **Java Accessibility API**.

The application helps musicians practice rhythm and timing while providing an interface designed to support keyboard navigation, screen readers, and users with visual impairments.

## Features

- **Adjustable tempo:** Set the tempo between 40 and 220 BPM, with a default of 100 BPM.
- **Time signatures:** Choose between 2/4, 3/4, 4/4 and many more.
- **Audio feedback:** Hear rhythmic clicks, with a distinct sound for the first beat of each measure.
- **Visual beat indicator:** Follow the current beat using a moving arrow positioned above the beat indicators.
- **Playback controls:** Start and stop the metronome at any time.
- **Real-time tempo adjustment:** Change BPM while the metronome is running.
- **Tempo presets:** Available presets, plus Custom.
- **Keyboard accessibility:** Control the metronome through the keyboard.

## Accessibility

Accessibility is a central goal of this project.

The application includes:

- **Screen reader support:** Accessible names and descriptions for interactive components using the Java Accessibility API.
- **Keyboard navigation:** Controls can be accessed without using a mouse.
- **Visible focus indicators:** A highlighted border identifies the currently focused component.
- **Color-independent feedback:** A moving arrow indicates the active beat without relying exclusively on color.
- **Accessible status information:** Playback state and tempo information are exposed through accessible component descriptions.
- **High-contrast indicators:** Visual elements use distinguishable colors to improve readability.

The application has been manually tested with **VoiceOver on macOS**, including recognition of interactive controls and keyboard navigation.

## Keyboard Shortcuts

| Shortcut    | Action                       |
|-------------|------------------------------|
| Space       | Start / Stop                 |
| ↑           | Previous time signature      |
| ↓           | Next time signature          |
| ←           | Decrease tempo               |
| →           | Increase tempo               |
| Tab         | Move to the next control     |
| Shift + Tab | Move to the previous control |
| M           | Mute / Unmute sound          |

## Technologies

- Java 21
- Java Swing
- Java Accessibility API (`javax.accessibility`)
- Java Sound API (`javax.sound.sampled`)
- Gradle

No external libraries are required for the application's core functionality.

## Project Structure

```text
src/main/java/
├── domain/
│   ├── MetronomeSettings.java
│   └── Beat.java
├── service/
│   └── MetronomeService.java
├── audio/
│   └── MetronomeAudio.java
├── ui/
│   └── MetronomeUi.java
└── Main.java
```

The application follows a layered architecture:

- **Domain:** Stores metronome settings and represents individual beats.
- **Service:** Manages playback, timing, and beat generation.
- **Audio:** Generates and plays metronome sounds.
- **UI:** Provides the graphical interface and accessibility features.

## Getting Started

### Requirements

- Java Development Kit (JDK) 21
- IntelliJ IDEA or another Java-compatible IDE
- An audio output device

### Running the Application

1. Clone the repository:

   ```bash
   git clone https://github.com/daisy1237/AccessibleMetronome.git
   ```

2. Open the project in IntelliJ IDEA.

3. Allow Gradle to import and synchronize the project.

4. Run `Main.java`.

