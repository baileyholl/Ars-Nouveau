package com.hollingsworth.arsnouveau.api.spell.wrapped_caster;

import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerCaster extends LivingCaster {
    public Player player;

    public PlayerCaster(Player livingEntity) {
        super(livingEntity);
        player = livingEntity;
    }

    @Override
    public InventoryManager getInvManager() {
        return new InventoryManager(getInventory()).extractSlotMax(9).insertSlotMax(-1);
    }

    @Override
    public @NotNull List<FilterableItemHandler> getInventory() {
        List<FilterableItemHandler> base = new ArrayList<>();
        base.add(new FilterableItemHandler(new PlayerMainInvWrapper(player.inventory), new ArrayList<>()));
        return base;
    }

    @Override
    public SpellContext.CasterType getCasterType() {
        return SpellContext.CasterType.PLAYER;
    }
}
