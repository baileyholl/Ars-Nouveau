package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBreak;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GlyphRegistry {

    public static void registerSpell(AbstractSpellPart part) {
        Registry.registerForHolder(ANRegistries.GLYPH_ITEMS, part.getRegistryName(), part::getGlyph);

        //register the spell part's config in
        ModConfigSpec spec;
        ModConfigSpec.Builder spellBuilder = new ModConfigSpec.Builder();
        part.buildConfig(spellBuilder);
        spec = spellBuilder.build();
        part.CONFIG = spec;
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.SERVER, spec, part.getRegistryName().getNamespace() + "/" + part.getRegistryName().getPath() + ".toml");
        Registry.registerForHolder(ANRegistries.GLYPH_TYPES, part.getRegistryName(), part);
    }

    public static List<AbstractSpellPart> getDefaultStartingSpells() {
        return ANRegistries.GLYPH_TYPES.stream().filter(Config::isStarterEnabled).collect(Collectors.toList());
    }

    public static @Nullable AbstractSpellPart getSpellPart(ResourceLocation id) {
        return ANRegistries.GLYPH_TYPES.get(id);
    }

    public static @NotNull AbstractSpellPart getSpellPartOrDefault(ResourceLocation id) {
        AbstractSpellPart part = ANRegistries.GLYPH_TYPES.get(id);
        return part == null ? EffectBreak.INSTANCE : part;
    }
}
