package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ANPlayerData implements INBTSerializable<CompoundTag> {
    public Set<AbstractSpellPart> glyphs = new HashSet<>();

    public Set<FamiliarData> familiars = new HashSet<>();

    public ResourceLocation lastSummonedFamiliar;

    @Override
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
        if(lastSummonedFamiliar != null){
            tag.putString("lastSummonedFamiliar", lastSummonedFamiliar.toString());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        glyphs = new HashSet<>();
        familiars = new HashSet<>();

        CompoundTag glyphsTag = nbt.getCompound("glyphs");
        for (int i = 0; i < glyphsTag.getInt("size"); i++) {
            ResourceLocation id = ResourceLocation.parse(glyphsTag.getString("glyph" + i));
            AbstractSpellPart part = GlyphRegistry.getSpellPart(id);
            if (part != null)
                glyphs.add(part);
        }

        CompoundTag familiarsTag = nbt.getCompound("familiars");
        for (int i = 0; i < familiarsTag.getInt("size"); i++) {
            familiars.add(new FamiliarData(familiarsTag.getCompound("familiar" + i)));
        }
        if(nbt.contains("lastSummonedFamiliar")){
            lastSummonedFamiliar = ResourceLocation.parse(nbt.getString("lastSummonedFamiliar"));
        }
    }
}
