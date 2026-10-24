# 🧪 Aureole Testing & Quality Assurance

## 1. Testing Requirements
- Every UI component must have screenshot tests (`@PreviewTest`) in `app/src/screenshotTest`.
- Every new feature or business logic change must include unit tests (`app/src/test/`).

## 2. Previews and Verification
- Every bottom sheet, dialog, popup, or screen must provide `@AureolePreview` (`private fun`) in the file.

## 3. Gradle Quality & Verification Gates
Before finishing complex changes, validate using:
1. `./gradlew detekt --auto-correct` (max 5 issues)
2. `./gradlew testDebugUnitTest` (100% passing)
3. `./gradlew validateDebugScreenshotTest`
4. `./gradlew assembleDebug`
