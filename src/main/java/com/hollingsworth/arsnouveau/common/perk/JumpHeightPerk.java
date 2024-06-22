package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class JumpHeightPerk extends Perk {

    public static final JumpHeightPerk INSTANCE = new JumpHeightPerk(ArsNouveau.prefix( "thread_heights"));
    public static final UUID PERK_UUID = UUID.fromString("e5f68a8c-589f-4dde-978d-b4c507a4485b");

    public JumpHeightPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public String getLangName() {
        return "Heights";
    }

    @Override
    public String getLangDescription() {
        return "Allows you to jump higher and increases how far you may fall before taking damage.";
    }
}
