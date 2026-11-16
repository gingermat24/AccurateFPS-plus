# AccurateFPS+

**See more than FPS!** A lightweight, client-side Minecraft HUD for live FPS, frametime, averages, low-percentile readings, and an extended framerate limit.

[Download on Modrinth](https://modrinth.com/project/AQGahqU5) · [Report an issue](https://github.com/gingermat24/AccurateFPS-plus/issues)

> **Latest: 1.4-r2** - more efficient history statistics and safer manual FPS-color thresholds.

<br>

![AccurateFPS+ HUD showing all seven metrics over Minecraft gameplay](https://cdn.modrinth.com/data/cached_images/8ff9e851312c58caf3633ba5ee912959c4eb9d73_0.webp)
> Current HUD: all seven metrics with dynamic FPS coloring (v1.2-r1 and later).

<br>

![Animated preview of the AccurateFPS+ HUD in Minecraft](https://cdn.modrinth.com/data/cached_images/14a662614a47160304e1bd56a96e22dd3d15b1ec.gif)
> Animated preview from v1.0-r1; the HUD layout and coloring have changed since.


## What it does

- **Shows seven metrics:** FPS, frametime, AVG, MAX, and MIN over your history window, plus 1% Low and 0.1% Low.
- **Helps reveal frame drops:** Low metrics use valid rendered-frame samples over your selected history window; they are not averages of the slowest frames.
- **Fits your HUD:** Choose a preset, position, size, metric order, precision, text, colors, shadow, and background.
- **Sets your FPS limit:** Choose 1–1,000 FPS in one-FPS steps or Unlimited.
- **Colors FPS at a glance:** Choose automatic thresholds that adapt to gameplay or set manual thresholds.

## Supported versions

| Release | Minecraft versions | Loaders |
|---|---|---|
| `1.4-r2` **(Latest)** | **1.21.1, 1.21.11, 26.1.x, 26.2, 26.3** | Fabric, NeoForge |
| `1.4-r1` | 1.21.1, 1.21.11, 26.1.x, 26.2, 26.3 | Fabric, NeoForge |
| `1.3-r1`, `1.2-r2` | 1.21.1, 1.21.11 | Fabric, NeoForge |
| `1.2-r1`, `1.1-r1` | 1.21.1, 1.21.11 | Fabric |
| `1.0-r2 Refabricated` | 1.21.11 | Fabric |
| `1.0-r1` | 1.21.1, 1.21.11 | Fabric, Forge, Quilt |

The 26.1.x builds support Minecraft 26.1, 26.1.1, and 26.1.2.

## Install

1. Install Fabric or NeoForge for your Minecraft version.
2. Download the matching AccurateFPS+ 1.4-r2 jar from [Modrinth](https://modrinth.com/project/AQGahqU5).
3. Put the jar in your Minecraft `mods` folder and launch the game.

**Required:** Fabric API is required for Fabric on Minecraft 1.21.1 and 1.21.11. No additional required library is needed for Fabric 26.x or NeoForge.

### Open the settings

- **Fabric:** Install Cloth Config or YACL for the Open Configuration keybind. Mod Menu adds a settings entry when installed.
- **NeoForge:** Open the built-in settings screen from the Mods list. Cloth Config and YACL are optional alternatives.

### Dependencies and integrations

| Minecraft | Loader | Required | Optional settings | Compatible integrations |
|---|---|---|---|---|
| 1.21.1 | Fabric | [Fabric API](https://modrinth.com/mod/fabric-api) | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl), [Mod Menu](https://modrinth.com/mod/modmenu) | [Sodium](https://modrinth.com/mod/sodium), [Sodium FPS Cap Fix](https://modrinth.com/mod/sodiumfpscapfix) |
|  | NeoForge | None | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl) | [Sodium](https://modrinth.com/mod/sodium) |
| 1.21.11 | Fabric | [Fabric API](https://modrinth.com/mod/fabric-api) | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl), [Mod Menu](https://modrinth.com/mod/modmenu) | [Sodium](https://modrinth.com/mod/sodium), [Sodium FPS Cap Fix](https://modrinth.com/mod/sodiumfpscapfix) |
|  | NeoForge | None | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl) | See Sodium compatibility note above |
| 26.1.x | Fabric | None | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl), [Mod Menu](https://modrinth.com/mod/modmenu) | [Sodium](https://modrinth.com/mod/sodium), [Sodium FPS Cap Fix](https://modrinth.com/mod/sodiumfpscapfix) |
|  | NeoForge | None | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl) | [Sodium](https://modrinth.com/mod/sodium) |
| 26.2 / 26.3 | Fabric | None | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl), [Mod Menu](https://modrinth.com/mod/modmenu) | [Sodium](https://modrinth.com/mod/sodium), [Sodium FPS Cap Fix](https://modrinth.com/mod/sodiumfpscapfix) |
|  | NeoForge | None | [Cloth Config](https://modrinth.com/mod/cloth-config), [YACL](https://modrinth.com/mod/yacl) | [Sodium](https://modrinth.com/mod/sodium) |

Fabric API is required only for Fabric on Minecraft 1.21.x. Cloth Config, YACL, Mod Menu, Sodium, and Sodium FPS Cap Fix are optional and are not bundled. Optional-mod combinations are not all tested.

> **Known issue - NeoForge 1.21.11:** Sodium 0.8.14 and 0.8.15-beta.1 crash during chunk rendering in `ShaderChunkRenderer.begin` (`ClassCastException: ValidationGpuTexture cannot be cast to GlTexture`). This is a Sodium/NeoForge renderer failure, not an AccurateFPS+ crash. AccurateFPS+ works on NeoForge 1.21.11 without Sodium.

## Customize

- **HUD:** Toggle it in-game, hide it while F3 is open (on by default for new installs), choose Minimal, Competitive, Detailed, or Custom, and set its anchor, custom offsets, and size (1%–200%; 100% by default).
- **Metrics:** Show or hide metrics, arrange them across the two HUD lines, and set 0–4 decimal places, prefixes, suffixes, colors, text shadow, and background.
- **Telemetry:** Choose Real-Time, Averaged, or EMA calculations; set the polling interval and a 1, 3, 5, 10, 30, or 60-second history window. AVG, MAX, MIN, 1% Low, and 0.1% Low have independent refresh intervals; FPS and frametime share one.
- **Coloring:** Choose automatic thresholds that adapt to gameplay or set manual Low, Medium, and High FPS thresholds. Manual thresholds stay ordered within 1–1,000 FPS when saved or loaded. Monitor detection sets High to your display's native refresh rate, Medium to 75%, and Low to 50%.
- **Controls:** Set the HUD-toggle and preset-cycle keys in **Controls → Key Binds → AccurateFPS+**. The preset-cycle key is unbound by default.

Settings are saved without restarting the game. Sampling pauses while the HUD is disabled, a screen is open, or F3 hides the HUD; frame gaps of one second or more are ignored.

## FPS limit compatibility

AccurateFPS+ extends Minecraft's framerate limit to 1–1,000 FPS, in one-FPS steps, plus Unlimited. The selected limit is saved separately. If a recognized FPS-cap or textbox mod is installed (including Sodium FPS Cap Fix, FPS Cap Fix, or FPS Textbox), AccurateFPS+ leaves the vanilla slider to that mod. This avoids replacing its controls, but does not guarantee compatibility with every FPS mod.

## Developers and modpacks

See the [changelog](./CHANGELOG.md) for version-specific compatibility, implementation, tests, and benchmark details. Source code is available in this [GitHub repository](https://github.com/gingermat24/AccurateFPS-plus) under the MIT License.

You may include AccurateFPS+ in private or public modpacks. Please credit the project and link to [Modrinth](https://modrinth.com/project/AQGahqU5).