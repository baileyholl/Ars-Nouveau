package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.perk.VampiricPerk;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class PerkEvents {

    @SubscribeEvent
    public static void equipmentChangedEvent(final LivingEquipmentChangeEvent event) {

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
