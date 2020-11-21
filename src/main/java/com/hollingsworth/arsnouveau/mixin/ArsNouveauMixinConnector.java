package com.hollingsworth.arsnouveau.mixin;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class ArsNouveauMixinConnector implements IMixinConnector {
    @Override
    public void connect() {
        Mixins.addConfiguration("arsnouveau.mixins.json");
    }
}
