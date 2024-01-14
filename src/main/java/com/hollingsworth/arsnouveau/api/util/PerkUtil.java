package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkProvider;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PerkUtil {

    public static @Nullable IPerkHolder<ItemStack> getPerkHolder(ItemStack stack){
        IPerkProvider<ItemStack> holder = PerkRegistry.getPerkProvider(stack.getItem());
        return holder == null ? null : holder.getPerkHolder(stack);
    }

    public static double perkValue(LivingEntity entity, Attribute attribute){
        AttributeInstance instance = entity.getAttribute(attribute);
        return instance == null ? attribute.getDefaultValue() : instance.getValue();
    }

    public static double valueOrZero(LivingEntity entity, Attribute attribute){
        return entity.getAttribute(attribute) == null ? 0 : entity.getAttributeValue(attribute);
    }

    public static List<PerkItem> getPerksAsItems(ItemStack stack){
        IPerkHolder<ItemStack> holder = getPerkHolder(stack);
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
        IPerkHolder<ItemStack> holder = getPerkHolder(stack);
        if(holder == null)
            return perkInstances;
        perkInstances.addAll(holder.getPerkInstances());
        return perkInstances;
    }

    @Deprecated
    public static List<PerkInstance> getPerksFromPlayer(Player player){
        return getPerksFromLiving(player);
    }

    public static List<PerkInstance> getPerksFromLiving(LivingEntity player){
        List<PerkInstance> perkInstances = new ArrayList<>();
        for(ItemStack stack : player.getArmorSlots()){
            perkInstances.addAll(getPerksFromItem(stack));
        }
        return perkInstances;
    }

    public static int countForPerk(IPerk perk, LivingEntity player){
        int maxCount = 0;
        for(ItemStack stack : player.getArmorSlots()){
            IPerkHolder<ItemStack> holder = getPerkHolder(stack);
            if(holder == null)
                continue;
            for(PerkInstance instance : holder.getPerkInstances()){
                if(instance.getPerk() == perk){
                    maxCount = Math.max(maxCount, instance.getSlot().value);
                }
            }
        }
        return maxCount;
    }

    @Deprecated(forRemoval = true)
    public static int countForPerk(IPerk perk, Player player){
        return countForPerk(perk, (LivingEntity) player);
    }

    public static @Nullable IPerkHolder<ItemStack> getHolderForPerk(IPerk perk, LivingEntity entity){
        IPerkHolder<ItemStack> highestHolder = null;
        int maxCount = 0;
        for(ItemStack stack : entity.getArmorSlots()){
            IPerkHolder<ItemStack> holder = getPerkHolder(stack);
            if(holder == null)
                continue;
            for(PerkInstance instance : holder.getPerkInstances()){
                if(instance.getPerk() == perk){
                    maxCount = Math.max(maxCount, instance.getSlot().value);
                    highestHolder = holder;
                }
            }
        }
        return highestHolder;
    }

    @Deprecated(forRemoval = true)
    public static @Nullable IPerkHolder<ItemStack> getHolderForPerk(IPerk perk, Player player){
        return getHolderForPerk(perk, (LivingEntity) player);
    }
}
