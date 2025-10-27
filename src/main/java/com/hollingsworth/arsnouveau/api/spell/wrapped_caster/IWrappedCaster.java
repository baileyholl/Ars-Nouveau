package com.hollingsworth.arsnouveau.api.spell.wrapped_caster;

import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface IWrappedCaster {

    SpellContext.CasterType getCasterType();

    default InventoryManager getInvManager() {
        return new InventoryManager(getInventory());
    }

    @NotNull
    default List<FilterableItemHandler> getInventory() {
        return new ArrayList<>();
    }

    /**
     * Returns the horizontal facing direction of the caster.
     */
    default Direction getFacingDirection() {
        return Direction.NORTH;
    }

    /**
     * Returns the first block entity that matches the predicate.
     */
    default @Nullable BlockEntity getNearbyBlockEntity(Predicate<BlockEntity> predicate) {
        return null;
    }

    default Vec3 getPosition() {
        return Vec3.ZERO;
    }

    default boolean enoughMana(int totalCost) {
        return false;
    }

    void expendMana(int totalCost);
}
