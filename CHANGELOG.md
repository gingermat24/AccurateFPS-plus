# Changelog

## 1.4-r1 - 31/10/2026 (12:10 italian time)

- Added Fabric and NeoForge build targets for Minecraft 26.1.x, 26.2, and 26.3 while retaining separate targets for 1.21.1 and 1.21.11. This produces ten loader/version runtime jars. The 26.1.x jars are built against Minecraft 26.1.2 and declare `[26.1,26.2)`; clean builds and tests also pass against 26.1 and 26.1.1 with matching loader versions.
- Configured Fabric 26.x with non-remapping Loom and Java 25 for unobfuscated Minecraft; 1.21.x retains remapping Loom and Java 21. Removed Fabric API from 26.x compile and runtime metadata. Deferred 26.x key mapping registration until client options are initialized, moved client tick and HUD callbacks to client Mixins, and updated the class-tweaker namespace for unobfuscated classes.
- Added version-specific adapters for screen access, keyboard input, display refresh-rate detection, and HUD rendering. Kept a separate HUD Mixin configuration for 26.1 because its render hooks differ from 26.2 and 26.3.
- Extended the FPS limit slider to 1–1000 FPS in one-FPS increments, followed by an Unlimited stop mapped to Minecraft's unlimited value. Updated Sodium's FPS formatter integration for the target method names: `lambda$fpsLimit$2` on 1.21.11 and `lambda$fpsLimit$0` on 26.x.
- Made the Sodium FPS-range constant injections optional with `require=0`. Sodium 0.9.3-beta.1 for 26.3 now derives its range from Minecraft's `OptionInstance` instead of the legacy 260/10 constants; the injections still adjust versions such as Sodium 0.9.2 on 26.2 where those constants are present.
- Fixed metric-visibility save callbacks so saving unchanged visibility values no longer switches the selected HUD preset to Custom. Registered the NeoForge 1.21.11 key category during the key-mapping event instead of using deprecated eager shared registration.
- Ported the native NeoForge settings screen to the 26.x screen and render-state APIs, including all settings pages, metric controls, validation, scrolling, preset handling, and JSON persistence. Registered the screen in the NeoForge 26.x Mods list through `IConfigScreenFactory`; Cloth Config and YACL remain optional alternative screens.
- Added root tasks to aggregate and validate the ten release jars. CI builds and uploads the full target matrix. `buildAllVersions` and `verifyReleaseJars` completed successfully, including the project tests.
- Updated Fabric Loader from 0.19.5 to 0.20.1 and Fabric Loom from 1.18.4 to 1.19.3 on 1.21.x and from 1.19.2 to 1.19.3 on 26.x. Updated NeoForge from 21.1.263 to 21.1.265 on 1.21.1, from 26.1.2.122 to 26.1.2.124 on 26.1.x, and from 26.3.0.95 to 26.3.0.99 on 26.3. NeoForge 21.11.48 and 26.2.0.93 were already current. Checked Fabric API, Cloth Config, YACL, Mod Menu, ModDevGradle, and JUnit pins; their existing versions were current for the supported targets.
- Updated NeoForge 26.1.x from 26.1.2.124 to 26.1.2.125 and both Gradle wrappers from 9.8.1 to 9.8.2 after newer stable releases became available.
- Updated the target-specific Sodium development dependencies to 0.8.15-beta.1 for Fabric and NeoForge 1.21.11, and 0.9.3-beta.1 for Fabric and NeoForge 26.3. Other Sodium pins remain 0.8.13 for 1.21.1 and 0.9.2 for 26.1.x and 26.2. Cloth Config, YACL, Mod Menu, and Sodium are compile-only integrations and are not bundled in the release jars.
- Runtime-tested Fabric 1.21.11 with Sodium 0.8.15-beta.1, Cloth Config 21.11.153, YACL 3.8.2, and Mod Menu 17.0.1; it entered a world and rendered without a client crash. Runtime-tested NeoForge 1.21.1 with Sodium 0.8.13, Cloth Config 15.0.140, and YACL 3.8.2; it entered a world and exited normally.
- Runtime-tested NeoForge 26.2 with Sodium 0.9.2, Cloth Config 26.2.155, and YACL 3.9.7; it loaded a same-version test-world copy, joined the world, rendered chunks, and saved on exit. Fabric 26.3 with Sodium 0.9.3-beta.1 reached a world; NeoForge 26.3 with the same Sodium, Cloth Config 26.3.159, and YACL 3.9.7 also loaded a same-version test-world copy and rendered in-world without a crash.
- Re-tested NeoForge 26.1.x on 26.1.2.125 with Sodium 0.9.2, Cloth Config 26.1.154, and YACL 3.9.7; it loaded an existing world without a runtime failure.
- Tested Sodium 0.8.15-beta.1 with NeoForge 21.11.48, Cloth Config 21.11.153, and YACL 3.8.2 in the 1.21.11 development client. The client entered a world and then crashed in Sodium's `ShaderChunkRenderer.begin` with `ClassCastException: ValidationGpuTexture cannot be cast to GlTexture`. Sodium 0.8.14 showed the same renderer failure with NeoForge 21.11.45 and 21.11.48; the 0.8.15-beta.1 test confirms it remains in this setup. The failing stack is in Sodium/NeoForge chunk rendering, not AccurateFPS+'s FPS-slider integration.

