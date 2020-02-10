package com.hollingsworth.craftedmagic.armor;

import com.hollingsworth.craftedmagic.capability.ManaCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class MagicArmor extends ArmorItem {

    public MagicArmor(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
//        if(world.isRemote || world.getGameTime() % 20 == 0)
//            return;
//        ManaCapability.getMana(player).ifPresent(mana ->{
//            mana.addMana(getRegenBonus());
//           // System.out.println(mana.getMaxMana());
//        });
    }

    public abstract int getMaxManaBonus();

    public abstract int getRegenBonus();
}
