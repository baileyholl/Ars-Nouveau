package com.hollingsworth.arsnouveau.api.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Comparator;

public class StructureComparator implements Comparator<StructureTemplate.StructureBlockInfo> {

    BlockPos targetPos;
    BlockPos offset;

    public StructureComparator(BlockPos targetPos, BlockPos offset) {
        this.targetPos = targetPos;
        this.offset = offset;
    }

    @Override
    public int compare(StructureTemplate.StructureBlockInfo o1, StructureTemplate.StructureBlockInfo o2) {
        BlockPos pos1 = targetPos.offset(o1.pos().getX(), o1.pos().getY(), o1.pos().getZ()).offset(offset);
        BlockPos pos2 = targetPos.offset(o2.pos().getX(), o2.pos().getY(), o2.pos().getZ()).offset(offset);
        double aDistFromMid = targetPos.distToCenterSqr(pos1.getX(), pos1.getY(), pos1.getZ());
        double bDistFromMid = targetPos.distToCenterSqr(pos2.getX(), pos2.getY(), pos2.getZ());
        int c = Double.compare(o1.pos().getY(), o2.pos().getY());
        if (c == 0) {
            c = Double.compare(aDistFromMid, bDistFromMid);
        }
        return c;
    }
}
