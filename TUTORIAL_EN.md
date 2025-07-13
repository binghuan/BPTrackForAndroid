# Blood Pressure Tracker - Architecture Tutorial

## 🏗️ Application Architecture Overview

This application adopts the **MVI (Model-View-Intent)** architecture pattern, combined with **Clean Architecture** principles, providing clear layer separation and unidirectional data flow.

### 🎯 Architecture Advantages
- **Unidirectional Data Flow**: Predictable data flow, easy to debug
- **State Management**: Centralized state management, UI state consistency
- **Testability**: Clear responsibilities for each layer, easy to unit test
- **Maintainability**: Clear code structure, easy to extend and modify

## 📁 Project Structure

```
app/src/main/java/com/example/bptrack/
├── MainActivity.kt                 # Application entry point
├── data/                          # Data layer
│   ├── entity/                    # Data entities
│   │   └── BloodPressureRecord.kt # Blood pressure record entity
│   ├── dao/                       # Data access objects
│   │   └── BloodPressureDao.kt    # Blood pressure record DAO
│   ├── database/                  # Database configuration
│   │   └── BloodPressureDatabase.kt # Room database
│   ├── converter/                 # Type converters
│   │   └── DateTimeConverter.kt   # Date time converter
│   └── repository/                # Data repositories
│       └── BloodPressureRepository.kt # Blood pressure data repository
└── ui/                           # UI layer
    ├── intent/                   # User intents
    │   └── BloodPressureIntent.kt # Blood pressure related intents
    ├── state/                    # UI states
    │   └── BloodPressureState.kt  # Blood pressure UI state
    ├── viewmodel/                # View models
    │   ├── BloodPressureViewModel.kt        # Blood pressure ViewModel
    │   └── BloodPressureViewModelFactory.kt # ViewModel factory
    ├── screen/                   # Screen components
    │   └── BloodPressureScreen.kt # Main screen
    ├── component/                # UI components
    │   ├── AddRecordDialog.kt     # Add record dialog
    │   └── BloodPressureRecordItem.kt # Blood pressure record item
    └── theme/                    # Theme configuration
        ├── Color.kt              # Color definitions
        ├── Theme.kt              # Theme configuration
        └── Type.kt               # Typography configuration
```

## 🔄 MVI Architecture Details

### 📊 MVI Components Explanation

#### 1. **Model (Data Model)**
- **BloodPressureState**: Contains all UI states
- **BloodPressureRecord**: Blood pressure record data entity

#### 2. **View (User Interface)**
- **BloodPressureScreen**: Main screen component
- **AddRecordDialog**: Add record dialog
- **BloodPressureRecordItem**: Record item component

#### 3. **Intent (User Intentions)**
- **BloodPressureIntent**: Encapsulates all user operation intents

### 🔄 Data Flow

```
User Action → Intent → ViewModel → Repository → Database
     ↑                                              ↓
     ←─────── UI State ←─────── State ←─────── Data ←
```

## 🎯 Core Components Details

### 1. BloodPressureIntent (User Intents)

```kotlin
sealed class BloodPressureIntent {
    // Basic operations
    object LoadRecords : BloodPressureIntent()
    object ShowAddDialog : BloodPressureIntent()
    object HideAddDialog : BloodPressureIntent()
    object SaveRecord : BloodPressureIntent()
    object ClearMessage : BloodPressureIntent()
    
    // Date time selection
    object ShowDatePicker : BloodPressureIntent()
    object HideDatePicker : BloodPressureIntent()
    object ShowTimePicker : BloodPressureIntent()
    object HideTimePicker : BloodPressureIntent()
    
    // Record operations
    data class EditRecord(val record: BloodPressureRecord) : BloodPressureIntent()
    data class DeleteRecord(val record: BloodPressureRecord) : BloodPressureIntent()
    
    // Input updates
    data class UpdateSystolic(val value: String) : BloodPressureIntent()
    data class UpdateDiastolic(val value: String) : BloodPressureIntent()
    data class UpdateHeartRate(val value: String) : BloodPressureIntent()
    data class UpdateNotes(val value: String) : BloodPressureIntent()
    data class UpdateDateTime(val dateTime: LocalDateTime) : BloodPressureIntent()
}
```

**Purpose**: Encapsulates all possible user operations, providing a type-safe intent system. Each Intent represents a clear user action or system event.

### 2. BloodPressureState (UI State)

