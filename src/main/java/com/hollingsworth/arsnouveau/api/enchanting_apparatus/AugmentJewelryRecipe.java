package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.item.AbstractAugmentItem;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class AugmentJewelryRecipe extends EnchantingApparatusRecipe{
    public final Item starterItem; // Item for crafting from a plain old item to the first tier augment (dull trinket, ring, etc).
    public final AbstractAugmentItem augmentedItem; // Item that holds the augment data (augment ring, necklace, etc.)

    public AugmentJewelryRecipe(Item starterItem, AbstractAugmentItem augmentedItem){
        this.starterItem = starterItem;
        this.augmentedItem = augmentedItem;
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        if(reagent.getItem() == starterItem){
            if(pedestalItems.size() != 4)
                return false;
            Item counter = pedestalItems.get(0).getItem();
            for(ItemStack stack : pedestalItems){
                if(!(stack.getItem() instanceof Glyph) || stack.getItem() != counter)
                    return false;
            }
            return true;
        }else if(reagent.getItem() instanceof AbstractAugmentItem){
            if(!reagent.hasTag() || !reagent.getTag().contains(AbstractAugmentItem.LEVEL) || !reagent.getTag().contains(AbstractAugmentItem.AUGMENT))
                return false;
            int level = reagent.getTag().getInt(AbstractAugmentItem.LEVEL);
            Glyph glyph = ArsNouveauAPI.getInstance().getGlyphMap().get(reagent.getTag().getString(AbstractAugmentItem.AUGMENT));
            if(glyph == null || pedestalItems.size() != level * 2 + 4) // 6 for level 2, 8 for level 3
                return false;

            for(ItemStack stack : pedestalItems){
                if(stack.getItem() !=  glyph)
                    return false;
            }
            return true;
        }
        return false;
    }


    public int getRequiredGlyphs(ItemStack stack){
        return 1;
    }


    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        return null;
    }

    @Override
    public boolean consumesMana() {
        return true;
    }

    @Override
    public int manaCost() {
        return 0;
    }
}
