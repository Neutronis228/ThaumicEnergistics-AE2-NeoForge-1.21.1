# Thaumic Energistics AE2 — NeoForge 1.21.1

Clean NeoForge 1.21.1 port of Thaumic Energistics for **Thaumic Research / Thaumcraft 6** and **Applied Energistics 2**.

## Target
- Minecraft 1.21.1
- NeoForge 21.1.248
- Java 21
- Applied Energistics 2 19.2.x
- Thaumic Research 0.2.x

## Port strategy
The legacy 1.12.2 `IStorageChannel<IAEEssentiaStack>` model is being ported to AE2's modern `AEKey` / `AEKeyType` storage architecture. Essentia remains a native ME storage type; it is **not** emulated as items or fluids.

## First vertical slice
1. `EssentiaKey` + `EssentiaKeyType`
2. ME storage adapter for Thaumcraft `TCEssentiaStorage`
3. Essentia Interface bridge
4. Import / Export buses
5. Digital essentia cells
6. Essentia terminal and monitors
7. Infusion provider / Arcane Assembler / crafting integration

Ukrainian localization is maintained from the first alpha.

## Provenance
Based on the MIT-licensed Thaumic Energistics 2.6.x codebase by Chris / BrockWS and the original Thaumic Energistics lineage. See `LICENSE` and `docs/PORTING_STATUS.md`.
