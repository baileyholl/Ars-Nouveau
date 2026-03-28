package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.time.Duration;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractWidget implements Renderable, GuiEventListener, LayoutElement, NarratableEntry {
    protected int width;
    protected int height;
    public int x;
    public int y;
    protected Component message;
    protected boolean isHovered;
    public boolean active = true;
    public boolean visible = true;
    protected float alpha = 1.0F;
    private int tabOrderGroup;
    private boolean focused;
    private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();

    public AbstractWidget(int p_93629_, int p_93630_, int p_93631_, int p_93632_, Component p_93633_) {
        this.x = p_93629_;
        this.y = p_93630_;
        this.width = p_93631_;
        this.height = p_93632_;
        this.message = p_93633_;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void render(GuiGraphics p_282421_, int p_93658_, int p_93659_, float p_93660_) {
        if (this.visible) {
            this.isHovered = p_282421_.containsPointInScissor(p_93658_, p_93659_) && this.areCoordinatesInRectangle(p_93658_, p_93659_);
            this.renderWidget(p_282421_, p_93658_, p_93659_, p_93660_);
            this.tooltip.refreshTooltipForNextRenderPass(p_282421_, p_93658_, p_93659_, this.isHovered(), this.isFocused(), this.getRectangle());
        }
    }

    protected void handleCursor(GuiGraphics p_461038_) {
        if (this.isHovered()) {
            p_461038_.requestCursor(this.isActive() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
        }
    }

    public void setTooltip(@Nullable Tooltip p_259796_) {
        this.tooltip.set(p_259796_);
    }

    public void setTooltipDelay(Duration p_319769_) {
        this.tooltip.setDelay(p_319769_);
    }

    protected MutableComponent createNarrationMessage() {
        return wrapDefaultNarrationMessage(this.getMessage());
    }

    public static MutableComponent wrapDefaultNarrationMessage(Component p_168800_) {
        return Component.translatable("gui.narrate.button", p_168800_);
    }

    protected abstract void renderWidget(GuiGraphics p_282139_, int p_268034_, int p_268009_, float p_268085_);

    public void renderScrollingStringOverContents(ActiveTextCollector p_457529_, Component p_457986_, int p_458306_) {
        int i = this.getX() + p_458306_;
        int j = this.getX() + this.getWidth() - p_458306_;
        int k = this.getY();
        int l = this.getY() + this.getHeight();
        p_457529_.acceptScrollingWithDefaultCenter(p_457986_, i, j, k, l);
    }

    public void onClick(MouseButtonEvent p_446284_, boolean p_434599_) {
    }

    public void onRelease(MouseButtonEvent p_445805_) {
    }

    protected void onDrag(MouseButtonEvent p_446172_, double p_93636_, double p_93637_) {
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent p_447133_, boolean p_434606_) {
        if (!this.isActive()) {
            return false;
        } else {
            if (this.isValidClickButton(p_447133_.buttonInfo())) {
                boolean flag = this.isMouseOver(p_447133_.x(), p_447133_.y());
                if (flag) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onClick(p_447133_, p_434606_);
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent p_446092_) {
        if (this.isValidClickButton(p_446092_.buttonInfo())) {
            this.onRelease(p_446092_);
            return true;
        } else {
            return false;
        }
    }

    protected boolean isValidClickButton(MouseButtonInfo p_447020_) {
        return p_447020_.button() == 0;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent p_445646_, double p_93645_, double p_93646_) {
        if (this.isValidClickButton(p_445646_.buttonInfo())) {
            this.onDrag(p_445646_, p_93645_, p_93646_);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent p_265640_) {
        if (!this.isActive()) {
            return null;
        } else {
            return !this.isFocused() ? ComponentPath.leaf(this) : null;
        }
    }

    @Override
    public boolean isMouseOver(double p_93672_, double p_93673_) {
        return this.isActive() && this.areCoordinatesInRectangle(p_93672_, p_93673_);
    }

    public void playDownSound(SoundManager p_93665_) {
        playButtonClickSound(p_93665_);
    }

    public static void playButtonClickSound(SoundManager p_366702_) {
        p_366702_.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    public void setWidth(int p_93675_) {
        this.width = p_93675_;
    }

    public void setHeight(int p_299883_) {
        this.height = p_299883_;
    }

    public void setAlpha(float p_93651_) {
        this.alpha = p_93651_;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setMessage(Component p_93667_) {
        this.message = p_93667_;
    }

    public Component getMessage() {
        return this.message;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    public boolean isHoveredOrFocused() {
        return this.isHovered() || this.isFocused();
    }

    @Override
    public boolean isActive() {
        return this.visible && this.active;
    }

    @Override
    public void setFocused(boolean p_93693_) {
        this.focused = p_93693_;
    }

    public static final int UNSET_FG_COLOR = -1;
    protected int packedFGColor = UNSET_FG_COLOR;
    public int getFGColor() {
        if (packedFGColor != UNSET_FG_COLOR) return packedFGColor;
        return this.active ? -1 : -6250336; // White : Light Grey
    }
    public void setFGColor(int color) {
        this.packedFGColor = color;
    }
    public void clearFGColor() {
        this.packedFGColor = UNSET_FG_COLOR;
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.isFocused()) {
            return NarratableEntry.NarrationPriority.FOCUSED;
        } else {
            return this.isHovered ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
        }
    }

    @Override
    public final void updateNarration(NarrationElementOutput p_259921_) {
        this.updateWidgetNarration(p_259921_);
        this.tooltip.updateNarration(p_259921_);
    }

    protected abstract void updateWidgetNarration(NarrationElementOutput p_259858_);

    protected void defaultButtonNarrationText(NarrationElementOutput p_168803_) {
        p_168803_.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                p_168803_.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.focused"));
            } else {
                p_168803_.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
            }
        }
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public void setX(int p_254495_) {
        this.x = p_254495_;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setY(int p_253718_) {
        this.y = p_253718_;
    }

    public int getRight() {
        return this.getX() + this.getWidth();
    }

    public int getBottom() {
        return this.getY() + this.getHeight();
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> p_265566_) {
        p_265566_.accept(this);
    }

    public void setSize(int p_313746_, int p_313734_) {
        this.width = p_313746_;
        this.height = p_313734_;
    }

    @Override
    public ScreenRectangle getRectangle() {
        return LayoutElement.super.getRectangle();
    }

    private boolean areCoordinatesInRectangle(double p_428453_, double p_428498_) {
        return p_428453_ >= this.getX() && p_428498_ >= this.getY() && p_428453_ < this.getRight() && p_428498_ < this.getBottom();
    }

    public void setRectangle(int p_313710_, int p_313740_, int p_313689_, int p_313709_) {
        this.setSize(p_313710_, p_313740_);
        this.setPosition(p_313689_, p_313709_);
    }

    @Override
    public int getTabOrderGroup() {
        return this.tabOrderGroup;
    }

    public void setTabOrderGroup(int p_268123_) {
        this.tabOrderGroup = p_268123_;
    }

    @OnlyIn(Dist.CLIENT)
    public abstract static class WithInactiveMessage extends AbstractWidget {
        private Component inactiveMessage;

        public static Component defaultInactiveMessage(Component p_458231_) {
            return ComponentUtils.mergeStyles(p_458231_, Style.EMPTY.withColor(-6250336));
        }

        public WithInactiveMessage(int p_458181_, int p_457656_, int p_457828_, int p_457982_, Component p_457580_) {
            super(p_458181_, p_457656_, p_457828_, p_457982_, p_457580_);
            this.inactiveMessage = defaultInactiveMessage(p_457580_);
        }

        @Override
        public Component getMessage() {
            return this.active ? super.getMessage() : this.inactiveMessage;
        }

        @Override
        public void setMessage(Component p_458299_) {
            super.setMessage(p_458299_);
            this.inactiveMessage = defaultInactiveMessage(p_458299_);
        }
    }
}
