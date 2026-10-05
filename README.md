# AccurateFPS+

A lightweight, client-side **FPS HUD** mod with an **extended framerate slider**, smooth **dynamic gradient coloring**, and detailed performance telemetry. See your performance clearly at a glance and customize the HUD to suit your setup.

---

## Highlights

* **7 Performance Metrics** - Track **Live FPS, Frametime, AVG, MAX, MIN, 1% Low, and 0.1% Low** to see current performance and framerate stability in detail.
* **1% & 0.1% Lows** - Monitor lower-end framerate to identify performance drops that average FPS can hide. Low percentiles use nearest-rank selection over sampled FPS values in the configured history window.
* **Extended FPS Slider** - When no recognized conflicting FPS-cap or textbox mod is installed, AccurateFPS+ replaces the vanilla framerate limit slider with a precise **1–1000 FPS** range and 1 FPS increments. The selected limit persists in its own config file.
* **Flexible FPS Calculation** - Choose between **Real-Time**, **Averaged**, or **EMA** (exponential moving average) calculation modes.
* **FPS History & Statistics** - Configure the history window used for AVG and low-percentile calculations from **1s to 60s**. MAX and MIN are also calculated from retained samples.
* **Measured Frametime** - Display the measured frame duration in milliseconds alongside the FPS metrics.
* **Smooth Dynamic Gradient** - Blend between customizable **Low, Medium, and High** colors so FPS values transition smoothly rather than switching between fixed colors.
* **Adaptive or Manual Coloring** - Let **Automatic** mode adapt color thresholds to gameplay performance using EMA baseline smoothing, or set exact thresholds in **Manual** mode.
* **Deep HUD Customization** - Customize metric visibility, order, position, decimal precision, prefixes, suffixes, colors, text shadow, and background overlay.
* **Presets** - Quickly switch between **Minimal, Competitive, Detailed, and Custom** HUD layouts.
* **Optional Config Screens** - Use Cloth Config or YetAnotherConfigLib (YACL) for in-game settings. Without either, the mod runs with its default settings and JSON configuration files.
* **Client API** - Companion mods can read current telemetry through `com.accuratefpsplus.api.AccurateFpsApi`.
* **Conflict Avoidance** - The framerate slider integration is skipped when specific known FPS-cap or textbox mod IDs are present; this avoids replacing those controls but does not guarantee compatibility with every FPS mod.

  ![preview of the All Metrics preset](https://cdn.modrinth.com/data/cached_images/14a662614a47160304e1bd56a96e22dd3d15b1ec.gif)
  > "_All Metrics_" preset + _Fixed Auto-Coloring_ (before dynamic gradient) - **v1.0-r1**

  ![All  7 Metrics](https://cdn.modrinth.com/data/cached_images/8ff9e851312c58caf3633ba5ee912959c4eb9d73_0.webp)
  > "_All Metrics_" preset + Dynamic Coloring on FPS - **v1.2-r1**

---

## Supported Versions

| Release | Minecraft | Loader |
| :--- | :---: | :---: |
| **`1.2-r1` (Latest)** | **1.21.11**, **1.21.1** | Fabric |
| `1.1-r1` | 1.21.11, 1.21.1 | Fabric |
| `1.0-r2 Refabricated` | 1.21.11 | Fabric |
| `1.0-r1` | 1.21.1, 1.21.11 | Fabric, Forge, Quilt |

---

## Configuration

<details>
<summary>Settings are organized into 4 categories. Save changes in the configuration screen; a game restart is not required:</summary>

### 1. General
- **Enable HUD** - Toggle HUD visibility.
- **HUD Toggle Keybind** - Configure the AccurateFPS+ keybind in Minecraft's Controls menu.
- **Hide with F3** - Automatically hide the HUD while Minecraft's debug screen is active.
- **Preset Profile** - Choose Minimal, Competitive, Detailed, or Custom.
- **HUD Anchor** - Top-Left, Top-Right, Bottom-Left, Bottom-Right, or Custom.
- **X & Y Offsets** - Fine-tune the HUD position.
- **Text Shadow** - Toggle text drop shadow.
- **Background Overlay** - Optional background box with customizable color and transparency.

### 2. Metrics
Organized into 7 metric groups:
- **FPS** - Current sampled framerate.
- **Frametime** - Measured frame duration in milliseconds.
- **Average (AVG)** - Average FPS over the retained history window.
- **Maximum (MAX)** - Maximum FPS in the retained history window.
- **Minimum (MIN)** - Minimum FPS in the retained history window.
- **1% Low** - Nearest-rank low-percentile FPS over the retained samples.
- **0.1% Low** - Nearest-rank low-percentile FPS over the retained samples.

Each metric can be configured with:
- Visibility toggle
- Display slot order (slots 1–4 on the main line and 1–3 on the secondary line)
- Decimal precision (0 to 4 digits)
- Custom prefix and suffix text
- Custom color and automatic coloring
- Its own refresh interval where supported; FPS and Frametime share the main refresh interval

### 3. Coloring
- **Threshold Mode** - Choose between:
  - **Automatic**: Adapts color thresholds to gameplay performance using a smoothed baseline.
  - **Manual**: Set exact FPS values for Low, Medium, and High transitions.
- **Monitor Refresh-Rate Detection** - Set manual thresholds from the detected display refresh rate (High = native Hz, Medium = 75%, Low = 50%).
- **Gradient Colors** - Pick custom RGB colors for Low, Medium, and High FPS states.

### 4. Engine
- **Calculation Mode** - Real-Time, Averaged, or EMA.
- **History Window** - 1s, 3s, 5s, 10s, 30s, or 60s for retained samples.
- **Polling Interval** - Set how often frame data is sampled.
- **Refresh Intervals** - FPS and Frametime update together; AVG, MAX, MIN, 1% Low, and 0.1% Low have independent refresh intervals.

</details>

---

## Performance

- **Bounded telemetry history** - Retains frame samples within the configured history and required statistic refresh windows.
- **Cached HUD text** - Text composition is skipped when telemetry values and configuration have not changed.
- **Cached overlay color and dimensions** - Reuses calculated overlay color and render dimensions.
- **Low-percentile selection** - Uses Quickselect with a median-of-three pivot instead of fully sorting the sample window.
- **Shared range-statistics pass** - Calculates due AVG, MAX, and MIN values together when any of their refresh deadlines is reached.
- **Automatic config migration** - Upgrades supported older settings formats when loading the configuration.

These describe implementation choices; no FPS or memory improvement benchmark is claimed.

---

## Dependencies & Compatibility

| Mod                                                                       | Status                                      |
| ------------------------------------------------------------------------- | ------------------------------------------- |
| [Fabric API](https://modrinth.com/mod/fabric-api)                         | 📦 **Required**                             |
| [Cloth Config](https://modrinth.com/mod/cloth-config)                     | ⚙️ **Optional**                             |
| [YetAnotherConfigLib (YACL)](https://modrinth.com/mod/yacl)               | ⚙️ **Optional**                             |
| [Mod Menu](https://modrinth.com/mod/modmenu)                              | ✅ **Recommended**                            |
| [Sodium](https://modrinth.com/mod/sodium)                                 | ✅ **Compatible**                             |
| [Sodium FPS Cap Fix](https://modrinth.com/mod/sodiumfpscapfix)            | ✅ **Compatible**                             |
| [Iris](https://modrinth.com/mod/iris)                                     | ✅ **Compatible**                             |
| [ImmediatelyFast](https://modrinth.com/mod/immediatelyfast)               | ✅ **Compatible**                             |

If neither Cloth Config nor YACL is installed, AccurateFPS+ runs with its defaults and can load settings from `config/accuratefpsplus.json`. The custom framerate limit is stored separately in `config/accuratefpsplus-fps.json`.

Optional mods and rendering combinations are not all tested together. Compatibility can depend on the exact Minecraft version and mod versions.

---

## Links & Modpack Policy

> **Modrinth is the recommended source for downloads, as releases are published there first and more frequently. Dowload from the link below :** 

- **[Modrinth](https://modrinth.com/project/AQGahqU5)**
- **[GitHub](https://github.com/gingermat24/AccurateFPS-plus)**
- **[Report an issue](https://github.com/gingermat24/AccurateFPS-plus/issues)**
- Contact: Discord (`matteo.sb`)

**MIT License** - Source code is available in this GitHub repository.

You are welcome to include AccurateFPS+ in any modpack, private or public. Please give credit and link back to the Modrinth project.

---

*Built with love by gingermat*

**Note:** I know this is an overused concept for a mod, but I just wanted to test myself and get better at coding. I have much more creative projects that I hope to finish soon so that y'all can enjoy them! I hope you find this mod useful!
