package com.hollingsworth.arsnouveau.client.shader;

import net.minecraft.resources.ResourceLocation;

/**
 * Basic tuple for a shader texture
 * Borrowed from <a href="https://github.com/jaredlll08/FunkyFrames/blob/1.19/common/src/main/java/com/blamejared/funkyframes/util/Texture.java">FunkyFrames</a>
 */
public record Texture(ResourceLocation location, boolean blur, boolean mipmap) {

    public Texture(ResourceLocation location, boolean blur) {

        this(location, blur, false);
    }

    public Texture(ResourceLocation location) {

        this(location, false, false);
    }

}