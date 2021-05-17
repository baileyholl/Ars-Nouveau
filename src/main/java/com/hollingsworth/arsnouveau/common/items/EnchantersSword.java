package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public class EnchantersSword extends SwordItem implements ICasterTool {
    public EnchantersSword(IItemTier iItemTier, int baseDamage, float baseAttackSpeed, Properties properties) {
        super(iItemTier, baseDamage, baseAttackSpeed, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack p_77644_1_, LivingEntity p_77644_2_, LivingEntity p_77644_3_) {
        return super.hurtEnemy(p_77644_1_, p_77644_2_, p_77644_3_);
    }
}
