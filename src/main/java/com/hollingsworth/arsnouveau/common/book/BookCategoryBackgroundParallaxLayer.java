/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BookCategoryBackgroundParallaxLayer {
    public static final Codec<BookCategoryBackgroundParallaxLayer> CODEC = RecordCodecBuilder.create((builder) ->
            builder.group(
                    ResourceLocation.CODEC.fieldOf("background").forGetter((overlay) -> overlay.background),
                    Codec.FLOAT.optionalFieldOf("speed", 0.5f).forGetter((overlay) -> overlay.speed),
                    Codec.FLOAT.optionalFieldOf("vanish_zoom", -1.0f).forGetter((overlay) -> overlay.vanishZoom)
            ).apply(builder, BookCategoryBackgroundParallaxLayer::new));

    /**
     * The texture to use for this layer.
     */
    protected ResourceLocation background;

    /**
     * The speed at which this layer moves.
     */
    protected float speed;

    /**
     * The zoom level at which this layer vanishes.
     */
    protected float vanishZoom;

    public BookCategoryBackgroundParallaxLayer(ResourceLocation background) {
        this(background, 0.5f, -1.0f);
    }

    public BookCategoryBackgroundParallaxLayer(ResourceLocation background, float speed, float vanishZoom) {
        this.background = background;
        this.speed = speed;
        this.vanishZoom = vanishZoom;
    }

    public static BookCategoryBackgroundParallaxLayer fromJson(JsonObject json) {
        return BookCategoryBackgroundParallaxLayer.CODEC.parse(JsonOps.INSTANCE, json).get().orThrow();
    }

    public static List<BookCategoryBackgroundParallaxLayer> fromJson(JsonArray json) {
        return StreamSupport.stream(json.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .map(BookCategoryBackgroundParallaxLayer::fromJson)
                .collect(Collectors.toList());
    }


    public static BookCategoryBackgroundParallaxLayer fromNetwork(FriendlyByteBuf buffer) {
        return buffer.readWithCodec(BookCategoryBackgroundParallaxLayer.CODEC);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeWithCodec(BookCategoryBackgroundParallaxLayer.CODEC, this);
    }

    public ResourceLocation getBackground() {
        return this.background;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getVanishZoom() {
        return this.vanishZoom;
    }

}
