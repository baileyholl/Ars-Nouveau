# Additional MC API Changes Discovered (1.21.1 → 1.21.11)

## SwordItem → REMOVED
- Old: `net.minecraft.world.item.SwordItem`
- Status: Completely removed. No direct replacement class.
- Fix: `extends SwordItem` → `extends Item`
- Must add DataComponents.WEAPON via properties manually
- Files: EnchantersSword.java

## Tier → ToolMaterial (record)
- Old: `net.minecraft.world.item.Tier` (interface)
- New: `net.minecraft.world.item.ToolMaterial` (record)
- Method changes:
  - `tier.getAttackDamageBonus()` → `toolMaterial.attackDamageBonus()`
  - `tier.getUses()` → `toolMaterial.durability()`
  - `tier.getSpeed()` → `toolMaterial.speed()`
  - `tier.getEnchantmentValue()` → `toolMaterial.enchantmentValue()`
- Files: EnchantersSword.java

## Arrow/AbstractArrow → package moved
- Old: `net.minecraft.world.entity.projectile.AbstractArrow`
- Old: `net.minecraft.world.entity.projectile.Arrow`
- New: `net.minecraft.world.entity.projectile.arrow.AbstractArrow`
- New: `net.minecraft.world.entity.projectile.arrow.Arrow`
- Files: SpellArrow.java, SpellBow.java, SpellCrossbow.java, EntitySpellArrow.java

## windcharge → subpackage
- Old: `net.minecraft.world.entity.projectile.windcharge.*`
- New: `net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.*`

## Boat → subpackage
- Old: `net.minecraft.world.entity.vehicle.Boat`
- New: `net.minecraft.world.entity.vehicle.boat.Boat`

## UseAnim → ItemUseAnimation
- Old: `net.minecraft.world.item.UseAnim`
- New: `net.minecraft.world.item.ItemUseAnimation`
- All `UseAnim.X` → `ItemUseAnimation.X`
- Files: PotionFlask.java, any item with getUseAnimation()

## TooltipContext → Item.TooltipContext (nested interface)
- Old: imported as `net.minecraft.world.item.TooltipContext` (was separate class in 1.21.1)
- New: nested interface in `Item`: use as `Item.TooltipContext`, no import needed
- appendHoverText signature also changed:
  - Old: `appendHoverText(ItemStack, TooltipContext, List<Component>, TooltipFlag)`
  - New: `appendHoverText(ItemStack, Item.TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag)`
  - BUT: NeoForge may have compatibility shim - check if it actually errors
- Files: EnchantersSword.java, AnimatedMagicArmor.java, many others

## BannerPatternItem → REMOVED
- Old: `net.minecraft.world.item.BannerPatternItem`
- New: Use `BannerItem` or subclass `Item` with banner_pattern tag
- Files: ItemsRegistry.java (createPatternItem method)

## WeightedEntry → moved
- Old: `net.minecraft.util.random.WeightedEntry`
- Check: `jar tf sources.zip | grep Weighted`
- Files: RitualAnimalSummoning.java

## advancements.critereon → advancements.criterion (package rename)
- Old: `net.minecraft.advancements.critereon.*`
- New: `net.minecraft.advancements.criterion.*` (dropped 'e')
- Files: EntitySubPredicatesRegistry.java

## INBTSerializable → removed from neoforge
- Old: `net.neoforged.neoforge.common.util.INBTSerializable`
- New: Check `ValueIOSerializable` or just implement `serializeNBT()`/`deserializeNBT()` methods directly
  without implementing the interface, OR use `net.neoforged.neoforge.attachment.IAttachmentSerializer`
- Files: SourceStorage.java, ANPlayerData.java, ManaData.java
- Note: May just need to implement the methods directly without the interface

## Curios API — needs to be added to build.gradle
- Artifact: `top.theillusivec4.curios:curios-neoforge:14.0.0+1.21.11`
- Available at: /VanyLLa3d/minecraft/mods/curios-neoforge-14.0.0+1.21.11.jar
- Package unchanged: `top.theillusivec4.curios.api`

## Patchouli — no 1.21.11 version
- Still commented out in build.gradle
- Files using patchouli need to be disabled/stubbed
