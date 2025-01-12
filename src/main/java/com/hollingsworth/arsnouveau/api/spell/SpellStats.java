package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.NeoForge;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for spell modifiers as they exist for a single effect before resolving.
 */
public class SpellStats {

    //true if at least one sensitive augment is present, keep using getBuffCount() for more than one
    private boolean sensitive = false;

    private double amplification;

    private float acceleration;

    private double damageModifier;

    private double durationMultiplier;

    private double aoeMultiplier;

    private boolean randomizing;

    private List<AbstractAugment> augments;

    private List<ItemStack> modifierItems;

    private SpellStats() {
        augments = new ArrayList<>();
        modifierItems = new ArrayList<>();
    }

    public int getDurationInTicks() {
        return (int) (20.0D * durationMultiplier);
    }

    public int getBuffCount(AbstractAugment abstractAugment) {
        return (int) augments.stream().filter(abstractAugment::equals).count();
    }

    public boolean hasBuff(AbstractAugment abstractAugment) {
        return getBuffCount(abstractAugment) > 0;
    }

    public List<Component> addTooltip(List<Component> components) {
        if (this.damageModifier != 0.0d)
            components.add(Component.translatable("tooltip.ars_nouveau.spell_damage", this.damageModifier).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
        if (this.getDurationMultiplier() != 0.0d)
            components.add(Component.translatable("tooltip.ars_nouveau.duration_modifier", this.getDurationMultiplier()).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
        if (this.amplification != 0.0d)
            components.add(Component.translatable("tooltip.ars_nouveau.amp_modifier", this.amplification).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));

        return components;
    }

    public double getAmpMultiplier() {
        return amplification;
    }

    public double getAoeMultiplier() {
        return aoeMultiplier;
    }

    public void setAmpMultiplier(double amplification) {
        this.amplification = amplification;
    }

    public float getAccMultiplier() {
        return acceleration;
    }

    public void setAccMultiplier(float acceleration) {
        this.acceleration = acceleration;
    }

    public double getDamageModifier() {
        return damageModifier;
    }

    public void setDamageModifier(double damageModifier) {
        this.damageModifier = damageModifier;
    }

    public boolean isRandomized() {
        return randomizing;
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

    public boolean isSensitive() {
        return sensitive;
    }

    public static class Builder {
        private SpellStats spellStats;

        public Builder() {
            this.spellStats = new SpellStats();
        }

        public SpellStats build(AbstractSpellPart spellPart, @Nullable HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {
            for (AbstractAugment abstractAugment : spellStats.augments) {
                abstractAugment.applyModifiers(this, spellPart, rayTraceResult, world, shooter, spellContext);
            }

            for (ItemStack stack : spellStats.modifierItems) {
                if (stack.getItem() instanceof ISpellModifierItem) {
                    for (int i = 0; i < stack.getCount(); i++) {
                        ((ISpellModifierItem) stack.getItem()).applyItemModifiers(stack, this, spellPart, rayTraceResult, world, shooter, spellContext);
                    }
                }
            }
            SpellModifierEvent modifierEvent = new SpellModifierEvent(shooter, this, spellPart, rayTraceResult, world, spellContext);
            NeoForge.EVENT_BUS.post(modifierEvent);
            return spellStats;
        }

        public SpellStats build() {
            return spellStats;
        }

        public Builder setSensitive() {
            spellStats.sensitive = true;
            return this;
        }

        public Builder setDamageModifier(double damageModifier) {
            spellStats.damageModifier = damageModifier;
            return this;
        }

        public Builder addDamageModifier(double damageModifier) {
            spellStats.damageModifier += damageModifier;
            return this;
        }

        public Builder setAugments(List<AbstractAugment> augments) {
            spellStats.augments = augments;
            return this;
        }

        /**
         * Pulls all items from hands, curios, and armor.
         */
        public Builder addItemsFromEntity(@Nullable LivingEntity entity) {
            if (entity == null)
                return this;
            var handler = CuriosUtil.getAllWornItems(entity);
            if(handler != null){
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack item = handler.getStackInSlot(i);
                    spellStats.modifierItems.add(item);
                }
            }
            for (ItemStack i : entity.getAllSlots()) {
                spellStats.modifierItems.add(i);
            }

            return this;
        }

        public Builder addAugment(AbstractAugment abstractAugment) {
            spellStats.augments.add(abstractAugment);
            return this;
        }

        public Builder setAmplification(double power) {
            spellStats.amplification = power;
            return this;
        }

        public Builder addAmplification(double amplification) {
            spellStats.amplification += amplification;
            return this;
        }

        public Builder setAOE(double aoe) {
            spellStats.aoeMultiplier = aoe;
            return this;
        }

        public Builder addAOE(double aoe) {
            spellStats.aoeMultiplier += aoe;
            return this;
        }

        @Deprecated(forRemoval = true)
        public Builder setDurationModifier(double duration) {
            spellStats.durationMultiplier = duration;
            return this;
        }

        public Builder addDurationModifier(double duration) {
            spellStats.durationMultiplier += duration;
            return this;
        }

        public Builder setAccelerationModifier(float acceleration) {
            spellStats.acceleration = acceleration;
            return this;
        }

        public Builder addAccelerationModifier(float acceleration) {
            spellStats.acceleration += acceleration;
            return this;
        }

        public Builder setItems(List<ItemStack> items) {
            spellStats.modifierItems = items;
            return this;
        }

        public Builder addItem(ItemStack itemStack) {
            spellStats.modifierItems.add(itemStack);
            return this;
        }

        public Builder randomize() {
            spellStats.randomizing = true;
            return this;
        }
    }

}
