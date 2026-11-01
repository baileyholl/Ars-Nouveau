package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateDimTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class StructureProjectorTile extends PlanariumRenderingTile {

    public StructureProjectorTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.STRUCTURE_PROJECTOR_TILE, pos, state);
    }

    @Override
    public void setName(Component name) {
        if (level instanceof ServerLevel serverLevel) {
            if (name == null) {
                this.name = null;
                this.key = null;
                updateBlock();
            } else {
                setDimension(name.getString(), serverLevel);
            }
        }
    }

    @Override
    public @Nullable ServerLevel setDimension(String dimName, ServerLevel serverLevel) {
        Tuple<ServerLevel, PlanariumTile.DimManager.Entry> dimension = PlanariumTile.dimManager.getExistingPlanarium(dimName, serverLevel, worldPosition);
        if (dimension == null) {
            this.key = null;
            this.name = null;
            updateBlock();
            return null;
        }
        this.key = dimension.getA().dimension();
        this.name = Component.literal(dimName);
        var managerEntry = dimension.getB();
        if (managerEntry != null) {
            lastUpdated = managerEntry.lastUpdated();
            Networking.sendToNearbyClient(level, worldPosition, new PacketUpdateDimTile(worldPosition, managerEntry.template()));
        }
        updateBlock();
        return dimension.getA();
    }

    public boolean isPlanariumRendering(){
        return this.key != null;
    }
}
