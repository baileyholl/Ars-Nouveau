package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.SpellSensorTile;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SpellSensor extends TickableModBlock{

    public SpellSensor(){
        super(defaultProperties());
    }

    public SpellSensor(Properties p_49795_) {
        super(p_49795_);
    }

    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        if(pBlockAccess.getBlockEntity(pPos) instanceof SpellSensorTile sensorTile && sensorTile.outputDuration > 0){
            return sensorTile.outputStrength;
        }
        return 0;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pLevel.isClientSide){
            return InteractionResult.SUCCESS;
        }
        ItemStack heldStack = pPlayer.getItemInHand(pHand);
        if(heldStack.getItem() instanceof SpellParchment){
            if(pLevel.getBlockEntity(pPos) instanceof SpellSensorTile sensorTile){
                sensorTile.parchment = heldStack.copy();
                sensorTile.updateBlock();
                pPlayer.sendSystemMessage(Component.translatable("ars_nouveau.sensor.set_spell"));
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);
        if(pLevel.getBlockEntity(pPos) instanceof SpellSensorTile sensorTile){
            sensorTile.onCooldown = false;
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SpellSensorTile(pPos, pState);
    }
}
