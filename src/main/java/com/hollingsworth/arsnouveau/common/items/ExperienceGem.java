package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.Map;

public abstract class ExperienceGem extends ModItem {

    public ExperienceGem(Properties properties) {
        super(properties);
    }

    public ExperienceGem() {
        super();
    }


    public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
        if (!world.isClientSide) {
            if (playerEntity.isCrouching()) {
                int val = getValue() * playerEntity.getItemInHand(hand).getCount();
                val = repairPlayerItems(playerEntity, val, val);
                if (val > 0)
                    playerEntity.giveExperiencePoints(val);
                playerEntity.getItemInHand(hand).shrink(playerEntity.getItemInHand(hand).getCount());
            } else {
                int val = getValue();
                val = repairPlayerItems(playerEntity, val, val);
                if (val > 0)
                    playerEntity.giveExperiencePoints(val);
                playerEntity.getItemInHand(hand).shrink(1);
            }

        }
        return InteractionResultHolder.pass(playerEntity.getItemInHand(hand));
    }

    public int repairPlayerItems(Player p_147093_, int remainingExp, int initialValue) {
        Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomItemWith(Enchantments.MENDING, p_147093_, ItemStack::isDamaged);
        if (entry != null) {
            ItemStack itemstack = entry.getValue();
            int i = Math.min((int) (initialValue * itemstack.getXpRepairRatio()), itemstack.getDamageValue());
            itemstack.setDamageValue(itemstack.getDamageValue() - i);
            int j = remainingExp - this.durabilityToXp(i);
            return j > 0 ? this.repairPlayerItems(p_147093_, j, initialValue) : 0;
        } else {
            return remainingExp;
        }
    }

    public int durabilityToXp(int pDurability) {
        return pDurability / 2;
    }


    public abstract int getValue();
}
