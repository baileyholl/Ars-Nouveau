package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.ScryerCrystal;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ScryerCrystalTile extends ModdedTile implements ITickable, ICameraMountable {
    public double cameraRotation = 0.0D;
    public boolean addToRotation = true;
    public boolean down = false;
    public boolean downSet = false;
    public int playersViewing = 0;
    double rotationSpeed = 0.018D;
    boolean shouldRotate = true;
//
//    private Option.DoubleOption rotationSpeedOption = new Option.DoubleOption(this::getBlockPos, "rotationSpeed", 0.018D, 0.01D, 0.025D, 0.001D, true);
//    private Option.BooleanOption shouldRotateOption = new Option.BooleanOption("shouldRotate", true);
//    private Option.DoubleOption customRotationOption;

    public ScryerCrystalTile(BlockPos pos, BlockState state) {
        this(BlockRegistry.SCRYER_CRYSTAL_TILE, pos, state);
    }

    public ScryerCrystalTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
//        this.customRotationOption = new Option.DoubleOption(this::getBlockPos, "customRotation", this.cameraRotation, 1.55D, -1.55D, (Double)this.rotationSpeedOption.get(), true);
    }


    @Override
    public void tick() {
        BlockState state = getBlockState();
        if (!this.downSet) {
            this.down = state.getValue(ScryerCrystal.FACING) == Direction.DOWN;
            this.downSet = true;
        }

        if (!shouldRotate) {
//            this.cameraRotation = (Double)this.customRotationOption.get();
        } else {
            if (this.addToRotation && this.cameraRotation <= 1.5499999523162842D) {
                this.cameraRotation += rotationSpeed;
            } else {
                this.addToRotation = false;
            }

            if (!this.addToRotation && this.cameraRotation >= -1.5499999523162842D) {
                this.cameraRotation -= rotationSpeed;
            } else {
                this.addToRotation = true;
            }

        }
    }

    @Override
    public void mountCamera(Level level, BlockPos pos, Player player) {
        ICameraMountable.super.mountCamera(level, pos, player);
    }

    @Override
    public void startViewing() {
        if (this.playersViewing++ >= 0) {
            this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(ScryerCrystal.BEING_VIEWED, true));
        }
    }

    @Override
    public void stopViewing() {
        if (--this.playersViewing <= 0) {
            this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(ScryerCrystal.BEING_VIEWED, false));
        }
    }
}
