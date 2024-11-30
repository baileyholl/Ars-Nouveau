package com.hollingsworth.arsnouveau.common.util;

import com.hollingsworth.arsnouveau.api.potion.IPotionProvider;
import com.hollingsworth.arsnouveau.api.registry.PotionProviderRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PotionUtil {

    public static ItemStack getPotion(Holder<Potion> potion) {
        ItemStack stack = new ItemStack(Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }

    public static boolean arePotionContentsEqual(PotionContents pot1, PotionContents pot2){
        if(pot1 == null || pot2 == null){
            return false;
        }
        Set<MobEffectInstance> pot1Effects = new HashSet<>();
        Set<MobEffectInstance> pot2Effects = new HashSet<>();
        pot1.getAllEffects().forEach(pot1Effects::add);
        pot2.getAllEffects().forEach(pot2Effects::add);
        return pot1Effects.equals(pot2Effects);
    }

    public static boolean isEmpty(@Nullable PotionContents potionContents) {
        return potionContents == null ||
                !potionContents.hasEffects() && (potionContents == PotionContents.EMPTY
                        || potionContents.is(Potions.WATER)
                        || potionContents.is(Potions.MUNDANE)
                        || potionContents.is(Potions.AWKWARD));
    }

    public static @NotNull PotionContents getContents(ItemStack stack){
        IPotionProvider provider = PotionProviderRegistry.from(stack);
        if(provider != null){
            return provider.getPotionData(stack);
        }
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    }

    public static void applyContents(PotionContents contents, @NotNull LivingEntity target, @Nullable LivingEntity source, @Nullable LivingEntity indirect){
        contents.forEachEffect(instance ->  {
            if (instance.getEffect().value().isInstantenous()) {
                instance.getEffect().value().applyInstantenousEffect(source, indirect, target, instance.getAmplifier(), 1.0);
            } else {
                target.addEffect(instance);
            }
        });
    }

    public static PotionContents merge(PotionContents contents1, PotionContents contents2){
        if(arePotionContentsEqual(contents1, contents2))
            return new PotionContents(contents1.potion(), contents1.customColor(), contents1.customEffects());
        Set<MobEffectInstance> set = new HashSet<>();
        for(MobEffectInstance effect : contents1.getAllEffects()){
            set.add(new MobEffectInstance(effect));
        }
        for(MobEffectInstance effect : contents2.getAllEffects()){
            set.add(new MobEffectInstance(effect));
        }
        if(contents1.potion().isPresent()) {
            contents1.potion().get().value().getEffects().forEach(set::remove);
        }
        return new PotionContents(contents1.potion(), contents1.customColor(), new ArrayList<>(set));
    }
}
