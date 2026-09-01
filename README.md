# Lenvora V2 — Final Project Package

This is the consolidated project package built during this conversation.

Included:
- backend API foundation
- admin API/workflow foundation
- dictionary database + API
- Android offline OCR/translation engine
- GitHub Actions for backend, admin and Android

Offline engine:
- OCR with ML Kit
- language identification
- on-device ML Kit translation
- CameraX foundation

Important:
1. ML Kit translation models are downloaded once before they can be used offline.
2. The Android Gradle Wrapper (`android/gradlew` and wrapper files) must be committed to GitHub for Android CI to build. The official Gradle guidance recommends using the Gradle Wrapper with `setup-gradle`.
3. This package is a consolidated foundation, not a claim that every production feature (full admin UI, ads provider, complete CameraX UI, database seed data, release signing) is finished.
