package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.function.Supplier;

public class GenericRenderer<T extends BlockEntity & GeoAnimatable> extends GeoBlockRenderer<T> {


    public GenericRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, String loc) {
        super(new GenericModel(loc));
    }

    public static Supplier<BlockEntityWithoutLevelRenderer> getISTER(String loc) {
        return () -> new GenericItemBlockRenderer(new GenericModel(loc));
    }
}
