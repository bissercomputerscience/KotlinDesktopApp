---
name: verify
description: Compile and test the Kotlin desktop app with Gradle and report failures. Use after making code changes to confirm they build.
---

1. Make sure you are inside a worktree (`master/` or `topic/`), not the bare repo root.
2. Run `./gradlew build --console=plain`. It compiles the code, runs the tests and runs `ktlintCheck`. For lint failures, run `./gradlew ktlintFormat` and re-run the build. The first run may take a while because Gradle downloads JDK 21 and the dependencies.
3. If it fails, report the first compiler or test error with `file:line` and the likely cause. Fix it only if the failure comes from your own change.
4. If the change affects UI or startup, say that `./gradlew run` is the manual check (it needs `DB_URL`, `DB_USER` and `DB_PASSWORD` for DB code paths). Don't launch the GUI unless asked.
5. Report the result in one line: pass or fail, plus the number of tests that ran.
