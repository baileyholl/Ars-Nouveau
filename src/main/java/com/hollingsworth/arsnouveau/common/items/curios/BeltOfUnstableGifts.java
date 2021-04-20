package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class BeltOfUnstableGifts extends ArsNouveauCurio {
    public BeltOfUnstableGifts(String registry){
        super(registry);
    }
    public static ArrayList<Effect> effectTable = new ArrayList<>(Arrays.asList(
            Effects.SLOW_FALLING, Effects.NIGHT_VISION, Effects.CONDUIT_POWER, Effects.ABSORPTION, Effects.DAMAGE_BOOST,
            Effects.FIRE_RESISTANCE, Effects.DIG_SPEED, Effects.HEALTH_BOOST,  Effects.MOVEMENT_SPEED, Effects.REGENERATION, Effects.DAMAGE_RESISTANCE,
            ModPotions.SHIELD_POTION
            ));

    @Override
    public void wearableTick(LivingEntity wearer) {
        World world = wearer.getCommandSenderWorld();
        if(world.isClientSide())
            return;
        if(world.getGameTime() % (20 * 6)  == 0){
            wearer.addEffect(new EffectInstance(effectTable.get(new Random().nextInt(effectTable.size())), 6 * 20, new Random().nextInt(3)));
        }
    }
}
