package com.hollingsworth.arsnouveau.api.potion;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public interface IPotionProvider {

    @NotNull PotionContents getPotionData(ItemStack stack);

    int usesRemaining(ItemStack stack);

    int maxUses(ItemStack stack);

    void consumeUses(ItemStack stack, int amount, @Nullable Player player);

    void addUse(ItemStack stack, int amount, @Nullable Player player);

    /**
     * Modify the effect instance before applying it to the target
     */
    default MobEffectInstance getEffectInstance(MobEffectInstance effectinstance) {
        return effectinstance;
    }

    default void applyEffects(ItemStack stack, Entity source, Entity inDirectSource, LivingEntity target) {
        for (MobEffectInstance effectinstance : getPotionData(stack).getAllEffects()) {
            effectinstance = getEffectInstance(effectinstance);
            if (effectinstance.getEffect().value().isInstantenous()) {
                effectinstance.getEffect().value().applyInstantenousEffect(source, inDirectSource, target, effectinstance.getAmplifier(), 1.0D);
            } else {
                target.addEffect(new MobEffectInstance(effectinstance), source);
            }
        }
    }

    default void addTooltip(ItemStack stack, List<Component> tooltips){
        PotionContents potionStack = getPotionData(stack);
        potionStack.addPotionTooltip(tooltips::add, 1.0F, 20.0f);
    }
}
