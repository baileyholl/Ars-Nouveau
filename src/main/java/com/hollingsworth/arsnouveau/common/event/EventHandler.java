package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateMana;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void playerClone(PlayerEvent.PlayerRespawnEvent e) {
        syncPlayerEvent(e.getPlayer());
    }

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.StartTracking e) {
        syncPlayerEvent(e.getPlayer());
    }

    @SubscribeEvent
    public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        syncPlayerEvent(e.getPlayer());
    }

    @SubscribeEvent
    public static void jumpEvent(LivingEvent.LivingJumpEvent e) {

        if(e.getEntityLiving() == null  || e.getEntityLiving().getActivePotionEffect(Effects.SLOWNESS) == null)
            return;
        EffectInstance effectInstance = e.getEntityLiving().getActivePotionEffect(Effects.SLOWNESS);

        if(effectInstance.getAmplifier() >= 20){
            e.getEntityLiving().setMotion(0,0,0);
        }


    }

    public static void syncPlayerEvent(PlayerEntity playerEntity){
        if (playerEntity instanceof ServerPlayerEntity) {
            ManaCapability.getMana(playerEntity).ifPresent(mana -> {
                mana.setMaxMana(ManaUtil.getMaxMana(playerEntity));
                Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerEntity), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana()));
            });
        }
    }

    @SubscribeEvent
    public static void playerOnTick(TickEvent.PlayerTickEvent e) {

        if (e.player instanceof ServerPlayerEntity && e.player.world.getGameTime() % 5 == 0) {
            if (e.player.world.getGameTime() % 20 == 0) {
                ManaCapability.getMana(e.player).ifPresent(mana -> {
                    double regenPerSecond = ManaUtil.getManaRegen(e.player);
                    if (mana.getCurrentMana() != mana.getMaxMana()) {
                        mana.addMana((int) regenPerSecond);
                        Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.player), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana()));
                    }
                });
            }
            if (e.player.world.getGameTime() % 10 == 0) {
                ManaCapability.getMana(e.player).ifPresent(mana -> {
                    mana.setMaxMana(ManaUtil.getMaxMana(e.player));
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.player), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana()));
                });
            }
        }
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent e){
        World world = e.world;
        if(world.isRemote)
            return;
        EventQueue.getInstance().tick();
    }


    @SubscribeEvent
    public static void clientTickEnd(TickEvent.ClientTickEvent event){
       if(event.phase == TickEvent.Phase.END){
           ClientInfo.ticksInGame++;
       }
    }



    @SubscribeEvent
    public static void playerDamaged(LivingDamageEvent e){
        if(e.getEntityLiving() != null && e.getEntityLiving().getActivePotionMap().containsKey(ModPotions.SHIELD_POTION)){
            if(e.getSource() == DamageSource.MAGIC || e.getSource() == DamageSource.GENERIC ){
                float damage = e.getAmount() - 1f * e.getEntityLiving().getActivePotionMap().get(ModPotions.SHIELD_POTION).getAmplifier();
                if (damage < 0) damage = 0;
                e.setAmount(damage);
            }
        }
    }

    @SubscribeEvent
    public static void spellCast(SpellCastEvent e){ }
}
