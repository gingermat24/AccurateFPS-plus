# AccurateFPS+

AccurateFPS+ is a client-side Fabric mod that adds a configurable performance HUD and a more precise framerate limit slider.

![AccurateFPS+ HUD preview](https://cdn.modrinth.com/data/cached_images/14a662614a47160304e1bd56a96e22dd3d15b1ec.gif)

## Supported versions

Separate Fabric builds are provided for Minecraft **1.21.1** and **1.21.11**. Both targets require Java 21. Install the jar built for your Minecraft version; these jars are not interchangeable.

## Features

### Performance metrics

The HUD can display seven metrics:

- FPS and frametime (milliseconds)
- Average, maximum, and minimum FPS
- 1% Low and 0.1% Low FPS

The low-percentile metrics use nearest-rank selection over samples in the configured history window. Choose between Real-Time, Averaged, and EMA calculation modes. The history window is configurable from 1 to 60 seconds.

Sampling uses one configurable polling interval. FPS and frametime share a refresh interval; Average, Maximum, Minimum, 1% Low, and 0.1% Low each have their own refresh interval.

The client-side API in `com.accuratefpsplus.api.AccurateFpsApi` exposes the current telemetry values for companion mods.

### Framerate limit

When no recognized FPS-cap or FPS-textbox mod is installed, AccurateFPS+ replaces the vanilla framerate slider with a 1–1000 FPS range and 1 FPS steps. Its selected limit is stored separately in `config/accuratefpsplus-fps.json`.

If a recognized conflicting cap or textbox mod is installed, AccurateFPS+ leaves the slider to that mod rather than replacing it. This is conflict avoidance, not a shared limiter or a guarantee of compatibility with every third-party FPS mod.

### HUD layout and appearance

- Minimal, Competitive, Detailed, and Custom presets
- Per-metric visibility, ordering, precision, color, prefix, and suffix
- Screen-corner anchors and X/Y position offsets
- Optional text shadow and background overlay
- Smooth color gradients with automatic or manually set FPS thresholds
- Optional monitor-refresh-rate detection to set manual color thresholds
- A keybind to toggle the HUD, and an option to hide it while the F3 debug screen is open

## Configuration

In-game configuration screens are available through Mod Menu when Cloth Config or YACL is installed. Changes are saved from the configuration screen and do not require a game restart.

Without either configuration library, the mod still runs with its defaults. Settings can be edited in `config/accuratefpsplus.json`; the framerate limit has its own file, `config/accuratefpsplus-fps.json`.

## Dependencies and compatibility

- **Fabric API** is required.
- **Cloth Config** and **YetAnotherConfigLib (YACL)** are optional alternatives for the in-game configuration screen.
- **Mod Menu** is optional and provides access to the mod's configuration screen.
- **Sodium** has an optional integration. AccurateFPS+'s Sodium mixin is only applied when Sodium is loaded.

Other rendering, HUD, and FPS-limit mods are not all tested in every combination. The FPS slider behavior described above applies to the conflicting mod IDs recognized by AccurateFPS+.

## Performance notes

The HUD samples frame durations at the configured polling interval, retains a bounded history window, and uses selection rather than a full sort for low-percentile calculations. HUD text and overlay color are cached to avoid rebuilding them when their inputs have not changed. These are implementation details, not a measured FPS improvement claim.

## Links

- [Modrinth](https://modrinth.com/project/AQGahqU5)
- [GitHub repository](https://github.com/gingermat24/AccurateFPS-plus)
- [Report an issue](https://github.com/gingermat24/AccurateFPS-plus/issues)

**Created by gingermat.** AccurateFPS+ is released under the MIT License. Modpack use is welcome; credit and a link to the project are appreciated.
