package com.hollingsworth.arsnouveau.api.spell.wrapped_caster;

import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerCaster extends LivingCaster{
    public Player player;

    public PlayerCaster(Player livingEntity) {
        super(livingEntity);
        player = livingEntity;
    }

    @Override
    public InventoryManager getInvManager() {
        return new InventoryManager(getInventory()).withSlotMax(9);
    }

    @Override
    public @NotNull List<FilterableItemHandler> getInventory() {
        return super.getInventory();
    }

    @Override
    public SpellContext.CasterType getCasterType() {
        return SpellContext.CasterType.PLAYER;
    }
}
