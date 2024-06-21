package com.hollingsworth.arsnouveau.api.potion;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.registries.ForgeRegistries;
import java.util.*;
import java.util.stream.Collectors;

public class PotionData implements Cloneable{
    private Potion potion = Potions.EMPTY;
    private List<MobEffectInstance> customEffects = new ArrayList<>();
    private Set<Potion> includedPotions = new HashSet<>();

    public PotionData(Potion potion, List<MobEffectInstance> customEffects, Set<Potion> includedPotions) {
        this.potion = potion;
        this.includedPotions = includedPotions;
        setCustomEffects(customEffects);
    }

    public PotionData(){
        this(Potions.EMPTY, new ArrayList<>(), new HashSet<>());
    }

    public PotionData(ItemStack stack){

        if(stack.getItem() instanceof IPotionProvider provider){
            PotionData data = provider.getPotionData(stack).clone();
            this.potion = data.getPotion();
            this.customEffects = data.getCustomEffects();
        }else{
            this.potion = PotionUtils.getPotion(stack);
            customEffects = new ArrayList<>();
            setCustomEffects(PotionUtils.getCustomEffects(stack));
        }
    }

    public PotionData(Potion potion){
        this(potion, new ArrayList<>(), new HashSet<>(Collections.singletonList(potion)));
    }

    public PotionData(PotionData data){
        this(data.getPotion(), new ArrayList<>(data.getCustomEffects()), new HashSet<>(data.getIncludedPotions()));
    }

    public ItemStack asPotionStack(){
        return asPotionStack(Items.POTION);
    }

    public ItemStack asPotionStack(Item item){
        ItemStack potionStack = new ItemStack(item);
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
        ListTag potionTagList = tag.getList("includedPotions", 8);
        Set<Potion> potions = instance.includedPotions;
        for(int i = 0; i < potionTagList.size(); i++){
            potions.add(Potion.byName(potionTagList.getString(i)));
        }
        return instance;
    }

    public CompoundTag toTag(){
        CompoundTag tag = new CompoundTag();
        tag.putString("Potion", ForgeRegistries.POTIONS.getKey(getPotion()).toString());
        if (!getCustomEffects().isEmpty()) {
            ListTag listnbt = new ListTag();

            for (MobEffectInstance effectinstance : getCustomEffects()) {
                listnbt.add(effectinstance.save(new CompoundTag()));
            }

            tag.put("CustomPotionEffects", listnbt);
        }
        ListTag potionTagList = new ListTag();
        List<String> potionNames = new ArrayList<>(getIncludedPotions().stream().map(potion -> ForgeRegistries.POTIONS.getKey(potion).toString()).toList());
        for(String potion : potionNames){
            potionTagList.add(StringTag.valueOf(potion));
        }
        tag.put("includedPotions", potionTagList);
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
            return new PotionData(getPotion(), getCustomEffects(), getIncludedPotions());
        Set<MobEffectInstance> set = new HashSet<>();
        set.addAll(this.fullEffects());
        set.addAll(other.fullEffects());

        Set<Potion> potions = new HashSet<>();
        potions.addAll(this.getIncludedPotions());
        potions.addAll(other.getIncludedPotions());
        return new PotionData(getPotion(), new ArrayList<>(set), potions);
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
            clone.setIncludedPotions(new HashSet<>(getIncludedPotions()));
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

    public Set<Potion> getIncludedPotions() {
        includedPotions.add(getPotion());
        includedPotions.removeIf(potion -> potion == Potions.EMPTY);
        return includedPotions;
    }

    public void setIncludedPotions(Set<Potion> includedPotions) {
        this.includedPotions = includedPotions;
    }
}