```kotlin
data class BloodPressureState(
    val records: List<BloodPressureRecord> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAddDialogVisible: Boolean = false,
    val editingRecord: BloodPressureRecord? = null,
    val systolicInput: String = "",
    val diastolicInput: String = "",
    // ... other states
)
```

**Purpose**: Unified management of all UI states, ensuring state consistency and predictability.

### 3. BloodPressureViewModel (View Model)

```kotlin
class BloodPressureViewModel(
    private val repository: BloodPressureRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(BloodPressureState())
    val state: StateFlow<BloodPressureState> = _state.asStateFlow()
    
    fun handleIntent(intent: BloodPressureIntent) {
        // Handle various user intents
    }
}
```

**Purpose**: Handles business logic, manages UI state, coordinates data layer operations.

```kotlin
// handleIntent implementation in ViewModel
fun handleIntent(intent: BloodPressureIntent) {
    when (intent) {
        is BloodPressureIntent.LoadRecords -> loadRecords()
        is BloodPressureIntent.ShowAddDialog -> showAddDialog()
        is BloodPressureIntent.SaveRecord -> saveRecord()
        is BloodPressureIntent.DeleteRecord -> deleteRecord(intent.record)
        is BloodPressureIntent.UpdateSystolic -> updateSystolic(intent.value)
        // ... other intent handling
    }
}

private fun showAddDialog() {
    _state.value = _state.value.copy(
        isAddDialogVisible = true,
        editingRecord = null,
        systolicInput = "",
        diastolicInput = "",
        heartRateInput = "",
        notesInput = ""
    )
}
```

## 🔄 Button Interaction Flows

### 💡 Implementation Examples

#### Button Triggers Intent in UI Components
```kotlin
// FloatingActionButton in BloodPressureScreen.kt
FloatingActionButton(
    onClick = { 
        viewModel.handleIntent(BloodPressureIntent.ShowAddDialog)
    }
) {
    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_record))
}

// Save button in AddRecordDialog.kt
Button(
    onClick = { 
        viewModel.handleIntent(BloodPressureIntent.SaveRecord)
    },
    enabled = isSystolicValid && isDiastolicValid
) {
    Text(stringResource(R.string.save))
}

// Edit button in BloodPressureRecordItem.kt
IconButton(
    onClick = { 
        viewModel.handleIntent(BloodPressureIntent.EditRecord(record))
    }
) {
    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
}
```

#### State Listening and UI Updates
```kotlin
// Listen to state changes in Composable
val state by viewModel.state.collectAsState()

// Show dialog based on state
if (state.isAddDialogVisible) {
    AddRecordDialog(
        state = state,
        onSave = { viewModel.handleIntent(BloodPressureIntent.SaveRecord) },
        onCancel = { viewModel.handleIntent(BloodPressureIntent.HideAddDialog) },
        onSystolicChange = { viewModel.handleIntent(BloodPressureIntent.UpdateSystolic(it)) }
    )
}
```

### 1. Add Record Flow

```mermaid
sequenceDiagram
    participant User
    participant Screen
    participant ViewModel
    participant Repository
    participant Database

    User->>Screen: Click FloatingActionButton
    Screen->>ViewModel: handleIntent(ShowAddDialog)
    ViewModel->>ViewModel: Update isAddDialogVisible = true
    ViewModel->>Screen: Send new state
    Screen->>Screen: Display AddRecordDialog

    User->>Screen: Fill blood pressure data
    Screen->>ViewModel: handleIntent(SystolicChange/DiastolicChange)
    ViewModel->>ViewModel: Update input state

    User->>Screen: Click save button
    Screen->>ViewModel: handleIntent(SaveRecord)
    ViewModel->>Repository: insertRecord(record)
    Repository->>Database: Save record
    Database-->>Repository: Operation result
    Repository-->>ViewModel: Return result
    ViewModel->>ViewModel: Update state (close dialog, show success message)
    ViewModel->>Screen: Send new state
    Screen->>Screen: Close dialog, refresh list
```

### 2. Edit Record Flow

