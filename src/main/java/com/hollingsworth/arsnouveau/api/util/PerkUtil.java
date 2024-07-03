package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PerkUtil {

    public static @Nullable IPerkHolder getPerkHolder(ItemStack stack){
        return stack.get(DataComponentRegistry.ARMOR_PERKS);
    }

    public static double perkValue(LivingEntity entity, Holder<Attribute> attribute){
        AttributeInstance instance = entity.getAttribute(attribute);
        return instance == null ? attribute.value().getDefaultValue() : instance.getValue();
    }

    public static double valueOrZero(LivingEntity entity, Holder<Attribute> attribute){
        return entity.getAttribute(attribute) == null ? 0 : entity.getAttributeValue(attribute);
    }

    public static List<PerkItem> getPerksAsItems(ItemStack stack){
        ArmorPerkHolder holder = stack.get(DataComponentRegistry.ARMOR_PERKS);
        List<PerkItem> perkItems = new ArrayList<>();
        if(holder == null){
            return perkItems;
        }
        for(IPerk perk : holder.getPerks()){
            PerkItem item = PerkRegistry.getPerkItemMap().get(perk.getRegistryName());
            if(item != null){
                perkItems.add(item);
            }
        }
        return perkItems;
    }

    public static List<PerkInstance> getPerksFromItem(ItemStack stack){
        List<PerkInstance> perkInstances = new ArrayList<>();
        var data = stack.get(DataComponentRegistry.ARMOR_PERKS);
        if(data == null){
            return perkInstances;
        }
        perkInstances.addAll(data.getPerkInstances(stack));
        return perkInstances;
    }

    public static List<PerkInstance> getPerksFromLiving(LivingEntity player){
        List<PerkInstance> perkInstances = new ArrayList<>();
        for(ItemStack stack : player.getArmorSlots()){
            perkInstances.addAll(getPerksFromItem(stack));
        }
        return perkInstances;
    }

    public static int countForPerk(IPerk perk, LivingEntity entity){
        int maxCount = 0;
        for(ItemStack stack : entity.getArmorSlots()){
            var data = stack.get(DataComponentRegistry.ARMOR_PERKS);
            if(data == null){
                continue;
            }
            for(PerkInstance instance : data.getPerkInstances(stack)){
                if(instance.getPerk() == perk){
                    maxCount = Math.max(maxCount, instance.getSlot().value());
                }
            }
        }
        return maxCount;
    }

    public static @Nullable ArmorPerkHolder getHolderForPerk(IPerk perk, LivingEntity entity){
        ArmorPerkHolder highestHolder = null;
        int maxCount = 0;
        for(ItemStack stack : entity.getArmorSlots()){
            var data = stack.get(DataComponentRegistry.ARMOR_PERKS);
            if(data == null){
                continue;
            }
            for(PerkInstance instance : data.getPerkInstances(stack)){
                if(instance.getPerk() == perk){
                    maxCount = Math.max(maxCount, instance.getSlot().value());
                    highestHolder = data;
                }
            }
        }
        return highestHolder;
    }
}
