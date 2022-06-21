package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class BeltOfUnstableGifts extends ArsNouveauCurio {

    public BeltOfUnstableGifts(){
        super();
    }

    public static ArrayList<MobEffect> effectTable = new ArrayList<>(Arrays.asList(
            MobEffects.SLOW_FALLING, MobEffects.NIGHT_VISION, MobEffects.CONDUIT_POWER, MobEffects.ABSORPTION, MobEffects.DAMAGE_BOOST,
            MobEffects.FIRE_RESISTANCE, MobEffects.DIG_SPEED, MobEffects.MOVEMENT_SPEED, MobEffects.REGENERATION, MobEffects.DAMAGE_RESISTANCE
    ));

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity wearer = slotContext.entity();
        if (wearer == null) return;
        if (slotContext.entity().getLevel() instanceof ServerLevel world) {
            if(world.getGameTime() % (20 * 6)  == 0){
                wearer.addEffect(new MobEffectInstance(effectTable.get(new Random().nextInt(effectTable.size())), 6 * 20, new Random().nextInt(3)));
            }
        }


    }

}
