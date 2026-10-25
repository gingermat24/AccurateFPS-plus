# AccurateFPS+

A lightweight, client-side **FPS HUD** mod with an **extended framerate slider**, smooth **dynamic gradient coloring**, and detailed performance telemetry. See your performance clearly at a glance and customize the HUD to suit your setup.

---

## Highlights

* **7 Performance Metrics** - Track **Live FPS, Frametime, AVG, MAX, MIN, 1% Low, and 0.1% Low** to see current performance and framerate stability in detail.
* **1% & 0.1% Lows** - Calculate low-percentile FPS from valid per-frame observations within the selected history window, revealing slow frames that an average can hide.
* **Extended FPS Slider** - When no recognized conflicting FPS-cap or textbox mod is installed, AccurateFPS+ replaces the vanilla framerate limit slider with a precise **1–1000 FPS** range, followed by **Unlimited**, in 1 FPS increments. The selected limit persists in its own config file.
* **Flexible FPS Calculation** - Choose between **Real-Time**, **Averaged**, or **EMA** (exponential moving average) calculation modes.
* **FPS History & Statistics** - Configure the history window used for AVG and low-percentile calculations from **1s to 60s**. MAX and MIN are also calculated from retained samples.
* **Measured Frametime** - Display the measured frame duration in milliseconds alongside the FPS metrics.
* **Smooth Dynamic Gradient** - Blend between customizable **Low, Medium, and High** colors so FPS values transition smoothly rather than switching between fixed colors.
* **Adaptive or Manual Coloring** - Let **Automatic** mode adapt color thresholds to gameplay performance using EMA baseline smoothing, or set exact thresholds in **Manual** mode.
* **Deep HUD Customization** - Customize metric visibility, order, position, decimal precision, prefixes, suffixes, colors, text shadow, and background overlay.
* **HUD Size** - Scale the complete HUD from **1% to 200%**; default is **100%**.
* **Presets** - Quickly switch between **Minimal, Competitive, Detailed, and Custom** HUD layouts.
* **Preset Cycle Keybind** - Cycle through Minimal, Competitive, and Detailed presets in-game; the keybind starts unbound to avoid conflicts with other mods.
* **In-Game Settings** - Cloth Config and YACL provide optional settings screens. On Fabric, the unbound-by-default **Open Configuration** keybind is available when at least one is installed; the Mod Menu entry is also available when Mod Menu is installed. NeoForge always has a Mods-list entry and a complete native settings screen; when Cloth Config or YACL is installed, it can use the selected optional screen instead. If both are installed, choose between them in General settings; Cloth Config is the default.
* **Client API** - Companion mods can read current telemetry through `com.accuratefpsplus.api.AccurateFpsApi`.
* **Conflict Avoidance** - The framerate slider integration is skipped when specific known FPS-cap or textbox mod IDs are present; this avoids replacing those controls but does not guarantee compatibility with every FPS mod.

  ![preview of the All Metrics preset](https://cdn.modrinth.com/data/cached_images/14a662614a47160304e1bd56a96e22dd3d15b1ec.gif)
  > "_All Metrics_" preset + _Fixed Auto-Coloring_ (before dynamic gradient) - **v1.0-r1**

  ![All  7 Metrics](https://cdn.modrinth.com/data/cached_images/8ff9e851312c58caf3633ba5ee912959c4eb9d73_0.webp)
  > "_All Metrics_" preset + Dynamic Coloring on FPS - **v1.2-r1+**

---

## Supported Versions

| Release | Minecraft | Loader |
| :--- | :---: | :---: |
| `1.4-r1` **(Latest)** | **1.21.1, 1.21.11** | Fabric, NeoForge |
|                          | **26.1.x** | Fabric |
|                          | **26.1.x** | NeoForge |
|                          | **26.2, 26.3** | Fabric, NeoForge |
| `1.2-r2` `1.3-r1` | 1.21.1, 1.21.11 | Fabric, NeoForge |
| `1.2-r1` `1.1-r1` | 1.21.1, 1.21.11 | Fabric |
| `1.0-r2 Refabricated` | 1.21.11 | Fabric |
| `1.0-r1` | 1.21.1, 1.21.11 | Fabric, Forge, Quilt |

---

## Configuration

<details>
<summary>Settings are grouped into up to 4 categories. Engine tuning remains available even when related metrics are disabled. Save changes in the configuration screen; a game restart is not required:</summary>

### 1. General
- **Enable HUD** - Toggle HUD visibility.
- **HUD Toggle Keybinds** - Configure the HUD toggle and preset-cycle keys in Minecraft's Controls menu under AccurateFPS+. The preset-cycle key starts unbound so it cannot conflict with another mod's default shortcut.
- **Hide with F3** - Automatically hide the HUD while Minecraft's debug screen is active; enabled by default for new installations.
- **Preset Profile** - Choose Minimal, Competitive, Detailed, or Custom.
- **HUD Anchor** - Top-Left, Top-Right, Bottom-Left, Bottom-Right, or Custom.
- **X & Y Offsets** - Fine-tune the HUD position.
- **Text Shadow** - Toggle text drop shadow.
- **HUD Size** - Scale all HUD text and the background overlay from 1% to 200% (100% by default).
- **Settings Screen** - Cloth Config and YACL are optional. NeoForge always provides a native settings screen through its Mods-list entry and configuration keybind; either optional library can be used instead. If both libraries are installed, choose one in General settings.
- **Background Overlay** - Optional background box with customizable color and transparency.

### 2. Metrics
Organized into 7 metric groups:
- **FPS** - Current sampled framerate.
- **Frametime** - Measured frame duration in milliseconds.
- **Average (AVG)** - Average FPS over the retained history window.
- **Maximum (MAX)** - Maximum FPS in the retained history window.
- **Minimum (MIN)** - Minimum FPS in the retained history window.
- **1% Low** - Nearest-rank low-percentile FPS over valid active render-frame samples.
- **0.1% Low** - Nearest-rank low-percentile FPS over valid active render-frame samples.

Low-percentile values use nearest-rank selection across valid rendered-frame samples in the selected history window; they are percentiles, not the average FPS of the slowest frames. Sampling pauses while the HUD is disabled, a screen is open, or F3 is hiding the HUD. Frame gaps of one second or longer are discarded as interruptions, so the 1% and 0.1% values can match when they select the same rank or FPS value.

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
- **Refresh Intervals** - FPS and Frametime update together; AVG, MAX, MIN, 1% Low, and 0.1% Low have independent refresh intervals. All intervals remain configurable even when their metrics are disabled.

</details>

Use **Cycle HUD Preset** in the Controls menu to rotate **Minimal → Competitive → Detailed → Minimal**. Cycling from a Custom preset starts at Minimal. The selected preset is saved immediately.

---

## Performance

- **Time-windowed telemetry history** - Retains frame samples within the configured history and required statistic refresh windows.
- **Frame-based low percentiles** - Uses valid render-frame intervals rather than polling-window samples, so short frame drops are represented in the low metrics.
- **Reusable percentile workspace** - Stores frame history in a time-windowed primitive ring and reuses its Quickselect workspace.
- **Cached HUD text** - Skips text composition when telemetry values and configuration have not changed.
- **Cached overlay rendering data** - Reuses calculated overlay colors and dimensions.
- **Low-percentile selection** - Uses Quickselect with a median-of-three pivot rather than sorting the full sample window.
- **Shared range-statistics pass** - Calculates due AVG, MAX, and MIN values together when any of their refresh deadlines is reached.
- **Configuration migration** - Migrates supported older settings formats when loading the configuration.

Synthetic engine-only stress profile (1,000 FPS, 60-second history, 1 ms statistic and percentile refresh intervals, 3 simulated seconds): elapsed time fell from 100.4 ms to 11.5 ms (about 88.5% lower), and allocation fell from 72.4 MB to 333 KB (about 99.5% lower). This is a synthetic engine-only measurement, not an in-game benchmark; it does not establish a gameplay performance gain.

---

## Dependencies & Compatibility

| Version                  | Loader   | 📦 Required                                      | ⚙️ Optional                                                                 | ✅ Compatible                                                                 |
|--------------------------|----------|--------------------------------------------------|-----------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| **1.21.1**               | Fabric   | [Fabric API](https://modrinth.com/mod/fabric-api) | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl), [Mod Menu](https://modrinth.com/mod/modmenu) | [Sodium](https://modrinth.com/mod/sodium), [Sodium FPS Cap Fix](https://modrinth.com/mod/sodiumfpscapfix) |
|                          | NeoForge | /                                                | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl) | [Sodium](https://modrinth.com/mod/sodium)                                     |
| **1.21.11**              | Fabric   | [Fabric API](https://modrinth.com/mod/fabric-api) | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl), [Mod Menu](https://modrinth.com/mod/modmenu) | [Sodium](https://modrinth.com/mod/sodium), [Sodium FPS Cap Fix](https://modrinth.com/mod/sodiumfpscapfix) |
|                          | NeoForge | /                                                | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl) | /                                                                             |
| **26.1.x**              | Fabric   | /                                                | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl), [Mod Menu](https://modrinth.com/mod/modmenu) | [Sodium](https://modrinth.com/mod/sodium), [Sodium FPS Cap Fix](https://modrinth.com/mod/sodiumfpscapfix) |
| **26.1.2**              | NeoForge | /                                                | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl) | [Sodium](https://modrinth.com/mod/sodium)                                     |
| **26.2 / 26.3**         | Fabric   | /                                                | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl), [Mod Menu](https://modrinth.com/mod/modmenu) | [Sodium](https://modrinth.com/mod/sodium), [Sodium FPS Cap Fix](https://modrinth.com/mod/sodiumfpscapfix) |
|                          | NeoForge | /                                                | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl) | [Sodium](https://modrinth.com/mod/sodium)                                     |

Settings are saved in `config/accuratefpsplus.json`; the extended FPS limit is saved in `config/accuratefpsplus-fps.json`.

Fabric API is required by the Fabric 1.21.x jars; the 26.x Fabric jars do not require it. Both 26.1.x jars are built against Minecraft 26.1.2 and declare compatibility with the 26.1 patch line. Clean builds and tests also pass against Minecraft 26.1 and 26.1.1 with their matching NeoForge loader builds. No dependencies are bundled. Cloth Config and YACL are optional alternative settings screens. Mod Menu is an optional Fabric menu entry, while Sodium and Sodium FPS Cap Fix are recognized for FPS-slider compatibility only. Optional mods and their combinations are not all tested together, and compatibility depends on their exact versions.

**NeoForge 1.21.11 and Sodium:** A development client using Sodium 0.8.14 with NeoForge 21.11.48 (also observed with 21.11.45) crashed during world rendering with a `ValidationGpuTexture` to `GlTexture` cast in Sodium's chunk renderer. AccurateFPS+'s Sodium integration only changes FPS-slider controls, not chunk rendering. This was observed with 1.3-r1; the 1.4-r1 runtime combination has not been verified.

---

## Links & Modpack Policy

> **Modrinth is the recommended source for downloads, as releases are published there first and more frequently. Download the jar matching your Minecraft version and loader.**

- **[Modrinth](https://modrinth.com/project/AQGahqU5)**
- **[GitHub](https://github.com/gingermat24/AccurateFPS-plus)**
- **[Report an issue](https://github.com/gingermat24/AccurateFPS-plus/issues)**
- Contact: Discord (`matteo.sb`)

**MIT License** - Source code is available in this GitHub repository.

You are welcome to include AccurateFPS+ in any modpack, private or public. Please give credit and link back to the Modrinth project.

---

*Built with love by gingermat*

**Note:** I know this is an overused concept for a mod, but I just wanted to test myself and get better at coding. I have much more creative projects that I hope to finish soon so that y'all can enjoy them! I hope you find this mod useful!