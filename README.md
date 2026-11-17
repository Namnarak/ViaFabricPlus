# ViaFabricPlus Legacy

A community-maintained legacy client line based on upstream ViaFabricPlus 4.4.15.

## Target

- **Minecraft client:** 1.21.11
- **Mod loader:** Fabric
- **Java:** 21+
- **Modern Java server translation:** up to 26.3 through ViaVersion + ViaBackwards 5.12.2
- **Legacy servers:** keeps the legacy protocol support from ViaFabricPlus (Release, Beta, Alpha, Classic and April Fools, subject to upstream translator coverage)

This branch keeps Minecraft 1.21.11 as the native game client. It does not turn the client into Minecraft 26.3; ViaBackwards translates the 1.21.11 client protocol when connecting to newer servers.

## Build

Use the **Build** workflow on this branch. The workflow uploads the remapped mod JAR as the `Artifacts` artifact.

For a local build:

```bash
./gradlew build
```

Install the resulting JAR in a Minecraft 1.21.11 Fabric instance with Java 21 or newer.

## Bedrock Edition

This branch upgrades the bundled Bedrock stack to **ViaBedrock 0.0.32 / Bedrock 1.26.60** and the Cloudburst NetherNet transport. Bedrock connections no longer use the old RakNet client pipeline. The 1.21.11-specific hooks cover direct Bedrock host connections, NetherNet LAN/Xbox address schemes, and the refreshed multiplayer identity used by online-mode/Realms flows.

Install **ViaFabricPlus Legacy only**; do not install the separate ViaFabricPlus Bedrock addon on top. The standalone addon targets ViaFabricPlus 5.1.3+, Minecraft 26.3 and Java 25 and is not binary-compatible with this 1.21.11 line.

## Maintenance

- Base: upstream ViaFabricPlus `ver/1.21.11`
- Translation libraries: ViaVersion 5.12.2 and ViaBackwards 5.12.2
- Custom branch: `legacy/1.21.11-26.3`

This is an independent community fork and is not an official ViaVersion release.
