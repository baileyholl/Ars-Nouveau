package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateFlight;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;

public class FlightEffect extends MobEffect {
    protected FlightEffect() {
        super(MobEffectCategory.BENEFICIAL, 2039587);
        setRegistryName(ArsNouveau.MODID, "flight");
    }


    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int p_76394_2_) {
        super.applyEffectTick(entity, p_76394_2_);
        if(entity instanceof Player){
            ((Player) entity).abilities.mayfly = entity.getEffect(ModPotions.FLIGHT_EFFECT).getDuration() > 2;
//            if(entity.getEffect(ModPotions.FLIGHT_EFFECT).getDuration() <= 1)
//                Networking.sendToPlayer(new PacketUpdateFlight(false), (PlayerEntity) entity);
        }
    }


    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap p_111187_2_, int p_111187_3_) {
        super.removeAttributeModifiers(entity, p_111187_2_, p_111187_3_);
        if(entity instanceof Player){
            ((Player) entity).abilities.mayfly = false;
            ((Player) entity).abilities.flying = false;
            Networking.sendToPlayer(new PacketUpdateFlight(false, false), (Player) entity);
        }
    }
}
