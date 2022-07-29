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

public class PotionData implements Cloneable{
    private Potion potion;
    private List<MobEffectInstance> customEffects;

    public PotionData(Potion potion, List<MobEffectInstance> customEffects) {
        this.potion = potion;
        setCustomEffects(customEffects);
    }

    public PotionData(){
        this(Potions.EMPTY, new ArrayList<>());
    }

    public PotionData(ItemStack stack){
        if(stack.getItem() instanceof IPotionProvider provider){
            PotionData data = provider.getPotionData(stack).clone();
            this.potion = data.getPotion();
            this.customEffects = data.getCustomEffects();
        }else{
            this.potion = PotionUtils.getPotion(stack);
            setCustomEffects(PotionUtils.getCustomEffects(stack));
        }
    }

    public PotionData(Potion potion){
        this(potion, new ArrayList<>());
    }

    public PotionData(PotionData data){
        this(data.getPotion(), new ArrayList<>(data.getCustomEffects()));
    }

    public ItemStack asPotionStack(){
        ItemStack potionStack = new ItemStack(Items.POTION);
        if(this.getPotion() == Potions.EMPTY)
            return potionStack;
        PotionUtils.setPotion(potionStack, this.getPotion());
        PotionUtils.setCustomEffects(potionStack, getCustomEffects());
        return potionStack;
    }

    public static PotionData fromTag(CompoundTag tag){
        PotionData instance = new PotionData();
        instance.setPotion(PotionUtils.getPotion(tag));
        instance.getCustomEffects().addAll(PotionUtils.getCustomEffects(tag));
        return instance;
    }

    public CompoundTag toTag(){
        CompoundTag tag = new CompoundTag();
        tag.putString("Potion", Registry.POTION.getKey(getPotion()).toString());
        if (!getCustomEffects().isEmpty()) {
            ListTag listnbt = new ListTag();

            for (MobEffectInstance effectinstance : getCustomEffects()) {
                listnbt.add(effectinstance.save(new CompoundTag()));
            }

            tag.put("CustomPotionEffects", listnbt);
        }
        return tag;
    }

    public List<MobEffectInstance> fullEffects(){
        List<MobEffectInstance> thisEffects = new ArrayList<>(getCustomEffects());
        thisEffects.addAll(getPotion().getEffects());
        return thisEffects;
    }

    public void applyEffects(Entity source, Entity inDirectSource, LivingEntity target){
        for (MobEffectInstance effectinstance : fullEffects()) {
            if (effectinstance.getEffect().isInstantenous()) {
                effectinstance.getEffect().applyInstantenousEffect(source, inDirectSource, target, effectinstance.getAmplifier(), 1.0D);
            } else {
                target.addEffect(new MobEffectInstance(effectinstance), source);
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
        return getPotion() == Potions.EMPTY || getPotion() == Potions.WATER || getPotion() == Potions.MUNDANE || fullEffects().isEmpty();
    }

    public boolean areSameEffects(PotionData other){
        return areSameEffects(other.fullEffects());
    }

    public PotionData mergeEffects(PotionData other){
        if(areSameEffects(other))
            return new PotionData(getPotion(), getCustomEffects());
        Set<MobEffectInstance> set = new HashSet<>();
        set.addAll(this.fullEffects());
        set.addAll(other.fullEffects());
        return new PotionData(getPotion(), new ArrayList<>(set));
    }

    public void appendHoverText(List<Component> tooltip) {
        if(getPotion() == Potions.EMPTY)
            return;
        ItemStack potionStack = asPotionStack();
        tooltip.add(potionStack.getHoverName());
        PotionUtils.addPotionTooltip(potionStack, tooltip, 1.0F);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PotionData other && areSameEffects(other);
    }

    @Override
    public PotionData clone() {
        try {
            PotionData clone = (PotionData) super.clone();
            clone.setPotion(getPotion());
            clone.setCustomEffects(new ArrayList<>(getCustomEffects()));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public Potion getPotion() {
        return potion;
    }

    public void setPotion(Potion potion) {
        this.potion = potion;
    }

    public List<MobEffectInstance> getCustomEffects() {
        return customEffects;
    }

    public void setCustomEffects(List<MobEffectInstance> customEffects) {
        this.customEffects = customEffects.stream().filter(e -> !potion.getEffects().contains(e)).collect(Collectors.toList());
    }
}