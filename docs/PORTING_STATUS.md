# Thaumic Energistics AE2 — NeoForge 1.21.1 port status

Last updated: 2026-08-31

Target: Minecraft 1.21.1 / NeoForge 21.1.248 / AE2 19.2.17 / Thaumic Research 0.2.x+
Current development line: 0.5.0-alpha5

## Implemented foundation

- NeoForge / Java 21 project and GitHub Actions build.
- Native AE2 `AEKey` / `AEKeyType` representation for Thaumcraft aspects.
- NBT and network serialization of essentia keys.
- Adapter exposing Thaumcraft essentia storage to AE2 storage APIs.
- Digital essentia storage cells and components: 1K, 4K, 16K, 64K, 256K, 1024K.
- Larger-cell type limits and distinct 256K/1024K item/drive models.
- Classic Thaumic Energistics devices restored as dedicated items/parts:
  - ME Essentia Import Bus;
  - ME Essentia Export Bus;
  - ME Essentia Storage Bus;
  - ME Essentia Level Emitter.
- Research progression and recipes for the current cell chain and restored buses/emitter.
- Ukrainian localization corpus.

## Confirmed parity gaps from 2026-08-31 in-game audit

### Legacy 1K–64K visuals

The current 1K/4K/16K/64K item models are still placeholders:

- cells resolve to vanilla `minecraft:item/amethyst_shard`;
- components resolve to vanilla `minecraft:item/quartz`;
- the four legacy cell tiers also share the same drive-cell model.

The MIT-licensed upstream `Nividica/ThaumicEnergistics` AE2-RV6 branch contains distinct original textures for 1K/4K/16K/64K cells and components. These should be imported and wired to modern item/drive models rather than replacing them with new placeholder art.

### Terminals and crafting integration

No terminal implementation currently exists in this repository. Required parity slices:

1. Essentia Terminal with server-authoritative AE2 network synchronization.
2. Arcane Terminal / crafting integration appropriate for the modern Thaumcraft arcane recipe system.
3. Arcane Assembler/autocrafting integration after terminal/menu foundations are stable.

### Remaining original devices/integrations

Still to audit and port against the legacy addon feature list:

- Infusion Provider;
- wireless/portable terminal functionality where applicable;
- vis/P2P and remaining AE2/Thaumcraft integrations;
- dedicated models, part rendering, menus, upgrades and status visuals for restored devices;
- full research-page and recipe parity.

## Next gate

1. Restore the original 1K/4K/16K/64K cell/component textures and per-tier drive visuals.
2. Implement the Essentia Terminal as the first complete menu/screen/network subsystem.
3. Port Arcane Terminal + crafting bridge.
4. Continue the remaining original device matrix one subsystem at a time with client/server tests.

Do not mark an item complete merely because it appears in the creative tab. A parity item is complete only when its gameplay behavior, model/assets, recipe/research gate, persistence/networking (if applicable), and dedicated-server safety have been validated.
