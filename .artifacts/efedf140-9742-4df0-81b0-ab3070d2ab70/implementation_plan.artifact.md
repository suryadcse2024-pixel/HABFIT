# Fix "FINISH" button not working in Onboarding

The user reports that clicking the "FINISH" button on the final step of onboarding (reminders) does not result in any action. Based on the analysis, the most likely cause is a failing Firestore update operation that is being caught and silenced in the ViewModel, or a silent return due to missing data.

## Proposed Changes

### [Data Layer]

#### [MODIFY] [FirestoreRepositoryImpl.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/data/repository/FirestoreRepositoryImpl.kt)
- Change `update()` to `set(..., SetOptions.merge())` in `saveOnboardingData` to ensure the operation succeeds even if the user document hasn't fully propagated yet.
- Add necessary `SetOptions` import.

### [UI Layer]

#### [MODIFY] [OnboardingViewModel.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/features/onboarding/OnboardingViewModel.kt)
- Add error logging in the `catch` block of `completeOnboarding`.
- Add a new `StateFlow` for error messages to provide feedback to the UI.
- Add logging for early returns to identify if data is missing.

#### [MODIFY] [OnboardingScreen.kt](file:///C:/Users/Surya D/AndroidStudioProjects/habfit22/app/src/main/java/com/habfit/app/features/onboarding/OnboardingScreen.kt)
- Observe the new error message state from the ViewModel.
- (Optional) Display a Toast or error text if onboarding fails.

## Verification Plan

### Manual Verification
- Deploy the app and go through the onboarding process.
- Select a reminder on Step 5 and click "FINISH".
- Verify that the app navigates to the Main Screen.
- Check Logcat for any "Onboarding failed" or "Missing data" logs if it still doesn't work.
