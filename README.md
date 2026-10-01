# AccurateFPS+

A lightweight, client-side **FPS HUD** mod with an **extended framerate slider**, smooth **dynamic gradient coloring**, and deep customization. See your performance clearly and in detail at a glance, exactly the way you want it.

---

## Highlights

* **6 FPS & Telemetry Metrics** - Track **Live FPS, AVG, MAX, MIN, 1% Low, and 0.1% Low** to see both your current performance and framerate stability in detail.
* **1% & 0.1% Lows** - Monitor the lower end of your framerate to identify performance drops and stuttering that average FPS can hide.  
* **Extended FPS Slider** - Replaces the vanilla framerate limit slider with a precise **1–1000 FPS** range, using 1 FPS increments instead of vanilla's 10 FPS steps. Your selected limit persists between sessions.
* **Flexible FPS Calculation** - Choose between **Real-Time** (instant), **Averaged** (smooth mean), or **EMA** (exponential moving average) calculation modes.
* **FPS History & Statistics** - Configure the history window used for AVG, MAX, MIN, 1% Low, and 0.1% Low calculations from **1s to 60s**.
* **Smooth Dynamic Gradient** - Continuously blends between customizable **Low, Medium, and High** colors so every framerate value gets a smooth, accurate color instead of jumping between fixed colors.
* **Adaptive or Manual Coloring** - Let **Automatic** mode continuously adapt color thresholds to your gameplay performance using EMA smoothing, or use **Manual** mode to define exact FPS thresholds yourself.
* **Deep HUD Customization** - Customize metric visibility, order, positioning, decimal precision, prefixes, suffixes, colors, update intervals, text shadows, and background overlays.
* **Presets** - Quickly switch between **Minimal, Competitive, Detailed, and Custom** HUD layouts.
* **Dual Config Library Support** - Use **either Cloth Config or YetAnotherConfigLib (YACL v3)** for in-game configuration. If neither is installed, the mod still works with its default settings and direct `config/accuratefpsplus.json` support.
* **Broad Compatibility** - Compatible with **Mod Menu, Sodium, Sodium FPS Cap Fix / FPS Textbox mods, Iris shaders, and ImmediatelyFast**.
* **CSV Export** - Run `/accuratefps+ exportcsv` to export your recorded framerate history and frametimes to a readable CSV table.


  ![preview of the All Metrics preset](https://cdn.modrinth.com/data/cached_images/14a662614a47160304e1bd56a96e22dd3d15b1ec.gif)
  > "_All Metrics_" Preset + _Fixed Auto-Coloring_ (before dynamic gradient) - v1.0-r1"

---

## Supported Versions

| Release | Minecraft | Loader |
| :--- | :---: | :---: |
| **`1.1-r1` (Latest)** | **1.21.11**, **1.21.1** | Fabric |
| `1.0-r2 Refabricated` | 1.21.11 | Fabric |
| `1.0-r1` | 1.21.1, 1.21.11 | Fabric |

---

## Configuration

<details>
<summary>Settings are organized into 4 clean tabs and apply immediately without restarting:</summary>

### 1. General
- **Enable HUD** - Toggle HUD on or off.
- **Preset Profile** - Quick-switch between Minimal, Competitive, Detailed, or Custom layouts.
- **HUD Anchor** - Top-Left, Top-Right, Bottom-Left, Bottom-Right, or Custom.
- **X & Y Offsets** - Fine-tune HUD position on screen.
- **Text Shadow** - Toggle text drop shadow.
- **Background Overlay** - Optional background box with customizable color and transparency.

### 2. Metrics
Organized into 6 independent metric groups:
- **FPS** - Real-time framerate.
- **Average (AVG)** - Mean framerate over the history window.
- **Maximum (MAX)** - Peak framerate.
- **Minimum (MIN)** - Lowest recorded framerate.
- **1% Low** - Framerate exceeded 99% of the time (stability benchmark).
- **0.1% Low** - Framerate exceeded 99.9% of the time (worst-case stutters), rendered on a separate line below the main HUD.

Each metric includes:
- Toggle visibility
- Display slot order (slots 1–4 for main line, 1–2 for low metrics)
- Decimal precision (0 to 4 digits)
- Custom prefix and suffix text
- Custom hex color and dynamic auto-color toggle

### 3. Coloring
- **Threshold Mode** - Clickable toggle button between:
  - **Automatic** (Default): Analyzes your session framerate and continuously adapts thresholds to your average performance using EMA smoothing.
  - **Manual**: Lets you define exact FPS values for Low, Medium, and High transitions.
- **Gradient Colors** - Pick custom RGB colors for:
  - Low FPS (Default: `#FF5555` Red)
  - Medium FPS (Default: `#FFFF55` Yellow)
  - High FPS (Default: `#55FF55` Green)
- **Manual Thresholds** - Set target FPS values when in Manual mode.

### 4. Engine
- **Calculation Mode** - Real-Time (instant), Averaged (mean over polling window), or EMA (exponential moving average).
- **History Window** - 1s, 3s, 5s, 10s, 30s, or 60s sample duration for averages and percentiles.
- **Sampling Interval** - Frame time polling rate (default 100 ms).
- **Refresh Intervals** - Independent update rates for FPS, AVG, MAX, MIN, 1% Low, and 0.1% Low.


</details>


---

## Performance

- **Zero-allocation hot path** - Primitive ring buffer for frame timings; no GC allocation per frame.
- **Cached HUD text** - Text composition is skipped when telemetry values are unchanged.
- **Cached overlay color & dimensions** - Pre-calculated RGBA packing and display dimensions.
- **Fast percentile calculation** - Quickselect algorithm with median-of-three pivot selection for zero stutter.
- **Async CSV export** - Disk I/O runs in a background thread to prevent gameplay hitches.
- **Automatic config migration** - Seamlessly upgrades configuration files between mod updates.

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

AccurateFPS+ automatically detects compatible FPS limiter integrations and avoids conflicting with dedicated FPS-cap controls when necessary.


---

## Links & Modpack Policy

- **[Modrinth Project Page](https://modrinth.com/project/AQGahqU5)**
- Contact: Discord (`matteo.sb`)

**MIT LICENSE** - Source code will be posted soon on GitHub.

You are welcome to include AccurateFPS+ in any modpack, private or public. Please give credit and link back to this Modrinth page.

---

*Built with love by gingermat*

**Note:** I know this is an overused concept for a mod, but I just wanted to test myself and get better at coding. I have much more creative projects that I hope to finish soon so that y'all can enjoy them! I hope you find this mod useful!
