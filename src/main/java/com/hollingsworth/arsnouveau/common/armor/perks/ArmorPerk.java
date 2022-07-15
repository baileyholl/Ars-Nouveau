package com.hollingsworth.arsnouveau.common.armor.perks;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.print.attribute.Attribute;
import java.util.Set;
import java.util.UUID;


public class ArmorPerk extends ForgeRegistryEntry<ArmorPerk>
{

    private final ResourceLocation key;
    private final Set<ResourceLocation> incompat;
    private boolean adverse;
    private String transKey = null;
    private IAttributeProvider provider;
    private int cost = 1;


    public ArmorPerk(ResourceLocation key)
    {
        this.key = key;
        this.adverse = false;
        this.incompat = Sets.newHashSet();
    }

    public int getCost() {
        return 1;
    }

    public ArmorPerk withAttributeModifier(IAttributeProvider provider)
    {
        this.provider = provider;
        return this;
    }

    public ArmorPerk withCost(int cost)
    {
        this.cost = cost;
        return this;
    }
    public interface IAttributeProvider
    {
        void handleAttributes(Perks perks, Multimap<Attribute, AttributeModifier> modifiers, UUID uuid, ArmorPerk perk);
    }
}
