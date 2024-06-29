package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.potion.IPotionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class PotionData{
    private Holder<Potion> potion;
    private List<MobEffectInstance> customEffects = new ArrayList<>();
    private Set<PotionContents> includedPotions = new HashSet<>();

    public PotionData(Holder<Potion> potion, List<MobEffectInstance> customEffects, Set<PotionContents> includedPotions) {
        this.potion = potion;
        this.includedPotions = includedPotions;
        setCustomEffects(customEffects);
    }

    public static @Nullable PotionContents from(ItemStack stack){
        if(stack.getItem() instanceof IPotionProvider provider) {
            return provider.getPotionData(stack);
        }else{
            return stack.get(DataComponents.POTION_CONTENTS);
        }
    }

    public PotionData(ItemStack stack){

        if(stack.getItem() instanceof IPotionProvider provider){
            PotionContents data = provider.getPotionData(stack);
            this.potion = data.potion;
            this.customEffects = data.getCustomEffects();
        }else{
            this.potion = stack.get(DataComponents.POTION_CONTENTS);
            customEffects = new ArrayList<>();
            setCustomEffects(potion.customEffects());
        }
    }

    public PotionData(PotionContents potion){
        this(potion.potion().get(), new ArrayList<>(), new HashSet<>(Collections.singletonList(potion)));
    }

    public PotionData(PotionData data){
        this(data.potion, new ArrayList<>(data.getCustomEffects()), new HashSet<>(data.getIncludedPotions()));
    }

    public ItemStack asPotionStack(){
        return asPotionStack(Items.POTION);
    }

    public ItemStack asPotionStack(Item item){
        if(this.getPotion() == PotionContents.EMPTY)
            return new ItemStack(item);

        PotionContents.setPotion(potionStack, this.getPotion());
        PotionUtils.setCustomEffects(potionStack, getCustomEffects());
        return potionStack;
    }

    public static PotionData fromTag(CompoundTag tag){
        PotionData instance = new PotionData();
        instance.setPotion(PotionUtils.getPotion(tag));
        instance.getCustomEffects().addAll(PotionUtils.getCustomEffects(tag));
        ListTag potionTagList = tag.getList("includedPotions", 8);
        Set<PotionContents> potions = instance.includedPotions;
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
        List<String> potionNames = new ArrayList<>(getIncludedPotions().stream().map(potion -> BuiltInRegistries.POTION.getKey(potion).toString()).toList());
        for(String potion : potionNames){
            potionTagList.add(StringTag.valueOf(potion));
        }
        tag.put("includedPotions", potionTagList);
        return tag;
    }

    public List<MobEffectInstance> fullEffects(){
        List<MobEffectInstance> thisEffects = new ArrayList<>(getCustomEffects());
        thisEffects.addAll(getPotion().getAllEffects());
        return thisEffects;
    }

    public void applyEffects(Entity source, Entity inDirectSource, LivingEntity target){
        for (MobEffectInstance effectinstance : fullEffects()) {
            if (effectinstance.getEffect().value().isInstantenous()) {
                effectinstance.getEffect().value().applyInstantenousEffect(source, inDirectSource, target, effectinstance.getAmplifier(), 1.0D);
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
        return getPotion() == PotionContents.EMPTY || getPotion().is( Potions.WATER) || getPotion().is(Potions.MUNDANE) || fullEffects().isEmpty();
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

        Set<PotionContents> potions = new HashSet<>();
        potions.addAll(this.getIncludedPotions());
        potions.addAll(other.getIncludedPotions());
        return new PotionData(getPotion(), new ArrayList<>(set), potions);
    }

    // TODO: fix ticksPerSecond?
    public void appendHoverText(List<Component> tooltip) {
        if(getPotion() == PotionContents.EMPTY)
            return;
        ItemStack potionStack = asPotionStack();
        tooltip.add(potionStack.getHoverName());
        PotionContents.addPotionTooltip(getPotion().getAllEffects(), tooltip::add, 1.0F, 20.0f);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PotionData other && areSameEffects(other);
    }

    public PotionContents getPotion() {
        return potion;
    }

    public void setPotion(PotionContents potion) {
        this.potion = potion;
    }

    public List<MobEffectInstance> getCustomEffects() {
        return customEffects;
    }

    public void setCustomEffects(List<MobEffectInstance> customEffects) {
        this.customEffects = customEffects.stream().filter(e -> !potion.getAllEffects().contains(e)).collect(Collectors.toList());
    }

    public Set<Holder<Potion>> getIncludedPotions() {
        includedPotions.add(getPotion());
        includedPotions.removeIf(potion -> potion == PotionContents.EMPTY);
        return includedPotions;
    }

    public void setIncludedPotions(Set<PotionContents> includedPotions) {
        this.includedPotions = includedPotions;
    }
}