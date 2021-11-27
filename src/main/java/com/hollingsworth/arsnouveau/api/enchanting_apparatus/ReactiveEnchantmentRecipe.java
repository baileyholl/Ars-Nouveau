package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
//TODO: Make Reactive SpellCaster
public class ReactiveEnchantmentRecipe extends EnchantmentRecipe{

    public ReactiveEnchantmentRecipe(ItemStack[] pedestalItems, int manaCost) {
        super(pedestalItems,  EnchantmentRegistry.REACTIVE_ENCHANTMENT, 1, manaCost);
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable PlayerEntity player) {
        ItemStack parchment = getParchment(pedestalItems);
        return super.isMatch(pedestalItems, reagent, enchantingApparatusTile, player) && !parchment.isEmpty() && !SpellParchment.getSpell(parchment).isEmpty();
    }

    public static @Nonnull ItemStack getParchment(List<ItemStack> pedestalItems){
        for(ItemStack stack : pedestalItems){
            if(stack.getItem() instanceof SpellParchment){
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        ItemStack stack = super.getResult(pedestalItems, reagent, enchantingApparatusTile);
        CompoundNBT tag = stack.getOrCreateTag();
        ItemStack parchment = getParchment(pedestalItems);

        tag.putString("spell", SpellParchment.getSpell(parchment).serialize());
        tag.putString("spell_color", SpellCaster.deserialize(parchment).getColor().serialize());
        stack.setTag(tag);
        return stack;
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(ArsNouveau.MODID, "reactive");
    }

}
