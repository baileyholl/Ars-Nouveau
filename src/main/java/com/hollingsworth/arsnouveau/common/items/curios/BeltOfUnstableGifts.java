package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.common.lib.PotionEffectTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class BeltOfUnstableGifts extends ArsNouveauCurio {

    public static Optional<HolderSet.Named<MobEffect>> effects = Registry.MOB_EFFECT.getTag(PotionEffectTags.UNSTABLE_GIFTS);
    public static ArrayList<MobEffect> effectTable;

    public BeltOfUnstableGifts() {
        super();
    }

    public static void fetchEffects() {
        effects.ifPresentOrElse(mobEffects -> {
            effectTable = new ArrayList<>();
            for (Holder<MobEffect> mobEffect : mobEffects) {
                effectTable.add(mobEffect.get());
            }
        }, () -> {
            effects = Registry.MOB_EFFECT.getTag(PotionEffectTags.UNSTABLE_GIFTS);
        });
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (effects.isEmpty() || effectTable == null) {
            fetchEffects();
            return;
        }
        if (effectTable.size() == 0) return;
        LivingEntity wearer = slotContext.entity();
        if (wearer == null) return;
        if (slotContext.entity().getLevel() instanceof ServerLevel world) {
            if (world.getGameTime() % (20 * 6) == 0) {
                wearer.addEffect(new MobEffectInstance(effectTable.get(new Random().nextInt(effectTable.size())), 6 * 20, new Random().nextInt(3)));
            }
        }
    }
}
