package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.common.lib.PotionEffectTags;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.Random;

public class BeltOfUnstableGifts extends ArsNouveauCurio {

    public BeltOfUnstableGifts() {
        super();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        ArrayList<Holder<MobEffect>> effectTable = PotionEffectTags.getEffects(slotContext.entity().level, PotionEffectTags.UNSTABLE_GIFTS);
        if (effectTable == null || effectTable.size() == 0) return;
        LivingEntity wearer = slotContext.entity();
        if (wearer == null) return;
        if (slotContext.entity().level instanceof ServerLevel world) {
            if (world.getGameTime() % (20 * 6) == 0) {
                wearer.addEffect(new MobEffectInstance(effectTable.get(new Random().nextInt(effectTable.size())), 6 * 20, new Random().nextInt(3)));
            }
        }
    }
}
