---
description: "Use when editing CHANGELOG.md or README.md. Defines audience, release-note detail, and version-labeling rules for this repository."
applyTo: ["CHANGELOG.md", "README.md"]
---
# Release Documentation Guidelines

- Write the changelog for both users and developers: explain user-visible behavior and compatibility clearly, and include concise technical details that help developers understand relevant implementation or verification.
- Keep changelog entries factual and focused on behavior, fixes, compatibility, and meaningful implementation details. Avoid task diaries and unsupported performance claims.
- Keep the README primarily user-facing, with clear feature descriptions, setup and configuration guidance, supported versions, dependencies, and known compatibility issues.
- Add technical detail to the README only when it helps users make decisions or understand setup, compatibility, or limitations.
- When preparing a release, write the README and changelog as if the new version is already the latest released version. The README must use the new version as the latest version and must not describe it as "in development" or otherwise unreleased.
- The changelog may use an "in development" label for a version that is still being prepared. This is allowed only in the changelog and must not be carried over into the README.
- The "in development" label in the changelog is temporary release-preparation information. The user will update the release date and time when the version is actually published.
- Do not leave "in development" or other pre-release wording in the README when preparing release-ready documentation.