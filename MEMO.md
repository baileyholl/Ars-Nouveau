# Migration Notes: Ars Nouveau 1.21.1 → 1.21.11

## Status
Session 2025-03-18: Most flagged API migration tasks already completed in previous sessions.
Key remaining: `Entity.RemovalReason` prefix fixes applied. `AllayBehavior.appendHoverText` call-site fixed.
Build dependencies (Curios + JEI) already in build.gradle.
See `research/` folder for all detailed findings.

## Sources Used for Research
- MC 1.21.11 sources: `~/.gradle/caches/neoformruntime/intermediate_results/sourcesWithNeoForge_*_output.zip`
- NeoForge 21.11.38-beta sources: `~/.gradle/caches/modules-2/.../neoforge-21.11.38-beta-sources.jar`
- GeckoLib 5.4.2 jar: `~/.gradle/caches/.../geckolib-neoforge-1.21.11-5.4.2.jar`
- Modpack with 1.21.11 mods: `/VanyLLa3d/minecraft/mods/` (curios, JEI, etc.)
- Nuggets source (1.21.1, no 1.21.11 release): `/Users/vany/l/nuggets/`
- DO NOT reference JustDireThings — it is an old mod, not reliable for 1.21.11 API

## Key API Changes (all verified from sources)

### Removed classes
| Old | New |
|-----|-----|
| `SwordItem` | `extends Item` + `DataComponents.WEAPON` manually |
| `Tier` (interface) | `ToolMaterial` (record) — `getAttackDamageBonus()` → `attackDamageBonus()` |
| `ItemInteractionResult` | `InteractionResult` (unified sealed interface) |
| `DirectionProperty` | `EnumProperty<Direction>` |
| `ItemNameBlockItem` | `BlockItem` |
| `DeferredSpawnEggItem` | `SpawnEggItem` + `DataComponents.ENTITY_DATA` + `TypedEntityData.of(entityType, new CompoundTag())` |
| `FlyingMob` | `PathfinderMob` |
| `BannerPatternItem` | `BannerItem` or plain `Item` |
| `BlockEntityWithoutLevelRenderer` | `IClientItemExtensions` (NeoForge) |
| `INBTSerializable` (NeoForge) | Implement `serializeNBT`/`deserializeNBT` directly without interface |
| `WeightedEntry` | Check `net.minecraft.util.random` - may be inner class |

### Moved packages
| Old | New |
|-----|-----|
| `projectile.AbstractArrow` | `projectile.arrow.AbstractArrow` |
| `projectile.Arrow` | `projectile.arrow.Arrow` |
| `projectile.windcharge.*` | `projectile.hurtingprojectile.windcharge.*` |
| `vehicle.Boat` | `vehicle.boat.Boat` |
| `world.level.GameRules` | `world.level.gamerules.GameRules` |
| `advancements.critereon` | `advancements.criterion` (dropped 'e') |
| `DataComponentInput` (inner) | `net.minecraft.core.component.DataComponentGetter` |
| `DimensionTransition` | `TeleportTransition` (same package `world.level.portal`) |

### Renamed classes
| Old | New |
|-----|-----|
| `UseAnim` | `ItemUseAnimation` (same package) |
| `TooltipContext` (was top-level) | `Item.TooltipContext` (nested interface, no import needed) |
| `AnimationState` (GeckoLib) | `AnimationTest` in `software.bernie.geckolib.animation.state` |

### Signature changes
- `appendHoverText(ItemStack, Item.TooltipContext, List<Component>, TooltipFlag)`
  → `appendHoverText(ItemStack, Item.TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag)`
  - Body: `.add(x)` → `.accept(x)`, `super` call needs `display` added
  - Affects ~44 files
- `GeckoLib`: wildcard `import software.bernie.geckolib.animation.*` misses subpackages
  - Need explicit imports for `AnimationTest`, `PlayState`, `AnimatableManager`

