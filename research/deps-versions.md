# Dependency Versions for 1.21.11

## Available in modpack (/VanyLLa3d/minecraft/mods/)
- curios-neoforge-14.0.0+1.21.11.jar → `top.theillusivec4.curios:curios-neoforge:14.0.0+1.21.11`
- jei-1.21.11-neoforge-27.4.0.15.jar → `mezz.jei:jei-1.21.11-neoforge:27.4.0.15`

## Not in modpack (need to find/update)
- GeckoLib: geckolib-neoforge-1.21.11:5.4.2 ✅ (already in build.gradle)
- Patchouli: no 1.21.11 version found yet
- EMI: no 1.21.11 version in modpack
- TerraBlender: no 1.21.11 version in modpack
- LambDynamicLights: no 1.21.11 version in modpack
- Nuggets: no 1.21.11 version (needs inline)
- Caelus: no 1.21.11 version

## build.gradle updates needed
```groovy
// Update these commented-out lines:
compileOnly "mezz.jei:jei-1.21.11-neoforge-api:27.4.0.15"
// runtimeOnly "mezz.jei:jei-1.21.11-neoforge:27.4.0.15"

implementation "top.theillusivec4.curios:curios-neoforge:14.0.0+1.21.11"

// DeferredSpawnEggItem removed — no special dep needed
```

## gradle.properties updates needed
```properties
jei_version=27.4.0.15
curios_version=14.0.0
# curios uses: curios-neoforge:${curios_version}+1.21.11
```
