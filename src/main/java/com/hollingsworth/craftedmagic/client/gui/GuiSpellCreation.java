package com.hollingsworth.craftedmagic.client.gui;

import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.api.CraftedMagicAPI;
import com.hollingsworth.craftedmagic.items.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jline.reader.Widget;
import org.lwjgl.opengl.GL11;

import java.util.Set;

public class GuiSpellCreation extends GuiScreen {

    private final int FULL_WIDTH = 256;
    private final int FULL_HEIGHT = 192;
    private static ResourceLocation background = new ResourceLocation(ExampleMod.MODID, "textures/gui/spell_creation.png");

    public int maxScale;

    public int numLinks = 5;
    public int bookLeft;
    public int bookTop;
    public int bookRight;
    public Spell spellBook;

    @Override
    public void initGui()
    {
        int guiScale = mc.gameSettings.guiScale;
        maxScale = getMaxAllowedScale();
        int persistentScale = 0;

        if(persistentScale > 0 && persistentScale != guiScale) {
            mc.gameSettings.guiScale = persistentScale;
            ScaledResolution res = new ScaledResolution(mc);
            width = res.getScaledWidth();
            height = res.getScaledHeight();
            mc.gameSettings.guiScale = guiScale;
        }
        // DEBUG
        bookLeft = width / 2 - FULL_WIDTH / 2;
        bookTop = height / 2 - FULL_HEIGHT / 2;
        bookRight = width / 2 + FULL_WIDTH / 2;
        int bookBottom = height / 2 + FULL_HEIGHT / 2;

        //Crafting slots
        for(int i = 0; i<= numLinks; i++){
            buttonList.add(new GuiSpellSlot(i, bookLeft + 10 + 28 * i, bookTop + FULL_HEIGHT - 24, true, null));
        }


        Set<String> keys = ExampleMod.proxy.getAPI().spell_map.keySet();

        int counter = 1;
        System.out.println(keys);
        for(String key  : keys){
            System.out.println(key);
            buttonList.add(new GuiSpellSlot(numLinks + counter, bookLeft + 10, bookTop + 20  + 18 * counter, false, CraftedMagicAPI.getInstance().spell_map.get(key).getIcon()));
            counter += 1;
        }
//        for(int i = numLinks; i < CraftedMagicAPI.craftedMagicAPI.spell_map.keySet().size(); i++){
//
//        }
//        buttonList.add(new GuiSpellSlot(++numLinks, bookLeft + 10, bookTop + 20  + 18, false, "projectile.png"));
//        buttonList.add(new GuiSpellSlot(++numLinks, bookLeft + 10, bookTop + 20  + 18*2, false, "dig.png"));
        buttonList.add(new GuiImageButton(30,  bookRight -45, bookBottom - 22, 0,0,13, 35, "textures/gui/create.png"));
    }

    public void initGui(Spell spellbook){
        this.initGui();
        this.spellBook = spellbook;
    }

    int getMaxAllowedScale() {
        Minecraft mc = Minecraft.getMinecraft();
        int scale = mc.gameSettings.guiScale;
        mc.gameSettings.guiScale = 0;
        ScaledResolution res = new ScaledResolution(mc);
        mc.gameSettings.guiScale = scale;
        return res.getScaleFactor();
    }
    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen()
    {

    }


    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int parWidth, int parHeight, float p_73863_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(background);
//
        int offsetFromScreenLeft = (width - FULL_WIDTH ) / 2;
        int offsetFromScreenTop = (height - FULL_HEIGHT) / 2;
       drawTexturedModalRect(offsetFromScreenLeft, offsetFromScreenTop, 0, 0, FULL_WIDTH, FULL_HEIGHT);
        fontRenderer.drawSplitString("Cast Type",
                bookLeft + 22, bookTop + 10, 116, 0);
//        int widthOfString;
//        String stringPageIndicator = "1";
//        widthOfString = fontRenderer.getStringWidth(stringPageIndicator);
//        fontRenderer.drawString(stringPageIndicator,
//                offsetFromScreenLeft - widthOfString + FULL_WIDTH
//                        - 44,
//                18, 0);
//        fontRenderer.drawSplitString("hello",
//                offsetFromScreenLeft + 36, 34, 116, 0);
        super.drawScreen(parWidth, parHeight, p_73863_3_);

    }

    /**
     * Called when a mouse button is pressed and the mouse is moved around.
     * Parameters are : mouseX, mouseY, lastButtonClicked &
     * timeSinceMouseClick.
     */
    @Override
    protected void mouseClickMove(int parMouseX, int parMouseY,
                                  int parLastButtonClicked, long parTimeSinceMouseClick)
    {

    }

    @Override
    protected void actionPerformed(GuiButton parButton)
    {
        System.out.println("Pressed button " + parButton.id);
        if(parButton instanceof GuiSpellSlot){
            GuiSpellSlot slot = (GuiSpellSlot) parButton;
            if(slot.isCraftingSlot){
                if(slot.resourceIcon != null && !slot.resourceIcon.equals("")){

                }
            }
        }

    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat
     * events
     */
    @Override
    public void onGuiClosed()
    {

    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in
     * single-player
     */
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}

