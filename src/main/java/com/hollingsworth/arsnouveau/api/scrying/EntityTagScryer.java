package com.hollingsworth.arsnouveau.api.scrying;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ScryRitualRecipe.EntityHighlight;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

public class EntityTagScryer implements IScryer {
    public static MapCodec<EntityTagScryer> CODEC = EntityHighlight.CODEC.xmap(highlight -> new EntityTagScryer(highlight.tag(), highlight.color()), scryer -> new EntityHighlight(scryer.entityTag, scryer.color));

    public static EntityTagScryer INSTANCE = new EntityTagScryer();
    private ParticleColor color;
    private TagKey<EntityType<?>> entityTag;

    public EntityTagScryer() {}

    public EntityTagScryer(TagKey<EntityType<?>> entityTag, ParticleColor color) {
        this.entityTag = entityTag;
        this.color = color;
    }

    @Override
    public ParticleColor getParticleColor() {
        return this.color;
    }

    @Override
    public boolean shouldRevealEntity(Entity entity, Player player) {
        return entity.getType().is(this.entityTag);
    }

    @Override
    public IScryer fromTag(CompoundTag tag) {
        return CODEC.codec().decode(NbtOps.INSTANCE, tag.get("Scryer"))
                .map(Pair::getFirst).result().orElseGet(EntityTagScryer::new);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag encode = CODEC.codec().encodeStart(NbtOps.INSTANCE, this).map(encoded -> {
            CompoundTag compound = new CompoundTag();
            compound.put("Scryer", encoded);
            return compound;
        }).result().orElse(tag);
        return IScryer.super.toTag(encode);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( "entity_tag_scryer");
    }

    @Override
    public boolean revealsBlocks() {
        return false;
    }

    @Override
    public boolean revealsEntities() {
        return true;
    }
}
