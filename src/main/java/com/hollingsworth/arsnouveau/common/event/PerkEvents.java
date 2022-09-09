package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.perk.VampiricPerk;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class PerkEvents {

    @SubscribeEvent
    public static void equipmentChangedEvent(final LivingEquipmentChangeEvent event) {
        if(!event.getEntity().level.isClientSide && event.getEntity() instanceof Player player){
            List<PerkInstance> perkInstances = PerkUtil.getPerksFromItem(event.getFrom());
            List<PerkInstance> toInstances = PerkUtil.getPerksFromItem(event.getTo());
            if(perkInstances.equals(toInstances))
                return;

            List<PerkInstance> perks = PerkUtil.getPerksFromPlayer(player);
            List<IPerk> itemPerks = PerkUtil.getPerksFromItem(event.getTo()).stream().map(PerkInstance::getPerk).toList();
            for(PerkInstance equippedPerks : perks){
                if(itemPerks.contains(equippedPerks.getPerk())){
                    PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.perks.duplicated"));
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void spellDamageEvent(final SpellDamageEvent.Post event) {
        if(event.caster instanceof Player player){
            int vampLevel = PerkUtil.countForPerk(VampiricPerk.INSTANCE, player);
            if(vampLevel > 0){
                float healAmount = event.damage * (0.2f * vampLevel);
                player.heal(healAmount);
            }
        }
    }
}
