package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.Identifier;

public class JumpHeightPerk extends Perk {

    public static final JumpHeightPerk INSTANCE = new JumpHeightPerk(ArsNouveau.prefix("thread_heights"));

    public JumpHeightPerk(Identifier key) {
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
