package com.hollingsworth.arsnouveau.api.client;

public interface IVariantColorProvider<T> extends IVariantTextureProvider<T>{

    @Deprecated(forRemoval = true)
    default String getColor(){
        return "";
    }
    @Deprecated(forRemoval = true)
    void setColor(String color);

    default void setColor(String color, T object){
        setColor(color);
    }

    default String getColor(T object){
        return getColor();
    }
}
