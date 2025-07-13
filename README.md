# Blood Pressure Tracker App

<div align="center">
  <img src="README/icon-256.png" alt="App Icon" width="150" height="150">
</div>

This is a blood pressure tracking application developed using Android Jetpack Compose, Room Database, and the MVI architecture pattern.

## Key Features

* 📊 Record blood pressure values (systolic/diastolic)
* ❤️ Record heart rate
* 📅 Record date and time
* 📝 Add notes
* ✏️ Edit existing records
* 🗑️ Delete records
* 🩺 Intelligent blood pressure classification (based on AHA standards)
* 📈 Blood pressure trend analysis
* 📋 Dual view modes (Detailed / Compact)
* 📤 Export to CSV for sharing
* 📥 Import from CSV
* 🎨 Theme support (Dark/Light modes)
* 🌐 Bilingual support (Chinese/English)
* 📱 Modern Material Design UI

## App Interface Preview

<div align="center">
  <img src="README/demo_compact_mode.jpg" alt="Compact View Mode" width="300">
  <img src="README/demo_view_mode.jpg" alt="View Mode Switch" width="300">
</div>

*Left: View mode switch button; Right: Compact view mode - more records, less space*

## Architecture

### MVI Architecture

* **Model**: Data layer (Entity, DAO, Repository)
* **View**: UI layer (Compose UI)
* **Intent**: User intents and state management

### Tech Stack

* **Kotlin**: Primary programming language
* **Jetpack Compose**: Modern UI toolkit
* **Room Database**: Local storage
* **Coroutines**: Asynchronous programming
* **StateFlow**: State management
* **Material Design 3**: UI design guidelines
* **Dark/Light Theme**: Theme support
* **Internationalization**: i18n support

## Project Structure

```
app/src/main/java/com/bh/bptrack/
├── data/
│   ├── converter/          # Data converters
│   ├── dao/               # Data Access Objects
│   ├── database/          # Database configuration
│   ├── entity/            # Data entities
│   └── repository/        # Data repository
├── ui/
│   ├── component/         # UI components
│   ├── intent/           # MVI intents
│   ├── screen/           # Screens
│   ├── state/            # State definitions
│   ├── theme/            # Theme configuration
│   └── viewmodel/        # ViewModel
└── MainActivity.kt       # Main activity
```

## Core Features

### 🩺 Intelligent Blood Pressure Classification

* **AHA Standard**: Automatic classification based on American Heart Association standards
* **Real-time Feedback**: Classification results displayed immediately upon input
* **Color-coded Levels**: Visual cues for different BP categories
* **Medical Guidance**: Detailed descriptions for each classification

### 📈 Blood Pressure Trend Analysis

* **Smart Comparison**: Automatically compares changes with previous records
* **Trend Indication**: Shows increasing, decreasing, or stable status
* **Visual Indicators**: Arrows and icons for intuitive trend representation

### 📋 Dual View Modes

* **Detailed View**: Shows classification, trends, and full record details
* **Compact View**: Maximizes visible records in a condensed layout
* **One-tap Switch**: Easily toggle between modes via toolbar
* **Smart Layout**: Font size and spacing adjust in compact mode

### 📤📥 CSV Data Management

* **Export CSV**: Export all records in standard CSV format
* **System Share**: Share via email, cloud, etc.
* **Import CSV**: Import records from external CSV files
* **Smart De-duplication**: Automatically filters duplicate records by date
* **Validation**: Full data validation and error handling

### 🎨 Theme Support

* **Dark/Light Modes**: Fully supports system theme switching
* **Dynamic Colors**: Text colors adapt to the current mode
* **Optimized Readability**: Ensures great visibility in both modes

## How to Use

### 📊 Basic Usage

1. Open the app
2. Tap the "+" button at the bottom right to add a new record
3. Fill in blood pressure, heart rate (optional), and notes
4. Tap "Save" to store the entry
5. View all records on the main screen
6. Tap the menu button on a record to edit or delete it

### 📋 Switching View Modes

1. Find the view mode button (📄/📋) in the top toolbar
2. Tap to switch between detailed and compact view
3. **Detailed View**: Shows full BP analysis and trends
4. **Compact View**: Displays more entries with a tighter layout

### 📤📥 Managing CSV Data

1. **Export Data**: Tap the top-right menu → choose "📤 Share CSV"
2. **Share**: Select an app (email, cloud, etc.) to send the file
3. **Import Data**: Tap the top-right menu → choose "📥 Import CSV"
4. **Select File**: Choose a CSV file from file manager
5. **Processing**: The system validates and merges the data

## Development Requirements

* Android Studio Arctic Fox or higher
* Kotlin 1.8.0 or higher
* Android SDK 24 or above
* Gradle 8.1.0 or higher

## Build & Run

1. Clone the project locally
2. Open in Android Studio
3. Wait for Gradle sync to complete
4. Run the app

## Feature Highlights

* ✅ Offline Storage (Room Database)
* ✅ Reactive UI (Jetpack Compose)
* ✅ State Management (MVI)
* ✅ Data Validation
* ✅ User-friendly Interface
* ✅ Record Editing & Deletion
* ✅ Modern Design
* ✅ Dual View Mode Switching
* ✅ CSV Import/Export
* ✅ Smart Data De-duplication

## Future Enhancements

* 📈 Graphs and statistics
* 📊 More detailed trend insights
* 🏥 Doctor report generation
* 🔔 Measurement reminders
* 📱 Widget support
* 🏷️ Tags and categories
* 📅 Time range filters

## License

This project is for learning and personal use only.
