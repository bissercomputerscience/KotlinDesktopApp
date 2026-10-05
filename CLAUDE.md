# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repo layout

The repository root is a **bare git repo**. The code lives in two worktrees, `master/` and `topic/`. `topic` is for experiments and `master` is the stable line. Run all Gradle and git commands from inside a worktree, never from the bare root.

## Stack

- Compose Multiplatform **desktop only**: a single Kotlin/JVM module, not KMP. It uses Material3.
- JDK 21 through `jvmToolchain(21)`; Gradle downloads the JDK via foojay if needed. Gradle wrapper 9.6.
- Dependency versions are written inline in `build.gradle.kts` (there is no version catalog).
- ktlint (Gradle plugin `org.jlleitschuh.gradle.ktlint`, `ktlint_official` style, rules in `.editorconfig`). A PostToolUse hook runs `./gradlew ktlintFormat` after every `.kt`/`.kts` edit, so unused imports are removed automatically. Add an import in the same edit that uses it.

## Commands

- Run the app: `./gradlew run`
- Build, test and lint: `./gradlew build` (this includes `ktlintCheck`). Run `./gradlew ktlintFormat` to auto-fix. Run a single test with `./gradlew test --tests "pkg.ClassName.method"`. There are no tests yet; put them in `src/test/kotlin`.
- Make a distributable: `./gradlew packageDistributionForCurrentOs`. No `nativeDistributions {}` block is configured yet.

## Database

MySQL via `mysql-connector-j`. `DatabaseConfig` reads the env vars `DB_URL`, `DB_USER` and `DB_PASSWORD`, which are usually set in an IntelliJ run configuration. Anything that touches the DB fails without them.

## Conventions

- Package root is `guru.bisser`; directory paths must match package names. The entry point is `guru.bisser.MainKt` (set as `mainClass` in `build.gradle.kts`).
- Commit messages use Conventional Commits (`feat:`, `fix:`, `refactor:`, …).
- `docs/Description.md` is the project description, written in Bulgarian.
