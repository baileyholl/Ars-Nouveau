# GeckoLib 5.4.2 Import Fixes

## Problem
Wildcard import `import software.bernie.geckolib.animation.*` does NOT include subpackages.
Classes in subpackages (`animation.state`, `animation.object`, `animatable.manager`) are not found.

## Import replacements (add explicit imports)

| Class | Old package (wildcard miss) | Correct import |
|---|---|---|
| `AnimatableManager` | was in `animation` | `software.bernie.geckolib.animatable.manager.AnimatableManager` |
| `AnimationTest` | was `AnimationState` in `animation` | `software.bernie.geckolib.animation.state.AnimationTest` |
| `PlayState` | was in `animation` | `software.bernie.geckolib.animation.object.PlayState` |
| `BakedGeoModel` | was `cache.object` | `software.bernie.geckolib.cache.model.BakedGeoModel` |
| `GeoBone` | was `cache.object` | `software.bernie.geckolib.cache.model.GeoBone` |

## Verified packages in geckolib-neoforge-1.21.11-5.4.2.jar
- `software.bernie.geckolib.animatable.manager` → AnimatableManager
- `software.bernie.geckolib.animation.state` → AnimationTest, AnimationTimeline, BoneSnapshot
- `software.bernie.geckolib.animation.object` → PlayState, EasingType, LoopType
- `software.bernie.geckolib.cache.model` → BakedGeoModel, GeoBone, GeoQuad, GeoVertex
- `software.bernie.geckolib.animation` → AnimationController, RawAnimation (still here)

## Files with broken wildcard imports
Search: `grep -rn "import software.bernie.geckolib.animation\.\*" src/`
Tiles: RedstoneRelayTile, WhirlisprigTile, TimerSpellTurretTile, BasicSpellTurretTile
Entities: EntityWixie, Whirlisprig, EntityDrygmy, WildenStalker, WildenGuardian,
          GiftStarbuncle, WealdWalker, WildenHunter, Starbuncle, others

## Fix strategy
Replace wildcard with explicit imports as needed per file.
Also note: `AnimationState` class was renamed to `AnimationTest`.
Lambda param `AnimationState<T> event` → `AnimationTest<T> event`
