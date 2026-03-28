package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ANPlayerData implements ValueIOSerializable {
    public Set<AbstractSpellPart> glyphs = new HashSet<>();

    public Set<FamiliarData> familiars = new HashSet<>();

    public Identifier lastSummonedFamiliar;

    @Override
    public void serialize(ValueOutput output) {
        List<String> glyphIds = glyphs.stream().map(g -> g.getRegistryName().toString()).collect(Collectors.toList());
        output.store("glyphs", Codec.list(Codec.STRING), glyphIds);
        List<CompoundTag> familiarTags = familiars.stream().map(FamiliarData::toTag).collect(Collectors.toList());
        output.store("familiars", Codec.list(CompoundTag.CODEC), familiarTags);
        if (lastSummonedFamiliar != null) {
            output.putString("lastSummonedFamiliar", lastSummonedFamiliar.toString());
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        glyphs = new HashSet<>();
        familiars = new HashSet<>();
        input.read("glyphs", Codec.list(Codec.STRING)).ifPresent(ids -> {
            for (String id : ids) {
                AbstractSpellPart part = GlyphRegistry.getSpellPart(Identifier.parse(id));
                if (part != null) glyphs.add(part);
            }
        });
        input.read("familiars", Codec.list(CompoundTag.CODEC)).ifPresent(tags -> {
            for (CompoundTag tag : tags) familiars.add(new FamiliarData(tag));
        });
        input.getString("lastSummonedFamiliar").ifPresent(s -> lastSummonedFamiliar = Identifier.parse(s));
    }

    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        CompoundTag glyphsTag = new CompoundTag();
        List<AbstractSpellPart> glyphsList = glyphs.stream().toList();
        for (int i = 0; i < glyphsList.size(); i++) {
            glyphsTag.putString("glyph" + i, glyphsList.get(i).getRegistryName().toString());
        }
        glyphsTag.putInt("size", glyphsList.size());
        tag.put("glyphs", glyphsTag);

        CompoundTag familiarsTag = new CompoundTag();
        List<FamiliarData> familiarsList = familiars.stream().toList();
        for (int i = 0; i < familiarsList.size(); i++) {
            familiarsTag.put("familiar" + i, familiarsList.get(i).toTag());
        }
        familiarsTag.putInt("size", familiarsList.size());
        tag.put("familiars", familiarsTag);
        if (lastSummonedFamiliar != null) {
            tag.putString("lastSummonedFamiliar", lastSummonedFamiliar.toString());
        }
        return tag;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        glyphs = new HashSet<>();
        familiars = new HashSet<>();

        CompoundTag glyphsTag = nbt.getCompoundOrEmpty("glyphs");
        for (int i = 0; i < glyphsTag.getIntOr("size", 0); i++) {
            Identifier id = Identifier.parse(glyphsTag.getStringOr("glyph" + i, ""));
            AbstractSpellPart part = GlyphRegistry.getSpellPart(id);
            if (part != null)
                glyphs.add(part);
        }

        CompoundTag familiarsTag = nbt.getCompoundOrEmpty("familiars");
        for (int i = 0; i < familiarsTag.getIntOr("size", 0); i++) {
            familiarsTag.getCompound("familiar" + i).ifPresent(t -> familiars.add(new FamiliarData(t)));
        }
        if (nbt.contains("lastSummonedFamiliar")) {
            lastSummonedFamiliar = Identifier.parse(nbt.getStringOr("lastSummonedFamiliar", ""));
        }
    }
}
