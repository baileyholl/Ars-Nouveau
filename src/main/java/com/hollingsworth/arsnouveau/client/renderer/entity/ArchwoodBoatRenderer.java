package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ArchwoodBoat;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ArchwoodBoatRenderer extends BoatRenderer {

    public ArchwoodBoatRenderer(EntityRendererProvider.Context renderContext, boolean isChestBoat) {
        super(renderContext, isChestBoat);
    }

    @Override
    public @NotNull Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(@NotNull Boat boat) {
        if (boat instanceof ArchwoodBoat archwoodBoat) {
            Pair<ResourceLocation, ListModel<Boat>> vanillaPair = super.getModelWithLocation(boat);

            ResourceLocation customTexture = ArsNouveau.prefix("textures/entity/boat/" + archwoodBoat.getArchwoodVariant().getName() + ".png");

            return Pair.of(customTexture, vanillaPair.getSecond());
        }

        return super.getModelWithLocation(boat);
    }
}
