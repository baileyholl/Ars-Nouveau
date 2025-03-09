package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import com.hollingsworth.arsnouveau.common.network.ClientToServerStoragePacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmiLecternRecipeHandler<T extends CraftingTerminalMenu> implements StandardRecipeHandler<T> {
    @Override
    public List<Slot> getInputSources(T handler) {
        var storedItems = handler.getStoredItems();
        var playerInventory = handler.slots;
        var fakeSlots = new ArrayList<Slot>(storedItems.size() + playerInventory.size());
        var container = new SimpleContainer(storedItems.size() + playerInventory.size());

        int idx = 0;
        for (StoredItemStack storedItem : storedItems) {
            container.setItem(idx, storedItem.getActualStack());
            fakeSlots.add(new Slot(container, idx, idx, 0));
            idx++;
        }

        for (Slot slot : playerInventory) {
            if (!(slot instanceof CraftingTerminalMenu.SlotCrafting) && !(slot instanceof CraftingTerminalMenu.ActiveResultSlot)) {
                container.setItem(idx, slot.getItem());
                fakeSlots.add(new Slot(container, idx, idx, 0));
                idx++;
            }
        }

        return fakeSlots;
    }

    @Override
    public List<Slot> getCraftingSlots(T handler) {
        var craftingSlots = new ArrayList<Slot>(9);
        for (var slot : handler.slots) {
            if (slot instanceof CraftingTerminalMenu.SlotCrafting) {
                craftingSlots.add(slot);
                if (craftingSlots.size() >= 9) {
                    break;
                }
            }
        }
        return craftingSlots;
    }

    @Override
    public @Nullable Slot getOutputSlot(T handler) {
        for (var slot : handler.slots) {
            if (slot instanceof CraftingTerminalMenu.ActiveResultSlot) {
                return slot;
            }
        }

        return null;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<T> context) {
        var handler = context.getScreenHandler();
        if (handler == null) {
            return false;
        }

        List<EmiIngredient> missing = new ArrayList<>();
        var requiredItems = recipe.getInputs();
        var sources = this.getInputSources(handler);
        List<List<ItemStack>> inputs = new ArrayList<>();

        for (var required : requiredItems) {
            List<ItemStack> possibleStacks = new ArrayList<>();
            for (var possible : required.getEmiStacks()) {
                possibleStacks.add(possible.getItemStack());
            }

            if (possibleStacks.isEmpty()) {
                inputs.add(null);
                continue;
            }

            inputs.add(possibleStacks);

            boolean found = false;
            for (ItemStack stack : possibleStacks) {
                if (stack != null && sources.stream().anyMatch(slot -> ItemStack.isSameItemSameComponents(slot.getItem(), stack))) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                missing.add(required);
            }

        }
        Networking.sendToServer(new ClientToServerStoragePacket(new ClientToServerStoragePacket.Data(Optional.empty(), Optional.empty(), Optional.of(inputs))));

        return missing.isEmpty();
    }
}
