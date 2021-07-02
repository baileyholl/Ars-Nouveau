package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateFlight;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class FlightEffect extends Effect {
    protected FlightEffect() {
        super(EffectType.BENEFICIAL, 2039587);
        setRegistryName(ArsNouveau.MODID, "flight");
    }


    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int p_76394_2_) {
        super.applyEffectTick(entity, p_76394_2_);
        if(entity instanceof PlayerEntity){
            ((PlayerEntity) entity).abilities.mayfly = entity.getEffect(ModPotions.FLIGHT_EFFECT).getDuration() > 2;
//            if(entity.getEffect(ModPotions.FLIGHT_EFFECT).getDuration() <= 1)
//                Networking.sendToPlayer(new PacketUpdateFlight(false), (PlayerEntity) entity);
        }
    }


    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeModifierManager p_111187_2_, int p_111187_3_) {
        super.removeAttributeModifiers(entity, p_111187_2_, p_111187_3_);
        if(entity instanceof PlayerEntity){
            ((PlayerEntity) entity).abilities.mayfly = false;
            ((PlayerEntity) entity).abilities.flying = false;
            Networking.sendToPlayer(new PacketUpdateFlight(false, false), (PlayerEntity) entity);
        }
    }
}
