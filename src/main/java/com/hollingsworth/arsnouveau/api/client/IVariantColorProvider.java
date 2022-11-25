package com.hollingsworth.arsnouveau.api.client;

public interface IVariantColorProvider<T> extends IVariantTextureProvider<T> {

    @Deprecated(forRemoval = true)
    default String getColor() {
        return "";
    }

    @Deprecated(forRemoval = true)
    default void setColor(String color) {
        setColor(color, null);
    }

    void setColor(String color, T object);

    default String getColor(T object) {
        return getColor();
    }
}
