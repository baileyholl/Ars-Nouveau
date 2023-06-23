package com.hollingsworth.arsnouveau.client.gui.radial_menu;

import net.minecraft.client.gui.GuiGraphics;

import java.util.List;
import java.util.function.IntConsumer;

public class RadialMenu<T> {
    private final IntConsumer setSelectedSlot;
    private final List<RadialMenuSlot<T>> radialMenuSlots;
    private final boolean showMoreSecondaryItems;
    private final SecondaryIconPosition secondaryIconStartingPosition;
    private final DrawCallback<T> drawCallback;
    private final int offset;

    /**
     * Returns the basic SpellBook-Like Radial Menu configuration.
     * Only one secondary Icon is shown below the primary Icon.
     * Look at the Spellbook for an example on how to use the radial menu.
     *
     * @param setSelectedSlot Provide a callback that sets the selected Slot to the provided integer.
     *                        REMEMBER to also handle the Serverside tag-setting!
     * @param drawCallback    Provide a callback that handles the drawing of the radial menu Icons. Refer to the SpellBook for an example
     *                        GuiRadialMenuUtils provides methods to handle either drawing Items or drawing textures provided as ResourceLocations
     *                        YOU are responsible to provide a method that handles the objects provided in your RadialMenuSlots
     * @param offset          Additional offset amount for secondary icons. If your Icons don't above each other try around with this parameter
     */
    public RadialMenu(IntConsumer setSelectedSlot, List<RadialMenuSlot<T>> radialMenuSlots, DrawCallback<T> drawCallback, int offset) {
        this.setSelectedSlot = setSelectedSlot;
        this.radialMenuSlots = radialMenuSlots;
        this.showMoreSecondaryItems = false;
        this.secondaryIconStartingPosition = SecondaryIconPosition.NORTH;
        this.drawCallback = drawCallback;
        this.offset = offset;
    }

    /**
     * Returns a Radial Menu configuration that displays up to 4 secondary Icons arranged around the primary Icon,
     * starting with the provided starting position and continuing counterclockwise
     * Look at the Spellbook for an example on how to use the radial menu.
     *
     * @param setSelectedSlot Provide a callback that sets the selected Slot to the provided integer. REMEMBER to also handle the Serverside tag-setting!
     * @param drawCallback    Provide a callback that handles the drawing of the radial menu Icons. Refer to the SpellBook for an example
     *                        GuiRadialMenuUtils provides methods to handle either drawing Items or drawing textures provided as ResourceLocations
     *                        YOU are responsible to provide a method that handles the objects provided in your RadialMenuSlots
     * @param offset          Additional offset amount for secondary icons. If your Icons don't above each other try around with this parameter
     */
    public RadialMenu(IntConsumer setSelectedSlot, List<RadialMenuSlot<T>> radialMenuSlots, SecondaryIconPosition secondaryIconStartingPosition, DrawCallback<T> drawCallback, int offset) {
        this.setSelectedSlot = setSelectedSlot;
        this.radialMenuSlots = radialMenuSlots;
        this.showMoreSecondaryItems = true;
        this.secondaryIconStartingPosition = secondaryIconStartingPosition;
        this.drawCallback = drawCallback;
        this.offset = offset;
    }

    public List<RadialMenuSlot<T>> getRadialMenuSlots() {
        return radialMenuSlots;
    }

    public void setCurrentSlot(int slot) {
        setSelectedSlot.accept(slot);
    }

    public boolean isShowMoreSecondaryItems() {
        return showMoreSecondaryItems;
    }

    public SecondaryIconPosition getSecondaryIconStartingPosition() {
        return this.secondaryIconStartingPosition;
    }

    public void drawIcon(T objectToBeDrawn, GuiGraphics poseStack, int positionX, int positionY, int size) {
        this.drawCallback.accept(objectToBeDrawn, poseStack, positionX, positionY, size, false);
    }

    public int getOffset() {
        return this.offset;
    }
}
