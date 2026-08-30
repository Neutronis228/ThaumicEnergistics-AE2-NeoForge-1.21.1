# Thaumic Energistics AE2 — NeoForge 1.21.1

This branch ports Thaumic Energistics 2.6.0 concepts to Minecraft 1.21.1 / NeoForge 21.1.248 / AE2 19.2.x and Thaumic Research.

The old AE2 `IStorageChannel<IAEEssentiaStack>` design is replaced by a native addon-defined `AEKey` / `AEKeyType` named `EssentiaKey`. Thaumcraft `TCEssentiaStorage` is exposed to AE2 through an `MEStorage` adapter. This keeps essentia a first-class ME resource rather than pretending it is an item or fluid.

Legacy feature parity is tracked in `docs/PORTING_STATUS.md`.
