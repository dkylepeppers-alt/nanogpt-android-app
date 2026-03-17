# Gradle wrapper bootstrap plan

Because this repo currently lacks a checked-in Gradle wrapper, and the development constraint is **Android phone only**, the cleanest bootstrap path is:

1. Open the GitHub repo in a browser or the GitHub mobile app.
2. Go to **Actions**.
3. Run the workflow **Bootstrap Gradle Wrapper** manually.
4. Let it generate and commit:
   - `gradlew`
   - `gradlew.bat`
   - `gradle/wrapper/gradle-wrapper.jar`
   - `gradle/wrapper/gradle-wrapper.properties`
5. After that commit lands, simplify `.github/workflows/android.yml` to wrapper-only commands:
   - `./gradlew ktlintCheck`
   - `./gradlew detekt`
   - `./gradlew assembleDebug`

## Notes

- The bootstrap workflow uses GitHub Actions-installed Gradle to generate the initial wrapper.
- This avoids needing desktop Android Studio or a local Gradle install.
- The workflow defaults to Gradle `8.7`, which matches the current CI setup.
- GitHub repository settings may need Actions write permissions enabled for workflow-created commits.
