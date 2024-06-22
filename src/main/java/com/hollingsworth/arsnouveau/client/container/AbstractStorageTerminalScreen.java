package com.hollingsworth.arsnouveau.client.container;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.buttons.StateButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.StorageSettingsButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.StorageTabButton;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.glfw.GLFW;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu.SlotAction.*;
import static com.hollingsworth.arsnouveau.client.container.TerminalSyncManager.getItemId;

public abstract class AbstractStorageTerminalScreen<T extends StorageTerminalMenu> extends AbstractContainerScreen<T> {
	private static final LoadingCache<StoredItemStack, List<String>> tooltipCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.SECONDS).build(new CacheLoader<>() {

		@Override
		public List<String> load(StoredItemStack key) {
			return key.getStack().getTooltipLines(Minecraft.getInstance().player, getTooltipFlag()).stream().map(Component::getString).collect(Collectors.toList());
		}

	});
	protected Minecraft mc = Minecraft.getInstance();

	/** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
	protected float currentScroll;
	/** True if the scrollbar is being dragged */
	protected boolean isScrolling;
	/**
	 * True if the left mouse button was held down last time drawScreen was
	 * called.
	 */
	private boolean refreshItemList;
	protected boolean wasClicking;
	protected NoShadowTextField searchField;
	protected int slotIDUnderMouse = -1;
	protected int controllMode;
	protected int rowCount;
	protected int searchType;
	protected boolean expanded;
	private String searchLast = "";
	protected boolean loadedSearch = false;
	private StoredItemStack.IStoredItemStackComparator comparator = new StoredItemStack.ComparatorAmount(false);
	protected static final ResourceLocation scrollBall = ArsNouveau.prefix( "textures/gui/scroll_ball.png");
	protected static final ResourceLocation tabImages = ArsNouveau.prefix( "textures/gui/bookwyrm_storage_tabs.png");
	protected StateButton buttonSortingType;
	protected StateButton buttonDirection;
	protected StateButton buttonSearchType;
	private Comparator<StoredItemStack> sortComp;
	List<String> tabNames = new ArrayList<>();
	public List<StorageTabButton> tabButtons = new ArrayList<>();
	public String selectedTab = null;

	public AbstractStorageTerminalScreen(T screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
		screenContainer.onPacket = this::onPacket;
	}

	protected void onPacket() {
		SortSettings s = menu.terminalData;
		if(s != null) {
			controllMode = s.controlMode;
			comparator = StoredItemStack.SortingTypes.VALUES[s.sortType % StoredItemStack.SortingTypes.VALUES.length].create(s.reverseSort);
			searchType = s.searchType;
			buttonSortingType.state = s.sortType;
			buttonDirection.state = s.reverseSort ? 1 : 0;
			buttonSearchType.state = searchType;
			expanded = s.expanded;
		}
		if(menu.tabNames != null && !menu.tabNames.isEmpty()){
			for(StorageTabButton tabButton : tabButtons){
				tabButton.visible = false;
			}
			// Set isAll tab visible
			tabButtons.get(0).visible = true;
			List<String> names = new ArrayList<>(new HashSet<>(menu.tabNames));
			names.sort(String::compareToIgnoreCase);
			for(int i = 0; i < names.size() && i < tabButtons.size(); i++){
				tabButtons.get(i+1).visible = true;
				tabButtons.get(i+1).highlightText = names.get(i);
			}
		}

		if(!loadedSearch && menu.search != null) {
			loadedSearch = true;
			searchField.setValue(menu.search);
			searchField.setFocused(true);
			if (searchField.getValue().isEmpty()) {
				searchField.setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.search").getString());
			}else{
				searchField.setSuggestion("");
			}
		}
	}

	protected void sendUpdate() {
		CompoundTag c = new CompoundTag();
		c.put("sortSettings", getSortSettings().toTag());
		StorageTabButton selectedTabButton = tabButtons.stream().filter(i -> i.visible && i.isSelected).findFirst().orElse(null);
		if(selectedTabButton != null && selectedTabButton.highlightText != null){
			c.putString("selectedTab", selectedTabButton.highlightText);
		}
		CompoundTag msg = new CompoundTag();
		msg.put("termData", c);
		menu.sendMessage(msg);
	}

	public SortSettings getSortSettings() {
		return new SortSettings(
				controllMode,
				comparator.isReversed(),
				comparator.type(),
				searchType,
				expanded
		);
	}

	@Override
	protected void init() {
		clearWidgets();
		inventoryLabelY = imageHeight - 92;
		super.init();

		this.searchField = new NoShadowTextField(getFont(), this.leftPos + 115, this.topPos + 6, 60, this.getFont().lineHeight, Component.translatable("narrator.ars_nouveau.search"));
		this.searchField.setMaxLength(100);
		this.searchField.setBordered(false);
		this.searchField.setVisible(true);
		this.searchField.setValue(searchLast);
		searchLast = "";
		addRenderableWidget(searchField);

		buttonSortingType = addRenderableWidget(new StorageSettingsButton(leftPos - 17, topPos + 14, 22, 12, 44, 13, 0, ArsNouveau.prefix( "textures/gui/sort_type.png"), b -> {
			comparator = StoredItemStack.SortingTypes.VALUES[(comparator.type() + 1) % StoredItemStack.SortingTypes.VALUES.length].create(comparator.isReversed());
			buttonSortingType.state = comparator.type();
			sendUpdate();
			refreshItemList = true;
		}));

		buttonDirection = addRenderableWidget(new StorageSettingsButton(leftPos - 17, topPos + 29, 22, 12, 44, 13, 0, ArsNouveau.prefix( "textures/gui/sort_order.png"), b -> {
			comparator.setReversed(!comparator.isReversed());
			buttonDirection.state = comparator.isReversed() ? 1 : 0;
			sendUpdate();
			refreshItemList = true;
		}));
		buttonSearchType = addRenderableWidget(new StorageSettingsButton(leftPos - 17, topPos + 44, 22, 12, 44, 13, 0, ArsNouveau.prefix( "textures/gui/search_sync.png"), b -> {
			searchType = searchType == 0 ? 1 : 0;
			buttonSearchType.state = searchType;
			sendUpdate();
		}));
		for(int i = 0; i < 12; i++){
			var button = addRenderableWidget(new StorageTabButton(leftPos - 13, topPos + 59 + i * 15, 18, 12, 256, 13, i, 0, tabImages, b -> {
				StorageTabButton tabButton = (StorageTabButton) b;
				setSelectedTab(tabButton.state);
				sendUpdate();
			}));
			button.visible = false;
			if(i == 0){
				button.isAll = true;
				button.isSelected = true;
			}
			this.tabButtons.add(button);
		}
		updateSearch();
	}

	public void setSelectedTab(int index){
		for(int i = 0; i < tabButtons.size(); i++){
			tabButtons.get(i).isSelected = i == index;
		}
	}

	protected void updateSearch() {
		String searchString = searchField.getValue().trim();
		if(searchField.getValue().isEmpty()){
			searchField.setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.search").getString());
		}else{
			searchField.setSuggestion("");
		}

		if (refreshItemList || !searchLast.equals(searchString)) {

			getMenu().itemListClientSorted.clear();
			boolean searchMod = false;
			String search = searchString;
			if (searchString.startsWith("@")) {
				searchMod = true;
				search = searchString.substring(1);
			}
			Pattern m = null;
			try {
				m = Pattern.compile(search.toLowerCase(), Pattern.CASE_INSENSITIVE);
			} catch (Throwable ignore) {
				try {
					m = Pattern.compile(Pattern.quote(search.toLowerCase()), Pattern.CASE_INSENSITIVE);
				} catch (Throwable __) {
					return;
				}
			}
			boolean notDone;
			try {
				for (int i = 0;i < getMenu().itemListClient.size();i++) {
					StoredItemStack is = getMenu().itemListClient.get(i);
					if (is != null && is.getStack() != null) {
						String dspName = searchMod ? getItemId(is.getStack().getItem()).getNamespace() : is.getStack().getHoverName().getString();
						notDone = true;
						if (m.matcher(dspName.toLowerCase()).find()) {
							addStackToClientList(is);
							notDone = false;
						}
						if (notDone) {
							for (String lp : tooltipCache.get(is)) {
								if (m.matcher(lp).find()) {
									addStackToClientList(is);
									break;
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Collections.sort(getMenu().itemListClientSorted, menu.noSort ? sortComp : comparator);
			if(!searchLast.equals(searchString)) {
				getMenu().scrollTo(0);
				this.currentScroll = 0;
				if (searchType == 1) {
					IAutoFillTerminal.sync(searchString);
				}
				CompoundTag nbt = new CompoundTag();
				nbt.putString("search", searchString);
				menu.sendMessage(nbt);

				onUpdateSearch(searchString);
			} else {
				getMenu().scrollTo(this.currentScroll);
			}
			refreshItemList = false;
			this.searchLast = searchString;
		}
	}

	private void addStackToClientList(StoredItemStack is) {
		getMenu().itemListClientSorted.add(is);
	}

	public static TooltipFlag getTooltipFlag(){
		return Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
	}

	@Override
	protected void containerTick() {
		updateSearch();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		PoseStack st = graphics.pose();
		boolean flag = GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_RELEASE;
		int i = this.leftPos;
		int j = this.topPos;
		int k = i + 187;
		int l = j + 18;
		int i1 = k + 14;
		int j1 = l + rowCount * 18;

		if(hasShiftDown()) {
			if(!menu.noSort) {
				List<StoredItemStack> list = getMenu().itemListClientSorted;
				Object2IntMap<StoredItemStack> map = new Object2IntOpenHashMap<>();
				map.defaultReturnValue(Integer.MAX_VALUE);
				for (int m = 0; m < list.size(); m++) {
					map.put(list.get(m), m);
				}
				sortComp = Comparator.comparing(map::getInt);
				menu.noSort = true;
			}
		} else if(menu.noSort) {
			sortComp = null;
			menu.noSort = false;
			refreshItemList = true;
			menu.itemListClient = new ArrayList<>(menu.itemList);
		}

		if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1) {
			this.isScrolling = this.needsScrollBars();
		}

		if (!flag) {
			this.isScrolling = false;
		}
		this.wasClicking = flag;

		if (this.isScrolling) {
			this.currentScroll = (mouseY - l - 7.5F) / (j1 - l - 15.0F);
			this.currentScroll = Mth.clamp(this.currentScroll, 0.0F, 1.0F);
			getMenu().scrollTo(this.currentScroll);
		}
		super.render(graphics, mouseX, mouseY, partialTicks);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		i = k;
		j = l;
		k = j1;
		graphics.blit(scrollBall, i, j + 3 + (int) ((k - j - 14) * this.currentScroll), 0, 0, 12, 12, 12, 12);


		if(this.menu.getCarried().isEmpty() && slotIDUnderMouse != -1) {
			SlotStorage slot = getMenu().storageSlotList.get(slotIDUnderMouse);
			if(slot.stack != null) {
				if (slot.stack.getQuantity() > 9999) {
					ClientInfo.setTooltip(Component.translatable("tooltip.ars_nouveau.amount", slot.stack.getQuantity()));
				}
				graphics.renderTooltip(font, slot.stack.getActualStack(), mouseX, mouseY);
				ClientInfo.setTooltip();
			}
		} else
			this.renderTooltip(graphics, mouseX, mouseY);

		if (buttonSortingType.isHoveredOrFocused()) {
			graphics.renderTooltip(font, Component.translatable("tooltip.ars_nouveau.sorting_" + buttonSortingType.state), mouseX, mouseY);
		}
		if (buttonSearchType.isHoveredOrFocused()) {
			graphics.renderTooltip(font, Component.translatable("tooltip.ars_nouveau.search_" + buttonSearchType.state, IAutoFillTerminal.getHandlerName()), mouseX, mouseY);
		}
		if(buttonDirection.isHoveredOrFocused()){
			graphics.renderTooltip(font, Component.translatable("tooltip.ars_nouveau.direction_" + buttonDirection.state, IAutoFillTerminal.getHandlerName()), mouseX, mouseY);
		}
		for(StorageTabButton tabButton : tabButtons) {
			if(tabButton.isHoveredOrFocused() && tabButton.isAll){
				graphics.renderTooltip(font, Component.translatable("tooltip.ars_nouveau.master_tab"), mouseX, mouseY);
			}else if (tabButton.isHoveredOrFocused() && tabButton.highlightText != null) {
				graphics.renderTooltip(font, Component.literal(tabButton.highlightText), mouseX, mouseY);
			}
		}
	}

	@Override
	protected void renderLabels(GuiGraphics p_281635_, int mouseX, int mouseY) {
		PoseStack st = p_281635_.pose();
		st.pushPose();
		slotIDUnderMouse = drawSlots(p_281635_, mouseX, mouseY);
		st.popPose();
	}

	protected int drawSlots(GuiGraphics st, int mouseX, int mouseY) {
		StorageTerminalMenu term = getMenu();
		int slotHover = -1;
		for (int i = 0;i < term.storageSlotList.size();i++) {
			if(drawSlot(st, term.storageSlotList.get(i), mouseX, mouseY)){
				slotHover = i;
			}
		}
		return slotHover;
	}

	protected boolean drawSlot(GuiGraphics st, SlotStorage slot, int mouseX, int mouseY) {
		if (slot.stack != null) {
			ItemStack stack = slot.stack.getStack().copy().split(1);
			int i = slot.xDisplayPosition, j = slot.yDisplayPosition;

			st.renderItem(stack, i, j);
			st.renderItemDecorations(this.font, stack, i, j, null);

			drawStackSize(st, getFont(), slot.stack.getQuantity(), i, j);
		}

		if (mouseX >= getGuiLeft() + slot.xDisplayPosition - 1 && mouseY >= getGuiTop() + slot.yDisplayPosition - 1 && mouseX < getGuiLeft() + slot.xDisplayPosition + 17 && mouseY < getGuiTop() + slot.yDisplayPosition + 17) {
			int l = slot.xDisplayPosition;
			int t = slot.yDisplayPosition;

			renderSlotHighlight(st, l, t, 0);
			return true;
		}
		return false;
	}

	private void drawStackSize(GuiGraphics graphics, Font fr, long size, int x, int y) {
		float scaleFactor = 0.6f;
		RenderSystem.disableDepthTest();
		RenderSystem.disableBlend();
		String stackSize = NumberFormatUtil.formatNumber(size);
		PoseStack st = graphics.pose();
		st.pushPose();
		st.scale(scaleFactor, scaleFactor, scaleFactor);
		st.translate(0, 0, 450);
		float inverseScaleFactor = 1.0f / scaleFactor;
		int X = (int) (((float) x + 0 + 16.0f - fr.width(stackSize) * scaleFactor) * inverseScaleFactor);
		int Y = (int) (((float) y + 0 + 16.0f - 7.0f * scaleFactor) * inverseScaleFactor);
		graphics.drawString(font, stackSize, X, Y, 16777215);
		st.popPose();
		RenderSystem.enableDepthTest();
	}

	protected boolean needsScrollBars() {
		return this.getMenu().itemListClientSorted.size() > rowCount * 9;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
		if (slotIDUnderMouse > -1) {
			if (isPullOne(mouseButton)) {
				if (getMenu().getSlotByID(slotIDUnderMouse).stack != null && getMenu().getSlotByID(slotIDUnderMouse).stack.getQuantity() > 0) {
					storageSlotClick(getMenu().getSlotByID(slotIDUnderMouse).stack, PULL_ONE, isTransferOne(mouseButton));
					return true;
				}
				return true;
			} else if (pullHalf(mouseButton)) {
				if (!menu.getCarried().isEmpty()) {
					storageSlotClick(null, hasControlDown() ? GET_QUARTER : GET_HALF, false);
				} else {
					if (getMenu().getSlotByID(slotIDUnderMouse).stack != null && getMenu().getSlotByID(slotIDUnderMouse).stack.getQuantity() > 0) {
						storageSlotClick(getMenu().getSlotByID(slotIDUnderMouse).stack, hasControlDown() ? GET_QUARTER : GET_HALF, false);
						return true;
					}
				}
			} else if (pullNormal(mouseButton)) {
				if (!menu.getCarried().isEmpty()) {
					storageSlotClick(null, PULL_OR_PUSH_STACK, false);
				} else {
					if (getMenu().getSlotByID(slotIDUnderMouse).stack != null) {
						if (getMenu().getSlotByID(slotIDUnderMouse).stack.getQuantity() > 0) {
							storageSlotClick(getMenu().getSlotByID(slotIDUnderMouse).stack, hasShiftDown() ? SHIFT_PULL : StorageTerminalMenu.SlotAction.PULL_OR_PUSH_STACK, false);
							return true;
						}
					}
				}
			}
		} else if (GLFW.glfwGetKey(mc.getWindow().getWindow(), GLFW.GLFW_KEY_SPACE) != GLFW.GLFW_RELEASE) {
			storageSlotClick(null, SPACE_CLICK, false);
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	protected void storageSlotClick(StoredItemStack slotStack, StorageTerminalMenu.SlotAction act, boolean pullOne) {
		menu.sync.sendClientInteract(slotStack, act, pullOne);
	}

	public boolean isPullOne(int mouseButton) {
		return mouseButton == 1 && hasShiftDown();
	}

	public boolean isTransferOne(int mouseButton) {
		return hasShiftDown() && hasControlDown();
	}

	public boolean pullHalf(int mouseButton) {
		return mouseButton == 1;
	}

	public boolean pullNormal(int mouseButton) {
		return mouseButton == 0;
	}


	public Font getFont() {
		return font;
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (p_keyPressed_1_ == 256) {
			this.onClose();
			return true;
		}
		return this.searchField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || this.searchField.canConsumeInput() || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		if(searchField.charTyped(p_charTyped_1_, p_charTyped_2_))return true;
		return super.charTyped(p_charTyped_1_, p_charTyped_2_);
	}

	@Override
	public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
		if (!this.needsScrollBars()) {
			return false;
		} else {
			int i = ((this.menu).itemListClientSorted.size() + 9 - 1) / 9 - 5;
			this.currentScroll = (float)(this.currentScroll - p_mouseScrolled_5_ / i);
			this.currentScroll = Mth.clamp(this.currentScroll, 0.0F, 1.0F);
			this.menu.scrollTo(this.currentScroll);
			return true;
		}
	}

	public abstract ResourceLocation getGui();

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		graphics.blit(getGui(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		graphics.blit(ArsNouveau.prefix( "textures/gui/search_paper.png"), this.leftPos + 102, this.topPos + 3, 0, 0, 72, 15, 72, 15);
	}

	protected void onUpdateSearch(String text) {}

	public void receive(CompoundTag tag) {
		menu.receiveClientNBTPacket(tag);
		refreshItemList = true;
	}

	private FakeSlot fakeSlotUnderMouse = new FakeSlot();

	@Override
	public Slot getSlotUnderMouse() {
		Slot s = super.getSlotUnderMouse();
		if(s != null)return s;
		if(slotIDUnderMouse > -1 && getMenu().getSlotByID(slotIDUnderMouse).stack != null) {
			fakeSlotUnderMouse.container.setItem(0, getMenu().getSlotByID(slotIDUnderMouse).stack.getStack());
			return fakeSlotUnderMouse;
		}
		return null;
	}
}
