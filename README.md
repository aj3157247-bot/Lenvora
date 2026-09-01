# Lenvora V2 — Owner Edition

A production-oriented Lenvora base with Android offline dictionary/OCR and a private Owner Control Center.

## Owner security
The Owner console accepts **only** the configured owner email and password. There is no generic Gmail login and no user self-registration. The password is verified server-side with PBKDF2-SHA256, the session is a signed JWT, and failed attempts are temporarily rate-limited.

Default owner email configured for this package: `owner.me2027@gmail.com`.

**Do not commit `.env` to GitHub.** Keep `ADMIN_EMAIL`, `ADMIN_PASSWORD_HASH`, `JWT_SECRET`, and `DATABASE_URL` in your deployment secrets. The Android APK never contains the Owner password.

## Backend
```bash
cd backend
npm install
npm run owner:hash -- "YOUR_OWNER_PASSWORD"
# Put the generated value in ADMIN_PASSWORD_HASH in your server environment.
npm run build
npm start
```

## Admin
```bash
cd admin
npm install
npm run build
```
Set `VITE_API_URL` to the deployed API URL.

## Android
Open `android/` in Android Studio and build `app`. The GitHub Actions workflow expects a Gradle wrapper (`android/gradlew`) generated/committed by Android Studio/Gradle.