```mermaid
sequenceDiagram
    participant User
    participant RecordItem
    participant ViewModel
    participant Repository
    participant Database

    User->>RecordItem: Click edit button
    RecordItem->>ViewModel: handleIntent(EditRecord(record))
    ViewModel->>ViewModel: Set editingRecord, populate input fields
    ViewModel->>RecordItem: Send new state
    RecordItem->>RecordItem: Display edit dialog

    User->>RecordItem: Modify data and save
    RecordItem->>ViewModel: handleIntent(SaveRecord)
    ViewModel->>Repository: updateRecord(record)
    Repository->>Database: Update record
    Database-->>Repository: Operation result
    Repository-->>ViewModel: Return result
    ViewModel->>ViewModel: Update state
    ViewModel->>RecordItem: Send new state
    RecordItem->>RecordItem: Close dialog, update display
```

### 3. Delete Record Flow

```mermaid
sequenceDiagram
    participant User
    participant RecordItem
    participant ViewModel
    participant Repository
    participant Database

    User->>RecordItem: Click delete button
    RecordItem->>RecordItem: Show confirmation dialog

    User->>RecordItem: Confirm deletion
    RecordItem->>ViewModel: handleIntent(DeleteRecord(record))
    ViewModel->>Repository: deleteRecord(record)
    Repository->>Database: Delete record
    Database-->>Repository: Operation result
    Repository-->>ViewModel: Return result
    ViewModel->>ViewModel: Update records list
    ViewModel->>RecordItem: Send new state
    RecordItem->>RecordItem: Update UI, remove deleted item
```

## 🔧 Technology Stack

### **UI Layer**
- **Jetpack Compose**: Modern UI framework
- **Material Design 3**: Google design language
- **Navigation Compose**: Navigation management

### **Architecture Layer**
- **MVI Pattern**: Architectural pattern
- **ViewModel**: State management
- **StateFlow**: Reactive state flow
- **Coroutines**: Asynchronous processing

### **Data Layer**
- **Room Database**: Local database
- **Repository Pattern**: Data access abstraction
- **Entity**: Data models

### **Dependency Injection**
- **Manual DI**: ViewModelFactory pattern

## 🚀 Key Features

### 1. **Blood Pressure Classification System**
- Automatic analysis of blood pressure levels (normal, elevated, high stage 1, etc.)
- Real-time color feedback prompts
- Medical standard-compliant classification

### 2. **Trend Analysis**
- Automatic comparison with previous measurements
- Visual trend indicators (increased, decreased, stable)
- Helps users understand blood pressure changes

### 3. **Internationalization Support**
- Traditional Chinese and English bilingual support
- Automatic switching based on system language
- Complete localization resources

### 4. **Modern UI**
- Material Design 3 design language
- Responsive layout design
- Smooth animation effects

## ⚡ Key Features Deep Dive

### 1. **Blood Pressure Classification System**

Blood pressure classification based on American Heart Association (AHA) standards:

```kotlin
// BloodPressureCategory enum
enum class BloodPressureCategory(
    val nameRes: Int,
    val color: Color,
    val descriptionRes: Int
) {
    NORMAL(R.string.bp_category_normal, Color(0xFF4CAF50), R.string.bp_category_normal_desc),
    ELEVATED(R.string.bp_category_elevated, Color(0xFFFF9800), R.string.bp_category_elevated_desc),
    HIGH_STAGE_1(R.string.bp_category_high_stage_1, Color(0xFFFF5722), R.string.bp_category_high_stage_1_desc),
    HIGH_STAGE_2(R.string.bp_category_high_stage_2, Color(0xFFD32F2F), R.string.bp_category_high_stage_2_desc),
    HYPERTENSIVE_CRISIS(R.string.bp_category_hypertensive_crisis, Color(0xFF880E4F), R.string.bp_category_hypertensive_crisis_desc)
}

// Classification calculation logic
fun calculateBloodPressureCategory(systolic: Int?, diastolic: Int?): BloodPressureCategory {
    return when {
        systolic >= 180 || diastolic >= 120 -> BloodPressureCategory.HYPERTENSIVE_CRISIS
        systolic >= 140 || diastolic >= 90 -> BloodPressureCategory.HIGH_STAGE_2
        systolic >= 130 || diastolic >= 80 -> BloodPressureCategory.HIGH_STAGE_1
        systolic >= 120 && diastolic < 80 -> BloodPressureCategory.ELEVATED
        systolic < 120 && diastolic < 80 -> BloodPressureCategory.NORMAL
        else -> BloodPressureCategory.HIGH_STAGE_1
    }
}
```

### 2. **Trend Analysis Algorithm**

