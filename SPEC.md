# Specifications for the project

## Target platform
- Minecraft: 1.21.11
- NeoForge: 21.11.38-beta
- Loader version range: [4,)
- MC version range: [1.21.11, 1.22)
- NeoForge version range: [21.11,)

## Dependency versions
| Dependency | Version | Status |
|-----------|---------|--------|
| GeckoLib | 5.4.5 | ✅ active |
| Curios | 14.0.0+1.21.11 | ✅ active |
| JEI | 27.4.0.17 | ✅ compileOnly |
| EMI | 1.1.19+1.21.1 | ❌ awaiting 1.21.11 release |
| Patchouli | 87-NEOFORGE | ❌ awaiting 1.21.11 release |
| Caelus | 7.0.0+1.21 | ❌ awaiting 1.21.11 release |
| LambDynamicLights | 4.5.1+1.21.1 | ❌ awaiting 1.21.11 release |

## Test instances (PrismLauncher)
- `1.21.11` — minimal test instance (GeckoLib 5.4.5, Curios 14.0.0+1.21.11, JEI 27.4.0.17)
- `VanyLLa3d` — full modpack instance; primary test target

## Source submodules (deps/)
- `deps/geckolib` — github.com/bernie-g/geckolib branch 1.21.11
- `deps/curios`   — github.com/TheIllusiveC4/Curios branch 1.21.x
- `deps/jei`      — github.com/mezz/JustEnoughItems branch 1.21.11
