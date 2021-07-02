package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class GravityEffect extends Effect {

    protected GravityEffect() {
        super(EffectType.HARMFUL, 2039587);
        setRegistryName(ArsNouveau.MODID, "gravity");
    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int p_76394_2_) {
        super.applyEffectTick(livingEntity, p_76394_2_);
        if(!livingEntity.isOnGround()){
            boolean isTooHigh = true;
            World world = livingEntity.level;
            if(livingEntity instanceof PlayerEntity) {
                for (int i = 1; i < 3; i++) {
                    if (world.getBlockState(livingEntity.blockPosition().below(i)).getMaterial() != Material.AIR) {
                        isTooHigh = false;
                        break;
                    }
                }
            }
            if(isTooHigh){
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0, -0.5, 0));
                livingEntity.hurtMarked = true;
            }

        }

    }
    // Disable flight here because items tick after our potions
    @SubscribeEvent
    public static void entityTick(TickEvent.PlayerTickEvent e){
        if(e.phase == TickEvent.Phase.END && e.player.hasEffect(ModPotions.GRAVITY_EFFECT) && !e.player.isOnGround() && !e.player.isCreative()){
            e.player.abilities.flying = false;
        }
    }

    @SubscribeEvent
    public static void entityHurt(LivingHurtEvent e){
        if(e.getSource().equals(DamageSource.FALL) && e.getEntityLiving().hasEffect(ModPotions.GRAVITY_EFFECT) ){
            e.setAmount(e.getAmount() * 2.0f);
        }
    }
}
