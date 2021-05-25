package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.List;

public class DrygmyEntity extends CreatureEntity implements IPickupResponder, IAnimatable, ITooltipProvider, IDispellable {
    protected DrygmyEntity(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
    }

    @Override
    public List<String> getTooltip() {
        return null;
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        return false;
    }

    @Override
    public ItemStack onPickup(ItemStack stack) {
        return null;
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }
    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
