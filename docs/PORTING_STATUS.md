# Thaumic Energistics AE2 — NeoForge 1.21.1 port status

Last updated: 2026-08-31

Target: Minecraft 1.21.1 / NeoForge 21.1.248 / AE2 19.2.17 / Thaumic Research 0.2.x+
Current development line: 0.5.0-alpha6

## Implemented foundation

- NeoForge / Java 21 project and GitHub Actions build.
- Native AE2 `AEKey` / `AEKeyType` representation for Thaumcraft aspects.
- NBT and network serialization of essentia keys.
- Adapter exposing Thaumcraft essentia storage to AE2 storage APIs.
- Digital essentia storage cells and components: 1K, 4K, 16K, 64K, 256K, 1024K.
- Larger-cell type limits and distinct 256K/1024K item/drive models.
- Original MIT-licensed 1K/4K/16K/64K cell and component textures, including distinct per-tier drive visuals.
- Classic Thaumic Energistics devices restored as dedicated items/parts:
  - ME Essentia Import Bus;
  - ME Essentia Export Bus;
  - ME Essentia Storage Bus;
  - ME Essentia Level Emitter.
- Research progression and recipes for the current cell chain and restored buses/emitter.
- Essentia Terminal part foundation using AE2's server-authoritative key-type terminal protocol.
- Arcane Terminal part foundation using AE2's real crafting-terminal inventory, persistence, menu, and synchronization path.
- Modernized original terminal face models/textures, item models, research entries, recipes, and a complete addon research-page catalog.
- Ukrainian localization corpus.

## 2026-08-31 alpha6 checkpoint

### Restored legacy 1K–64K visuals

The original MIT-licensed 1K/4K/16K/64K cell and component textures have been restored from the legacy addon and wired to modern item and per-tier drive models. These entries no longer resolve to vanilla amethyst/quartz placeholders.

### Terminals and crafting integration

The first terminal foundations now exist:

1. Essentia Terminal is a real AE2 terminal part and uses the registered essentia `AEKeyType` through AE2's server-authoritative terminal protocol.
2. Arcane Terminal is a real AE2 crafting-terminal part with working network/crafting inventory, persistence, menus, and synchronization.
3. Both terminals have restored modernized legacy models, research gates, and arcane recipes.

Still required:

- client-side screen/model interaction regression tests;
- the dedicated Thaumcraft arcane-recipe and aura-vis bridge inside Arcane Terminal;
- Arcane Assembler/autocrafting integration after that bridge is stable.

### Remaining original devices/integrations

Still to audit and port against the legacy addon feature list:

- Infusion Provider;
- wireless/portable terminal functionality where applicable;
- vis/P2P and remaining AE2/Thaumcraft integrations;
- dedicated models, part rendering, menus, upgrades and status visuals for restored devices;
- full research-page and recipe parity.

## Next gate

1. Validate both terminal parts in a real client network, including placement, power states, item screens, essentia insertion/extraction, and crafting-terminal synchronization.
2. Implement the Thaumcraft arcane-recipe/aura-vis bridge for Arcane Terminal without moving authority to the client.
3. Port Arcane Assembler and the remaining original device matrix one subsystem at a time.
4. Complete dedicated models, status visuals, upgrades, research pages, and client/server regression tests.

Do not mark an item complete merely because it appears in the creative tab. A parity item is complete only when its gameplay behavior, model/assets, recipe/research gate, persistence/networking (if applicable), and dedicated-server safety have been validated.
