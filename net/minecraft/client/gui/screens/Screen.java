package net.minecraft.client.gui.screens;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.net.URI;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.ScreenNarrationCollector;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class Screen extends AbstractContainerEventHandler implements Renderable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component USAGE_NARRATION = Component.translatable("narrator.screen.usage");
    public static final Identifier MENU_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/menu_background.png");
    public static final Identifier HEADER_SEPARATOR = Identifier.withDefaultNamespace("textures/gui/header_separator.png");
    public static final Identifier FOOTER_SEPARATOR = Identifier.withDefaultNamespace("textures/gui/footer_separator.png");
    private static final Identifier INWORLD_MENU_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/inworld_menu_background.png");
    public static final Identifier INWORLD_HEADER_SEPARATOR = Identifier.withDefaultNamespace("textures/gui/inworld_header_separator.png");
    public static final Identifier INWORLD_FOOTER_SEPARATOR = Identifier.withDefaultNamespace("textures/gui/inworld_footer_separator.png");
    protected static final float FADE_IN_TIME = 2000.0F;
    protected final Component title;
    private final List<GuiEventListener> children = Lists.newArrayList();
    private final List<NarratableEntry> narratables = Lists.newArrayList();
    protected final Minecraft minecraft;
    private boolean initialized;
    public int width;
    public int height;
    public final List<Renderable> renderables = Lists.newArrayList();
    protected final Font font;
    private static final long NARRATE_SUPPRESS_AFTER_INIT_TIME = TimeUnit.SECONDS.toMillis(2L);
    private static final long NARRATE_DELAY_NARRATOR_ENABLED = NARRATE_SUPPRESS_AFTER_INIT_TIME;
    private static final long NARRATE_DELAY_MOUSE_MOVE = 750L;
    private static final long NARRATE_DELAY_MOUSE_ACTION = 200L;
    private static final long NARRATE_DELAY_KEYBOARD_ACTION = 200L;
    private final ScreenNarrationCollector narrationState = new ScreenNarrationCollector();
    private long narrationSuppressTime = Long.MIN_VALUE;
    private long nextNarrationTime = Long.MAX_VALUE;
    protected @Nullable CycleButton<NarratorStatus> narratorButton;
    private @Nullable NarratableEntry lastNarratable;
    protected final Executor screenExecutor;

    protected Screen(Component p_96550_) {
        this(Minecraft.getInstance(), Minecraft.getInstance().font, p_96550_);
    }

    protected Screen(Minecraft p_454696_, Font p_455353_, Component p_455224_) {
        this.minecraft = p_454696_;
        this.font = p_455353_;
        this.title = p_455224_;
        this.screenExecutor = p_454168_ -> p_454696_.execute(() -> {
            if (p_454696_.screen == this) {
                p_454168_.run();
            }
        });
    }

    public Component getTitle() {
        return this.title;
    }

    public Component getNarrationMessage() {
        return this.getTitle();
    }

    public final void renderWithTooltipAndSubtitles(GuiGraphics p_434220_, int p_435721_, int p_435145_, float p_435742_) {
        p_434220_.nextStratum();
        this.renderBackground(p_434220_, p_435721_, p_435145_, p_435742_);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.client.event.ScreenEvent.Render.Background(this, p_434220_, p_435721_, p_435145_, p_435742_));
        p_434220_.nextStratum();
        this.render(p_434220_, p_435721_, p_435145_, p_435742_);
        p_434220_.renderDeferredElements();
    }

    @Override
    public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
        for (Renderable renderable : this.renderables) {
            renderable.render(p_281549_, p_281550_, p_282878_, p_282465_);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent p_446782_) {
        if (p_446782_.isEscape() && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else if (super.keyPressed(p_446782_)) {
            return true;
        } else {
            FocusNavigationEvent focusnavigationevent = (FocusNavigationEvent)(switch (p_446782_.key()) {
                case 258 -> this.createTabEvent(!p_446782_.hasShiftDown());
                default -> null;
                case 262 -> this.createArrowEvent(ScreenDirection.RIGHT);
                case 263 -> this.createArrowEvent(ScreenDirection.LEFT);
                case 264 -> this.createArrowEvent(ScreenDirection.DOWN);
                case 265 -> this.createArrowEvent(ScreenDirection.UP);
            });
            if (focusnavigationevent != null) {
                ComponentPath componentpath = super.nextFocusPath(focusnavigationevent);
                if (componentpath == null && focusnavigationevent instanceof FocusNavigationEvent.TabNavigation) {
                    this.clearFocus();
                    componentpath = super.nextFocusPath(focusnavigationevent);
                }

                if (componentpath != null) {
                    this.changeFocus(componentpath);
                }
            }

            return false;
        }
    }

    private FocusNavigationEvent.TabNavigation createTabEvent(boolean p_445690_) {
        return new FocusNavigationEvent.TabNavigation(p_445690_);
    }

    private FocusNavigationEvent.ArrowNavigation createArrowEvent(ScreenDirection p_265049_) {
        return new FocusNavigationEvent.ArrowNavigation(p_265049_);
    }

    protected void setInitialFocus() {
        if (this.minecraft.getLastInputType().isKeyboard()) {
            FocusNavigationEvent.TabNavigation focusnavigationevent$tabnavigation = new FocusNavigationEvent.TabNavigation(true);
            ComponentPath componentpath = super.nextFocusPath(focusnavigationevent$tabnavigation);
            if (componentpath != null) {
                this.changeFocus(componentpath);
            }
        }
    }

    protected void setInitialFocus(GuiEventListener p_265756_) {
        ComponentPath componentpath = ComponentPath.path(this, p_265756_.nextFocusPath(new FocusNavigationEvent.InitialFocus()));
        if (componentpath != null) {
            this.changeFocus(componentpath);
        }
    }

    public void clearFocus() {
        ComponentPath componentpath = this.getCurrentFocusPath();
        if (componentpath != null) {
            componentpath.applyFocus(false);
        }
    }

    @VisibleForTesting
    protected void changeFocus(ComponentPath p_265308_) {
        this.clearFocus();
        p_265308_.applyFocus(true);
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void onClose() {
        this.minecraft.popGuiLayer();
    }

    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T p_169406_) {
        this.renderables.add(p_169406_);
        return this.addWidget(p_169406_);
    }

    protected <T extends Renderable> T addRenderableOnly(T p_254514_) {
        this.renderables.add(p_254514_);
        return p_254514_;
    }

    protected <T extends GuiEventListener & NarratableEntry> T addWidget(T p_96625_) {
        this.children.add(p_96625_);
        this.narratables.add(p_96625_);
        return p_96625_;
    }

    protected void removeWidget(GuiEventListener p_169412_) {
        if (p_169412_ instanceof Renderable) {
            this.renderables.remove((Renderable)p_169412_);
        }

        if (p_169412_ instanceof NarratableEntry) {
            this.narratables.remove((NarratableEntry)p_169412_);
        }

        if (this.getFocused() == p_169412_) {
            this.clearFocus();
        }

        this.children.remove(p_169412_);
    }

    protected void clearWidgets() {
        this.renderables.clear();
        this.children.clear();
        this.narratables.clear();
    }

    public static List<Component> getTooltipFromItem(Minecraft p_281881_, ItemStack p_282833_) {
        return p_282833_.getTooltipLines(
            Item.TooltipContext.of(p_281881_.level),
            p_281881_.player,
            net.neoforged.neoforge.client.ClientTooltipFlag.of(p_281881_.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL)
        );
    }

    protected void insertText(String p_96587_, boolean p_96588_) {
    }

    protected static void defaultHandleGameClickEvent(ClickEvent p_427374_, Minecraft p_427447_, @Nullable Screen p_427331_) {
        LocalPlayer localplayer = Objects.requireNonNull(p_427447_.player, "Player not available");
        switch (p_427374_) {
            case ClickEvent.RunCommand(String s):
                clickCommandAction(localplayer, s, p_427331_);
                break;
            case ClickEvent.ShowDialog clickevent$showdialog:
                localplayer.connection.showDialog(clickevent$showdialog.dialog(), p_427331_);
                break;
            case ClickEvent.Custom clickevent$custom:
                localplayer.connection.send(new ServerboundCustomClickActionPacket(clickevent$custom.id(), clickevent$custom.payload()));
                if (p_427447_.screen != p_427331_) {
                    p_427447_.setScreen(p_427331_);
                }
                break;
            default:
                defaultHandleClickEvent(p_427374_, p_427447_, p_427331_);
        }
    }

    protected static void defaultHandleClickEvent(ClickEvent p_425971_, Minecraft p_426157_, @Nullable Screen p_426037_) {
        boolean flag = switch (p_425971_) {
            case ClickEvent.OpenUrl(URI uri) -> {
                clickUrlAction(p_426157_, p_426037_, uri);
                yield false;
            }
            case ClickEvent.OpenFile clickevent$openfile -> {
                Util.getPlatform().openFile(clickevent$openfile.file());
                yield true;
            }
            case ClickEvent.SuggestCommand(String s2) -> {
                String s1 = s2;
                if (p_426037_ != null) {
                    p_426037_.insertText(s1, true);
                }

                yield true;
            }
            case ClickEvent.CopyToClipboard(String s) -> {
                p_426157_.keyboardHandler.setClipboard(s);
                yield true;
            }
            default -> {
                LOGGER.error("Don't know how to handle {}", p_425971_);
                yield true;
            }
        };
        if (flag && p_426157_.screen != p_426037_) {
            p_426157_.setScreen(p_426037_);
        }
    }

    protected static boolean clickUrlAction(Minecraft p_426119_, @Nullable Screen p_426200_, URI p_426104_) {
        if (!p_426119_.options.chatLinks().get()) {
            return false;
        } else {
            if (p_426119_.options.chatLinksPrompt().get()) {
                p_426119_.setScreen(new ConfirmLinkScreen(p_465510_ -> {
                    if (p_465510_) {
                        Util.getPlatform().openUri(p_426104_);
                    }

                    p_426119_.setScreen(p_426200_);
                }, p_426104_.toString(), false));
            } else {
                Util.getPlatform().openUri(p_426104_);
            }

            return true;
        }
    }

    protected static void clickCommandAction(LocalPlayer p_427378_, String p_425805_, @Nullable Screen p_427311_) {
        p_427378_.connection.sendUnattendedCommand(Commands.trimOptionalPrefix(p_425805_), p_427311_);
    }

    public final void init(int p_96608_, int p_96609_) {
        this.width = p_96608_;
        this.height = p_96609_;
        if (!this.initialized) {
            if (!net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.client.event.ScreenEvent.Init.Pre(this, this.children, this::addEventWidget, this::removeWidget)).isCanceled()) {
            this.init();
            this.setInitialFocus();
            }
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.client.event.ScreenEvent.Init.Post(this, this.children, this::addEventWidget, this::removeWidget));
        } else {
            this.repositionElements();
        }

        this.initialized = true;
        this.triggerImmediateNarration(false);
        if (this.minecraft.getLastInputType().isKeyboard()) {
            this.setNarrationSuppressTime(Long.MAX_VALUE);
        } else {
            this.suppressNarration(NARRATE_SUPPRESS_AFTER_INIT_TIME);
        }
    }

    protected void rebuildWidgets() {
        this.clearWidgets();
        this.clearFocus();
        if (!net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.client.event.ScreenEvent.Init.Pre(this, this.children, this::addEventWidget, this::removeWidget)).isCanceled()) {
        this.init();
        this.setInitialFocus();
        }
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.client.event.ScreenEvent.Init.Post(this, this.children, this::addEventWidget, this::removeWidget));
    }

    protected void fadeWidgets(float p_421625_) {
        for (GuiEventListener guieventlistener : this.children()) {
            if (guieventlistener instanceof AbstractWidget abstractwidget) {
                abstractwidget.setAlpha(p_421625_);
            }
        }
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    protected void init() {
    }

    public void tick() {
    }

    public void removed() {
    }

    public void added() {
    }

    public void renderBackground(GuiGraphics p_283688_, int p_296369_, int p_296477_, float p_294317_) {
        if (this.isInGameUi()) {
            this.renderTransparentBackground(p_283688_);
        } else {
            if (this.minecraft.level == null) {
                this.renderPanorama(p_283688_, p_294317_);
            }

            this.renderBlurredBackground(p_283688_);
            this.renderMenuBackground(p_283688_);
        }

        this.minecraft.gui.renderDeferredSubtitles();
    }

    protected void renderBlurredBackground(GuiGraphics p_420069_) {
        float f = this.minecraft.options.getMenuBackgroundBlurriness();
        if (f >= 1.0F) {
            p_420069_.blurBeforeThisStratum();
        }
    }

    protected void renderPanorama(GuiGraphics p_331628_, float p_331316_) {
        this.minecraft.gameRenderer.getPanorama().render(p_331628_, this.width, this.height, this.panoramaShouldSpin());
    }

    protected void renderMenuBackground(GuiGraphics p_331074_) {
        this.renderMenuBackground(p_331074_, 0, 0, this.width, this.height);
    }

    protected void renderMenuBackground(GuiGraphics p_331077_, int p_331957_, int p_331095_, int p_331894_, int p_332138_) {
        renderMenuBackgroundTexture(
            p_331077_, this.minecraft.level == null ? MENU_BACKGROUND : INWORLD_MENU_BACKGROUND, p_331957_, p_331095_, 0.0F, 0.0F, p_331894_, p_332138_
        );
    }

    public static void renderMenuBackgroundTexture(
        GuiGraphics p_331514_, Identifier p_467804_, int p_330327_, int p_331282_, float p_334038_, float p_334054_, int p_331309_, int p_331449_
    ) {
        int i = 32;
        p_331514_.blit(RenderPipelines.GUI_TEXTURED, p_467804_, p_330327_, p_331282_, p_334038_, p_334054_, p_331309_, p_331449_, 32, 32);
    }

    public void renderTransparentBackground(GuiGraphics p_294586_) {
        p_294586_.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
    }

    public boolean isPauseScreen() {
        return true;
    }

    public boolean isInGameUi() {
        return false;
    }

    protected boolean panoramaShouldSpin() {
        return true;
    }

    public boolean isAllowedInPortal() {
        return this.isPauseScreen();
    }

    protected void repositionElements() {
        this.rebuildWidgets();
    }

    public void resize(int p_96576_, int p_96577_) {
        this.width = p_96576_;
        this.height = p_96577_;
        this.repositionElements();
    }

    public void fillCrashDetails(CrashReport p_381106_) {
        CrashReportCategory crashreportcategory = p_381106_.addCategory("Affected screen", 1);
        crashreportcategory.setDetail("Screen name", () -> this.getClass().getCanonicalName());
    }

    protected boolean isValidCharacterForName(String p_96584_, int p_96586_, int p_445999_) {
        int i = p_96584_.indexOf(58);
        int j = p_96584_.indexOf(47);
        if (p_96586_ == 58) {
            return (j == -1 || p_445999_ <= j) && i == -1;
        } else {
            return p_96586_ == 47
                ? p_445999_ > i
                : p_96586_ == 95 || p_96586_ == 45 || p_96586_ >= 97 && p_96586_ <= 122 || p_96586_ >= 48 && p_96586_ <= 57 || p_96586_ == 46;
        }
    }

    @Override
    public boolean isMouseOver(double p_96595_, double p_96596_) {
        return true;
    }

    public void onFilesDrop(List<Path> p_96591_) {
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    private void scheduleNarration(long p_169381_, boolean p_169382_) {
        this.nextNarrationTime = Util.getMillis() + p_169381_;
        if (p_169382_) {
            this.narrationSuppressTime = Long.MIN_VALUE;
        }
    }

    private void suppressNarration(long p_169379_) {
        this.setNarrationSuppressTime(Util.getMillis() + p_169379_);
    }

    private void setNarrationSuppressTime(long p_434675_) {
        this.narrationSuppressTime = p_434675_;
    }

    public void afterMouseMove() {
        this.scheduleNarration(750L, false);
    }

    public void afterMouseAction() {
        this.scheduleNarration(200L, true);
    }

    public void afterKeyboardAction() {
        this.scheduleNarration(200L, true);
    }

    private boolean shouldRunNarration() {
        return SharedConstants.DEBUG_UI_NARRATION || this.minecraft.getNarrator().isActive();
    }

    public void handleDelayedNarration() {
        if (this.shouldRunNarration()) {
            long i = Util.getMillis();
            if (i > this.nextNarrationTime && i > this.narrationSuppressTime) {
                this.runNarration(true);
                this.nextNarrationTime = Long.MAX_VALUE;
            }
        }
    }

    public void triggerImmediateNarration(boolean p_169408_) {
        if (this.shouldRunNarration()) {
            this.runNarration(p_169408_);
        }
    }

    private void runNarration(boolean p_169410_) {
        this.narrationState.update(this::updateNarrationState);
        String s = this.narrationState.collectNarrationText(!p_169410_);
        if (!s.isEmpty()) {
            this.minecraft.getNarrator().saySystemNow(s);
        }
    }

    protected boolean shouldNarrateNavigation() {
        return true;
    }

    protected void updateNarrationState(NarrationElementOutput p_169396_) {
        p_169396_.add(NarratedElementType.TITLE, this.getNarrationMessage());
        if (this.shouldNarrateNavigation()) {
            p_169396_.add(NarratedElementType.USAGE, USAGE_NARRATION);
        }

        this.updateNarratedWidget(p_169396_);
    }

    protected void updateNarratedWidget(NarrationElementOutput p_169403_) {
        List<? extends NarratableEntry> list = this.narratables
            .stream()
            .flatMap(p_386212_ -> p_386212_.getNarratables().stream())
            .filter(NarratableEntry::isActive)
            .sorted(Comparator.comparingInt(TabOrderedElement::getTabOrderGroup))
            .toList();
        Screen.NarratableSearchResult screen$narratablesearchresult = findNarratableWidget(list, this.lastNarratable);
        if (screen$narratablesearchresult != null) {
            if (screen$narratablesearchresult.priority.isTerminal()) {
                this.lastNarratable = screen$narratablesearchresult.entry;
            }

            if (list.size() > 1) {
                p_169403_.add(
                    NarratedElementType.POSITION, Component.translatable("narrator.position.screen", screen$narratablesearchresult.index + 1, list.size())
                );
                if (screen$narratablesearchresult.priority == NarratableEntry.NarrationPriority.FOCUSED) {
                    p_169403_.add(NarratedElementType.USAGE, this.getUsageNarration());
                }
            }

            screen$narratablesearchresult.entry.updateNarration(p_169403_.nest());
        }
    }

    protected Component getUsageNarration() {
        return Component.translatable("narration.component_list.usage");
    }

    public static Screen.@Nullable NarratableSearchResult findNarratableWidget(List<? extends NarratableEntry> p_169401_, @Nullable NarratableEntry p_169402_) {
        Screen.NarratableSearchResult screen$narratablesearchresult = null;
        Screen.NarratableSearchResult screen$narratablesearchresult1 = null;
        int i = 0;

        for (int j = p_169401_.size(); i < j; i++) {
            NarratableEntry narratableentry = p_169401_.get(i);
            NarratableEntry.NarrationPriority narratableentry$narrationpriority = narratableentry.narrationPriority();
            if (narratableentry$narrationpriority.isTerminal()) {
                if (narratableentry != p_169402_) {
                    return new Screen.NarratableSearchResult(narratableentry, i, narratableentry$narrationpriority);
                }

                screen$narratablesearchresult1 = new Screen.NarratableSearchResult(narratableentry, i, narratableentry$narrationpriority);
            } else if (narratableentry$narrationpriority.compareTo(
                    screen$narratablesearchresult != null ? screen$narratablesearchresult.priority : NarratableEntry.NarrationPriority.NONE
                )
                > 0) {
                screen$narratablesearchresult = new Screen.NarratableSearchResult(narratableentry, i, narratableentry$narrationpriority);
            }
        }

        return screen$narratablesearchresult != null ? screen$narratablesearchresult : screen$narratablesearchresult1;
    }

    public void updateNarratorStatus(boolean p_352211_) {
        if (p_352211_) {
            this.scheduleNarration(NARRATE_DELAY_NARRATOR_ENABLED, false);
        }

        if (this.narratorButton != null) {
            this.narratorButton.setValue(this.minecraft.options.narrator().get());
        }
    }

    public Font getFont() {
        return this.font;
    }

    public boolean showsActiveEffects() {
        return false;
    }

    public boolean canInterruptWithAnotherScreen() {
        return this.shouldCloseOnEsc();
    }

    @Override
    public ScreenRectangle getRectangle() {
        return new ScreenRectangle(0, 0, this.width, this.height);
    }

    public @Nullable Music getBackgroundMusic() {
        return null;
    }

    private void addEventWidget(GuiEventListener b) {
        if (b instanceof Renderable r)
            this.renderables.add(r);
        if (b instanceof NarratableEntry ne)
            this.narratables.add(ne);
        children.add(b);
    }

    @OnlyIn(Dist.CLIENT)
    public record NarratableSearchResult(NarratableEntry entry, int index, NarratableEntry.NarrationPriority priority) {
    }
}
