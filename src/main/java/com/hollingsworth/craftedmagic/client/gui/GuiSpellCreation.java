package com.hollingsworth.craftedmagic.client.gui;

import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.api.CraftedMagicAPI;
import com.hollingsworth.craftedmagic.items.Spell;
import com.hollingsworth.craftedmagic.network.Networking;
import com.hollingsworth.craftedmagic.network.PacketOpenGUI;
import com.hollingsworth.craftedmagic.network.PacketUpdateSpellbook;
import com.mojang.blaze3d.platform.GlStateManager;
import javafx.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GuiSpellCreation extends Screen {

    private final int FULL_WIDTH = 256;
    private final int FULL_HEIGHT = 192;
    private static ResourceLocation background = new ResourceLocation(ExampleMod.MODID, "textures/gui/spell_creation.png");

    public int maxScale;

    public int numLinks = 5;
    public int bookLeft;
    public int bookTop;
    public int bookRight;
    private float scaleFactor;
    public Spell spellBook;
    public CraftedMagicAPI api;

    public ArrayList<Integer> craftingSlotIds;
    public List<String> tooltip;
    private int offsetFromScreenLeft;
    private int offsetFromScreenTop;
    private int selected_cast_slot;
    TextFieldWidget spell_name;
    public CompoundNBT spell_book_tag;

    public GuiSpellCreation(CraftedMagicAPI api, CompoundNBT tag) {
        super(new StringTextComponent(""));
        this.api = api;
        selected_cast_slot = 1;
        craftingSlotIds = new ArrayList<>();
        this.spell_book_tag = tag;

    }

    @Override
    public void init() {
        int guiScale = minecraft.gameSettings.guiScale;
        MainWindow res = minecraft.mainWindow;
        double oldGuiScale = res.calcGuiScale(minecraft.gameSettings.guiScale, minecraft.getForceUnicodeFont());
        maxScale = getMaxAllowedScale();
        int persistentScale = Math.min(0, maxScale);;
        double newGuiScale = res.calcGuiScale(persistentScale, minecraft.getForceUnicodeFont());

        if(persistentScale > 0 && newGuiScale != oldGuiScale) {
            scaleFactor = (float) newGuiScale / (float) res.getGuiScaleFactor();

            res.setGuiScale(newGuiScale);
            width = res.getScaledWidth();
            height = res.getScaledHeight();
            res.setGuiScale(oldGuiScale);
        } else scaleFactor = 1;
        // DEBUG
        offsetFromScreenLeft = (width - FULL_WIDTH) / 2;
        offsetFromScreenTop = (height - FULL_HEIGHT) / 2;
        bookLeft = width / 2 - FULL_WIDTH / 2;
        bookTop = height / 2 - FULL_HEIGHT / 2;
        bookRight = width / 2 + FULL_WIDTH / 2;

        int bookBottom = height / 2 + FULL_HEIGHT / 2;



        //Crafting slots
        ArrayList<AbstractSpellPart> spell_recipe = this.spell_book_tag != null ? Spell.getRecipeFromTag(spell_book_tag, 1) : null;

        for (int i = 0; i <= numLinks; i++) {
            String icon = null;
            String spell_id = "";

            if(spell_recipe != null &&  spell_recipe.size() > i){
                System.out.println(i);
                icon = spell_recipe.get(i).getIcon();
                spell_id = spell_recipe.get(i).tag;
            }
            addButton(new GuiSpellSlot(this,bookLeft + 10 + 28 * i, bookTop + FULL_HEIGHT - 24, true, icon, spell_id, i+1));
            craftingSlotIds.add(i+1);

        }

        Set<String> keys = this.api.spell_map.keySet();
        //Adding spell parts
        int counter = 1;
        for(String key  : keys){
//            System.out.println(key);
            addButton(new GuiSpellSlot(this,  bookLeft + 10, bookTop + 20  + 18 * counter, false, this.api.spell_map.get(key).getIcon(),
                    this.api.spell_map.get(key).tag));
            counter += 1;

        }
        addButton(new GuiImageButton(bookRight -45, bookBottom - 22, 0,0,35, 13, "textures/gui/create.png", this::onCreateClick));
        spell_name = new TextFieldWidget(minecraft.fontRenderer, bookLeft + 6, bookTop + FULL_HEIGHT - 42, 100, 12, null, "Spell Name");
        spell_name.setText("Spell 1");
        addButton(spell_name);

    }

    public List<GuiSpellSlot> getCraftingSlots(){
        List<GuiSpellSlot> slots = new ArrayList<>();
        for(Widget b : this.buttons){
            if(b instanceof GuiSpellSlot && ((GuiSpellSlot) b).isCraftingSlot){
                slots.add((GuiSpellSlot) b);
            }
        }
        return slots;
    }


    public void onCraftingSlotClick(Button button){
        System.out.println("Clicked button");
        if(button instanceof GuiSpellSlot && !((GuiSpellSlot) button).isCraftingSlot){
            System.out.println("clicked spellslot");
            List<Widget> test = this.buttons.stream().filter(b-> b instanceof  GuiSpellSlot && ((GuiSpellSlot) b).id != 0).collect(Collectors.toList());
            for(Widget b : test){
                System.out.println(((GuiSpellSlot) b).spell_id);
                System.out.println(((GuiSpellSlot) b).id);
                if(((GuiSpellSlot) b).spell_id.equals("") || ((GuiSpellSlot) b).spell_id == null){
                    System.out.println("Setting id");
                    System.out.println( this.api.spell_map.get(((GuiSpellSlot) button).spell_id).getIcon());
                    ((GuiSpellSlot) b).spell_id = ((GuiSpellSlot) button).spell_id;
                    ((GuiSpellSlot) b).resourceIcon = this.api.spell_map.get(((GuiSpellSlot) button).spell_id).getIcon();
                    break;
                }
            }
        }else if(button instanceof GuiSpellSlot && ((GuiSpellSlot) button).isCraftingSlot){
            ((GuiSpellSlot) button).spell_id = "";
            ((GuiSpellSlot) button).resourceIcon = "";
        }
    }

    public void onCreateClick(Button button){


        List<String> ids = new ArrayList<>();
        for(GuiSpellSlot slot : getCraftingSlots()){
            ids.add(slot.spell_id);
        }
        System.out.println("From client");
        System.out.println(ids.toString());
        Networking.INSTANCE.sendToServer(new PacketUpdateSpellbook(ids.toString(), this.selected_cast_slot));
    }


    public static void open(CraftedMagicAPI api, CompoundNBT spell_book_tag){
        Minecraft.getInstance().displayGuiScreen(new GuiSpellCreation(api, spell_book_tag));
    }

    int getMaxAllowedScale() {
        return minecraft.mainWindow.calcGuiScale(0, minecraft.getForceUnicodeFont());
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void tick() {

    }


    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        if(scaleFactor != 1) {
            GlStateManager.scalef(scaleFactor, scaleFactor, scaleFactor);

            mouseX /= scaleFactor;
            mouseY /= scaleFactor;
        }

        drawScreenAfterScale(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();

    }

    final void drawScreenAfterScale(int mouseX, int mouseY, float partialTicks) {
        resetTooltip();
        renderBackground();

        GlStateManager.pushMatrix();
        GlStateManager.translatef(bookLeft, bookTop, 0);
        GlStateManager.color3f(1F, 1F, 1F);

        drawBackgroundElements(mouseX, mouseY, partialTicks);
        drawForegroundElements(mouseX, mouseY, partialTicks);

        GlStateManager.popMatrix();

        super.render(mouseX, mouseY, partialTicks);

        drawTooltip(mouseX, mouseY);
    }

    final void drawBackgroundElements(int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().textureManager.bindTexture(background);
        // blit(x, y, u, v, w, h, 512, 256);
        blit(0, 0, 0, 0, FULL_WIDTH, FULL_HEIGHT);

    }

    public static void drawFromTexture(ResourceLocation resourceLocation, int x, int y, int u, int v, int w, int h) {
        Minecraft.getInstance().textureManager.bindTexture(resourceLocation);
        blit(x, y, u, v, w, h, 256, 256);
    }

    private void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//        Minecraft.getInstance().textureManager.bindTexture(background);
////
//        int offsetFromScreenLeft = (width - FULL_WIDTH) / 2;
//        int offsetFromScreenTop = (height - FULL_HEIGHT) / 2;
//        blit(offsetFromScreenLeft, offsetFromScreenTop, 0, 0, FULL_WIDTH, FULL_HEIGHT);
/*        font.drawSplitString("Cast Type",
                bookLeft + 22, bookTop + 10, 116, 0);*/

    }

    final void resetTooltip() {
        //tooltipStack = null;
        tooltip = null;

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();


    }

    public boolean isMouseInRelativeRange(int mouseX, int mouseY, int x, int y, int w, int h) {

        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    final void drawTooltip(int mouseX, int mouseY) {
        if(tooltip != null) {

//            List<String> tooltip = this.getTooltipFromItem(tooltipStack);
//            Pair<BookEntry, Integer> provider = book.contents.getEntryForStack(tooltipStack);
//            if(provider != null && (!(this instanceof GuiBookEntry) || ((GuiBookEntry) this).entry != provider.getLeft())) {
//                tooltip.add(TextFormatting.GOLD + "(" + I18n.format("patchouli.gui.lexicon.shift_for_recipe") + ')');
//            }
            //GuiUtils.preItemToolTip(tooltipStack);
            FontRenderer font = Minecraft.getInstance().fontRenderer;
            this.renderTooltip(tooltip, mouseX, mouseY, (font == null ? this.font : font));

        } else if(tooltip != null && !tooltip.isEmpty()) {
            List<String> wrappedTooltip = new ArrayList<>();
            for (String s : tooltip)
                Collections.addAll(wrappedTooltip, s.split("\n"));
            GuiUtils.drawHoveringText(wrappedTooltip, mouseX, mouseY, width, height, -1, this.font);
        }
    }

}