package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

/**
 * A school that spell parts belong to.
 * NOTE: A spell being in a sub-school is also considered part of the parent school.
 */
public class SpellSchool {
    private final String id;
    private Set<SpellSchool> subSchools = new HashSet<>();
    private Set<AbstractSpellPart> spellParts = new HashSet<>();
    private DocAssets.BlitInfo docIcon;

    public SpellSchool(String id) {
        this(id, DocAssets.NA_ICON);
    }

    public SpellSchool(String id, DocAssets.BlitInfo docIcon) {
        this.id = id;
        this.docIcon = docIcon;
    }

    public boolean isPartOfSchool(AbstractSpellPart part) {
        if (getSpellParts().contains(part))
            return true;
        for (SpellSchool spellSchool : getSubSchools()) {
            if (spellSchool.getSpellParts().contains(part))
                return true;
        }
        return false;
    }

    public boolean addSpellPart(AbstractSpellPart spellPart) {
        return getSpellParts().add(spellPart);
    }

    public Component getTextComponent() {
        return Component.translatable("ars_nouveau.school." + getId());
    }

    public String getId() {
        return id;
    }

    public DocAssets.BlitInfo getIcon() {
        return docIcon;
    }

    public Set<SpellSchool> getSubSchools() {
        return subSchools;
    }

    public void setSubSchools(Set<SpellSchool> subSchools) {
        this.subSchools = subSchools;
    }

    public SpellSchool withSubSchool(SpellSchool spellSchool) {
        this.getSubSchools().add(spellSchool);
        return this;
    }

    public Set<AbstractSpellPart> getSpellParts() {
        return spellParts;
    }

    public void setSpellParts(Set<AbstractSpellPart> spellParts) {
        this.spellParts = spellParts;
    }

    public ResourceLocation getTexturePath() {
        return ResourceLocation.fromNamespaceAndPath("ars_nouveau", "textures/gui/schools/" + getId() + "_tooltip.png");
    }
}
