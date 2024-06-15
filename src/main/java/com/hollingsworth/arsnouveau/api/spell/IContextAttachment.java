package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.resources.ResourceLocation;

import java.io.Serializable;

public interface IContextAttachment extends Serializable {

    ResourceLocation id();
}