## Dependencies Confirmed for 1.21.11
- GeckoLib: `geckolib-neoforge-1.21.11:5.4.2` ✅ already in build.gradle
- Curios: `curios-neoforge:14.0.0+1.21.11` — needs adding to build.gradle
- JEI: `jei-1.21.11-neoforge:27.4.0.15` — needs adding to build.gradle
- Nuggets: no 1.21.11 release → inline classes into AN keeping same package
  - Copy from `/Users/vany/l/nuggets/common/src/main/java/com/hollingsworth/nuggets/`
  - Keep package `com.hollingsworth.nuggets.*` to avoid changing all imports

## What's Done
- [x] gradle.properties: versions updated
- [x] build.gradle: moddev 2.0.140, parchment disabled, geckolib updated, Curios + JEI present
- [x] neoforge.mods.toml: version ranges updated
- [x] Bulk rename: ResourceLocation→Identifier (360 files)
- [x] Nuggets files inlined into src; BaseScreen ScreenAccessor mixin resolved
- [x] Arrow/AbstractArrow package fixed
- [x] windcharge package fixed
- [x] Boat package fixed
- [x] UseAnim → ItemUseAnimation fixed
- [x] GameRules package fixed
- [x] advancements.critereon → criterion fixed
- [x] TooltipContext → Item.TooltipContext (import removed, type updated)
- [x] EnchantersSword: SwordItem→Item, Tier→ToolMaterial, constructor fully fixed
- [x] appendHoverText signature: all ~44 files updated with TooltipDisplay + Consumer
- [x] AllayBehavior.appendHoverText call-site fixed (2025-03-18)
- [x] BannerPatternItem, DeferredSpawnEggItem, ItemNameBlockItem — already replaced
- [x] ItemInteractionResult → InteractionResult — already replaced
- [x] DirectionProperty → EnumProperty<Direction> — already replaced
- [x] INBTSerializable — already removed from all files
- [x] DataComponentGetter — correctly used in MobJarTile, AbstractSourceMachine, etc.
- [x] FlyingMob → PathfinderMob (EntityBookwyrm already fixed)
- [x] DimensionTransition → TeleportTransition (PortalTile already fixed)
- [x] Entity.RemovalReason — fixed bare `RemovalReason` in 24 entity files (2025-03-18)
- [x] GeckoLib AnimationState → AnimationTest — all files have correct explicit imports
- [x] **GeckoLib 5 AnimationController type inference** — fixed all diamond `<>` / raw / wildcard usages
  - `AnimationController<EntityClass>` explicit type required when passing lambda predicates
  - Fixed in: WildenChimera, Alakarkinos, Nook, AnimBlockSummon, Starbuncle, EnchantingApparatusTile,
    WildenGuardian, WildenStalker, WildenHunter, EntityWixie, Whirlisprig, AmethystGolem, GiftStarbuncle,
    WealdWalker, EntityDrygmy, BasicSpellTurretTile, WhirlisprigTile, ImbuementTile, ArcaneCoreTile,
    EnchantedTurretTile, TimerSpellTurretTile, ScribesTile, RelayTile, SourcelinkTile, PotionMelderTile,
    FamiliarEntity, FamiliarJabberwog, FamiliarWhirlisprig, FamiliarStarbuncle, FamiliarBookwyrm,
    FamiliarAmethystGolem, FamiliarDrygmy, Wand, Lily, RepositoryCatalogTile, DecorBlossomTile
