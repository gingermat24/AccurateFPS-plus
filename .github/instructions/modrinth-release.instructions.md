---
description: "Use when validating or publishing Minecraft mod artifacts to Modrinth."
---
# Modrinth Release Workflow

- Before publishing, run `.\gradlew.bat buildAllVersions` and verify the release jars. Upload runtime jars only, not source jars.
- Inspect each jar's embedded metadata and filename to confirm its mod version, Minecraft version, and loader before uploading. Set the Modrinth game versions and loader to match that artifact.
- Publish from the oldest Minecraft target to the newest. For each target, publish Fabric first, then NeoForge: 1.21.1, 1.21.11, 26.1.x, 26.2, 26.3.
- Leave Modrinth's default version title unchanged (normally `AccurateFPS+ <version>`) and leave the changelog empty unless the user asks otherwise.
- For each artifact, set every applicable Required, Optional, Compatible, Incompatible, or Embedded dependency. Scope version-specific dependencies to the relevant Minecraft version, loader, and dependency version. Do not mark a library Embedded unless it is actually bundled in the jar.
- For AccurateFPS+, Fabric API is Required only for Fabric on Minecraft 1.21.1 and 1.21.11; Cloth Config and YACL are Optional settings screens; Mod Menu is Optional on Fabric. Mark compatible integrations as Compatible where applicable.
- Mark Sodium 0.8.14 and 0.8.15-beta.1 as Incompatible with NeoForge 1.21.11 only. Scope those incompatibilities to the exact Sodium versions; do not make the restriction broader than the documented issue.
- After each upload, verify the attached file, game version, loader, and dependency labels before moving to the next artifact.
