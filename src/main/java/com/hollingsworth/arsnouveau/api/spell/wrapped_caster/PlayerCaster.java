package com.hollingsworth.arsnouveau.api.spell.wrapped_caster;

import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import java.util.ArrayList;

public class PlayerCaster extends LivingCaster{
    public Player player;

    public PlayerCaster(Player livingEntity) {
        super(livingEntity);
        player = livingEntity;
        this.filterableItemHandlers = new ArrayList<>();
        this.filterableItemHandlers.add(new FilterableItemHandler(new PlayerMainInvWrapper(player.inventory), new ArrayList<>()));
    }

    @Override
    public InventoryManager getInvManager() {
        return new InventoryManager(getInventory()).extractSlotMax(9).insertSlotMax(-1);
    }

    @Override
    public SpellContext.CasterType getCasterType() {
        return SpellContext.CasterType.PLAYER;
    }
}