```kotlin
fun calculateBloodPressureTrend(
    current: BloodPressureRecord,
    previous: BloodPressureRecord?
): BloodPressureTrend {
    if (previous == null) return BloodPressureTrend.FIRST_RECORD
    
    val currentAvg = (current.systolic + current.diastolic) / 2.0
    val previousAvg = (previous.systolic + previous.diastolic) / 2.0
    
    return when {
        currentAvg > previousAvg + 2 -> BloodPressureTrend.INCREASED
        currentAvg < previousAvg - 2 -> BloodPressureTrend.DECREASED
        else -> BloodPressureTrend.STABLE
    }
}
```

### 3. **Error Handling Mechanism**

```kotlin
// Unified error handling in ViewModel
private fun handleOperation(operation: suspend () -> Unit) {
    viewModelScope.launch {
        try {
            _state.value = _state.value.copy(isLoading = true, error = null)
            operation()
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoading = false,
                error = e.message ?: "Unknown error occurred"
            )
        }
    }
}
```

## 📝 Development Best Practices

### 1. **State Management**
```kotlin
// ✅ Good practice: Use immutable state
_state.value = _state.value.copy(
    isLoading = false,
    records = newRecords
)

// ❌ Avoid: Direct state modification
_state.value.records.clear()
```

### 2. **Error Handling**
```kotlin
// ✅ Good practice: Unified error handling
try {
    repository.insertRecord(record)
    _state.value = _state.value.copy(message = "Record saved")
} catch (e: Exception) {
    _state.value = _state.value.copy(error = e.message)
}
```

### 3. **Resource Management**
```kotlin
// ✅ Good practice: Use string resources
Text(stringResource(R.string.blood_pressure_records))

// ❌ Avoid: Hard-coded text
Text("Blood Pressure Records")
```

## 🔄 Extension Guide

### Adding New Features
1. **Define new Intent**: Add new operation in `BloodPressureIntent`
2. **Update State**: Add necessary state in `BloodPressureState`
3. **Implement Logic**: Handle new Intent in `ViewModel`
4. **Update UI**: Respond to state changes in corresponding components

### Adding New Pages
1. **Create Screen Component**: New Composable function
2. **Define Navigation**: Configure Navigation routes
3. **State Management**: Consider if new ViewModel is needed

## 🧪 Testing Strategy

### Unit Tests
- **ViewModel Tests**: Test business logic and state management
- **Repository Tests**: Test data operations
- **Utility Tests**: Test utility functions

### UI Tests
- **Compose Tests**: Test UI component behavior
- **Navigation Tests**: Test page navigation
- **Integration Tests**: Test complete user flows

## 🎓 Summary

### 🏆 Application Highlights

1. **Modern Architecture**: Adopts MVI architecture pattern, ensuring code maintainability and testability
2. **Medical Standards**: AHA standard-based blood pressure classification system, providing professional health guidance
3. **Smart Analysis**: Automatic trend analysis, helping users understand blood pressure change patterns
4. **Internationalization**: Complete multi-language support, adapting to different regional user needs
5. **User Experience**: Material Design 3 design, providing intuitive and easy-to-use interface

### 📈 Future Extension Directions

- **Data Export**: Support CSV, PDF format export
- **Chart Analysis**: Add blood pressure trend charts
- **Reminder System**: Scheduled measurement reminder functionality
- **Cloud Sync**: Data backup and synchronization functionality
- **Health Recommendations**: AI-based personalized health recommendations

### 🔗 Related Technical Documentation

- **Android Architecture Components**: [Official Guide](https://developer.android.com/topic/architecture)
- **Jetpack Compose**: [Developer Documentation](https://developer.android.com/jetpack/compose)
- **Room Database**: [Persistent Data](https://developer.android.com/training/data-storage/room)
- **Kotlin Coroutines**: [Asynchronous Programming](https://kotlinlang.org/docs/coroutines-overview.html)

## 📚 Learning Resources

- [Jetpack Compose Official Documentation](https://developer.android.com/jetpack/compose)
- [MVI Architecture Guide](https://hannesdorfmann.com/android/model-view-intent/)
- [Room Database Documentation](https://developer.android.com/training/data-storage/room)
- [Material Design 3](https://m3.material.io/)

---

📝 **This tutorial covers the complete architecture and interaction flows of the blood pressure tracking application. By understanding these concepts, you can better maintain and extend application functionality.**

🎯 **If you have any questions during development, it's recommended to refer to the actual code implementation of each component and combine it with the architectural explanations in this tutorial for understanding.** 