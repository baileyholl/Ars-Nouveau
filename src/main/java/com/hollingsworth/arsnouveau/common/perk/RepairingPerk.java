package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class RepairingPerk extends Perk {

    public static final RepairingPerk INSTANCE = new RepairingPerk(ArsNouveau.prefix( "thread_repairing"));
    public static final UUID PERK_UUID = UUID.fromString("e2a7e5bc-ab34-4ea2-b3b6-ef23d352fa47");

    public RepairingPerk(ResourceLocation key) {
        super(key);
    }

    public static void attemptRepair(ItemStack stack, Player entity){
        if(entity.level.getGameTime() % 200 != 0 || stack.getDamageValue() <= 0)
            return;
        double repairLevel = PerkUtil.countForPerk(RepairingPerk.INSTANCE, entity);
        CapabilityRegistry.getMana(entity).ifPresent(mana -> {
            if (mana.getCurrentMana() > 20) {
                mana.removeMana(20);
                stack.setDamageValue(stack.getDamageValue() - Math.min(stack.getDamageValue(), (int)repairLevel + 1));
            }
        });
    }

    @Override
    public String getLangName() {
        return "Repairing";
    }

    @Override
    public String getLangDescription() {
        return "Allows the wearer to repair ANY magical armor or enchanters item by consuming Mana over time. Additional levels increase the speed at which the items repair. This perk applies to all relevant items, not only the item with this perk.";
    }
}
