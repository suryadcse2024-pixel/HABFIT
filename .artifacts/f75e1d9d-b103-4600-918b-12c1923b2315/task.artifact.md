# Task: Firestore Database Setup & Sync

- [x] Implement Domain Layer
    - [x] Create `FirestoreRepository.kt` interface
- [x] Implement Data Layer
    - [x] Create `FirestoreRepositoryImpl.kt`
- [x] Setup Dependency Injection
    - [x] Update `FirebaseModule.kt` to provide `FirestoreRepository`
- [x] Integrate Sync Logic
    - [x] Update `HabitDatabase.kt` and `AppModule.kt` to include dependencies
    - [x] Update `HabitRepositoryImpl.kt` to use `FirestoreRepository` for syncing
- [x] Update Feature Logic
    - [x] Update `CommunityViewModel.kt` to observe Firestore posts
- [x] Verification
    - [x] Gradle Sync
    - [x] Build and verify data mirroring in Firestore
