package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ANPlayerDataCap implements IPlayerCap {

    public Set<AbstractSpellPart> glyphs = new HashSet<>();

    public Set<FamiliarData> familiars = new HashSet<>();

    public ResourceLocation lastSummonedFamiliar;

    public ANPlayerDataCap() {
    }

    @Override
    public Collection<AbstractSpellPart> getKnownGlyphs() {
        return glyphs;
    }

    @Override
    public void setKnownGlyphs(Collection<AbstractSpellPart> glyphs) {
        this.glyphs = new HashSet<>(glyphs);
    }

    @Override
    public boolean unlockGlyph(AbstractSpellPart spellPart) {
        return glyphs.add(spellPart);
    }

    @Override
    public boolean knowsGlyph(AbstractSpellPart spellPart) {
        return glyphs.contains(spellPart);
    }

    @Override
    public boolean unlockFamiliar(AbstractFamiliarHolder holderID) {
        return familiars.add(new FamiliarData(holderID.getRegistryName()));
    }

    @Override
    public boolean ownsFamiliar(AbstractFamiliarHolder holderID) {
        return familiars.stream().anyMatch(f -> f.familiarHolder.getRegistryName().equals(holderID.getRegistryName()));
    }

    @Override
    public Collection<FamiliarData> getUnlockedFamiliars() {
        return familiars;
    }

    @Override
    @Nullable
    public FamiliarData getFamiliarData(ResourceLocation id) {
        return this.familiars.stream().filter(f -> f.familiarHolder.getRegistryName().equals(id)).findFirst().orElse(null);
    }

    @Nullable
    @Override
    public FamiliarData getLastSummonedFamiliar() {
        return lastSummonedFamiliar == null ? null : getFamiliarData(lastSummonedFamiliar);
    }

    public void setLastSummonedFamiliar(ResourceLocation lastSummonedFamiliar) {
        this.lastSummonedFamiliar = lastSummonedFamiliar;
    }

    @Override
    public void setUnlockedFamiliars(Collection<FamiliarData> familiars) {
        this.familiars = new HashSet<>(familiars);
    }

    @Override
    public boolean removeFamiliar(AbstractFamiliarHolder holderID) {
        return this.familiars.removeIf(f -> f.familiarHolder.getRegistryName().equals(holderID.getRegistryName()));
    }

    @Override
    public CompoundTag serializeNBT() {
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
    public void deserializeNBT(CompoundTag nbt) {
        glyphs = new HashSet<>();
        familiars = new HashSet<>();

        CompoundTag glyphsTag = nbt.getCompound("glyphs");
        for (int i = 0; i < glyphsTag.getInt("size"); i++) {
            ResourceLocation id = new ResourceLocation(glyphsTag.getString("glyph" + i));
            AbstractSpellPart part = GlyphRegistry.getSpellPart(id);
            if (part != null)
                glyphs.add(part);
        }

        CompoundTag familiarsTag = nbt.getCompound("familiars");
        for (int i = 0; i < familiarsTag.getInt("size"); i++) {
            familiars.add(new FamiliarData(familiarsTag.getCompound("familiar" + i)));
        }
        if(nbt.contains("lastSummonedFamiliar")){
            lastSummonedFamiliar = new ResourceLocation(nbt.getString("lastSummonedFamiliar"));
        }
    }
}
