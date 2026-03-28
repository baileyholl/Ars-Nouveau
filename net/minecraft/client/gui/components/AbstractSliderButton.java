package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSliderButton extends AbstractWidget.WithInactiveMessage {
    private static final Identifier SLIDER_SPRITE = Identifier.withDefaultNamespace("widget/slider");
    private static final Identifier HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("widget/slider_highlighted");
    private static final Identifier SLIDER_HANDLE_SPRITE = Identifier.withDefaultNamespace("widget/slider_handle");
    private static final Identifier SLIDER_HANDLE_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("widget/slider_handle_highlighted");
    protected static final int TEXT_MARGIN = 2;
    public static final int DEFAULT_HEIGHT = 20;
    protected static final int HANDLE_WIDTH = 8;
    private static final int HANDLE_HALF_WIDTH = 4;
    protected double value;
    protected boolean canChangeValue;
    protected boolean dragging;

    public AbstractSliderButton(int p_93579_, int p_93580_, int p_93581_, int p_93582_, Component p_93583_, double p_93584_) {
        super(p_93579_, p_93580_, p_93581_, p_93582_, p_93583_);
        this.value = p_93584_;
    }

    protected Identifier getSprite() {
        return this.isActive() && this.isFocused() && !this.canChangeValue ? HIGHLIGHTED_SPRITE : SLIDER_SPRITE;
    }

    protected Identifier getHandleSprite() {
        return !this.isActive() || !this.isHovered && !this.canChangeValue ? SLIDER_HANDLE_SPRITE : SLIDER_HANDLE_HIGHLIGHTED_SPRITE;
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return Component.translatable("gui.narrate.slider", this.getMessage());
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput p_168798_) {
        p_168798_.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                if (this.canChangeValue) {
                    p_168798_.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.focused"));
                } else {
                    p_168798_.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.focused.keyboard_cannot_change_value"));
                }
            } else {
                p_168798_.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.hovered"));
            }
        }
    }

    @Override
    public void renderWidget(GuiGraphics p_283427_, int p_281447_, int p_282852_, float p_282409_) {
        p_283427_.blitSprite(
            RenderPipelines.GUI_TEXTURED, this.getSprite(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha)
        );
        p_283427_.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            this.getHandleSprite(),
            this.getX() + (int)(this.value * (this.width - 8)),
            this.getY(),
            8,
            this.getHeight(),
            ARGB.white(this.alpha)
        );
        this.renderScrollingStringOverContents(p_283427_.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE), this.getMessage(), 2);
        if (this.isHovered()) {
            p_283427_.requestCursor(this.dragging ? CursorTypes.RESIZE_EW : CursorTypes.POINTING_HAND);
        }
    }

    @Override
    public void onClick(MouseButtonEvent p_446812_, boolean p_434040_) {
        this.dragging = this.active;
        this.setValueFromMouse(p_446812_);
    }

    @Override
    public void setFocused(boolean p_265705_) {
        super.setFocused(p_265705_);
        if (!p_265705_) {
            this.canChangeValue = false;
        } else {
            InputType inputtype = Minecraft.getInstance().getLastInputType();
            if (inputtype == InputType.MOUSE || inputtype == InputType.KEYBOARD_TAB) {
                this.canChangeValue = true;
            }
        }
    }

    @Override
    public boolean keyPressed(KeyEvent p_446064_) {
        if (p_446064_.isSelection()) {
            this.canChangeValue = !this.canChangeValue;
            return true;
        } else {
            if (this.canChangeValue) {
                boolean flag = p_446064_.isLeft();
                boolean flag1 = p_446064_.isRight();
                if (flag || flag1) {
                    float f = flag ? -1.0F : 1.0F;
                    this.setValue(this.value + f / (this.width - 8));
                    return true;
                }
            }

            return false;
        }
    }

    private void setValueFromMouse(MouseButtonEvent p_446404_) {
        this.setValue((p_446404_.x() - (this.getX() + 4)) / (this.width - 8));
    }

    protected void setValue(double p_93612_) {
        double d0 = this.value;
        this.value = Mth.clamp(p_93612_, 0.0, 1.0);
        if (d0 != this.value) {
            this.applyValue();
        }

        this.updateMessage();
    }

    @Override
    protected void onDrag(MouseButtonEvent p_445921_, double p_93591_, double p_93592_) {
        this.setValueFromMouse(p_445921_);
        super.onDrag(p_445921_, p_93591_, p_93592_);
    }

    @Override
    public void playDownSound(SoundManager p_93605_) {
    }

    @Override
    public void onRelease(MouseButtonEvent p_447332_) {
        this.dragging = false;
        super.playDownSound(Minecraft.getInstance().getSoundManager());
    }

    protected abstract void updateMessage();

    protected abstract void applyValue();
}
