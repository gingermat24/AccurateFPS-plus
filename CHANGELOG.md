# Changelog

## 1.3-r1 - 18/10/2026 (18:00 italian time)

- Updated the NeoForge build targets to 21.1.263 for Minecraft 1.21.1 and 21.11.48 for Minecraft 1.21.11.
- Confirmed that the latest stable Sodium build for NeoForge 1.21.11 (0.8.14) crashes during chunk rendering with NeoForge 21.11.48; the failure is in Sodium's renderer, outside AccurateFPS+'s FPS-slider integration. The same issue was recorded with Sodium 0.8.14 / NeoForge 21.11.45 during 1.2-r2 testing, so this is not a new 1.3-r1 regression. Packaged-runtime compatibility remains unverified.
- Verified the latest NeoForge 1.21.1 profile with Sodium, Cloth Config, and YACL, and NeoForge 1.21.11 with Cloth Config and YACL but without Sodium; both loaded and saved a world.
- Updated Fabric Loom from 1.18.3 to 1.18.4 for both Fabric Minecraft targets.
- Fixed invisible setting names in the NeoForge 1.21.11 native settings screen by drawing custom text with opaque colors; buttons display only their selected values.
- Kept the NeoForge Mods-list settings screen as a native fallback when neither optional screen library is installed; Fabric configuration still requires Cloth Config or YACL.
- Kept the unbound Open Configuration keybind on both loaders; Fabric Mod Menu entries require at least one supported screen library, while NeoForge always provides its Mods-list settings entry.
- Routed the NeoForge Open Configuration keybind through the native fallback when neither optional screen library is installed.
- Kept Cloth Config and YACL optional and independent; if both are installed, users can choose between them, with Cloth Config selected by default.
- Preserved the saved screen preference when migrating existing configuration files.
- Hide the Settings Screen selector unless both screen libraries are installed; route Mod Menu, NeoForge, and the keybind through the same saved preference.
- Added a persistent HUD Size slider from 1% to 200% (100% default), scaling both HUD lines, layout, anchor clamping, and the background overlay.
- Made both optional settings screens contextual: hide custom coordinates, inactive metric details, and unused overlay/coloring controls; Engine tuning settings remain visible regardless of enabled metrics.
- Kept third-party UI libraries optional and unembedded.
- Aligned Fabric keybind categories with their NeoForge counterparts.
- Pause telemetry while the HUD is disabled, a screen is open, or Hide with F3 is active; reset the frame-time and polling baseline on resume.
- Discard active frame gaps of 1 second or longer so suspends and long interruptions do not become FPS, frametime, rolling-statistic, or percentile samples.
- Calculate 1% Low and 0.1% Low from per-rendered-frame FPS observations using nearest-rank selection over the configured history window; small histories can still produce equal values when their selected ranks coincide.
- Store percentile frame history in a time-windowed primitive ring and reuse a Quickselect workspace; calculate both lows from one copied sample set when their refreshes coincide.
- Added deterministic tests for inactive intervals, one-second gaps, irregular frame durations, small sample windows, and distinct low-percentile ranks.
- Synthetic engine-only stress profile (1,000 FPS, 60-second history, 1 ms statistic and percentile refresh intervals, 3 simulated seconds): elapsed time fell from 100.4 ms to 11.5 ms (about 88.5% lower), and allocation fell from 72.4 MB to 333 KB (about 99.5% lower). This is a synthetic engine-only measurement, not an in-game benchmark; it does not establish a gameplay performance gain.

## 1.2-r2 - 2026-10-11

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

## 1.2-r1 - 05/10/2026

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

## 1.1-r1 - 27/09/2026

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

## 1.0-r2 Refabricated - 14/09/2026

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

## 1.0-r1 02/05/2026

No changelog was provided.
