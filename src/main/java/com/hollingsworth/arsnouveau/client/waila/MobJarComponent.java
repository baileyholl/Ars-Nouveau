package com.hollingsworth.arsnouveau.client.waila;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.impl.EntityAccessorImpl;
import snownee.jade.impl.WailaClientRegistration;

public enum MobJarComponent implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockEntity be = blockAccessor.getBlockEntity();
        if (be instanceof MobJarTile tile) {
            var handler = WailaClientRegistration.instance().getAccessorHandler(EntityAccessor.class);
            var accessor = new EntityAccessorImpl.Builder()
                    .entity(tile.getEntity())
                    .level(blockAccessor.getLevel())
                    .serverConnected(blockAccessor.isServerConnected())
                    .showDetails(blockAccessor.showDetails())
                    .build();
            handler.gatherComponents(accessor, ($) -> tooltip);
            tooltip.remove(JadeIds.CORE_MOD_NAME);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ArsNouveau.prefix("mob_jar");
    }
}