- [x] `causeFallDamage(float,float)` → `causeFallDamage(double,float,DamageSource)` — AbstractFlyingCreature, EnchantedFallingBlock
- [x] `GameRules.RULE_DOMOBSPAWNING` → `GameRules.SPAWN_MOBS`; `.getBoolean()` → `.get()` — WorldHelpers.java
- [x] `Direction.getNearest()` nullable return — null-checked in PathingStuckHandler.java
- [x] `EntityDataSerializers.OPTIONAL_UUID` removed — AnimBlockSummon, FamiliarEntity, EntityDummy migrated to plain UUID fields
- [x] `doHurtTarget(Entity)` → `doHurtTarget(ServerLevel, Entity)` — WildenStalker, AnimBlockSummon
- [x] `hurt(DamageSource,float)` → `hurtServer(ServerLevel,DamageSource,float)` — all entity files
- [x] `isAlliedTo(Entity)` now final — WealdWalker renamed to `isAlliedToWealdWalker()`
- [x] `save(CompoundTag)/load(CompoundTag)` → `addAdditionalSaveData/readAdditionalSaveData(ValueOutput/ValueInput)` — all tile/entity files
- [x] `MushroomCow` package: `animal` → `animal.cow` — MooshroomBehavior.java
- [x] **`Capabilities.ItemHandler` removed** — created `LegacyItemHandlerAdapter.java` bridging `IItemHandler` → `ResourceHandler<ItemResource>`; updated `CapabilityRegistry.java` to use `Capabilities.Item.BLOCK` with `VanillaContainerWrapper.of()` for Container tiles and `LegacyItemHandlerAdapter.of()` for custom IItemHandler tiles
- [x] **`StructureTemplate.fillFromWorld` last arg** changed from `@Nullable Block` to `List<Block>` — `WorldHelpers.java`
- [x] **`GuiGraphics.pose()` returns `Matrix3x2fStack` not `PoseStack`** — fixed `GuiHelpers.java` (last().pose() → new Matrix4f()), `DocClientUtils.java` (pushPose/popPose/translate(x,y,z) → pushMatrix/popMatrix/translate(x,y)), `PedestalRecipeEntry.java`, `EntityEntry.java` (entity rendering rewritten to use `InventoryScreen.renderEntityInInventoryFollowsAngle`)

## Development Environment (Docker / Ubuntu)

Minimal Ubuntu Docker setup — no Maven/Gradle needed, Gradle wrapper handles everything.

```dockerfile
FROM ubuntu:24.04
RUN apt-get update && apt-get install -y \
    openjdk-21-jdk \
    git \
    curl \
    unzip \
    && rm -rf /var/lib/apt/lists/*
```

- Gradle wrapper (`./gradlew`) downloads correct Gradle version on first run
- All mod deps downloaded by Gradle (~500MB–1GB on first build) into `~/.gradle/caches/`
- **Mount `~/.gradle` as a volume** to avoid re-downloading every container run:
  `docker run -v ~/.gradle:/root/.gradle ...`
- For headless builds (no display): `JAVA_TOOL_OPTIONS=-Djava.awt.headless=true`
- If runClient/runData opens a window: add `xvfb` package

## Additional API Changes (2026-03-23)

### Capabilities (NeoForge 1.21.11)
| Old | New |
|-----|-----|
| `Capabilities.ItemHandler.BLOCK` | `Capabilities.Item.BLOCK` (returns `ResourceHandler<ItemResource>`) |
| `Capabilities.ItemHandler.ENTITY_AUTOMATION` | `Capabilities.Item.ENTITY_AUTOMATION` |
| `InvWrapper(Container)` → `IItemHandler` | `VanillaContainerWrapper.of(Container)` → `ResourceHandler<ItemResource>` |

- `LegacyItemHandlerAdapter` created: wraps `IItemHandler` → `ResourceHandler<ItemResource>`
- `IItemHandler.of(ResourceHandler<ItemResource>)` — static factory (deprecated but available) for reverse conversion
- `CapabilityRegistry.java` migrated: container tiles use `VanillaContainerWrapper`, custom use `LegacyItemHandlerAdapter`

### GUI Rendering (1.21.11)
- `GuiGraphics.pose()` returns `Matrix3x2fStack` (2D only, NOT `PoseStack`)
  - `pushMatrix()`/`popMatrix()`, `translate(float,float)`, `scale(float,float)`
- `Font.drawInBatch8xOutline(...)` still takes `Matrix4f` → pass `new Matrix4f()` (identity)
- `PoseStack` still valid for 3D world/entity renderers

