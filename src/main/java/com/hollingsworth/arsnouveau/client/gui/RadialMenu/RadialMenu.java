package com.hollingsworth.arsnouveau.client.gui.RadialMenu;

import java.util.List;
import java.util.function.IntConsumer;

public class RadialMenu {
    private final IntConsumer setSelectedSlot;
    private final List<RadialMenuSlot> radialMenuSlots;
    private final boolean showMoreSecondaryItems;
    private final SecondaryIconPosition secondaryIconStartingPosition;


    private RadialMenu(IntConsumer setSelectedSlot, List<RadialMenuSlot> radialMenuSlots, boolean showMoreSecondaryItems, SecondaryIconPosition secondaryIconStartingPosition) {
        this.setSelectedSlot = setSelectedSlot;
        this.radialMenuSlots = radialMenuSlots;
        this.showMoreSecondaryItems = showMoreSecondaryItems;
        this.secondaryIconStartingPosition = secondaryIconStartingPosition;
    }

    /**
     * Returns the basic SpellBook-Like Radial Menu configuration.
     * Only one secondary Icon is shown below the primary Icon.
     * Look at the Spellbook for an example on how to use the radial menu.
     *
     * @param setSelectedSlot Provide a callback that sets the selected Slot to the provided integer. REMEMBER to also handle the Serverside tag-setting!
     */
    public static RadialMenu getRadialMenu(IntConsumer setSelectedSlot, List<RadialMenuSlot> radialMenuSlots) {
        return new RadialMenu(setSelectedSlot, radialMenuSlots, false, SecondaryIconPosition.SOUTH);
    }

    /**
     * Returns a Radial Menu configuration that displays up to 4 secondary Icons arranged around the primary Icon,
     * starting with the provided starting position and continuing counterclockwise
     * Look at the Spellbook for an example on how to use the radial menu.
     *
     * @param setSelectedSlot Provide a callback that sets the selected Slot to the provided integer. REMEMBER to also handle the Serverside tag-setting!
     */
    public static RadialMenu getAdvancedRadialMenu(IntConsumer setSelectedSlot, List<RadialMenuSlot> radialMenuSlots, SecondaryIconPosition secondaryIconStartingPosition) {
        return new RadialMenu(setSelectedSlot, radialMenuSlots, true, secondaryIconStartingPosition);
    }

    public List<RadialMenuSlot> getRadialMenuSlots() {
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
}