## 1.3-r1 - 18/10/2026 (18:00 italian time)

- Updated the NeoForge build targets to 21.1.263 for Minecraft 1.21.1 and 21.11.48 for Minecraft 1.21.11.
- Updated Fabric Loom from 1.18.3 to 1.18.4 for both Fabric Minecraft targets.
- Confirmed that the latest stable Sodium build for NeoForge 1.21.11 (0.8.14) crashes during chunk rendering with NeoForge 21.11.48; the failure is in Sodium's renderer, outside AccurateFPS+'s FPS-slider integration. The same issue was recorded with Sodium 0.8.14 / NeoForge 21.11.45 during 1.2-r2 testing, so this is not a new 1.3-r1 regression. Packaged-runtime compatibility remains unverified.
- Verified the latest NeoForge 1.21.1 profile with Sodium, Cloth Config, and YACL, and NeoForge 1.21.11 with Cloth Config and YACL but without Sodium; both loaded and saved a world.
- Fixed invisible setting names in the NeoForge 1.21.11 native settings screen by drawing custom text with opaque colors; buttons display only their selected values.
- Routed the NeoForge Open Configuration keybind through the native fallback when neither optional screen library is installed.
- Hid the Settings Screen selector unless both screen libraries are installed, and routed Mod Menu, NeoForge, and the keybind through the same saved preference.
- Preserved the saved screen preference when migrating existing configuration files.
- Added a persistent HUD Size slider from 1% to 200% (100% default), scaling both HUD lines, layout, anchor clamping, and the background overlay.
- Made both optional settings screens contextual: hide custom coordinates, inactive metric details, and unused overlay/coloring controls; Engine tuning settings remain visible regardless of enabled metrics.
- Paused telemetry while the HUD was disabled, a screen was open, or Hide with F3 was active; reset the frame-time and polling baseline on resume.
- Discarded active frame gaps of 1 second or longer so suspends and long interruptions did not become FPS, frametime, rolling-statistic, or percentile samples.
- Calculated 1% Low and 0.1% Low from per-rendered-frame FPS observations using nearest-rank selection over the configured history window; small histories can still produce equal values when their selected ranks coincide.
- Stored percentile frame history in a time-windowed primitive ring and reused a Quickselect workspace; calculated both lows from one copied sample set when their refreshes coincided.
- Added deterministic tests for inactive intervals, one-second gaps, irregular frame durations, small sample windows, and distinct low-percentile ranks.
- Synthetic engine-only stress profile (1,000 FPS, 60-second history, 1 ms statistic and percentile refresh intervals, 3 simulated seconds): elapsed time fell from 100.4 ms to 11.5 ms (about 88.5% lower), and allocation fell from 72.4 MB to 333 KB (about 99.5% lower). This is a synthetic engine-only measurement, not an in-game benchmark; it does not establish a gameplay performance gain.

## 1.2-r2 - 2026-10-11 (21:50 italian time)

- Added separate NeoForge release jars for Minecraft 1.21.1 and 1.21.11, while retaining the existing Fabric jars and the `1.2-r2` version.
- Ported the HUD, all seven telemetry metrics, settings, presets, keybinds, F3 behavior, precise FPS slider, and optional Sodium slider integration to NeoForge.
- Compatibility note: development testing with Sodium 0.8.14 and NeoForge 21.11.45 reached a world before failing in Sodium's chunk renderer. This limitation predates 1.3-r1.
- Added a NeoForge Mods-list settings button with a built-in configuration screen when neither optional UI library is installed; Cloth Config and YACL screens remain supported when available.
- Added an unbound-by-default keybind to cycle through the Minimal, Competitive, and Detailed HUD presets in-game; preset changes are saved immediately.
- Enabled Hide with F3 by default for new installations while preserving existing saved preferences.
- Preserved saved FPS limits above the vanilla slider cap by installing the extended range before Minecraft loads its options.
- Pinned NeoForge 1.21.1 to 21.1.259 and 1.21.11 to 21.11.45; defer optional-mod discovery until the loader has initialized its mod list.
- Split loader initialization from shared HUD/configuration behavior and made the FPS slider and Sodium integrations use loader-specific mod/config services.
- Verified player-facing feature parity across all four Minecraft-version/loader jars; only loader- and game-API adapters differ.
- Added release-jar checks for shared feature classes, metadata, and loader-specific entrypoints; CI now publishes only the four runtime jars.
- Updated the Gradle wrapper to 9.8.1 and Fabric Loom to 1.18.3 for both supported Minecraft targets.
- Updated the JUnit BOM to 6.1.3 for both target builds.

## 1.2-r1 - 05/10/2026 (19:20 italian time)

