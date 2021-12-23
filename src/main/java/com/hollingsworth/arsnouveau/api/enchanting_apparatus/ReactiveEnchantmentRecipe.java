package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ReactiveEnchantmentRecipe extends EnchantmentRecipe{

    public ReactiveEnchantmentRecipe(ItemStack[] pedestalItems, int manaCost) {
        super(pedestalItems,  EnchantmentRegistry.REACTIVE_ENCHANTMENT, 1, manaCost);
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable Player player) {
        ItemStack parchment = getParchment(pedestalItems);
        return super.isMatch(pedestalItems, reagent, enchantingApparatusTile, player) && !parchment.isEmpty() && !CasterUtil.getCaster(parchment).getSpell().isEmpty();
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
        ItemStack resultStack = super.getResult(pedestalItems, reagent, enchantingApparatusTile);
        ItemStack parchment = getParchment(pedestalItems);
        ISpellCaster parchmentCaster = CasterUtil.getCaster(parchment);
        ReactiveCaster reactiveCaster = new ReactiveCaster(resultStack);
        reactiveCaster.setColor(parchmentCaster.getColor());
        reactiveCaster.setSpell(parchmentCaster.getSpell());
        return resultStack;
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(ArsNouveau.MODID, "reactive");
    }

}
