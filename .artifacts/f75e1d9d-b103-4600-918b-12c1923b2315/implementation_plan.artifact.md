# Firebase Analytics Integration Plan

Based on your project configuration (Location: India, Name: Habitfit), I will integrate the Firebase Analytics SDK into your app to track user engagement and app performance.

## User Review Required

> [!NOTE]
> **Data Privacy**: By enabling Analytics, the app will collect anonymized data about user interactions. Ensure your app's Privacy Policy reflects this.
> **Automatic Tracking**: Firebase automatically tracks basic events like `first_open`, `session_start`, and screen views.

## Proposed Changes

### Dependencies

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/gradle/libs.versions.toml)
- Add `firebase-analytics` library.

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/build.gradle.kts)
- Add `libs.firebase.analytics` to the dependencies block.

---

### Analytics Implementation

#### [NEW] [AnalyticsHelper.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/core/analytics/AnalyticsHelper.kt)
- Create a helper class to encapsulate Firebase Analytics logging.
- Define custom events relevant to HABFIT (e.g., `habit_completed`, `ai_coach_asked`, `workout_logged`).

---

### Integration

#### [MODIFY] [HabfitRepositoryImpl.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/data/repository/HabfitRepositoryImpl.kt)
- Inject `AnalyticsHelper`.
- Log events when habits are toggled or workouts are saved.

#### [MODIFY] [GroqRepository.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/domain/repository/GroqRepository.kt)
- Log an event whenever the user asks the AI coach a question.

## Verification Plan

### Automated Tests
- Verify that the app builds successfully with the new dependencies.
- Use `analyze_file` to ensure no syntax errors.

### Manual Verification
- Interact with the app (complete a habit, log a workout, use AI).
- Check the **DebugView** in the Firebase Console to see real-time events appearing from your device.
