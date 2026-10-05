# Changelog

## 1.2-r1 (Unreleased)

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

## 1.1-r1

- Replaced discrete color jumps with a smooth RGB gradient that dynamically blends across Low, Medium, and High colors. Every framerate value now has an accurate, smoothly transitioning color.
- Added visual color pickers to customize all three gradient anchor points. Defaults: Low (`#FF5555` Red), Medium (`#FFFF55` Yellow), High (`#55FF55` Green).
- Added a button to switch how color thresholds are handled:
  - Automatic (Default): Tracks gameplay framerates in real time and automatically adapts thresholds to your average FPS, smoothed with EMA to avoid sudden color jumping.
  - Manual: Lets you specify exact FPS thresholds to match your display refresh rate or target performance.
- Reorganized settings into 4 clean tabs (General, Metrics, Coloring, and Engine). All individual metrics are organized under expandable sections with concise, uncluttered tooltips.
- Added dual config library support: use **either** Cloth Config **or** YetAnotherConfigLib (YACL v3) for in-game configuration. If neither is present, the mod runs cleanly with default settings and config file support.
- Avoided replacing the vanilla framerate slider when recognized FPS-cap or FPS-textbox mods are installed, leaving those mods' controls in place.
- Optimized percentile calculations (`quickSelect`) with median-of-three pivot selection and NaN sanitization, avoiding a full sort of the sample window.
- Upgraded config storage with automatic migration for older settings, UTF-8 file encoding, and formatted JSON output.

## 1.0-r2 Refabricated

This release restored the Fabric 1.21.11 build from the previously published JAR after the original project files were lost. It updated the 1.21.11 target only.

- Restored Fabric 1.21.11 compatibility and decompiled sources.
- Fixed configuration loading so existing user settings are preserved instead of being overwritten with defaults.
- Fixed HUD cache invalidation so refreshed percentile and average metrics display immediately.
- Prevented potential crash loops when saving configs, and sanitized invalid values on startup.
- Added Sodium compatibility for the max FPS slider.
- Fixed `/accuratefps+ exportcsv` to generate proper comma-separated values.
- Updated Cloth Config and Mod Menu integrations.
