package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class GravityEffect extends MobEffect {

    protected GravityEffect() {
        super(MobEffectCategory.HARMFUL, 2039587);
    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int p_76394_2_) {
        super.applyEffectTick(livingEntity, p_76394_2_);
        if (!livingEntity.isOnGround()) {
            boolean isTooHigh = true;
            Level world = livingEntity.level;
            if (livingEntity instanceof Player) {
                for (int i = 1; i < 3; i++) {
                    if (world.getBlockState(livingEntity.blockPosition().below(i)).getMaterial() != Material.AIR) {
                        isTooHigh = false;
                        break;
                    }
                }
            }
            if (isTooHigh) {
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0, -0.5, 0));
                livingEntity.hurtMarked = true;
            }

        }

    }

    // Disable flight here because items tick after our potions
    @SubscribeEvent
    public static void entityTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END && e.player.hasEffect(ModPotions.GRAVITY_EFFECT.get()) && !e.player.isOnGround() && !e.player.isCreative()) {
            e.player.abilities.flying = false;
        }
    }

    @SubscribeEvent
    public static void entityHurt(LivingHurtEvent e) {
        if (e.getSource().equals(DamageSource.FALL) && e.getEntity().hasEffect(ModPotions.GRAVITY_EFFECT.get())) {
            e.setAmount(e.getAmount() * 2.0f);
        }
    }
}