### Recipe API (1.21.11)
- `Recipe.matches()` takes `RecipeInput` subtype — `CrushRecipe.matches(new SingleRecipeInput(stack), level)`
- `StructureTemplate.fillFromWorld` last arg: `@Nullable Block` → `List<Block>`

### Entity rendering in GUI (1.21.11)
- Use `InventoryScreen.renderEntityInInventoryFollowsAngle(GuiGraphics, int,int,int,int,int,float,float,float,LivingEntity)`

## GeckoLib 5 Render State Fixes (2026-03-28)

### Root cause: addGeckolibData / getDataMap mismatch
`EntityRenderStateMixin` (and BlockEntity/HumanoidRenderState variants) calls `addGeckolibData` which writes
directly to the `geckolib$data` field injected by the mixin — NOT via `getDataMap()`.
`ArsEntityRenderState.getDataMap()` returned a different `HashMap`, so reads always missed writes.

**Fix**: override `addGeckolibData`, `hasGeckolibData`, and `getDataMap` in all three render state classes
to use the **same** `Reference2ObjectOpenHashMap` (identity keys, matching GeckoLib's mixin type).
Applied to: `ArsEntityRenderState`, `ArsBlockEntityRenderState`, `ArsHumanoidRenderState`.

### DataTickets must be singletons
`Reference2ObjectOpenHashMap` uses reference equality for keys. `DataTicket.create()` each time
produces a different instance → always a miss. Centralized in `ANDataTickets` as `static final` fields.

### Armor renderer ClassCastException (inventory screen)
`GeoArmorRenderer.captureRenderStates` (called via mixin on `EntityRenderer.createRenderState`) passes
the **player's** render state (e.g. `AvatarRenderState` in inventory screen) through a blind cast.
Overriding typed `addRenderData(AnimatedMagicArmor, RenderData, ArsHumanoidRenderState, float)` generates
a Java bridge method that casts the 3rd arg → `ClassCastException` at runtime.

**Fix**: move color injection to `DyeableGeoModel.addAdditionalStateData(T, Object, GeoRenderState)` —
this method takes `GeoRenderState` directly (no typed bridge cast). `relatedObject` is `RenderData`,
which carries the `ItemStack` with `DataComponents.BASE_COLOR`. `ArmorRenderer` is now trivial.

### Drygmy missing texture
`drygmy.png` was emptied when color variants were added. `DrygmyModel.getTextureResource()` now reads
`DRYGMY_COLOR` from the render state and returns `drygmy_{color}.png`. Default color: `brown`.

## i18n Audit (2026-03-28)
All hardcoded `Component.literal(...)` user-visible strings replaced with `Component.translatable(...)`.
New keys added to `en_us.json`:
- `ars_nouveau.dominion_wand.side_sensitive`
- `ars_nouveau.command.{learn_glyph.all, glyph_known, data_dump.error, data_dump.success, dropless.naive, dropless.simulated}`
- `ars_nouveau.jei.apparatus.{any_item, needs_lower}`
- `ars_nouveau.key.{cmd, ctrl}`
- `ars_nouveau.perk.tier_of_armor`
- `ars_nouveau.turret.aims_to`
Existing keys already in lang, now correctly used: `ars_nouveau.hue`, `ars_nouveau.lightness`,
`ars_nouveau.sat`, `ars_nouveau.learn_glyph` (%s form).

## Spellbook EditBox text invisible fix (2026-03-29)

SearchBar and EnterTextField (spell name field) in GuiSpellBook had invisible text after migration.

**Root cause**: MC 1.21.11 deferred text pipeline (`GuiRenderState.submitText`) calls
`GuiTextRenderState.ensurePrepared()` which computes `bounds()` by applying the captured pose matrix
(`Matrix3x2f`) to the text's screen-space rect via `transformMaxBounds`. When the pose is the
**identity matrix**, the transformed bounds are effectively zero-area, so `findAppropriateNode()`
returns false and the text is **silently dropped** — never rendered.
`BaseBook.drawScreenAfterScale()` does `popMatrix()` before rendering widgets, leaving them in identity
matrix context. All working `drawString` calls in the book are inside a `translate(bookLeft, bookTop)`
context — widgets were the only callsite in identity space.

**Fix**: rewrote both classes to extend `EditBox` directly; `renderWidget` wraps `super.renderWidget`
in `pushMatrix()`/`translate(getX(), getY())`/`popMatrix()` to inject a non-identity pose.
- `setBordered(false)` — suppresses EditBox's own border sprite
- `textShadow = false` — no shadow
- `setTextColor(0xFFC1CF93)` — yellow-green color (alpha=255)
- Background blit at absolute (x,y) BEFORE the matrix push; textX/textY use LOCAL coords (relative to widget origin): SearchBar `+13px`, EnterTextField `+15px`
- `super.renderWidget` = `EditBox.renderWidget` = MC 1.21.11 native pipeline with correct pose

**EnterTextField suggestion fix**: `EditBox.renderWidget` shows `suggestion` when `cursorPos == value.length()`,
which means it appears alongside typed text (after the last character). Fix: temporarily null the
suggestion when `!value.isEmpty()` before calling super, restore after. Suggestion shows only when
field is empty (default/unset state), disappears as soon as the first character is typed.

**Key lesson**: MC 1.21.11 deferred text pipeline drops text when pose matrix is identity at submission
time. Any EditBox (or custom widget calling `drawString`) rendered outside a `pushMatrix/translate`
block will silently produce no text. Always ensure a non-identity pose is active when submitting text.

## GeoBlockRenderer tryRotateByBlockstate bug (2026-03-29)

EnchantingApparatus model rendered flat/horizontal instead of upright. ArcaneCore same.

**Root cause**: GeckoLib 5 `GeoBlockRenderer.adjustRenderPose` (base class) calls `tryRotateByBlockstate`,
which reads the block's FACING property and applies an automatic yaw rotation. This works correctly for
blocks with horizontal-only FACING (NSEW). For blocks with **6-way FACING** (UP/DOWN/NSEW via
`context.getClickedFace()`), the UP-facing case is mis-handled — model is rotated flat.

Affected: any block using `BlockStateProperties.FACING` (6-way) placed on the ground (FACING=UP).

**Fix**: override `adjustRenderPose` with an empty body to suppress `tryRotateByBlockstate`.
Applied to:
- `EnchantingApparatusRenderer` — FACING=UP when placed on top of ArcaneCore ✅
- `ArcaneCoreRenderer` — FACING defaults to UP (getNearestLookingDirection) ✅
- `ImbuementRenderer` — same 6-way FACING via getClickedFace ✅

Not applied (horizontal FACING only, auto-rotation likely correct — needs in-game test):
- `LecternRenderer` (CraftingLecternBlock: HorizontalDirectionalBlock.FACING)
- `RedstoneRelayRenderer` (RedstoneRelay: HorizontalDirectionalBlock.FACING)

**Key lesson**: Always override `adjustRenderPose` without calling super in `ArsGeoBlockRenderer`
subclasses for 6-way FACING blocks. `tryRotateByBlockstate` is only safe for horizontal FACING.

## What's Pending
- [ ] Patchouli still commented out (no 1.21.11 version)
- [ ] Caelus still commented out (no 1.21.11 version)
- [ ] EMI, TerraBlender, LambDynamicLights — no 1.21.11 versions
- [ ] LecternRenderer + RedstoneRelayRenderer: test in-game (horizontal FACING, tryRotateByBlockstate may be correct or wrong)
- [ ] EnchantingApparatusBlock: debug LOGGER calls still in useItemOn + getStateForPlacement — strip before release
- [ ] In-game test: spell casting, familiars, rituals, doc screen, GeoItem rendering, armor dye colors, apparatus crafting
