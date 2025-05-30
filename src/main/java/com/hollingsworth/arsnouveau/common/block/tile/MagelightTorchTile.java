package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.MagelightTorch;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.HitResult;

public class MagelightTorchTile extends SconceTile {
    private boolean horizontalFire;
    public MagelightTorchTile(BlockPos pPos, BlockState pBlockState) {
        super(BlockRegistry.MAGELIGHT_TORCH_TILE.get(), pPos, pBlockState);
    }

    @Override
    public void onLight(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats stats, SpellContext spellContext) {
        super.onLight(rayTraceResult, world, shooter, stats, spellContext);
    }

    @Override
    public void tick() {
        if (!level.isClientSide() || !lit)
            return;
        BlockPos pos = getBlockPos();
        double xzOffset = 0.15;
        BlockState state = getLevel().getBlockState(getBlockPos());
        boolean onFloor = state.hasProperty(MagelightTorch.FLOOR) && state.getValue(MagelightTorch.FLOOR);
        boolean onRoof = state.hasProperty(MagelightTorch.ROOF) && state.getValue(MagelightTorch.ROOF);
        double yOffset = onFloor ? 0.4 : 0.5;
        yOffset = onRoof ? 0.5 : yOffset;
        double xOffset = ParticleUtil.inRange(-xzOffset / 4, xzOffset / 4) + 0.5;
        double zOffset = ParticleUtil.inRange(-xzOffset / 4, xzOffset / 4) + 0.5;
        double centerX = pos.getX() + xOffset;
        double centerZ = pos.getZ() + zOffset;

        double xSpeedOffset = 0;
        double ySpeedOffset = ParticleUtil.inRange(0.0, 0.03f);
        double zSpeedOffset = 0;

        xSpeedOffset = ParticleUtil.inRange(-0.01f, 0.01f);
        zSpeedOffset = ParticleUtil.inRange(-0.01f, 0.01f);
        if(!onFloor && !onRoof && isHorizontalFire() && state.hasProperty(BlockStateProperties.FACING)){

            Direction facing = state.getValue(BlockStateProperties.FACING);
            switch(facing){
                case NORTH:
                    zSpeedOffset = ParticleUtil.inRange(-0.03f, 0.0f);
                    break;
                case SOUTH:
                    zSpeedOffset = ParticleUtil.inRange(0.0f, 0.03f);
                    break;
                case EAST:
                    xSpeedOffset = ParticleUtil.inRange(0.0f, 0.03f);
                    break;
                case WEST:
                    xSpeedOffset = ParticleUtil.inRange(-0.03f, 0.0f);
                    break;
            }
            ySpeedOffset = ParticleUtil.inRange(-0.01f, 0.01f);
        }

        ySpeedOffset = onRoof ? -ySpeedOffset : ySpeedOffset;
        ParticleColor nextColor = this.color.nextColor((int) level.getGameTime() * 10);
        for (int i = 0; i < 5; i++) {
            level.addAlwaysVisibleParticle(
                    GlowParticleData.createData(nextColor),
                    true,
                    centerX, pos.getY() + yOffset + ParticleUtil.inRange(-0.00, 0.1), centerZ,
                    xSpeedOffset, ySpeedOffset, zSpeedOffset);

        }

    }

    public void setHorizontalFire(boolean horizontalFire) {
        this.horizontalFire = horizontalFire;
        updateBlock();
    }

    public boolean isHorizontalFire() {
        return horizontalFire;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putBoolean("horizontalFire", horizontalFire);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        horizontalFire = compound.getBoolean("horizontalFire");
    }
}
