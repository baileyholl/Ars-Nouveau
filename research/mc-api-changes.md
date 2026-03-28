# MC API Changes: 1.21.1 → 1.21.11

## FlyingMob → REMOVED
- Old: `net.minecraft.world.entity.FlyingMob`
- Status: Class completely removed
- Replacement: Extend `PathfinderMob` + add `FlyingMoveControl` for movement
- Interface: `net.minecraft.world.entity.animal.FlyingAnimal` (optional)
- **Fix for EntityBookwyrm**: Change `extends FlyingMob` → `extends PathfinderMob`

## RemovalReason → Entity inner class (qualified)
- Old: bare `RemovalReason` with `import net.minecraft.world.entity.Entity.RemovalReason`
- New: `Entity.RemovalReason` (use qualified name, it's a nested enum)
- Values unchanged: KILLED, DISCARDED, UNLOADED_TO_CHUNK, UNLOADED_WITH_PLAYER, CHANGED_DIMENSION

## ItemInteractionResult → REMOVED, merged into InteractionResult
- Old: `net.minecraft.world.ItemInteractionResult` (enum)
- New: `net.minecraft.world.InteractionResult` (sealed interface)
- Method signature change:
  - Block.useItemOn() now returns `InteractionResult` (not `ItemInteractionResult`)
  - Block.useWithoutItem() returns `InteractionResult`
- Value mapping:
  - `ItemInteractionResult.SUCCESS` → `InteractionResult.SUCCESS`
  - `ItemInteractionResult.SUCCESS_NO_ITEM_USED` → `InteractionResult.SUCCESS`
  - `ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION` → `InteractionResult.TRY_WITH_EMPTY_HAND`
  - `ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION` → `InteractionResult.FAIL`
  - `ItemInteractionResult.FAIL` → `InteractionResult.FAIL`
- Files affected: ~20 block files

## DimensionTransition → TeleportTransition
- Old: `net.minecraft.world.level.portal.DimensionTransition`
- New: `net.minecraft.world.level.portal.TeleportTransition`
- Constructor has more params: add `missingRespawn`, `asPassenger`, `relatives` parameters
- Constants: DO_NOTHING, PLAY_PORTAL_SOUND, PLACE_PORTAL_TICKET
- Files: EntityProjectileSpell.java, PortalTile.java

## DirectionProperty → EnumProperty<Direction>
- Old: `net.minecraft.world.level.block.state.properties.DirectionProperty`
- New: `net.minecraft.world.level.block.state.properties.EnumProperty<Direction>`
- Usage: `BlockStateProperties.FACING` static constant still works as before
- Creation: `EnumProperty.create("facing", Direction.class)` instead of `DirectionProperty.create(...)`
- Files affected: SconceBlock, TableBlock, RuneBlock, BasicSpellTurret, SpellPrismBlock, RedstoneRelay, RepositoryCatalog, ScryerCrystal, MobJar

## GameRules → moved package
- Old: `net.minecraft.world.level.GameRules`
- New: `net.minecraft.world.level.gamerules.GameRules`
- Files: MagicFire.java

## ItemNameBlockItem → REMOVED
- Old: `net.minecraft.world.item.ItemNameBlockItem`
- New: Use `BlockItem` directly — just set food/properties on the Properties object
- The class only existed to bypass name prefix; not needed anymore
- Files: BlockRegistry.java

## BlockEntityWithoutLevelRenderer → REMOVED
- Old: `net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer`
- Status: Removed in 1.21.11
- Replacement: Custom item rendering now done via `IClientItemExtensions` (NeoForge)
  - See `net.neoforged.neoforge.client.extensions.common.IClientItemExtensions`
  - Register via `RegisterClientExtensionsEvent`
- Files: BlockRegistry.java

## DataComponentInput → DataComponentGetter
- Old: `net.minecraft.world.level.block.entity.BlockEntity.DataComponentInput` (inner interface)
- New: `net.minecraft.core.component.DataComponentGetter`
- Method: `applyImplicitComponents(DataComponentGetter pComponentInput)`
- Files: MobJarTile.java, and any other BlockEntity subclass overriding applyImplicitComponents
