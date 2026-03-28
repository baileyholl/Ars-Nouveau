# NeoForge API Changes: 21.1.x → 21.11.x

## DeferredSpawnEggItem → SpawnEggItem with DataComponents
- Old: `net.neoforged.neoforge.common.DeferredSpawnEggItem`
  ```java
  new DeferredSpawnEggItem(ModEntities.ENTITY_DRYGMY, 0xFF0000, 0x00FF00, properties)
  ```
- New: `net.minecraft.world.item.SpawnEggItem` + `TypedEntityData`
  ```java
  new SpawnEggItem(properties.component(DataComponents.ENTITY_DATA,
      TypedEntityData.of(ModEntities.ENTITY_DRYGMY.get(), new CompoundTag())))
  ```
- Imports needed:
  - `net.minecraft.world.item.SpawnEggItem`
  - `net.minecraft.core.component.DataComponents`
  - `net.minecraft.world.item.component.TypedEntityData`
  - `net.minecraft.nbt.CompoundTag`
- Note: colors (primary/secondary) set via `DataComponents.BASE_COLOR` if needed, or just ignored
- The `.get()` call on DeferredHolder is safe inside the `() ->` lambda (called at registration time)
- Files: ItemsRegistry.java (~7 registrations)

## IClientItemExtensions / BlockEntityWithoutLevelRenderer
- `IClientItemExtensions.getCustomRenderer()` REMOVED in 1.21.11
- New location: `net.neoforged.neoforge.client.extensions.common.IClientItemExtensions`
- Available methods: getArmPose(), applyForgeHandTransform(), getHumanoidArmorModel(), getGenericArmorModel()
- Register via `RegisterClientExtensionsEvent`

## Curios API (curios-neoforge-14.0.0+1.21.11)
- Main package: `top.theillusivec4.curios.api` — UNCHANGED
- Artifact: `top.theillusivec4.curios:curios-neoforge:14.0.0+1.21.11`
- No breaking import changes; just update version in build.gradle

## JEI API (jei-1.21.11-neoforge-27.4.0.15)
- Main package: `mezz.jei.api` — UNCHANGED
- Artifact: `mezz.jei:jei-1.21.11-neoforge:27.4.0.15` (update artifact ID + version)
- No breaking import changes
