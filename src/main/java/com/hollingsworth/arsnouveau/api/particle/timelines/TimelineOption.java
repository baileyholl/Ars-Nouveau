package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record TimelineOption(ResourceLocation id,
                             TimelineEntryData entry,
                             ImmutableList<IParticleMotionType<?>> options) {

    public static ResourceLocation SPAWN = ArsNouveau.prefix("spawn");
    public static ResourceLocation TRAIL = ArsNouveau.prefix("trail");
    public static ResourceLocation IMPACT = ArsNouveau.prefix("impact");
    public static ResourceLocation FLAIR = ArsNouveau.prefix("flair");
    public static ResourceLocation TICK = ArsNouveau.prefix("tick");


    public Component name() {
        return Component.translatable(id.getNamespace() + ".timeline." + id.getPath());
    }

    public Component description() {
        return Component.translatable(id.getNamespace() + ".timeline." + id.getPath() + ".description");
    }

    public Component tooltip() {
        return Component.translatable(id.getNamespace() + ".timeline." + id.getPath() + ".tooltip");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TimelineOption that = (TimelineOption) o;
        return Objects.equals(id, that.id) && Objects.equals(entry, that.entry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entry);
    }
}
