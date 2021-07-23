package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.item.ISpellStatModifier;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for spell modifiers as they exist for a single effect before resolving.
 */
public class SpellStats {
    public double amplification;

    public double damageModifier;
    /**
     * Duration in ticks
     */
    public double durationModifier;

    public List<AbstractAugment> augments;

    public List<ItemStack> modifierItems;

    private SpellStats(){
        augments = new ArrayList<>();
        modifierItems = new ArrayList<>();
    }

    public static class Builder{
        private SpellStats spellStats;

        public Builder(){
            this.spellStats = new SpellStats();
        }

        public SpellStats build(AbstractSpellPart spellPart){
            for(AbstractAugment abstractAugment : spellStats.augments){
                abstractAugment.applyModifiers(this, spellPart);
            }
            for(ItemStack stack : spellStats.modifierItems){
                if(stack.getItem() instanceof ISpellStatModifier) {
                    for (int i = 0; i < stack.getCount(); i++) {
                        ((ISpellStatModifier) stack.getItem()).applyModifiers(this, spellPart);
                    }
                }
            }
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
            spellStats.durationModifier = duration;
            return this;
        }

        public Builder addDurationModifier(double duration){
            spellStats.durationModifier += duration;
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
