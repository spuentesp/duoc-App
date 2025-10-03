# Repository Guidelines

## Project Structure & Module Organization
The Android app lives under `app/`, with module config in `app/build.gradle.kts` and shared settings at the root Gradle files. Kotlin sources follow a feature-first layout: `com.example.app/view` hosts Jetpack Compose screens, `viewmodel` exposes `State` for each screen, `data` handles repositories, and `model` holds simple DTOs. Design tokens and theming sit in `ui/theme`. Place static assets in `app/src/main/res`, keeping drawable, mipmap, and values folders organized.

## Build, Test, and Development Commands
- `./gradlew assembleDebug` builds the debug APK for device installs.
- `./gradlew lint` runs Android lint, catching Compose and resource issues before review.
- `./gradlew testDebugUnitTest` executes local JUnit unit tests.
- `./gradlew connectedDebugAndroidTest` runs instrumentation and Compose UI tests on a plugged-in device or emulator.

## Coding Style & Naming Conventions
Write Kotlin with 4-space indentation and idiomatic null-safety. Keep one `@Composable` per file when it represents a screen, using `PascalCaseScreen` (e.g., `HomeScreen`). ViewModels end with `ViewModel`, repository classes with `Repository`, and LiveData/StateFlow properties in `camelCase`. Prefer `val` over `var`, extract strings to `res/values/strings.xml`, and use `AppTheme` wrappers for new surfaces. Run `./gradlew lint` before pushing to enforce formatting and resource discipline.

## Testing Guidelines
Unit tests belong in `app/src/test`, using JUnit 4 assertions and fakes for repositories. Compose UI or navigation flows should be covered in `app/src/androidTest` with `createAndroidComposeRule`. Name tests with the pattern `FunctionUnderTest_State_ExpectedOutcome` to clarify intent. Aim to exercise new ViewModel branches and authentication flows, and document emulator needs when UI tests depend on them.

## Commit & Pull Request Guidelines
Follow concise, present-tense commit messages like `Update MainActivity to implement navigation`; group related Kotlin and resource changes together. Each PR should describe the feature, list critical screenshots for UI changes, call out affected screens, and reference any tracking issue. Confirm the Gradle commands you ran and note outstanding test coverage gaps before requesting review.

## Security & Configuration Notes
Keep SDK paths and API keys in `local.properties` or environment variables; never commit them. When adding config flags, thread them through `gradle.properties` and read them via BuildConfig so release builds remain reproducible. Review Play Store requirements if you change the `minSdk` or permissions.
