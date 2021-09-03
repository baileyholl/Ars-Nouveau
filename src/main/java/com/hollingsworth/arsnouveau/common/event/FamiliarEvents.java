package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.FamiliarSummonEvent;
import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import com.hollingsworth.arsnouveau.common.entity.familiar.*;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class FamiliarEvents {

    public static List<FamiliarEntity> getFamiliars(Predicate<FamiliarEntity> predicate){
        List<FamiliarEntity> stale = new ArrayList<>();
        List<FamiliarEntity> matching = new ArrayList<>();
        for(FamiliarEntity familiarEntity : FamiliarEntity.FAMILIAR_SET){
            if(familiarEntity.removed || familiarEntity.terminatedFamiliar || familiarEntity.getOwner() == null) {
                stale.add(familiarEntity);
            }else if(predicate.test(familiarEntity)){
                matching.add(familiarEntity);
            }
        }
        FamiliarEntity.FAMILIAR_SET.removeAll(stale);
        return matching;
    }

    @SubscribeEvent
    public static void summonEvent(FamiliarSummonEvent event) {
        for(FamiliarEntity entity : getFamiliars((f) -> true)){
            if(entity.getOwner() != null && entity.getOwner().equals(event.owner)){
                entity.onFamiliarSpawned(event);
            }
        }

    }

    @SubscribeEvent
    public static void maxManaCalc(MaxManaCalcEvent event) {
        for(FamiliarEntity entity : getFamiliars(familiarEntity -> true)){
            if(!entity.isAlive())
                return;
            if(entity.getOwner() != null && entity.getOwner().equals(event.getEntity())){
                event.setMax((int) (event.getMax() -  event.getMax() * entity.getManaReserveModifier()));
            }
        }
    }

    @SubscribeEvent
    public static void spellResolveEvent(SpellModifierEvent event) {
        for(FamiliarEntity entity : getFamiliars((familiarEntity -> familiarEntity instanceof ISpellCastListener))){
            if(entity instanceof ISpellCastListener){
                ((ISpellCastListener) entity).onModifier(event);
            }
        }
    }

    @SubscribeEvent
    public static void modifierEvent(SpellModifierEvent event) {
        for(FamiliarEntity entity :getFamiliars((familiarEntity -> familiarEntity instanceof ISpellCastListener))){
            if(entity instanceof ISpellCastListener){
                ((ISpellCastListener) entity).onModifier(event);
            }
        }
    }

    @SubscribeEvent
    public static void fortuneEvent(LootingLevelEvent event) {
        for(FamiliarEntity entity : getFamiliars((familiarEntity -> familiarEntity instanceof FamiliarDrygmy))) {
            if(entity instanceof FamiliarDrygmy){
                ((FamiliarDrygmy) entity).onLootingEvent(event);
            }
        }
    }

    @SubscribeEvent
    public static void eatEvent(LivingEntityUseItemEvent.Finish event) {
        for(FamiliarEntity entity : getFamiliars((familiarEntity -> familiarEntity instanceof FamiliarSylph))){
            if(entity instanceof FamiliarSylph){
                ((FamiliarSylph) entity).eatEvent(event);
            }
        }
    }

    @SubscribeEvent
    public static void potionEvent(PotionEvent.PotionAddedEvent event) {
        for(FamiliarEntity entity : getFamiliars((familiarEntity -> familiarEntity instanceof FamiliarWixie))){
            if(entity instanceof FamiliarWixie){
                ((FamiliarWixie) entity).potionEvent(event);
            }
        }
    }
}
