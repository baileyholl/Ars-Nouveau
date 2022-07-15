package com.hollingsworth.arsnouveau.common.armor.perks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.print.attribute.Attribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Perks {

    public static final int DEFAULT_BOOTS_POINTS = 3;
    public static final int DEFAULT_LEGS_POINTS = 3;
    public static final int DEFAULT_CHEST_POINTS = 3;
    public static final int DEFAULT_HELMET_POINTS = 3;


    protected final Map<ArmorPerk, Integer> perks;

    public Perks(Map<ArmorPerk, Integer> perks) {
        this.perks = perks;
    }

    public static Perks fromNBT(CompoundTag arsPerks) {
    }

    public int getPerkCount() {
        int count = 0;
        for(Map.Entry<ArmorPerk, Integer> perk : perks.entrySet())
        {
            count += (perk.getKey().getCost() * perk.getValue());
        }
        return count;
    }

    public Object getMaxPerks(EquipmentSlot equipmentSlot) {
    }

    public ImmutableMap<ArmorPerk, Integer> getPerks() {
        return ImmutableMap.copyOf(perks);
    }

    public static Perks fromPlayer(Player player)
    {
        Iterable<ItemStack> armor = player.getArmorSlots();
        Map<ArmorPerk, Integer> perks = new HashMap<>();
        for (ItemStack piece: armor) {
            if(piece.getItem() instanceof IPerkHolder)
            {
                perks.putAll(((IPerkHolder)piece.getItem()).getPerks(piece));
            }
        }



    }


}
