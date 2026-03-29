# GeckoLib 5 Block Renderers

## GeoBlockRenderer.adjustRenderPose — tryRotateByBlockstate bug

### What it does
`GeoBlockRenderer.adjustRenderPose` (base implementation) calls `tryRotateByBlockstate`.
This reads the block's FACING property and applies an automatic rotation to orient the model.

`preRenderPass` first auto-translates `(0.5, 0, 0.5)` to center the model on the block.
Then `adjustRenderPose` applies directional rotation.

### When it works
**Horizontal-only FACING** (NSEW, `HorizontalDirectionalBlock.FACING`): auto-rotation is correct
assuming the model's natural facing direction is NORTH. Works for lecterns, relays, etc.

### When it breaks
**6-way FACING** (`BlockStateProperties.FACING`, UP/DOWN/NSEW): the UP-facing case is mishandled.
Model ends up flat/horizontal instead of upright.

Affected block pattern:
```java
// In block class:
this.registerDefaultState(state.setValue(BlockStateProperties.FACING, Direction.UP));

@Override
public BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState().setValue(BlockStateProperties.FACING, context.getClickedFace());
}
```
When placed on the ground → `getClickedFace() = UP` → model rendered flat.

### Affected renderers (confirmed)
| Renderer | Block | FACING type | Fix applied |
|---|---|---|---|
| `EnchantingApparatusRenderer` | EnchantingApparatusBlock | 6-way, default UP | ✅ empty override |
| `ArcaneCoreRenderer` | ArcaneCore | 6-way, default UP | ✅ empty override |
| `ImbuementRenderer` | ImbuementBlock | 6-way, default UP | ✅ empty override |

### Not affected (horizontal FACING — test in-game to confirm)
| Renderer | Block | FACING type | Status |
|---|---|---|---|
| `LecternRenderer` | CraftingLecternBlock | NSEW only | needs test |
| `RedstoneRelayRenderer` | RedstoneRelay | NSEW only | needs test |

### Renderers already overriding adjustRenderPose correctly
- `RuneRenderer` — custom per-face rotation; explicitly documents "DO NOT call super"
- `BasicTurretRenderer`, `RotatingTurretRenderer`, `AlterationTableRenderer`, `BlossomRenderer`, `ScribesRenderer`

### Fix pattern
```java
@Override
public void adjustRenderPose(RenderPassInfo<ArsBlockEntityRenderState> renderPassInfo) {
    // Suppress tryRotateByBlockstate: misrotates 6-way FACING blocks placed facing UP.
    // No rotation needed — model is symmetric / UP is the natural orientation.
}
```

### Rule
For any `ArsGeoBlockRenderer` subclass whose block uses 6-way FACING:
**always override `adjustRenderPose` with an empty body** to prevent the bad auto-rotation.

For blocks that need actual directional rotation (like RuneRenderer), implement the rotation
manually per direction and **do not call super**.

## captureDefaultRenderState pattern (passing blockstate to renderer)

GeckoLib 5 render state is captured on the server/main thread and passed to the render thread.
Block FACING must be read in `captureDefaultRenderState` and stored via `DataTicket`.

```java
private static final DataTicket<Direction> FACING =
    DataTicket.create("ars_nouveau:my_block_facing", Direction.class);

@Override
public void captureDefaultRenderState(MyTile animatable, Void relatedObject,
        ArsBlockEntityRenderState renderState, float partialTick) {
    super.captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);
    if (animatable != null) {
        renderState.addGeckolibData(FACING,
            animatable.getBlockState().getValue(BlockStateProperties.FACING));
    }
}

@Override
public void adjustRenderPose(RenderPassInfo<ArsBlockEntityRenderState> renderPassInfo) {
    Direction dir = renderPassInfo.renderState().getOrDefaultGeckolibData(FACING, null);
    // apply rotation based on dir
}
```

**DataTicket must be a singleton** (`static final`) — `Reference2ObjectOpenHashMap` uses
reference equality for keys. Creating a new ticket each time always misses. See `ANDataTickets`.

## ArsBlockEntityRenderState
Compile-time satisfier for `GeoBlockRenderer<T, R extends BlockEntityRenderState & GeoRenderState>`.
GeckoLib's mixin adds `GeoRenderState` to `BlockEntityRenderState` at runtime, but Java can't see it.
Override `addGeckolibData`/`hasGeckolibData`/`getDataMap` to use the **same** `Reference2ObjectOpenHashMap`,
matching the mixin's field type. Without this, mixin writes and Java reads go to different maps.
