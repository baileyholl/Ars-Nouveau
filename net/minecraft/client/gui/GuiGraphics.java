package net.minecraft.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.ColoredRectangleRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.client.gui.render.state.TiledBlitRenderState;
import net.minecraft.client.gui.render.state.pip.GuiBannerResultRenderState;
import net.minecraft.client.gui.render.state.pip.GuiBookModelRenderState;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import net.minecraft.client.gui.render.state.pip.GuiProfilerChartRenderState;
import net.minecraft.client.gui.render.state.pip.GuiSignRenderState;
import net.minecraft.client.gui.render.state.pip.GuiSkinRenderState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.AtlasIds;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector2ic;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class GuiGraphics implements net.neoforged.neoforge.client.extensions.IGuiGraphicsExtension {
    private static final int EXTRA_SPACE_AFTER_FIRST_TOOLTIP_LINE = 2;
    final Minecraft minecraft;
    private final Matrix3x2fStack pose;
    private final GuiGraphics.ScissorStack scissorStack = new GuiGraphics.ScissorStack();
    private final MaterialSet materials;
    private final TextureAtlas guiSprites;
    final GuiRenderState guiRenderState;
    private CursorType pendingCursor = CursorType.DEFAULT;
    final int mouseX;
    final int mouseY;
    private @Nullable Runnable deferredTooltip;
    @Nullable Style hoveredTextStyle;
    @Nullable Style clickableTextStyle;
    private ItemStack tooltipStack = ItemStack.EMPTY;

    private GuiGraphics(Minecraft p_282144_, Matrix3x2fStack p_415937_, GuiRenderState p_415955_, int p_457710_, int p_458161_) {
        this.minecraft = p_282144_;
        this.pose = p_415937_;
        this.mouseX = p_457710_;
        this.mouseY = p_458161_;
        AtlasManager atlasmanager = p_282144_.getAtlasManager();
        this.materials = atlasmanager;
        this.guiSprites = atlasmanager.getAtlasOrThrow(AtlasIds.GUI);
        this.guiRenderState = p_415955_;
    }

    public GuiGraphics(Minecraft p_283406_, GuiRenderState p_416249_, int p_457961_, int p_457979_) {
        this(p_283406_, new Matrix3x2fStack(16), p_416249_, p_457961_, p_457979_);
    }

    public void requestCursor(CursorType p_443154_) {
        this.pendingCursor = p_443154_;
    }

    public void applyCursor(Window p_442894_) {
        p_442894_.selectCursor(this.pendingCursor);
    }

    public int guiWidth() {
        return this.minecraft.getWindow().getGuiScaledWidth();
    }

    public int guiHeight() {
        return this.minecraft.getWindow().getGuiScaledHeight();
    }

    public void nextStratum() {
        this.guiRenderState.nextStratum();
    }

    public void blurBeforeThisStratum() {
        this.guiRenderState.blurBeforeThisStratum();
    }

    public Matrix3x2fStack pose() {
        return this.pose;
    }

    public void hLine(int p_283318_, int p_281662_, int p_281346_, int p_281672_) {
        if (p_281662_ < p_283318_) {
            int i = p_283318_;
            p_283318_ = p_281662_;
            p_281662_ = i;
        }

        this.fill(p_283318_, p_281346_, p_281662_ + 1, p_281346_ + 1, p_281672_);
    }

    public void vLine(int p_282951_, int p_281591_, int p_281568_, int p_282718_) {
        if (p_281568_ < p_281591_) {
            int i = p_281591_;
            p_281591_ = p_281568_;
            p_281568_ = i;
        }

        this.fill(p_282951_, p_281591_ + 1, p_282951_ + 1, p_281568_, p_282718_);
    }

    public void enableScissor(int p_281479_, int p_282788_, int p_282924_, int p_282826_) {
        ScreenRectangle screenrectangle = new ScreenRectangle(p_281479_, p_282788_, p_282924_ - p_281479_, p_282826_ - p_282788_)
            .transformAxisAligned(this.pose);
        this.scissorStack.push(screenrectangle);
    }

    public void disableScissor() {
        this.scissorStack.pop();
    }

    public boolean containsPointInScissor(int p_332689_, int p_332771_) {
        return this.scissorStack.containsPoint(p_332689_, p_332771_);
    }

    public void fill(int p_282988_, int p_282861_, int p_281278_, int p_281710_, int p_281470_) {
        this.fill(RenderPipelines.GUI, p_282988_, p_282861_, p_281278_, p_281710_, p_281470_);
    }

    public void fill(RenderPipeline p_416410_, int p_281437_, int p_283660_, int p_282606_, int p_283413_, int p_283428_) {
        if (p_281437_ < p_282606_) {
            int i = p_281437_;
            p_281437_ = p_282606_;
            p_282606_ = i;
        }

        if (p_283660_ < p_283413_) {
            int j = p_283660_;
            p_283660_ = p_283413_;
            p_283413_ = j;
        }

        this.submitColoredRectangle(p_416410_, TextureSetup.noTexture(), p_281437_, p_283660_, p_282606_, p_283413_, p_283428_, null);
    }

    public void fillGradient(int p_283290_, int p_283278_, int p_282670_, int p_281698_, int p_283374_, int p_283076_) {
        this.submitColoredRectangle(RenderPipelines.GUI, TextureSetup.noTexture(), p_283290_, p_283278_, p_282670_, p_281698_, p_283374_, p_283076_);
    }

    public void fill(RenderPipeline p_416027_, TextureSetup p_415746_, int p_286234_, int p_286444_, int p_286244_, int p_286411_) {
        this.submitColoredRectangle(p_416027_, p_415746_, p_286234_, p_286444_, p_286244_, p_286411_, -1, null);
    }

    private void submitColoredRectangle(
        RenderPipeline p_416727_,
        TextureSetup p_416131_,
        int p_415712_,
        int p_416427_,
        int p_416376_,
        int p_415748_,
        int p_415666_,
        @Nullable Integer p_415938_
    ) {
        this.guiRenderState
            .submitGuiElement(
                new ColoredRectangleRenderState(
                    p_416727_,
                    p_416131_,
                    new Matrix3x2f(this.pose),
                    p_415712_,
                    p_416427_,
                    p_416376_,
                    p_415748_,
                    p_415666_,
                    p_415938_ != null ? p_415938_ : p_415666_,
                    this.scissorStack.peek()
                )
            );
    }

    public void textHighlight(int p_428831_, int p_428851_, int p_428846_, int p_428835_, boolean p_455950_) {
        if (p_455950_) {
            this.fill(RenderPipelines.GUI_INVERT, p_428831_, p_428851_, p_428846_, p_428835_, -1);
        }

        this.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, p_428831_, p_428851_, p_428846_, p_428835_, -16776961);
    }

    public void drawCenteredString(Font p_282122_, String p_282898_, int p_281490_, int p_282853_, int p_281258_) {
        this.drawString(p_282122_, p_282898_, p_281490_ - p_282122_.width(p_282898_) / 2, p_282853_, p_281258_);
    }

    public void drawCenteredString(Font p_282901_, Component p_282456_, int p_283083_, int p_282276_, int p_281457_) {
        FormattedCharSequence formattedcharsequence = p_282456_.getVisualOrderText();
        this.drawString(p_282901_, formattedcharsequence, p_283083_ - p_282901_.width(formattedcharsequence) / 2, p_282276_, p_281457_);
    }

    public void drawCenteredString(Font p_282592_, FormattedCharSequence p_281854_, int p_281573_, int p_283511_, int p_282577_) {
        this.drawString(p_282592_, p_281854_, p_281573_ - p_282592_.width(p_281854_) / 2, p_283511_, p_282577_);
    }

    public void drawString(Font p_282003_, @Nullable String p_281403_, int p_282714_, int p_282041_, int p_281908_) {
        this.drawString(p_282003_, p_281403_, p_282714_, p_282041_, p_281908_, true);
    }

    public void drawString(Font p_283019_, @Nullable String p_415853_, int p_283379_, int p_283346_, int p_282119_, boolean p_416601_) {
        if (p_415853_ != null) {
            this.drawString(p_283019_, Language.getInstance().getVisualOrder(FormattedText.of(p_415853_)), p_283379_, p_283346_, p_282119_, p_416601_);
        }
    }

    public void drawString(Font p_281653_, FormattedCharSequence p_416271_, int p_283102_, int p_282347_, int p_281429_) {
        this.drawString(p_281653_, p_416271_, p_283102_, p_282347_, p_281429_, true);
    }

    public void drawString(Font p_283343_, FormattedCharSequence p_416388_, int p_283569_, int p_283418_, int p_281560_, boolean p_282130_) {
        if (ARGB.alpha(p_281560_) != 0) {
            this.guiRenderState
                .submitText(
                    new GuiTextRenderState(
                        p_283343_, p_416388_, new Matrix3x2f(this.pose), p_283569_, p_283418_, p_281560_, 0, p_282130_, false, this.scissorStack.peek()
                    )
                );
        }
    }

    public void drawString(Font p_281547_, Component p_282131_, int p_282857_, int p_281250_, int p_282195_) {
        this.drawString(p_281547_, p_282131_, p_282857_, p_281250_, p_282195_, true);
    }

    public void drawString(Font p_282636_, Component p_416319_, int p_281586_, int p_282816_, int p_281743_, boolean p_282394_) {
        this.drawString(p_282636_, p_416319_.getVisualOrderText(), p_281586_, p_282816_, p_281743_, p_282394_);
    }

    public void drawWordWrap(Font p_281494_, FormattedText p_283463_, int p_282183_, int p_283250_, int p_282564_, int p_282629_) {
        this.drawWordWrap(p_281494_, p_283463_, p_282183_, p_283250_, p_282564_, p_282629_, true);
    }

    public void drawWordWrap(Font p_382905_, FormattedText p_382794_, int p_383047_, int p_382923_, int p_382857_, int p_382915_, boolean p_383224_) {
        for (FormattedCharSequence formattedcharsequence : p_382905_.split(p_382794_, p_382857_)) {
            this.drawString(p_382905_, formattedcharsequence, p_383047_, p_382923_, p_382915_, p_383224_);
            p_382923_ += 9;
        }
    }

    public void drawStringWithBackdrop(Font p_348650_, Component p_348614_, int p_348465_, int p_348495_, int p_348581_, int p_348666_) {
        int i = this.minecraft.options.getBackgroundColor(0.0F);
        if (i != 0) {
            int j = 2;
            this.fill(p_348465_ - 2, p_348495_ - 2, p_348465_ + p_348581_ + 2, p_348495_ + 9 + 2, ARGB.multiply(i, p_348666_));
        }

        this.drawString(p_348650_, p_348614_, p_348465_, p_348495_, p_348666_, true);
    }

    public void renderOutline(int p_455568_, int p_455637_, int p_455813_, int p_454765_, int p_456139_) {
        this.fill(p_455568_, p_455637_, p_455568_ + p_455813_, p_455637_ + 1, p_456139_);
        this.fill(p_455568_, p_455637_ + p_454765_ - 1, p_455568_ + p_455813_, p_455637_ + p_454765_, p_456139_);
        this.fill(p_455568_, p_455637_ + 1, p_455568_ + 1, p_455637_ + p_454765_ - 1, p_456139_);
        this.fill(p_455568_ + p_455813_ - 1, p_455637_ + 1, p_455568_ + p_455813_, p_455637_ + p_454765_ - 1, p_456139_);
    }

    public void blitSprite(RenderPipeline p_416634_, Identifier p_468290_, int p_294223_, int p_296245_, int p_296255_, int p_295669_) {
        this.blitSprite(p_416634_, p_468290_, p_294223_, p_296245_, p_296255_, p_295669_, -1);
    }

    public void blitSprite(RenderPipeline p_415593_, Identifier p_467390_, int p_294695_, int p_296458_, int p_294279_, int p_295235_, float p_467865_) {
        this.blitSprite(p_415593_, p_467390_, p_294695_, p_296458_, p_294279_, p_295235_, ARGB.white(p_467865_));
    }

    private static GuiSpriteScaling getSpriteScaling(TextureAtlasSprite p_434667_) {
        return p_434667_.contents().getAdditionalMetadata(GuiMetadataSection.TYPE).orElse(GuiMetadataSection.DEFAULT).scaling();
    }

    public void blitSprite(RenderPipeline p_421916_, Identifier p_468680_, int p_422055_, int p_421599_, int p_422343_, int p_422460_, int p_469461_) {
        TextureAtlasSprite textureatlassprite = this.guiSprites.getSprite(p_468680_);
        GuiSpriteScaling guispritescaling = getSpriteScaling(textureatlassprite);
        switch (guispritescaling) {
            case GuiSpriteScaling.Stretch guispritescaling$stretch:
                this.blitSprite(p_421916_, textureatlassprite, p_422055_, p_421599_, p_422343_, p_422460_, p_469461_);
                break;
            case GuiSpriteScaling.Tile guispritescaling$tile:
                this.blitTiledSprite(
                    p_421916_,
                    textureatlassprite,
                    p_422055_,
                    p_421599_,
                    p_422343_,
                    p_422460_,
                    0,
                    0,
                    guispritescaling$tile.width(),
                    guispritescaling$tile.height(),
                    guispritescaling$tile.width(),
                    guispritescaling$tile.height(),
                    p_469461_
                );
                break;
            case GuiSpriteScaling.NineSlice guispritescaling$nineslice:
                this.blitNineSlicedSprite(p_421916_, textureatlassprite, guispritescaling$nineslice, p_422055_, p_421599_, p_422343_, p_422460_, p_469461_);
                break;
            default:
        }
    }

    public void blitSprite(
        RenderPipeline p_415703_,
        Identifier p_467465_,
        int p_295058_,
        int p_294415_,
        int p_294535_,
        int p_295510_,
        int p_468399_,
        int p_467529_,
        int p_469729_,
        int p_466933_
    ) {
        this.blitSprite(p_415703_, p_467465_, p_295058_, p_294415_, p_294535_, p_295510_, p_468399_, p_467529_, p_469729_, p_466933_, -1);
    }

    public void blitSprite(
        RenderPipeline p_416106_,
        Identifier p_469700_,
        int p_294560_,
        int p_295075_,
        int p_294098_,
        int p_295872_,
        int p_294414_,
        int p_362199_,
        int p_363608_,
        int p_365523_,
        int p_416361_
    ) {
        TextureAtlasSprite textureatlassprite = this.guiSprites.getSprite(p_469700_);
        GuiSpriteScaling guispritescaling = getSpriteScaling(textureatlassprite);
        if (guispritescaling instanceof GuiSpriteScaling.Stretch) {
            this.blitSprite(p_416106_, textureatlassprite, p_294560_, p_295075_, p_294098_, p_295872_, p_294414_, p_362199_, p_363608_, p_365523_, p_416361_);
        } else {
            this.enableScissor(p_294414_, p_362199_, p_294414_ + p_363608_, p_362199_ + p_365523_);
            this.blitSprite(p_416106_, p_469700_, p_294414_ - p_294098_, p_362199_ - p_295872_, p_294560_, p_295075_, p_416361_);
            this.disableScissor();
        }
    }

    public void blitSprite(RenderPipeline p_416325_, TextureAtlasSprite p_416471_, int p_416622_, int p_416202_, int p_416408_, int p_416282_) {
        this.blitSprite(p_416325_, p_416471_, p_416622_, p_416202_, p_416408_, p_416282_, -1);
    }

    public void blitSprite(RenderPipeline p_416121_, TextureAtlasSprite p_364680_, int p_295194_, int p_295164_, int p_294823_, int p_295650_, int p_295401_) {
        if (p_294823_ != 0 && p_295650_ != 0) {
            this.innerBlit(
                p_416121_,
                p_364680_.atlasLocation(),
                p_295194_,
                p_295194_ + p_294823_,
                p_295164_,
                p_295164_ + p_295650_,
                p_364680_.getU0(),
                p_364680_.getU1(),
                p_364680_.getV0(),
                p_364680_.getV1(),
                p_295401_
            );
        }
    }

    private void blitSprite(
        RenderPipeline p_416146_,
        TextureAtlasSprite p_295122_,
        int p_295850_,
        int p_296348_,
        int p_295804_,
        int p_296465_,
        int p_295717_,
        int p_360779_,
        int p_363595_,
        int p_364585_,
        int p_361093_
    ) {
        if (p_363595_ != 0 && p_364585_ != 0) {
            this.innerBlit(
                p_416146_,
                p_295122_.atlasLocation(),
                p_295717_,
                p_295717_ + p_363595_,
                p_360779_,
                p_360779_ + p_364585_,
                p_295122_.getU((float)p_295804_ / p_295850_),
                p_295122_.getU((float)(p_295804_ + p_363595_) / p_295850_),
                p_295122_.getV((float)p_296465_ / p_296348_),
                p_295122_.getV((float)(p_296465_ + p_364585_) / p_296348_),
                p_361093_
            );
        }
    }

    private void blitNineSlicedSprite(
        RenderPipeline p_415939_,
        TextureAtlasSprite p_294394_,
        GuiSpriteScaling.NineSlice p_295735_,
        int p_294769_,
        int p_294546_,
        int p_294421_,
        int p_295807_,
        int p_295009_
    ) {
        GuiSpriteScaling.NineSlice.Border guispritescaling$nineslice$border = p_295735_.border();
        int i = Math.min(guispritescaling$nineslice$border.left(), p_294421_ / 2);
        int j = Math.min(guispritescaling$nineslice$border.right(), p_294421_ / 2);
        int k = Math.min(guispritescaling$nineslice$border.top(), p_295807_ / 2);
        int l = Math.min(guispritescaling$nineslice$border.bottom(), p_295807_ / 2);
        if (p_294421_ == p_295735_.width() && p_295807_ == p_295735_.height()) {
            this.blitSprite(p_415939_, p_294394_, p_295735_.width(), p_295735_.height(), 0, 0, p_294769_, p_294546_, p_294421_, p_295807_, p_295009_);
        } else if (p_295807_ == p_295735_.height()) {
            this.blitSprite(p_415939_, p_294394_, p_295735_.width(), p_295735_.height(), 0, 0, p_294769_, p_294546_, i, p_295807_, p_295009_);
            this.blitNineSliceInnerSegment(
                p_415939_,
                p_295735_,
                p_294394_,
                p_294769_ + i,
                p_294546_,
                p_294421_ - j - i,
                p_295807_,
                i,
                0,
                p_295735_.width() - j - i,
                p_295735_.height(),
                p_295735_.width(),
                p_295735_.height(),
                p_295009_
            );
            this.blitSprite(
                p_415939_,
                p_294394_,
                p_295735_.width(),
                p_295735_.height(),
                p_295735_.width() - j,
                0,
                p_294769_ + p_294421_ - j,
                p_294546_,
                j,
                p_295807_,
                p_295009_
            );
        } else if (p_294421_ == p_295735_.width()) {
            this.blitSprite(p_415939_, p_294394_, p_295735_.width(), p_295735_.height(), 0, 0, p_294769_, p_294546_, p_294421_, k, p_295009_);
            this.blitNineSliceInnerSegment(
                p_415939_,
                p_295735_,
                p_294394_,
                p_294769_,
                p_294546_ + k,
                p_294421_,
                p_295807_ - l - k,
                0,
                k,
                p_295735_.width(),
                p_295735_.height() - l - k,
                p_295735_.width(),
                p_295735_.height(),
                p_295009_
            );
            this.blitSprite(
                p_415939_,
                p_294394_,
                p_295735_.width(),
                p_295735_.height(),
                0,
                p_295735_.height() - l,
                p_294769_,
                p_294546_ + p_295807_ - l,
                p_294421_,
                l,
                p_295009_
            );
        } else {
            this.blitSprite(p_415939_, p_294394_, p_295735_.width(), p_295735_.height(), 0, 0, p_294769_, p_294546_, i, k, p_295009_);
            this.blitNineSliceInnerSegment(
                p_415939_,
                p_295735_,
                p_294394_,
                p_294769_ + i,
                p_294546_,
                p_294421_ - j - i,
                k,
                i,
                0,
                p_295735_.width() - j - i,
                k,
                p_295735_.width(),
                p_295735_.height(),
                p_295009_
            );
            this.blitSprite(
                p_415939_, p_294394_, p_295735_.width(), p_295735_.height(), p_295735_.width() - j, 0, p_294769_ + p_294421_ - j, p_294546_, j, k, p_295009_
            );
            this.blitSprite(
                p_415939_, p_294394_, p_295735_.width(), p_295735_.height(), 0, p_295735_.height() - l, p_294769_, p_294546_ + p_295807_ - l, i, l, p_295009_
            );
            this.blitNineSliceInnerSegment(
                p_415939_,
                p_295735_,
                p_294394_,
                p_294769_ + i,
                p_294546_ + p_295807_ - l,
                p_294421_ - j - i,
                l,
                i,
                p_295735_.height() - l,
                p_295735_.width() - j - i,
                l,
                p_295735_.width(),
                p_295735_.height(),
                p_295009_
            );
            this.blitSprite(
                p_415939_,
                p_294394_,
                p_295735_.width(),
                p_295735_.height(),
                p_295735_.width() - j,
                p_295735_.height() - l,
                p_294769_ + p_294421_ - j,
                p_294546_ + p_295807_ - l,
                j,
                l,
                p_295009_
            );
            this.blitNineSliceInnerSegment(
                p_415939_,
                p_295735_,
                p_294394_,
                p_294769_,
                p_294546_ + k,
                i,
                p_295807_ - l - k,
                0,
                k,
                i,
                p_295735_.height() - l - k,
                p_295735_.width(),
                p_295735_.height(),
                p_295009_
            );
            this.blitNineSliceInnerSegment(
                p_415939_,
                p_295735_,
                p_294394_,
                p_294769_ + i,
                p_294546_ + k,
                p_294421_ - j - i,
                p_295807_ - l - k,
                i,
                k,
                p_295735_.width() - j - i,
                p_295735_.height() - l - k,
                p_295735_.width(),
                p_295735_.height(),
                p_295009_
            );
            this.blitNineSliceInnerSegment(
                p_415939_,
                p_295735_,
                p_294394_,
                p_294769_ + p_294421_ - j,
                p_294546_ + k,
                j,
                p_295807_ - l - k,
                p_295735_.width() - j,
                k,
                j,
                p_295735_.height() - l - k,
                p_295735_.width(),
                p_295735_.height(),
                p_295009_
            );
        }
    }

    private void blitNineSliceInnerSegment(
        RenderPipeline p_415702_,
        GuiSpriteScaling.NineSlice p_371657_,
        TextureAtlasSprite p_371812_,
        int p_371894_,
        int p_371565_,
        int p_371606_,
        int p_371781_,
        int p_371379_,
        int p_371448_,
        int p_371442_,
        int p_371801_,
        int p_371588_,
        int p_371206_,
        int p_371311_
    ) {
        if (p_371606_ > 0 && p_371781_ > 0) {
            if (p_371657_.stretchInner()) {
                this.innerBlit(
                    p_415702_,
                    p_371812_.atlasLocation(),
                    p_371894_,
                    p_371894_ + p_371606_,
                    p_371565_,
                    p_371565_ + p_371781_,
                    p_371812_.getU((float)p_371379_ / p_371588_),
                    p_371812_.getU((float)(p_371379_ + p_371442_) / p_371588_),
                    p_371812_.getV((float)p_371448_ / p_371206_),
                    p_371812_.getV((float)(p_371448_ + p_371801_) / p_371206_),
                    p_371311_
                );
            } else {
                this.blitTiledSprite(
                    p_415702_,
                    p_371812_,
                    p_371894_,
                    p_371565_,
                    p_371606_,
                    p_371781_,
                    p_371379_,
                    p_371448_,
                    p_371442_,
                    p_371801_,
                    p_371588_,
                    p_371206_,
                    p_371311_
                );
            }
        }
    }

    private void blitTiledSprite(
        RenderPipeline p_415914_,
        TextureAtlasSprite p_294349_,
        int p_295093_,
        int p_296434_,
        int p_295268_,
        int p_295203_,
        int p_296398_,
        int p_295542_,
        int p_296165_,
        int p_296256_,
        int p_294814_,
        int p_296352_,
        int p_296203_
    ) {
        if (p_295268_ > 0 && p_295203_ > 0) {
            if (p_296165_ > 0 && p_296256_ > 0) {
                AbstractTexture abstracttexture = this.minecraft.getTextureManager().getTexture(p_294349_.atlasLocation());
                GpuTextureView gputextureview = abstracttexture.getTextureView();
                this.submitTiledBlit(
                    p_415914_,
                    gputextureview,
                    abstracttexture.getSampler(),
                    p_296165_,
                    p_296256_,
                    p_295093_,
                    p_296434_,
                    p_295093_ + p_295268_,
                    p_296434_ + p_295203_,
                    p_294349_.getU((float)p_296398_ / p_294814_),
                    p_294349_.getU((float)(p_296398_ + p_296165_) / p_294814_),
                    p_294349_.getV((float)p_295542_ / p_296352_),
                    p_294349_.getV((float)(p_295542_ + p_296256_) / p_296352_),
                    p_296203_
                );
            } else {
                throw new IllegalArgumentException("Tile size must be positive, got " + p_296165_ + "x" + p_296256_);
            }
        }
    }

    public void blit(
        RenderPipeline p_416366_,
        Identifier p_468983_,
        int p_282732_,
        int p_283541_,
        float p_282660_,
        float p_281522_,
        int p_281760_,
        int p_283298_,
        int p_283429_,
        int p_282193_,
        int p_469876_
    ) {
        this.blit(p_416366_, p_468983_, p_282732_, p_283541_, p_282660_, p_281522_, p_281760_, p_283298_, p_281760_, p_283298_, p_283429_, p_282193_, p_469876_);
    }

    public void blit(
        RenderPipeline p_416301_,
        Identifier p_467724_,
        int p_416262_,
        int p_416224_,
        float p_415758_,
        float p_415576_,
        int p_416133_,
        int p_416212_,
        int p_416081_,
        int p_416306_
    ) {
        this.blit(p_416301_, p_467724_, p_416262_, p_416224_, p_415758_, p_415576_, p_416133_, p_416212_, p_416133_, p_416212_, p_416081_, p_416306_);
    }

    public void blit(
        RenderPipeline p_469422_,
        Identifier p_467769_,
        int p_283671_,
        int p_282377_,
        float p_282285_,
        float p_283199_,
        int p_282058_,
        int p_281939_,
        int p_469665_,
        int p_468548_,
        int p_467191_,
        int p_468669_
    ) {
        this.blit(p_469422_, p_467769_, p_283671_, p_282377_, p_282285_, p_283199_, p_282058_, p_281939_, p_469665_, p_468548_, p_467191_, p_468669_, -1);
    }

    public void blit(
        RenderPipeline p_416258_,
        Identifier p_470007_,
        int p_283574_,
        int p_283670_,
        float p_283029_,
        float p_283061_,
        int p_283545_,
        int p_282845_,
        int p_282558_,
        int p_282832_,
        int p_416564_,
        int p_467691_,
        int p_466939_
    ) {
        this.innerBlit(
            p_416258_,
            p_470007_,
            p_283574_,
            p_283574_ + p_283545_,
            p_283670_,
            p_283670_ + p_282845_,
            (p_283029_ + 0.0F) / p_416564_,
            (p_283029_ + p_282558_) / p_416564_,
            (p_283061_ + 0.0F) / p_467691_,
            (p_283061_ + p_282832_) / p_467691_,
            p_466939_
        );
    }

    public void blit(
        Identifier p_468874_, int p_282225_, int p_281487_, int p_281985_, int p_281329_, float p_363958_, float p_363869_, float p_467048_, float p_470004_
    ) {
        this.innerBlit(RenderPipelines.GUI_TEXTURED, p_468874_, p_282225_, p_281985_, p_281487_, p_281329_, p_363958_, p_363869_, p_467048_, p_470004_, -1);
    }

    private void innerBlit(
        RenderPipeline p_415722_,
        Identifier p_469180_,
        int p_283092_,
        int p_281930_,
        int p_282113_,
        int p_281388_,
        float p_281327_,
        float p_281676_,
        float p_283166_,
        float p_282630_,
        int p_283583_
    ) {
        AbstractTexture abstracttexture = this.minecraft.getTextureManager().getTexture(p_469180_);
        this.submitBlit(
            p_415722_,
            abstracttexture.getTextureView(),
            abstracttexture.getSampler(),
            p_283092_,
            p_282113_,
            p_281930_,
            p_281388_,
            p_281327_,
            p_281676_,
            p_283166_,
            p_282630_,
            p_283583_
        );
    }

    private void submitBlit(
        RenderPipeline p_416205_,
        GpuTextureView p_423465_,
        GpuSampler p_456192_,
        int p_415899_,
        int p_415585_,
        int p_416253_,
        int p_416402_,
        float p_415781_,
        float p_415619_,
        float p_416198_,
        float p_415668_,
        int p_415686_
    ) {
        this.guiRenderState
            .submitGuiElement(
                new BlitRenderState(
                    p_416205_,
                    TextureSetup.singleTexture(p_423465_, p_456192_),
                    new Matrix3x2f(this.pose),
                    p_415899_,
                    p_415585_,
                    p_416253_,
                    p_416402_,
                    p_415781_,
                    p_415619_,
                    p_416198_,
                    p_415668_,
                    p_415686_,
                    this.scissorStack.peek()
                )
            );
    }

    private void submitTiledBlit(
        RenderPipeline p_449407_,
        GpuTextureView p_449437_,
        GpuSampler p_455308_,
        int p_449341_,
        int p_449290_,
        int p_449760_,
        int p_449749_,
        int p_449577_,
        int p_449669_,
        float p_449837_,
        float p_449755_,
        float p_449269_,
        float p_449219_,
        int p_449864_
    ) {
        this.guiRenderState
            .submitGuiElement(
                new TiledBlitRenderState(
                    p_449407_,
                    TextureSetup.singleTexture(p_449437_, p_455308_),
                    new Matrix3x2f(this.pose),
                    p_449341_,
                    p_449290_,
                    p_449760_,
                    p_449749_,
                    p_449577_,
                    p_449669_,
                    p_449837_,
                    p_449755_,
                    p_449269_,
                    p_449219_,
                    p_449864_,
                    this.scissorStack.peek()
                )
            );
    }

    public void renderItem(ItemStack p_281978_, int p_282647_, int p_281944_) {
        this.renderItem(this.minecraft.player, this.minecraft.level, p_281978_, p_282647_, p_281944_, 0);
    }

    public void renderItem(ItemStack p_282262_, int p_283221_, int p_283496_, int p_283435_) {
        this.renderItem(this.minecraft.player, this.minecraft.level, p_282262_, p_283221_, p_283496_, p_283435_);
    }

    public void renderFakeItem(ItemStack p_281946_, int p_283299_, int p_283674_) {
        this.renderFakeItem(p_281946_, p_283299_, p_283674_, 0);
    }

    public void renderFakeItem(ItemStack p_312904_, int p_312257_, int p_312674_, int p_312138_) {
        this.renderItem(null, this.minecraft.level, p_312904_, p_312257_, p_312674_, p_312138_);
    }

    public void renderItem(LivingEntity p_282154_, ItemStack p_282777_, int p_282110_, int p_281371_, int p_283572_) {
        this.renderItem(p_282154_, p_282154_.level(), p_282777_, p_282110_, p_281371_, p_283572_);
    }

    private void renderItem(@Nullable LivingEntity p_283524_, @Nullable Level p_282461_, ItemStack p_283653_, int p_283141_, int p_282560_, int p_282425_) {
        if (!p_283653_.isEmpty()) {
            TrackingItemStackRenderState trackingitemstackrenderstate = new TrackingItemStackRenderState();
            this.minecraft
                .getItemModelResolver()
                .updateForTopItem(trackingitemstackrenderstate, p_283653_, ItemDisplayContext.GUI, p_282461_, p_283524_, p_282425_);

            try {
                this.guiRenderState
                    .submitItem(
                        new GuiItemRenderState(
                            p_283653_.getItem().getName().toString(),
                            new Matrix3x2f(this.pose),
                            trackingitemstackrenderstate,
                            p_283141_,
                            p_282560_,
                            this.scissorStack.peek()
                        )
                    );
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
                crashreportcategory.setDetail("Item Type", () -> String.valueOf(p_283653_.getItem()));
                crashreportcategory.setDetail("Item Components", () -> String.valueOf(p_283653_.getComponents()));
                crashreportcategory.setDetail("Item Foil", () -> String.valueOf(p_283653_.hasFoil()));
                throw new ReportedException(crashreport);
            }
        }
    }

    public void renderItemDecorations(Font p_281721_, ItemStack p_281514_, int p_282056_, int p_282683_) {
        this.renderItemDecorations(p_281721_, p_281514_, p_282056_, p_282683_, null);
    }

    public void renderItemDecorations(Font p_282005_, ItemStack p_283349_, int p_282641_, int p_282146_, @Nullable String p_282803_) {
        if (!p_283349_.isEmpty()) {
            this.pose.pushMatrix();
            this.renderItemBar(p_283349_, p_282641_, p_282146_);
            this.renderItemCooldown(p_283349_, p_282641_, p_282146_);
            this.renderItemCount(p_282005_, p_283349_, p_282641_, p_282146_, p_282803_);
            this.pose.popMatrix();
            // TODO 1.21.2: This probably belongs in one of the sub-methods.
            net.neoforged.neoforge.client.ItemDecoratorHandler.of(p_283349_).render(this, p_282005_, p_283349_, p_282641_, p_282146_);
        }
    }

    public void setTooltipForNextFrame(Component p_419574_, int p_419861_, int p_419548_) {
        this.setTooltipForNextFrame(List.of(p_419574_.getVisualOrderText()), p_419861_, p_419548_);
    }

    public void setTooltipForNextFrame(List<FormattedCharSequence> p_419480_, int p_419761_, int p_420077_) {
        this.setTooltipForNextFrame(this.minecraft.font, p_419480_, DefaultTooltipPositioner.INSTANCE, p_419761_, p_420077_, false);
    }

    public void setTooltipForNextFrame(Font p_419878_, ItemStack p_419655_, int p_419935_, int p_419559_) {
        this.tooltipStack = p_419655_;
        this.setTooltipForNextFrame(
            p_419878_,
            Screen.getTooltipFromItem(this.minecraft, p_419655_),
            p_419655_.getTooltipImage(),
            p_419935_,
            p_419559_,
            p_419655_.get(DataComponents.TOOLTIP_STYLE)
        );
        this.tooltipStack = ItemStack.EMPTY;
    }

    public void setTooltipForNextFrame(Font font, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, ItemStack stack, int mouseX, int mouseY) {
        setTooltipForNextFrame(font, textComponents, tooltipComponent, stack, mouseX, mouseY, null);
    }

    public void setTooltipForNextFrame(Font font, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, ItemStack stack, int mouseX, int mouseY, @Nullable Identifier backgroundTexture) {
        this.tooltipStack = stack;
        this.setTooltipForNextFrame(font, textComponents, tooltipComponent, mouseX, mouseY, backgroundTexture);
        this.tooltipStack = ItemStack.EMPTY;
    }

    public void setTooltipForNextFrame(Font p_419603_, List<Component> p_419948_, Optional<TooltipComponent> p_419787_, int p_419566_, int p_420005_) {
        this.setTooltipForNextFrame(p_419603_, p_419948_, p_419787_, p_419566_, p_420005_, null);
    }

    public void setTooltipForNextFrame(
        Font p_420056_, List<Component> p_468746_, Optional<TooltipComponent> p_468924_, int p_420073_, int p_419473_, @Nullable Identifier p_469674_
    ) {
        List<ClientTooltipComponent> list = net.neoforged.neoforge.client.ClientHooks.gatherTooltipComponents(this.tooltipStack, p_468746_, p_468924_, p_420073_, guiWidth(), guiHeight(), p_420056_);
        this.setTooltipForNextFrameInternal(p_420056_, list, p_420073_, p_419473_, DefaultTooltipPositioner.INSTANCE, p_469674_, false);
    }

    public void setTooltipForNextFrame(Font p_420070_, Component p_419840_, int p_419594_, int p_419902_) {
        this.setTooltipForNextFrame(p_420070_, p_419840_, p_419594_, p_419902_, null);
    }

    public void setTooltipForNextFrame(Font p_420034_, Component p_468061_, int p_419571_, int p_419535_, @Nullable Identifier p_469408_) {
        this.setTooltipForNextFrame(p_420034_, List.of(p_468061_.getVisualOrderText()), p_419571_, p_419535_, p_469408_);
    }

    public void setComponentTooltipForNextFrame(Font p_419927_, List<Component> p_419807_, int p_419887_, int p_420035_) {
        this.setComponentTooltipForNextFrame(p_419927_, p_419807_, p_419887_, p_420035_, (Identifier) null);
    }

    public void setComponentTooltipForNextFrame(Font p_419540_, List<Component> p_419714_, int p_419554_, int p_419672_, @Nullable Identifier p_468623_) {
        List<ClientTooltipComponent> components = net.neoforged.neoforge.client.ClientHooks.gatherTooltipComponents(this.tooltipStack, p_419714_, p_419554_, guiWidth(), guiHeight(), p_419540_);
        this.setTooltipForNextFrameInternal(
            p_419540_,
            components,
            p_419554_,
            p_419672_,
            DefaultTooltipPositioner.INSTANCE,
            p_468623_,
            false
        );
    }

    public void setComponentTooltipForNextFrame(Font font, List<? extends net.minecraft.network.chat.FormattedText> tooltips, int mouseX, int mouseY, ItemStack stack) {
        setComponentTooltipForNextFrame(font, tooltips, mouseX, mouseY, stack, null);
    }

    public void setComponentTooltipForNextFrame(Font font, List<? extends net.minecraft.network.chat.FormattedText> tooltips, int mouseX, int mouseY, ItemStack stack, @Nullable Identifier backgroundTexture) {
        this.tooltipStack = stack;
        List<ClientTooltipComponent> components = net.neoforged.neoforge.client.ClientHooks.gatherTooltipComponents(stack, tooltips, mouseX, guiWidth(), guiHeight(), font);
        this.setTooltipForNextFrameInternal(font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, backgroundTexture, false);
        this.tooltipStack = ItemStack.EMPTY;
    }

    public void setComponentTooltipFromElementsForNextFrame(Font font, List<com.mojang.datafixers.util.Either<FormattedText, TooltipComponent>> elements, int mouseX, int mouseY, ItemStack stack) {
        setComponentTooltipFromElementsForNextFrame(font, elements, mouseX, mouseY, stack, null);
    }

    public void setComponentTooltipFromElementsForNextFrame(Font font, List<com.mojang.datafixers.util.Either<FormattedText, TooltipComponent>> elements, int mouseX, int mouseY, ItemStack stack, @Nullable Identifier backgroundTexture) {
        this.tooltipStack = stack;
        List<ClientTooltipComponent> components = net.neoforged.neoforge.client.ClientHooks.gatherTooltipComponentsFromElements(stack, elements, mouseX, guiWidth(), guiHeight(), font);
        this.setTooltipForNextFrameInternal(font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, backgroundTexture, false);
        this.tooltipStack = ItemStack.EMPTY;
    }

    public void setTooltipForNextFrame(Font p_419718_, List<? extends FormattedCharSequence> p_419502_, int p_419583_, int p_419996_) {
        this.setTooltipForNextFrame(p_419718_, p_419502_, p_419583_, p_419996_, null);
    }

    public void setTooltipForNextFrame(
        Font p_419582_, List<? extends FormattedCharSequence> p_419728_, int p_419586_, int p_420052_, @Nullable Identifier p_469259_
    ) {
        this.setTooltipForNextFrameInternal(
            p_419582_,
            p_419728_.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()),
            p_419586_,
            p_420052_,
            DefaultTooltipPositioner.INSTANCE,
            p_469259_,
            false
        );
    }

    public void setTooltipForNextFrame(
        Font p_419832_, List<FormattedCharSequence> p_419662_, ClientTooltipPositioner p_419693_, int p_420011_, int p_420014_, boolean p_419517_
    ) {
        this.setTooltipForNextFrameInternal(
            p_419832_, p_419662_.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), p_420011_, p_420014_, p_419693_, null, p_419517_
        );
    }

    private void setTooltipForNextFrameInternal(
        Font p_419941_,
        List<ClientTooltipComponent> p_419687_,
        int p_419453_,
        int p_419611_,
        ClientTooltipPositioner p_419886_,
        @Nullable Identifier p_468840_,
        boolean p_419788_
    ) {
        if (!p_419687_.isEmpty()) {
            if (this.deferredTooltip == null || p_419788_) {
                ItemStack capturedTooltipStack = this.tooltipStack;
                this.deferredTooltip = () -> this.renderTooltip(p_419941_, p_419687_, p_419453_, p_419611_, p_419886_, p_468840_, capturedTooltipStack);
            }
        }
    }

    public void renderTooltip(
        Font p_371715_, List<ClientTooltipComponent> p_371741_, int p_371500_, int p_371755_, ClientTooltipPositioner p_419610_, @Nullable Identifier p_469154_
    ) {
        this.renderTooltip(p_371715_, p_371741_, p_371500_, p_371755_, p_419610_, p_469154_, ItemStack.EMPTY);
    }

    public void renderTooltip(
            Font p_371715_,
            List<ClientTooltipComponent> p_371741_,
            int p_371500_,
            int p_371755_,
            ClientTooltipPositioner p_419610_,
            @Nullable Identifier p_469154_,
            ItemStack tooltipStack
    ) {
        var preEvent = net.neoforged.neoforge.client.ClientHooks.onRenderTooltipPre(tooltipStack, this, p_371500_, p_371755_, guiWidth(), guiHeight(), p_371741_, p_371715_, p_419610_);
        if (preEvent.isCanceled()) return;

        p_371715_ = preEvent.getFont();
        p_371500_ = preEvent.getX();
        p_371755_ = preEvent.getY();

        int i = 0;
        int j = p_371741_.size() == 1 ? -2 : 0;

        for (ClientTooltipComponent clienttooltipcomponent : p_371741_) {
            int k = clienttooltipcomponent.getWidth(p_371715_);
            if (k > i) {
                i = k;
            }

            j += clienttooltipcomponent.getHeight(p_371715_);
        }

        int l1 = i;
        int i2 = j;
        Vector2ic vector2ic = p_419610_.positionTooltip(this.guiWidth(), this.guiHeight(), p_371500_, p_371755_, i, j);
        int l = vector2ic.x();
        int i1 = vector2ic.y();
        this.pose.pushMatrix();
        var textureEvent = net.neoforged.neoforge.client.ClientHooks.onRenderTooltipTexture(this.tooltipStack, this, l, i1, preEvent.getFont(), p_371741_, p_469154_);
        TooltipRenderUtil.renderTooltipBackground(this, l, i1, i, j, textureEvent.getTexture());
        int j1 = i1;

        for (int k1 = 0; k1 < p_371741_.size(); k1++) {
            ClientTooltipComponent clienttooltipcomponent1 = p_371741_.get(k1);
            clienttooltipcomponent1.renderText(this, p_371715_, l, j1);
            j1 += clienttooltipcomponent1.getHeight(p_371715_) + (k1 == 0 ? 2 : 0);
        }

        j1 = i1;

        for (int j2 = 0; j2 < p_371741_.size(); j2++) {
            ClientTooltipComponent clienttooltipcomponent2 = p_371741_.get(j2);
            clienttooltipcomponent2.renderImage(p_371715_, l, j1, l1, i2, this);
            j1 += clienttooltipcomponent2.getHeight(p_371715_) + (j2 == 0 ? 2 : 0);
        }

        this.pose.popMatrix();
    }

    public void renderDeferredElements() {
        if (this.hoveredTextStyle != null) {
            this.renderComponentHoverEffect(this.minecraft.font, this.hoveredTextStyle, this.mouseX, this.mouseY);
        }

        if (this.clickableTextStyle != null && this.clickableTextStyle.getClickEvent() != null) {
            this.requestCursor(CursorTypes.POINTING_HAND);
        }

        if (this.deferredTooltip != null) {
            this.nextStratum();
            this.deferredTooltip.run();
            this.deferredTooltip = null;
        }
    }

    private void renderItemBar(ItemStack p_380278_, int p_379972_, int p_379916_) {
        if (p_380278_.isBarVisible()) {
            int i = p_379972_ + 2;
            int j = p_379916_ + 13;
            this.fill(RenderPipelines.GUI, i, j, i + 13, j + 2, -16777216);
            this.fill(RenderPipelines.GUI, i, j, i + p_380278_.getBarWidth(), j + 1, ARGB.opaque(p_380278_.getBarColor()));
        }
    }

    private void renderItemCount(Font p_380115_, ItemStack p_379291_, int p_379544_, int p_380291_, @Nullable String p_380189_) {
        if (p_379291_.getCount() != 1 || p_380189_ != null) {
            String s = p_380189_ == null ? String.valueOf(p_379291_.getCount()) : p_380189_;
            this.drawString(p_380115_, s, p_379544_ + 19 - 2 - p_380115_.width(s), p_380291_ + 6 + 3, -1, true);
        }
    }

    private void renderItemCooldown(ItemStack p_380199_, int p_380397_, int p_379741_) {
        LocalPlayer localplayer = this.minecraft.player;
        float f = localplayer == null
            ? 0.0F
            : localplayer.getCooldowns().getCooldownPercent(p_380199_, this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));
        if (f > 0.0F) {
            int i = p_379741_ + Mth.floor(16.0F * (1.0F - f));
            int j = i + Mth.ceil(16.0F * f);
            this.fill(RenderPipelines.GUI, p_380397_, i, p_380397_ + 16, j, Integer.MAX_VALUE);
        }
    }

    public void renderComponentHoverEffect(Font p_282584_, @Nullable Style p_282156_, int p_283623_, int p_282114_) {
        if (p_282156_ != null) {
            if (p_282156_.getHoverEvent() != null) {
                switch (p_282156_.getHoverEvent()) {
                    case HoverEvent.ShowItem(ItemStack itemstack):
                        this.setTooltipForNextFrame(p_282584_, itemstack, p_283623_, p_282114_);
                        break;
                    case HoverEvent.ShowEntity(HoverEvent.EntityTooltipInfo hoverevent$entitytooltipinfo1):
                        HoverEvent.EntityTooltipInfo hoverevent$entitytooltipinfo = hoverevent$entitytooltipinfo1;
                        if (this.minecraft.options.advancedItemTooltips) {
                            this.setComponentTooltipForNextFrame(p_282584_, hoverevent$entitytooltipinfo.getTooltipLines(), p_283623_, p_282114_);
                        }
                        break;
                    case HoverEvent.ShowText(Component component):
                        this.setTooltipForNextFrame(p_282584_, p_282584_.split(component, Math.max(this.guiWidth() / 2, 200)), p_283623_, p_282114_);
                        break;
                    default:
                }
            }
        }
    }

    public void submitMapRenderState(MapRenderState p_415810_) {
        Minecraft minecraft = Minecraft.getInstance();
        TextureManager texturemanager = minecraft.getTextureManager();
        AbstractTexture abstracttexture = texturemanager.getTexture(p_415810_.texture);
        this.submitBlit(
            RenderPipelines.GUI_TEXTURED, abstracttexture.getTextureView(), abstracttexture.getSampler(), 0, 0, 128, 128, 0.0F, 1.0F, 0.0F, 1.0F, -1
        );

        for (MapRenderState.MapDecorationRenderState maprenderstate$mapdecorationrenderstate : p_415810_.decorations) {
            if (maprenderstate$mapdecorationrenderstate.renderOnFrame) {
                this.pose.pushMatrix();
                this.pose.translate(maprenderstate$mapdecorationrenderstate.x / 2.0F + 64.0F, maprenderstate$mapdecorationrenderstate.y / 2.0F + 64.0F);
                this.pose.rotate((float) (Math.PI / 180.0) * maprenderstate$mapdecorationrenderstate.rot * 360.0F / 16.0F);
                this.pose.scale(4.0F, 4.0F);
                this.pose.translate(-0.125F, 0.125F);
                TextureAtlasSprite textureatlassprite = maprenderstate$mapdecorationrenderstate.atlasSprite;
                if (textureatlassprite != null) {
                    AbstractTexture abstracttexture1 = texturemanager.getTexture(textureatlassprite.atlasLocation());
                    this.submitBlit(
                        RenderPipelines.GUI_TEXTURED,
                        abstracttexture1.getTextureView(),
                        abstracttexture1.getSampler(),
                        -1,
                        -1,
                        1,
                        1,
                        textureatlassprite.getU0(),
                        textureatlassprite.getU1(),
                        textureatlassprite.getV1(),
                        textureatlassprite.getV0(),
                        -1
                    );
                }

                this.pose.popMatrix();
                if (maprenderstate$mapdecorationrenderstate.name != null) {
                    Font font = minecraft.font;
                    float f = font.width(maprenderstate$mapdecorationrenderstate.name);
                    float f1 = Mth.clamp(25.0F / f, 0.0F, 6.0F / 9.0F);
                    this.pose.pushMatrix();
                    this.pose
                        .translate(
                            maprenderstate$mapdecorationrenderstate.x / 2.0F + 64.0F - f * f1 / 2.0F,
                            maprenderstate$mapdecorationrenderstate.y / 2.0F + 64.0F + 4.0F
                        );
                    this.pose.scale(f1, f1);
                    this.guiRenderState
                        .submitText(
                            new GuiTextRenderState(
                                font,
                                maprenderstate$mapdecorationrenderstate.name.getVisualOrderText(),
                                new Matrix3x2f(this.pose),
                                0,
                                0,
                                -1,
                                Integer.MIN_VALUE,
                                false,
                                false,
                                this.scissorStack.peek()
                            )
                        );
                    this.pose.popMatrix();
                }
            }
        }
    }

    public void submitEntityRenderState(
        EntityRenderState p_415907_,
        float p_415695_,
        Vector3f p_415772_,
        Quaternionf p_416089_,
        @Nullable Quaternionf p_416355_,
        int p_416675_,
        int p_416412_,
        int p_415766_,
        int p_416432_
    ) {
        this.guiRenderState
            .submitPicturesInPictureState(
                new GuiEntityRenderState(
                    p_415907_, p_415772_, p_416089_, p_416355_, p_416675_, p_416412_, p_415766_, p_416432_, p_415695_, this.scissorStack.peek()
                )
            );
    }

    public void submitSkinRenderState(
        PlayerModel p_481214_,
        Identifier p_467356_,
        float p_416346_,
        float p_416524_,
        float p_416465_,
        float p_416434_,
        int p_416207_,
        int p_415726_,
        int p_415642_,
        int p_416359_
    ) {
        this.guiRenderState
            .submitPicturesInPictureState(
                new GuiSkinRenderState(
                    p_481214_, p_467356_, p_416524_, p_416465_, p_416434_, p_416207_, p_415726_, p_415642_, p_416359_, p_416346_, this.scissorStack.peek()
                )
            );
    }

    public void submitBookModelRenderState(
        BookModel p_478640_,
        Identifier p_468386_,
        float p_416200_,
        float p_415771_,
        float p_416342_,
        int p_416018_,
        int p_416230_,
        int p_416557_,
        int p_416220_
    ) {
        this.guiRenderState
            .submitPicturesInPictureState(
                new GuiBookModelRenderState(
                    p_478640_, p_468386_, p_415771_, p_416342_, p_416018_, p_416230_, p_416557_, p_416220_, p_416200_, this.scissorStack.peek()
                )
            );
    }

    public void submitBannerPatternRenderState(
        BannerFlagModel p_478113_, DyeColor p_415755_, BannerPatternLayers p_415569_, int p_415681_, int p_416288_, int p_416302_, int p_415867_
    ) {
        this.guiRenderState
            .submitPicturesInPictureState(
                new GuiBannerResultRenderState(p_478113_, p_415755_, p_415569_, p_415681_, p_416288_, p_416302_, p_415867_, this.scissorStack.peek())
            );
    }

    public void submitSignRenderState(Model.Simple p_434494_, float p_416226_, WoodType p_416002_, int p_415719_, int p_416488_, int p_416444_, int p_416195_) {
        this.guiRenderState
            .submitPicturesInPictureState(
                new GuiSignRenderState(p_434494_, p_416002_, p_415719_, p_416488_, p_416444_, p_416195_, p_416226_, this.scissorStack.peek())
            );
    }

    public void submitProfilerChartRenderState(List<ResultField> p_415873_, int p_415651_, int p_416392_, int p_415782_, int p_416254_) {
        this.guiRenderState
            .submitPicturesInPictureState(new GuiProfilerChartRenderState(p_415873_, p_415651_, p_416392_, p_415782_, p_416254_, this.scissorStack.peek()));
    }

    /**
     * Neo: Submit a custom {@link net.minecraft.client.gui.render.state.GuiElementRenderState} for rendering
     */
    public void submitGuiElementRenderState(net.minecraft.client.gui.render.state.GuiElementRenderState renderState) {
        this.guiRenderState.submitGuiElement(renderState);
    }

    /**
     * Neo: Submit a custom {@link net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState} for rendering
     *
     * @see net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent
     */
    public void submitPictureInPictureRenderState(net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState renderState) {
        this.guiRenderState.submitPicturesInPictureState(renderState);
    }

    /**
     * Neo: Returns the top-most scissor rectangle, if present, for use with custom {@link net.minecraft.client.gui.render.state.GuiElementRenderState}s
     * and {@link net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState}s
     */
    @Nullable
    public ScreenRectangle peekScissorStack() {
        return this.scissorStack.peek();
    }

    public TextureAtlasSprite getSprite(Material p_433908_) {
        return this.materials.get(p_433908_);
    }

    public ActiveTextCollector textRendererForWidget(AbstractWidget p_457920_, GuiGraphics.HoveredTextEffects p_458304_) {
        return new GuiGraphics.RenderingTextCollector(this.createDefaultTextParameters(p_457920_.getAlpha()), p_458304_, null);
    }

    public ActiveTextCollector textRenderer() {
        return this.textRenderer(GuiGraphics.HoveredTextEffects.TOOLTIP_ONLY);
    }

    public ActiveTextCollector textRenderer(GuiGraphics.HoveredTextEffects p_457690_) {
        return this.textRenderer(p_457690_, null);
    }

    public ActiveTextCollector textRenderer(GuiGraphics.HoveredTextEffects p_457696_, @Nullable Consumer<Style> p_458269_) {
        return new GuiGraphics.RenderingTextCollector(this.createDefaultTextParameters(1.0F), p_457696_, p_458269_);
    }

    private ActiveTextCollector.Parameters createDefaultTextParameters(float p_458005_) {
        return new ActiveTextCollector.Parameters(new Matrix3x2f(this.pose), p_458005_, this.scissorStack.peek());
    }

    @OnlyIn(Dist.CLIENT)
    public static enum HoveredTextEffects {
        NONE(false, false),
        TOOLTIP_ONLY(true, false),
        TOOLTIP_AND_CURSOR(true, true);

        public final boolean allowTooltip;
        public final boolean allowCursorChanges;

        private HoveredTextEffects(boolean p_457925_, boolean p_458300_) {
            this.allowTooltip = p_457925_;
            this.allowCursorChanges = p_458300_;
        }

        public static GuiGraphics.HoveredTextEffects notClickable(boolean p_458239_) {
            return p_458239_ ? TOOLTIP_ONLY : NONE;
        }
    }

    @OnlyIn(Dist.CLIENT)
    class RenderingTextCollector implements ActiveTextCollector, Consumer<Style> {
        private ActiveTextCollector.Parameters defaultParameters;
        private final GuiGraphics.HoveredTextEffects hoveredTextEffects;
        private final @Nullable Consumer<Style> additionalConsumer;

        RenderingTextCollector(ActiveTextCollector.Parameters p_457723_, GuiGraphics.HoveredTextEffects p_457748_, @Nullable Consumer<Style> p_458217_) {
            this.defaultParameters = p_457723_;
            this.hoveredTextEffects = p_457748_;
            this.additionalConsumer = p_458217_;
        }

        @Override
        public ActiveTextCollector.Parameters defaultParameters() {
            return this.defaultParameters;
        }

        @Override
        public void defaultParameters(ActiveTextCollector.Parameters p_458180_) {
            this.defaultParameters = p_458180_;
        }

        public void accept(Style p_457775_) {
            if (this.hoveredTextEffects.allowTooltip && p_457775_.getHoverEvent() != null) {
                GuiGraphics.this.hoveredTextStyle = p_457775_;
            }

            if (this.hoveredTextEffects.allowCursorChanges && p_457775_.getClickEvent() != null) {
                GuiGraphics.this.clickableTextStyle = p_457775_;
            }

            if (this.additionalConsumer != null) {
                this.additionalConsumer.accept(p_457775_);
            }
        }

        @Override
        public void accept(TextAlignment p_457771_, int p_457733_, int p_458130_, ActiveTextCollector.Parameters p_458099_, FormattedCharSequence p_457997_) {
            boolean flag = this.hoveredTextEffects.allowCursorChanges || this.hoveredTextEffects.allowTooltip || this.additionalConsumer != null;
            int i = p_457771_.calculateLeft(p_457733_, GuiGraphics.this.minecraft.font, p_457997_);
            GuiTextRenderState guitextrenderstate = new GuiTextRenderState(
                GuiGraphics.this.minecraft.font, p_457997_, p_458099_.pose(), i, p_458130_, ARGB.white(p_458099_.opacity()), 0, true, flag, p_458099_.scissor()
            );
            if (ARGB.as8BitChannel(p_458099_.opacity()) != 0) {
                GuiGraphics.this.guiRenderState.submitText(guitextrenderstate);
            }

            if (flag) {
                ActiveTextCollector.findElementUnderCursor(guitextrenderstate, GuiGraphics.this.mouseX, GuiGraphics.this.mouseY, this);
            }
        }

        @Override
        public void acceptScrolling(
            Component p_458032_, int p_458050_, int p_457798_, int p_458078_, int p_457780_, int p_457539_, ActiveTextCollector.Parameters p_457792_
        ) {
            int i = GuiGraphics.this.minecraft.font.width(p_458032_);
            int j = 9;
            this.defaultScrollingHelper(p_458032_, p_458050_, p_457798_, p_458078_, p_457780_, p_457539_, i, j, p_457792_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class ScissorStack {
        private final Deque<ScreenRectangle> stack = new ArrayDeque<>();

        public ScreenRectangle push(ScreenRectangle p_281812_) {
            ScreenRectangle screenrectangle = this.stack.peekLast();
            if (screenrectangle != null) {
                ScreenRectangle screenrectangle1 = Objects.requireNonNullElse(p_281812_.intersection(screenrectangle), ScreenRectangle.empty());
                this.stack.addLast(screenrectangle1);
                return screenrectangle1;
            } else {
                this.stack.addLast(p_281812_);
                return p_281812_;
            }
        }

        public @Nullable ScreenRectangle pop() {
            if (this.stack.isEmpty()) {
                throw new IllegalStateException("Scissor stack underflow");
            } else {
                this.stack.removeLast();
                return this.stack.peekLast();
            }
        }

        public @Nullable ScreenRectangle peek() {
            return this.stack.peekLast();
        }

        public boolean containsPoint(int p_332682_, int p_332655_) {
            return this.stack.isEmpty() ? true : this.stack.peek().containsPoint(p_332682_, p_332655_);
        }
    }
}
