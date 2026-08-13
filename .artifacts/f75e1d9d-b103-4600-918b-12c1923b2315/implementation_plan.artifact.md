# Firebase Integration Finalization Plan

This plan aims to wrap up the Firebase integration by ensuring all key events are tracked, data is synced comprehensively, and common placeholders are addressed.

## User Review Required

> [!IMPORTANT]
> **Google Sign-In Web Client ID**: The `LoginScreen.kt` currently uses a placeholder `"YOUR_GOOGLE_WEB_CLIENT_ID_HERE"`. To make Google Sign-In functional, you need to:
> 1. Go to the [Firebase Console](https://console.firebase.google.com/).
> 2. Enable Google Sign-In in the Authentication section.
> 3. Add your SHA-1 fingerprint in Project Settings.
> 4. Copy the "Web client ID" and replace the placeholder.

## Proposed Changes

### Core & DI
#### [MODIFY] [FirebaseModule.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/core/di/FirebaseModule.kt)
- Add `@Provides` for `FirebaseAnalytics`.

### Application Initialization
#### [MODIFY] [HabfitApplication.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/HabfitApplication.kt)
- (Optional) Explicitly initialize Firebase if needed (though the plugin usually handles it).
- Inject and log an "app_open" event with Analytics.

### Data Layer
#### [MODIFY] [HabfitRepositoryImpl.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/data/repository/HabfitRepositoryImpl.kt)
- Sync habit completion status to Firestore.
- Log habit completion events to Firebase Analytics.
- Sync community posts to Firestore.

### UI Layer
#### [MODIFY] [LoginScreen.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/features/auth/LoginScreen.kt)
- Log "login" and "google_login_started" events.

## Verification Plan

### Automated Tests
- Run `./gradlew app:assembleDebug` to ensure no build errors.

### Manual Verification
- Check Logcat for Firebase initialization logs.
- Verify that calling repository methods (like `addHabit`) still works as expected.