- Added a client-side public API (`com.accuratefpsplus.api.AccurateFpsApi`) for companion and third-party mods to read the live HUD metrics: FPS, AVG, MAX, MIN, 1% Low, 0.1% Low, and Frametime.
- Added a real-time Frametime metric (`ms`) to monitor measured frame duration alongside framerate.
- Fixed frametime display accuracy: `displayedFrametimeMs` now reports the measured frame duration instead of deriving it from FPS.
- Synchronized all frametime telemetry directly through the telemetry engine to guarantee identical readings between the HUD display and the public API.
- Placed Frametime on the secondary telemetry line (aligned with 1% and 0.1% lows), keeping the primary line clean for main framerate stats.
- Scaled secondary line metrics (1% Low, 0.1% Low, and Frametime) to a crisp 75% size to create a clean visual hierarchy under the main FPS line.
- Added an in-game toggle keybind (`Controls -> Key Binds -> AccurateFPS+`) to easily show or hide the HUD during gameplay.
- Added an option to automatically hide the HUD whenever the F3 debug screen is active.
- Added a button in Coloring settings to automatically detect your monitor's native refresh rate via GLFW and set manual thresholds in one click.
- Added automatic screen boundary clamping so the HUD, secondary line, and background box never clip or go off-screen on any resolution or anchor corner.
- Fixed config saving so customizing individual metrics no longer gets reset by preset profiles, and automatically marks the active profile as Custom.
- Removed the legacy `/accuratefps+ exportcsv` command; the base mod retains samples for its configured telemetry window rather than CSV history export.
- Removed dead code, unused fields, and eliminated compiler warnings across vanilla options mixins and configuration screens.
- Corrected the Real-Time and Averaged calculation modes so each setting uses its matching sampling algorithm.
- Aggregated elapsed frame time directly instead of truncating the averaging window to a fixed 1,024-frame buffer.
- Reduced telemetry history traversal by calculating average, maximum, and minimum FPS in one pass when any of their refresh deadlines is due.
- Corrected 1% Low and 0.1% Low rank selection to use nearest-rank percentiles, including small sample windows.
- Made the telemetry engine shared by both supported Minecraft targets and added deterministic regression tests for calculation modes, history windows, percentiles, and non-monotonic timestamps.
- Deferred YACL's Custom preset marker until settings are saved, preventing a visibility toggle from invalidating the preset option during YACL's apply cycle.
- Kept Cloth Config, YACL, and Sodium compile-time optional; added opt-in local runtime profiles for testing each integration independently.
- Switched telemetry window timing to the monotonic clock and report measured frametime consistently on both supported Minecraft versions.
- Reported FPS-limit and settings-file I/O failures and preserved unreadable settings instead of silently replacing them with defaults.
- Updated the root build to build both supported Minecraft targets and place both release jars in `build/libs`; CI uploads that shared output folder.

## 1.1-r1 - 27/09/2026 (17:30 italian time)

- Replaced discrete color jumps with a smooth RGB gradient that dynamically blends across Low, Medium, and High colors. Every framerate value now has an accurate, smoothly transitioning color.
- Added visual color pickers to customize all three gradient anchor points. Defaults: Low (`#FF5555` Red), Medium (`#FFFF55` Yellow), High (`#55FF55` Green).
- Added a button to switch how color thresholds are handled:
  - Automatic (Default): Tracks gameplay framerates in real time and automatically adapts thresholds to your average FPS, smoothed with EMA to avoid sudden color jumping.
  - Manual: Lets you specify exact FPS thresholds to match your display refresh rate or target performance.
- Reorganized settings into 4 clean tabs (General, Metrics, Coloring, and Engine). All individual metrics are organized under expandable sections with concise, uncluttered tooltips.
- Added dual config library support: use **either** Cloth Config **or** YetAnotherConfigLib (YACL v3) for in-game configuration. If neither is present, the mod runs cleanly with default settings and config file support.
- Added soft compatibility for Sodium FPS Cap Fix and FPS textbox mods, yielding framerate limit controls when a dedicated textbox mod is installed.
- Confirmed compatibility with Iris shaders and ImmediatelyFast HUD batching.
- Optimized percentile calculations (`quickSelect`) with median-of-three pivot selection and NaN sanitization to ensure fast, stutter-free 1% and 0.1% low tracking.
- Upgraded config storage with automatic migration for older settings, UTF-8 file encoding, and formatted JSON output.

## 1.0-r2 Refabricated - 14/09/2026 (18:55 italian time)

**Finally managed to fix the code and recover everything directly from the uploaded JAR file!**

_The only updated version is Fabric 1.21.11 (version with the most downloads), as updating would've been atrocious without the original multi-version build system._

- Restored Fabric 1.21.11 compatibility and decompiled sources.
- Fixed configuration loading so existing user settings are preserved instead of being overwritten with defaults.
- Fixed HUD cache invalidation so refreshed percentile and average metrics display immediately.
- Prevented potential crash loops when saving configs, and sanitized invalid values on startup.
- Added Sodium compatibility for the max FPS slider.
- Fixed `/accuratefps+ exportcsv` to generate proper comma-separated values.
- Updated Cloth Config and Mod Menu integrations.

_Note: Sadly, a lot of unreleased additions and features in progress were lost when the original project files were lost._

## 1.0-r1 02/05/2026 (11:40 italian time)

No changelog was provided.
