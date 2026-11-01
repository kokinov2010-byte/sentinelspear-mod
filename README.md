# Sentinel Chain Spear

A NeoForge mod for **Minecraft Java 1.21.1** (NeoForge 21.1.x, Java 21).

## Features

- Sentinel Chain Spear item, listed in the Combat creative tab.
- 36 attack damage and 1.4 attack speed; epic rarity and fire resistant.
- **X — Chain Throw & Pull:** targets an entity or block up to 15 blocks away, damages a living target and pulls the player toward the hit point. 2-second cooldown.
- **Z — Ultra Sweep:** after a short wind-up, damages nearby living entities in a 7-block radius and knocks them back. 4-second cooldown.
- Cooldown HUD while holding the spear.
- User-supplied transparent pixel-art image used as the in-game item sprite.

## Requirements

- Minecraft Java Edition 1.21.1
- NeoForge 21.1.x (the project is configured for 21.1.72)
- Java 21
- Gradle 8.8 or newer (Gradle 8.10.2 is recommended)

## Build

From this folder, run:

```bash
gradle build
```

On Windows, run `gradle.bat build` if Gradle is installed and on PATH.

The mod JAR will be created in `build/libs/`. Copy that JAR into the `mods` folder of the Minecraft instance that is running **Minecraft 1.21.1 + NeoForge**.

## Controls

The default X and Z bindings can be changed in Minecraft: Options → Controls → Key Binds → Sentinel Chain Spear.

## Notes

This archive contains the complete Gradle source project. A compiled JAR is not included: this environment did not have a working Gradle installation or access to download the Gradle distribution, so the project could not be compiled and runtime-tested here. Build it locally with the requirements above.
