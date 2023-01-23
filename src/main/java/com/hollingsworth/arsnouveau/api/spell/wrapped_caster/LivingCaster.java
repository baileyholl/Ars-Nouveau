package com.hollingsworth.arsnouveau.api.spell.wrapped_caster;

import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LivingCaster implements IWrappedCaster {
    public LivingEntity livingEntity;

    protected List<FilterableItemHandler> filterableItemHandlers;

    public LivingCaster(LivingEntity livingEntity){
        this.livingEntity = livingEntity;
        filterableItemHandlers = new ArrayList<>();

        livingEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap ->{
            filterableItemHandlers.add(new FilterableItemHandler(cap));
        });
    }

    public static LivingCaster from(LivingEntity livingEntity){
        if(livingEntity instanceof Player player && !(player instanceof FakePlayer)){
            return new PlayerCaster(player);
        }
        return new LivingCaster(livingEntity);
    }

    @Override
    public SpellContext.CasterType getCasterType() {
        return SpellContext.CasterType.LIVING_ENTITY;
    }

    @Override
    public @NotNull List<FilterableItemHandler> getInventory() {
        return filterableItemHandlers;
    }

    @Override
    public Direction getFacingDirection() {
        return livingEntity.getDirection();
    }
}
