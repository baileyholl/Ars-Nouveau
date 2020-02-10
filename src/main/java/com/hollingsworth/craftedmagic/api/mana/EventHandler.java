package com.hollingsworth.craftedmagic.api.mana;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.api.util.ManaUtil;
import com.hollingsworth.craftedmagic.capability.ManaCapability;
import com.hollingsworth.craftedmagic.network.Networking;
import com.hollingsworth.craftedmagic.network.PacketUpdateBookGUI;
import com.hollingsworth.craftedmagic.network.PacketUpdateMana;
import com.hollingsworth.craftedmagic.potions.ModPotions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void playerOnTick(TickEvent.PlayerTickEvent e){
        if(e.player instanceof ServerPlayerEntity)
            ManaCapability.getMana(e.player).ifPresent(mana ->{
                mana.setMaxMana(ManaUtil.getMaxMana(e.player));
                double regenPerSecond = 5 + ManaUtil.getArmorRegen(e.player);
                if(mana.getCurrentMana() != mana.getMaxMana())
                    mana.addMana((int)Math.ceil(regenPerSecond / 20.0));
                Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()-> (ServerPlayerEntity) e.player), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana()));
            });
    }

    @SubscribeEvent
    public static void playerDamaged(LivingDamageEvent e){
        System.out.println(e.getEntity());
        if(e.getEntityLiving() != null && e.getEntityLiving().getActivePotionMap().containsKey(ModPotions.SHIELD_POTION)){
            float damage = e.getAmount() - 2.5f * e.getEntityLiving().getActivePotionMap().get(ModPotions.SHIELD_POTION).getAmplifier();
            if(damage < 0) damage = 0;
            e.setAmount(damage);
        }
    }
//        if(e.getEntityLiving().world.isRemote )
//            return;
//        float damage = e.getAmount();
//        EffectInstance effect = e.getEntityLiving().getActivePotionMap().get(ModPotions.SHIELD_POTION);
//        damage -= effect.getAmplifier() * 2.5;
//        e.setAmount(damage);
//    }

}
