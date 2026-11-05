# Project Guidelines

## User-Facing Parity

- Keep features, settings, defaults, config behavior, and compatibility consistent across every Minecraft version and loader.
- Share implementation where possible; use version-specific adapters only for API differences. Document intentional user-visible differences.
- Before considering cross-version code changes complete, run `.\gradlew.bat buildAllVersions` and verify all release targets.

## Changes and Commits

- Batch related implementation, tests, and changelog edits into one focused change.
- Use short, consistent, imperative commit subjects. Avoid separate commits for small parts of the same update.
