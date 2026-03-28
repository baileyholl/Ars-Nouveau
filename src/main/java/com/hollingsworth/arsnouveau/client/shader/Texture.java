package com.hollingsworth.arsnouveau.client.shader;

import net.minecraft.resources.Identifier;

/**
 * Basic tuple for a shader texture
 * Borrowed from <a href="https://github.com/jaredlll08/FunkyFrames/blob/1.19/common/src/main/java/com/blamejared/funkyframes/util/Texture.java">FunkyFrames</a>
 */
public record Texture(Identifier location, boolean blur, boolean mipmap) {

    public Texture(Identifier location, boolean blur) {

        this(location, blur, false);
    }

    public Texture(Identifier location) {

        this(location, false, false);
    }

}