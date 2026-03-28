package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public record TimelineOption(Identifier id,
                             TimelineEntryData entry,
                             ImmutableList<IParticleMotionType<?>> options) {

    public static Identifier SPAWN = ArsNouveau.prefix("spawn");
    public static Identifier TRAIL = ArsNouveau.prefix("trail");
    public static Identifier IMPACT = ArsNouveau.prefix("impact");
    public static Identifier FLAIR = ArsNouveau.prefix("flair");
    public static Identifier TICK = ArsNouveau.prefix("tick");


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
