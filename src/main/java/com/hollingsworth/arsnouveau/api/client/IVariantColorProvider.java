package com.hollingsworth.arsnouveau.api.client;

public interface IVariantColorProvider<T> extends IVariantTextureProvider<T> {

    void setColor(String color, T object);

    default String getColor(T object) {
        return "";
    }
}
