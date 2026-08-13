# Implementation Plan - HABFIT Android App

HABFIT is an all-in-one habit and fitness platform designed as a premium, native Android application using Jetpack Compose, Material 3, and a modern Clean Architecture (MVVM).

## User Review Required

> [!IMPORTANT]
> **API Keys & Configuration**: This project requires several external configurations that I cannot perform automatically:
> 1. **Firebase**: You must create a project in the Firebase Console and add the `google-services.json` file to the `app/` directory.
> 2. **Google Maps**: You must obtain a Google Maps API Key from the Google Cloud Console and add it to your `local.properties` or `AndroidManifest.xml`.
> 3. **Gemini AI**: You must obtain a Gemini API key from Google AI Studio.

> [!WARNING]
> **Package Renaming**: You suggested `com.habfit.app` as the package name. The current project is set up as `com.example.habfit2`. I will proceed with renaming the package to `com.habfit.app` to match your request, which will involve moving files and updating the `build.gradle.kts` and `AndroidManifest.xml`.

## Proposed Changes

### Core Project Setup & Dependencies

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/gradle/libs.versions.toml)
- Add versions and libraries for:
    - Navigation Compose
    - Hilt (Dependency Injection)
    - Room (Local Database)
    - Firebase BOM (Auth, Firestore, Storage, Cloud Messaging)
    - Google AI (Gemini)
    - Google Maps Compose
    - Coil (Image Loading)
    - Kotlinx Serialization

#### [MODIFY] [build.gradle.kts (project)](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/build.gradle.kts)
- Add Hilt and Google Services plugins.

#### [MODIFY] [build.gradle.kts (app)](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/build.gradle.kts)
- Apply Hilt, Google Services, and Serialization plugins.
- Add all required dependencies from the version catalog.
- Configure Room and Hilt compiler.

---

### Architecture & Design System

#### [NEW] [Design System Components](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/ui/components)
- Implement reusable HABFIT components: `HabfitCard`, `HabfitButton`, `HabfitProgressRing`, `HabfitStatCard`, etc.
- Set up the Premium Dark Theme in `ui/theme/Color.kt` and `Theme.kt` using the specified color palette (#050505, #00FF85, etc.).

#### [NEW] [Base Architecture](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/core)
- Establish the package structure: `core`, `data`, `domain`, `features`, `ui`, `services`.
- Setup Hilt `AppModule` and `RepositoryModule`.

---

### Feature Implementation (Iterative Phases)

1.  **Auth & Onboarding**:
    - Firebase Auth integration.
    - Splash screen with branding.
    - Pager-based onboarding for goal setting.
2.  **Habit Tracker & Room**:
    - Room database for local caching of habits and logs.
    - Habit creation and daily tracking UI.
    - Streak calculation logic.
3.  **Home Dashboard**:
    - Premium dashboard with Life Score, weekly activity, and mission cards.
    - Integration of data from Habits and Fitness modules.
4.  **Fitness & Workouts**:
    - Step and calorie tracking (mocked or integrated with Sensors).
    - Workout timer and exercise list system.
5.  **AI Integration (Gemini)**:
    - HABIT AI chat interface.
    - Smart goal generation and weekly insights using Gemini.
6.  **Community & Social**:
    - Social feed with Firebase Firestore and Storage for media.
    - Influencer profiles and follow system.
7.  **Maps & Nearby**:
    - Google Maps integration for gyms and run clubs.
    - Custom dark map styling and markers.
8.  **Gamification**:
    - HAB Coins system, XP levels, and Daily Missions.
    - Leaderboards and Achievements.

## Verification Plan

### Automated Tests
- Unit tests for Streak calculation logic and XP leveling.
- Repository tests using Hilt and Mockito (where applicable).

### Manual Verification
- Deploy to Android Emulator/Device to verify:
    - Premium Dark Theme visual consistency.
    - Navigation flow between all 15+ routes.
    - Firebase Auth sign-in/up flow.
    - Habit completion animations and coin rewards.
    - Maps marker interaction.
