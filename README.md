# Lenvora V2 Android Base

Android/Kotlin/Jetpack Compose foundation for the offline-first Lenvora app.

Included:
- Jetpack Compose UI
- Word/sentence input
- Language selection foundation
- Camera permission
- Offline-first UI architecture point
- Room dependencies for local dictionary storage

Not yet included:
- Real OCR model
- Real offline translation models
- Full Room entities/DAO
- CameraX capture flow
- Backend sync
- Ads SDK

Those should be added in the next stages rather than pretending the placeholder is already an offline translation engine.

## GitHub Actions

If this repository already has `.github/workflows/android.yml`, it can build the Gradle project after the Gradle wrapper is committed.
