package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Unbreakable;

public class RepairingPerk extends Perk {

    public static final RepairingPerk INSTANCE = new RepairingPerk(ArsNouveau.prefix( "thread_repairing"));

    public RepairingPerk(ResourceLocation key) {
        super(key);
    }

    public static void attemptRepair(ItemStack stack, LivingEntity entity){
        if(entity.level.getGameTime() % 200 != 0 || stack.getDamageValue() <= 0)
            return;
        double repairLevel = PerkUtil.countForPerk(RepairingPerk.INSTANCE, entity);
        var cap = CapabilityRegistry.getMana(entity);
        if(cap != null){
            if(cap.getCurrentMana() < 20)
                return;
            cap.removeMana(20);
            stack.setDamageValue(stack.getDamageValue() - Math.min(stack.getDamageValue(), (int)repairLevel + Config.BASE_ARMOR_REPAIR_RATE.get()));
        }
    }

    @Override
    public void onAdded(LivingEntity entity) {
        double repairLevel = PerkUtil.countForPerk(RepairingPerk.INSTANCE, entity);
        if (repairLevel >= 3) {
            for (ItemStack slot : entity.getArmorSlots()) {
                slot.set(DataComponentRegistry.UNBREAKING, true);
            }
        }
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        for (ItemStack slot : entity.getArmorSlots()) {
            slot.remove(DataComponentRegistry.UNBREAKING);
        }
    }

    @Override
    public String getLangName() {
        return "Repairing";
    }

    @Override
    public String getLangDescription() {
        return "Allows the wearer to repair ANY magical armor or enchanters item by consuming Mana over time. Additional levels increase the speed at which the items repair. This perk applies to all relevant items, not only the item with this perk. When in a slot of level 3 or higher, it makes all equipped magical armor unbreakable.";
    }
}
