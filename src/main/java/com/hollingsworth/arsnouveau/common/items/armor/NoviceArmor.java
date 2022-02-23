package com.hollingsworth.arsnouveau.common.items.armor;

import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;

public class NoviceArmor extends GeoArmorItem implements IAnimatable, IManaEquipment {

    public NoviceArmor(EquipmentSlot slot) {
        super(Materials.novice, slot, ItemsRegistry.defaultItemProperties());
    }

    @Override
    public int getMaxManaBoost(ItemStack i) {
        return 25;
    }

    @Override
    public int getManaRegenBonus(ItemStack i) {
        return 2;
    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
