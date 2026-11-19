package com.hollingsworth.arsnouveau.api.source;

import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public record SourceAccess(BlockPos pos, ISourceCap cap) {

    public static @Nullable SourceAccess at(Level level, BlockPos pos, @Nullable Direction side) {
        if (!level.isLoaded(pos)) {
            return null;
        }
        pos = pos.immutable();
        ISourceCap cap = level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, pos, side);
        if (cap == null && level.getBlockEntity(pos) instanceof AbstractSourceMachine machine) {
            cap = machine.getSourceStorage();
        }
        return cap == null ? null : new SourceAccess(pos, cap);
    }
}
