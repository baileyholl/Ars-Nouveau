package com.hollingsworth.arsnouveau.api.item.inv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiSlotReference<SReference extends SlotReference> {
    protected List<SReference> slots;

    public MultiSlotReference() {
        this.slots = new ArrayList<>();
    }

    public MultiSlotReference(List<SReference> slots) {
        this.slots = slots;
    }

    public MultiSlotReference(SReference... slotReferences) {
        this.slots = new ArrayList<>();
        Collections.addAll(slots, slotReferences);
    }

    public List<SReference> getSlots() {
        return slots;
    }

    public boolean isEmpty() {
        return slots.isEmpty();
    }
}
