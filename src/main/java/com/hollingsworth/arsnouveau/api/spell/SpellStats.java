package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for spell modifiers as they exist for a single effect before resolving.
 */
public class SpellStats {
    private double amplification;

    private double damageModifier;

    private double durationMultiplier;

    private List<AbstractAugment> augments;

    private List<ItemStack> modifierItems;

    private SpellStats(){
        augments = new ArrayList<>();
        modifierItems = new ArrayList<>();
    }

    public int getDurationInTicks(){
        return (int) (20.0D * durationMultiplier);
    }

    public int getBuffCount(AbstractAugment abstractAugment){
        return (int) augments.stream().filter(abstractAugment::equals).count();
    }

    public boolean hasBuff(AbstractAugment abstractAugment){
        return getBuffCount(abstractAugment) > 0;
    }

    public List<ITextComponent> addTooltip(List<ITextComponent> components){
        if(this.damageModifier != 0.0d)
            components.add(new TranslationTextComponent("tooltip.ars_nouveau.spell_damage", this.damageModifier).setStyle(Style.EMPTY.withColor(TextFormatting.BLUE)));
        if(this.durationMultiplier != 0.0d)
            components.add(new TranslationTextComponent("tooltip.ars_nouveau.duration_modifier", this.durationMultiplier).setStyle(Style.EMPTY.withColor(TextFormatting.GREEN)));
        if(this.amplification != 0.0d)
            components.add(new TranslationTextComponent("tooltip.ars_nouveau.amp_modifier", this.amplification).setStyle(Style.EMPTY.withColor(TextFormatting.RED)));

        return components;
    }

    public double getAmpMultiplier() {
        return amplification;
    }

    public void setAmpMultiplier(double amplification) {
        this.amplification = amplification;
    }

    public double getDamageModifier() {
        return damageModifier;
    }

    public void setDamageModifier(double damageModifier) {
        this.damageModifier = damageModifier;
    }

    public double getDurationMultiplier() {
        return durationMultiplier;
    }

    public void setDurationMultiplier(double durationMultiplier) {
        this.durationMultiplier = durationMultiplier;
    }

    public List<AbstractAugment> getAugments() {
        return augments;
    }

    public void setAugments(List<AbstractAugment> augments) {
        this.augments = augments;
    }

    public List<ItemStack> getModifierItems() {
        return modifierItems;
    }

    public void setModifierItems(List<ItemStack> modifierItems) {
        this.modifierItems = modifierItems;
    }

    public static class Builder{
        private SpellStats spellStats;

        public Builder(){
            this.spellStats = new SpellStats();
        }

        public SpellStats build(AbstractSpellPart spellPart, RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellContext spellContext){
            for(AbstractAugment abstractAugment : spellStats.augments){
                abstractAugment.applyModifiers(this, spellPart);
            }

            for(ItemStack stack : spellStats.modifierItems){
                if(stack.getItem() instanceof ISpellModifierItem) {
                    for (int i = 0; i < stack.getCount(); i++) {
                        ((ISpellModifierItem) stack.getItem()).applyItemModifiers(stack, this, spellPart, rayTraceResult, world, shooter, spellContext);
                    }
                }
            }
            SpellModifierEvent modifierEvent = new SpellModifierEvent(shooter, this, spellPart, rayTraceResult, world, spellContext);
            MinecraftForge.EVENT_BUS.post(modifierEvent);
            return spellStats;
        }

        public SpellStats build(){
            return spellStats;
        }

        public Builder setDamageModifier(double damageModifier){
            spellStats.damageModifier = damageModifier;
            return this;
        }

        public Builder addDamageModifier(double damageModifier){
            spellStats.damageModifier += damageModifier;
            return this;
        }

        public Builder setAugments(List<AbstractAugment> augments){
            spellStats.augments = augments;
            return this;
        }

        /**
         * Pulls all items from hands, curios, and armor.
         */
        public Builder addItemsFromEntity(@Nullable LivingEntity entity){
            if(entity == null)
                return this;
            CuriosUtil.getAllWornItems(entity).ifPresent(e ->{
                for(int i = 0; i < e.getSlots(); i++){
                    ItemStack item = e.getStackInSlot(i);
                    spellStats.modifierItems.add(item);
                }
            });
            for(ItemStack i : entity.getAllSlots()){
                spellStats.modifierItems.add(i);
            }

            return this;
        }

        public Builder addAugment(AbstractAugment abstractAugment){
            spellStats.augments.add(abstractAugment);
            return this;
        }

        public Builder setAmplification(double power){
            spellStats.amplification = power;
            return this;
        }

        public Builder addAmplification(double amplification){
            spellStats.amplification += amplification;
            return this;
        }

        public Builder setDurationModifier(double duration){
            spellStats.durationMultiplier = duration;
            return this;
        }

        public Builder addDurationModifier(double duration){
            spellStats.durationMultiplier += duration;
            return this;
        }

        public Builder setItems(List<ItemStack> items){
            spellStats.modifierItems = items;
            return this;
        }

        public Builder addItem(ItemStack itemStack){
            spellStats.modifierItems.add(itemStack);
            return this;
        }
    }

}
