package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ANPlayerDataCap implements IPlayerCap{

    public Set<AbstractSpellPart> glyphs = new HashSet<>();

    public Set<FamiliarData> familiars = new HashSet<>();

    public ANPlayerDataCap(){}

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
    public FamiliarData getFamiliarData(ResourceLocation id) {
        return this.familiars.stream().filter(f -> f.familiarHolder.id.equals(id)).findFirst().orElse(null);
    }

    @Override
    public void setUnlockedFamiliars(Collection<FamiliarData> familiars) {
        this.familiars = new HashSet<>(familiars);
    }

    @Override
    public boolean removeFamiliar(AbstractFamiliarHolder holderID) {
        return this.familiars.remove(holderID);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        NBTUtil.writeResourceLocations(tag, "glyph_", glyphs.stream().map(AbstractSpellPart::getRegistryName).collect(Collectors.toList()));
        for(FamiliarData f : familiars){
            tag.put("familiar_" + f.familiarHolder.id.toString(), f.toTag());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ArsNouveauAPI api = ArsNouveauAPI.getInstance();
        for(ResourceLocation s : NBTUtil.readResourceLocations(nbt, "glyph_")){
            if(api.getSpellpartMap().containsKey(s)){
                glyphs.add(api.getSpellpartMap().get(s));
            }
        }

        for(String s : nbt.getAllKeys()){
            if(s.contains("familiar_")){
                familiars.add(new FamiliarData(nbt.getCompound(s)));
            }
        }
    }

    public static ANPlayerDataCap deserialize(CompoundTag tag){
        ANPlayerDataCap cap = new ANPlayerDataCap();
        cap.deserializeNBT(tag);
        return cap;
    }
}
