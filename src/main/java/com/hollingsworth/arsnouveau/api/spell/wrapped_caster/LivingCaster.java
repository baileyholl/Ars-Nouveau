package com.hollingsworth.arsnouveau.api.spell.wrapped_caster;

import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LivingCaster implements IWrappedCaster {
    public LivingEntity livingEntity;

    public LivingCaster(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    public static LivingCaster from(LivingEntity livingEntity) {
        if (livingEntity instanceof Player player && !(player instanceof FakePlayer)) {
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
        List<FilterableItemHandler> filterableItemHandlers = new ArrayList<>();
        var cap = livingEntity.getCapability(Capabilities.ItemHandler.ENTITY);
        if (cap != null) {
            filterableItemHandlers.add(new FilterableItemHandler(cap));
        }
        return filterableItemHandlers;
    }

    @Override
    public Direction getFacingDirection() {
        return livingEntity.getDirection();
    }

    @Override
    public Vec3 getPosition() {
        return livingEntity.position();
    }

    @Override
    public boolean enoughMana(int totalCost) {
        IManaCap mana = CapabilityRegistry.getMana(livingEntity);
        if (mana == null) return false;
        return totalCost <= mana.getCurrentMana();
    }

    @Override
    public void expendMana(int totalCost) {
        IManaCap mana = CapabilityRegistry.getMana(livingEntity);
        if (mana != null) {
            mana.removeMana(totalCost);
        }
    }
}
