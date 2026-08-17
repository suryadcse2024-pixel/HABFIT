# Implementation Plan - HABFIT Onboarding Flow

This plan outlines the steps to implement the onboarding flow for the HABFIT application, including data collection, Firestore integration, and navigation logic.

## User Review Required

> [!IMPORTANT]
> The onboarding check will happen after login/signup and at app startup (Splash). This requires an asynchronous check against Firestore. I will implement a "Navigation Handler" approach to manage this transition smoothly.

## Proposed Changes

### Domain Layer
#### [MODIFY] [User.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/domain/model/User.kt)
- Add `onboardingCompleted: Boolean = false`
- Add `onboardingCompletedAt: Long? = null`
- Add `goals: List<String> = emptyList()`
- Update `preferredActivities` to be a `List<String>` instead of `String` for consistency.

#### [NEW] [OnboardingData.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/domain/model/OnboardingData.kt)
- Create a data class to hold onboarding selections: `goals`, `experienceLevel`, `activities`, `availableTime`, `reminderPreference`.

#### [MODIFY] [FirestoreRepository.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/domain/repository/FirestoreRepository.kt)
- Add `suspend fun isOnboardingCompleted(userId: String): Boolean`
- Add `suspend fun saveOnboardingData(userId: String, data: OnboardingData)`

#### [MODIFY] [HabfitRepository.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/domain/repository/HabfitRepository.kt)
- Update `saveUserPreferences` (or replace with a new onboarding completion method) to handle the new fields and sync to Firestore.

---

### Data Layer
#### [MODIFY] [FirestoreRepositoryImpl.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/data/repository/FirestoreRepositoryImpl.kt)
- Implement `isOnboardingCompleted` by fetching the user document and checking the field.
- Implement `saveOnboardingData` using a structured map as requested.

#### [MODIFY] [HabfitRepositoryImpl.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/data/repository/HabfitRepositoryImpl.kt)
- Implement the updated onboarding completion logic, ensuring both Room and Firestore are updated.

---

### UI & Feature Layer
#### [MODIFY] [OnboardingViewModel.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/features/onboarding/OnboardingViewModel.kt)
- Update to collect the 5 steps of information.
- Use `StateFlow` for each step's selection.
- Implement `completeOnboarding` to save data and trigger navigation.

#### [MODIFY] [OnboardingScreen.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/features/onboarding/OnboardingScreen.kt)
- Implement a 5-step HorizontalPager UI.
- Use attractive cards/chips for selections as requested.
- Add back/continue buttons and progress indicator.

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/features/home/HomeScreen.kt)
- Simplify to the requested placeholder state.
- Show user name, streak, progress, and placeholder buttons.

#### [MODIFY] [SplashViewModel.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/features/splash/SplashViewModel.kt)
- Update logic to check onboarding status asynchronously if a user is logged in.

#### [MODIFY] [HabfitNavGraph.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/ui/navigation/NavGraph.kt)
- Adjust navigation flow to handle the onboarding check after login/signup.

---

### Navigation Flow Logic
1. **Splash**: If `currentUser != null`, check Firestore for `onboardingCompleted`.
2. **Login/Signup**: On success, check Firestore for `onboardingCompleted`.
3. **If `onboardingCompleted == false`**: Navigate to `OnboardingScreen`.
4. **If `onboardingCompleted == true`**: Navigate to `MainScreen`.

## Verification Plan

### Manual Verification
- **New User Flow**: Register -> Verify Onboarding appears -> Complete Onboarding -> Verify data in Firestore -> Verify HomeScreen appears.
- **Existing User (No Onboarding)**: Login -> Verify Onboarding appears.
- **Existing User (Completed Onboarding)**: Login -> Verify directly to HomeScreen.
- **App Restart**: Restart app -> Verify Splash goes directly to HomeScreen if logged in and onboarding completed.
- **Back Button**: Verify back button on Onboarding doesn't bypass it.

### Automated Tests
- Build the project to ensure no compilation errors.
- (Optional) Unit tests for `OnboardingViewModel` logic.
