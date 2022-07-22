package com.hollingsworth.arsnouveau.api.potion;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.util.*;
import java.util.stream.Collectors;

public class PotionData {
    public Potion potion;
    public List<MobEffectInstance> customEffects;

    public PotionData(Potion potion, List<MobEffectInstance> customEffects) {
        this.potion = potion;
        this.customEffects = customEffects;
        this.customEffects = customEffects.stream().filter(e -> !potion.getEffects().contains(e)).collect(Collectors.toList());
    }

    public PotionData(){
        this(Potions.EMPTY, new ArrayList<>());
    }

    public PotionData(ItemStack stack){
        this(PotionUtils.getPotion(stack), PotionUtils.getMobEffects(stack));
    }

    public PotionData(Potion potion){
        this(potion, new ArrayList<>());
    }

    public ItemStack asPotionStack(){
        ItemStack potionStack = new ItemStack(Items.POTION);
        if(this.potion == Potions.EMPTY)
            return potionStack;
        PotionUtils.setPotion(potionStack, this.potion);
        PotionUtils.setCustomEffects(potionStack, customEffects);
        return potionStack;
    }

    public static PotionData fromTag(CompoundTag tag){
        PotionData instance = new PotionData();
        instance.potion = PotionUtils.getPotion(tag);
        instance.customEffects.addAll(PotionUtils.getCustomEffects(tag));
        return instance;
    }

    public CompoundTag toTag(){
        CompoundTag tag = new CompoundTag();
        tag.putString("Potion", Registry.POTION.getKey(potion).toString());
        if (!customEffects.isEmpty()) {
            ListTag listnbt = new ListTag();

            for (MobEffectInstance effectinstance : customEffects) {
                listnbt.add(effectinstance.save(new CompoundTag()));
            }

            tag.put("CustomPotionEffects", listnbt);
        }
        return tag;
    }

    public List<MobEffectInstance> fullEffects(){
        List<MobEffectInstance> thisEffects = new ArrayList<>(customEffects);
        thisEffects.addAll(potion.getEffects());
        return thisEffects;
    }

    public void applyEffects(Entity source, Entity inDirectSource, LivingEntity target){
        for (MobEffectInstance effectinstance : fullEffects()) {
            if (effectinstance.getEffect().isInstantenous()) {
                effectinstance.getEffect().applyInstantenousEffect(source, inDirectSource, target, effectinstance.getAmplifier(), 1.0D);
            } else {
                target.addEffect(new MobEffectInstance(effectinstance));
            }
        }
    }

    public boolean areSameEffects(List<MobEffectInstance> effects){
        List<MobEffectInstance> thisEffects = fullEffects();
        if(thisEffects.size() != effects.size())
            return false;
        effects.sort(Comparator.comparing(MobEffectInstance::toString));
        thisEffects.sort(Comparator.comparing(MobEffectInstance::toString));
        return thisEffects.equals(effects);
    }

    public boolean isEmpty(){
        return potion == Potions.EMPTY;
    }

    public boolean areSameEffects(PotionData other){
        return areSameEffects(other.fullEffects());
    }

    public PotionData mergeEffects(PotionData other){
        if(areSameEffects(other))
            return new PotionData(potion, customEffects);
        Set<MobEffectInstance> set = new HashSet<>();
        set.addAll(this.fullEffects());
        set.addAll(other.fullEffects());
        return new PotionData(potion, new ArrayList<>(set));
    }

    public void appendHoverText(List<Component> tooltip) {
        if(potion == Potions.EMPTY)
            return;
        ItemStack potionStack = asPotionStack();
        tooltip.add(potionStack.getHoverName());
        PotionUtils.addPotionTooltip(potionStack, tooltip, 1.0F);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PotionData other && areSameEffects(other);
    }
}