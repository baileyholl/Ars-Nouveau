# MC 1.21.11 GUI Rendering

## Deferred Text Pipeline

### How it works
`GuiGraphics.drawString()` → `GuiRenderState.submitText()` → text is submitted to a node tree.
Actual rendering happens later in `GuiRenderer.render()` → `prepareText()` → GPU draw.

Text submission goes through `GuiTextRenderState.ensurePrepared()`:
1. Calls `font.prepareText(text, x, y, color, ...)` to get glyph layout
2. Computes `bounds()` = `preparedText.bounds().transformMaxBounds(this.pose)`
   where `this.pose` is the **captured Matrix3x2f at submission time**
3. `GuiRenderState.findAppropriateNode(bounds)` — if `bounds() == null`, text is **silently dropped**

### Identity matrix drops text
When `pose` is the identity matrix, `transformMaxBounds` produces effectively zero-area bounds.
`findAppropriateNode` returns false → text never renders. No error, no warning.

**This affects any `drawString` or `EditBox.renderWidget` call made outside a `pushMatrix/translate` block.**

### Pattern: BaseBook / BaseScreen
`BaseBook.drawScreenAfterScale()` structure:
```java
graphics.pose().pushMatrix();
graphics.pose().translate(bookLeft, bookTop);
// ... draw background, foreground elements (text works here)
graphics.pose().popMatrix();
// ... render widgets (identity matrix — text FAILS here)
```

**Fix**: wrap widget `renderWidget` override in its own `pushMatrix/translate(getX,getY)/popMatrix`.

## EditBox in MC 1.21.11

### Fields
- `textX`, `textY` — public, mutable; used directly in `renderWidget` for text drawing position
- `textShadow` — public boolean
- `suggestion` — public String, shown when `cursorPos == value.length()` (at cursor end)

### renderWidget behavior (bordered=false)
- Skips border sprite blit
- Draws text at `(textX, textY)` in current pose space
- Shows `suggestion` when `cursorPos == value.length()` — **even when value is non-empty!**
  (cursor is at end of typed text → flag2=false → suggestion rendered after the last char)
- Does NOT call `updateTextPosition()` internally

### updateTextPosition() resets textX/textY to absolute coords
```java
textX = getX() + (isCentered() ? (width - font.width(s)) / 2 : (bordered ? 4 : 0));
textY = bordered ? getY() + (height - 8) / 2 : getY();  // non-bordered: just getY()!
```
Called from: constructor, `setX()`, `setY()`, `insertText()` (on every keystroke), etc.

**Pattern for custom EditBox subclasses:**
```java
@Override
public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    // 1. Blit background at absolute coords (before matrix push)
    DocClientUtils.blit(graphics, DocAssets.BACKGROUND, x, y);
    // 2. Push non-identity matrix
    graphics.pose().pushMatrix();
    graphics.pose().translate(getX(), getY());
    // 3. Set textX/textY as LOCAL offsets (relative to widget origin)
    this.textX = 13;
    this.textY = (this.height - 8) / 2;
    // 4. Optionally suppress suggestion when value is non-empty
    String savedSuggestion = this.suggestion;
    if (!value.isEmpty()) this.suggestion = null;
    super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    this.suggestion = savedSuggestion;
    graphics.pose().popMatrix();
}
```

### Suggestion / placeholder behavior
`EditBox` shows suggestion at cursor position, not only when empty — confusing for name fields.
Fix: null the suggestion before super when `!value.isEmpty()`, restore after.
This gives HTML-style placeholder: visible when empty, gone once user types anything.

## Matrix3x2fStack API
`GuiGraphics.pose()` returns `Matrix3x2fStack` (2D only, NOT `PoseStack`).
- `pushMatrix()` / `popMatrix()`
- `translate(float x, float y)`
- `scale(float x, float y)`

`PoseStack` is still valid for 3D world/entity renderers and in `adjustRenderPose`.
