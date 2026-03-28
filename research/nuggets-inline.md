# Nuggets Library — Inline Strategy

## Status
No 1.21.11 version of nuggets-neoforge published. Source available at `/Users/vany/l/nuggets/`.
The nuggets gradle.properties shows `minecraft_version=1.21.1` — no 1.21.11 branch.

## Classes used by Ars Nouveau (from `import com.hollingsworth.nuggets.*`)
Run: `grep -rn "import.*nuggets" src/main/java/ | sort -u`

Known usages:
- `com.hollingsworth.nuggets.client.gui.BaseScreen`
- `com.hollingsworth.nuggets.client.gui.BaseButton`
- `com.hollingsworth.nuggets.client.gui.NuggetImageButton`
- `com.hollingsworth.nuggets.client.gui.NuggetMultilLineLabel`
- `com.hollingsworth.nuggets.client.gui.GuiHelpers`
- `com.hollingsworth.nuggets.client.gui.ITooltipRenderer`
- `com.hollingsworth.nuggets.client.gui.NestedWidgets`
- `com.hollingsworth.nuggets.client.gui.NoShadowTextField`
- `com.hollingsworth.nuggets.client.gui.SearchBar` (in AN itself)
- `com.hollingsworth.nuggets.common.util.BlockPosHelpers`
- `com.hollingsworth.nuggets.common.util.WorldHelpers`
- `com.hollingsworth.nuggets.common.inventory.*` (possibly)

## Source location
All common classes: `/Users/vany/l/nuggets/common/src/main/java/com/hollingsworth/nuggets/`
NeoForge-specific: `/Users/vany/l/nuggets/neoforge/src/main/java/com/hollingsworth/nuggets/`

## Inline plan
1. Create `src/main/java/com/hollingsworth/arsnouveau/client/gui/lib/` (or similar)
2. Copy needed class sources from nuggets/common/ keeping same class names
3. Update imports in AN files from `com.hollingsworth.nuggets.*` → `com.hollingsworth.arsnouveau.client.gui.lib.*`
4. Remove nuggets from build.gradle dependencies

## Alternative
Check if nuggets compiles against 1.21.11 NeoForge — if so, just add as local dependency.
Build nuggets locally targeting neo 21.11.x and jarJar it.
