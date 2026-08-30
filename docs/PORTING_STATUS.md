# Thaumic Energistics AE2 — NeoForge 1.21.1 port status

Target: Minecraft 1.21.1 / NeoForge 21.1.248 / AE2 19.2.17 / Thaumic Research 0.2.x

## 0.1.0-alpha1 gate

Implemented:
- NeoForge / Java 21 project and GitHub Actions build
- native AE2 `AEKey`/`AEKeyType` representation for Thaumcraft aspects
- NBT and network serialization of essentia keys
- adapter exposing `TCEssentiaStorage` as AE2 `MEStorage`
- original 1K/4K/16K/64K digital essentia storage-cell capacities, 12 types per cell
- Ukrainian localization corpus including legacy research text

Next parity slices:
1. Essentia Storage Bus (jars -> ME storage)
2. Essentia Import/Export Buses and suction behavior
3. Essentia Terminal and network synchronization
4. Level emitter / status monitoring
5. Infusion Provider
6. Arcane Terminal + Arcane Assembler
7. Wireless terminal / vis P2P / remaining integrations

Alpha1 is a real native-storage foundation, not yet full Thaumic Energistics 2.6.0 feature parity.
