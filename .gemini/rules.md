Whenever you modify Kotlin code in this project, you MUST run `./gradlew detekt` (or `gradle_build(commandLine="detekt")`) afterward. If the analysis fails, you must automatically fix the issues you introduced until the build passes before providing your final response.

After completing code modifications or operations:
- Always run a project build (e.g., using `gradle_build(commandLine="assembleDebug")` or `./gradlew assembleDebug`).
- If an Android device is connected, install/deploy the app on the device after the operations (e.g., using `deploy` or `./gradlew installDebug`).
