package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EnchantedFallingBlock extends FallingBlockEntity {
    public EnchantedFallingBlock(EntityType<? extends FallingBlockEntity> p_31950_, Level p_31951_) {
        super(p_31950_, p_31951_);
    }

    public EnchantedFallingBlock(Level world, double v, double y, double v1, BlockState blockState) {
        this(ModEntities.FALLING_BLOCK, world);
        this.blockState = blockState;
        this.blocksBuilding = true;
        this.setPos(v, y, v1);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = v;
        this.yo = y;
        this.zo = v1;
        this.setStartPos(this.blockPosition());
    }

    public EnchantedFallingBlock(Level world, BlockPos pos, BlockState blockState) {
        this(world, pos.getX(), pos.getY(), pos.getZ(), blockState);
    }

    public static EnchantedFallingBlock fall(Level p_201972_, BlockPos p_201973_, LivingEntity owner, SpellContext context) {
        BlockState blockState = p_201972_.getBlockState(p_201973_);
        EnchantedFallingBlock fallingblockentity = new EnchantedFallingBlock(p_201972_, p_201973_.getX() + 0.5D, p_201973_.getY(), p_201973_.getZ() + 0.5D, blockState.hasProperty(BlockStateProperties.WATERLOGGED) ? blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE) : blockState);
        p_201972_.setBlock(p_201973_, blockState.getFluidState().createLegacyBlock(), 3);
        p_201972_.addFreshEntity(fallingblockentity);
        return fallingblockentity;
    }


    @Override
    public EntityType<?> getType() {
        return ModEntities.FALLING_BLOCK;
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return super.canCollideWith(pEntity) && !(pEntity instanceof FallingBlockEntity);
    }

    @Override
    public boolean displayFireAnimation() {
        return this.isOnFire() && !this.isSpectator();
    }

    @Override
    public void tick() {
        if (this.blockState.isAir()) {
            this.discard();
            return;
        }
        Block block = this.blockState.getBlock();
        ++this.time;
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        if (!this.level.isClientSide) {
            BlockPos blockpos = this.blockPosition();
            boolean isConcrete = this.blockState.getBlock() instanceof ConcretePowderBlock;
            boolean isConcreteInWater = isConcrete && this.level.getFluidState(blockpos).is(FluidTags.WATER);
            double d0 = this.getDeltaMovement().lengthSqr();
            if (isConcrete && d0 > 1.0D) { // if we are concrete powder
                BlockHitResult blockhitresult = this.level.clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
                if (blockhitresult.getType() != HitResult.Type.MISS && this.level.getFluidState(blockhitresult.getBlockPos()).is(FluidTags.WATER)) {
                    blockpos = blockhitresult.getBlockPos();
                    isConcreteInWater = true;
                }
            }

            if (!this.onGround && !isConcreteInWater) {
                if (!this.level.isClientSide && (this.time > 100 && (blockpos.getY() <= this.level.getMinBuildHeight() || blockpos.getY() > this.level.getMaxBuildHeight()) || this.time > 600)) {
                    if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        this.spawnAtLocation(block);
                    }

                    this.discard();
                }
            } else { // on ground
                BlockState blockstate = this.level.getBlockState(blockpos);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
                if (!blockstate.is(Blocks.MOVING_PISTON)) {
                    if (!this.cancelDrop) {
                        boolean flag2 = blockstate.canBeReplaced(new DirectionalPlaceContext(this.level, blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                        boolean flag3 = FallingBlock.isFree(this.level.getBlockState(blockpos.below())) && (!isConcrete || !isConcreteInWater);
                        boolean flag4 = this.blockState.canSurvive(this.level, blockpos) && !flag3;
                        if (flag2 && flag4) {
                            if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level.getFluidState(blockpos).getType() == Fluids.WATER) {
                                this.blockState = this.blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true));
                            }

                            if (this.level.setBlock(blockpos, this.blockState, 3)) {
                                ((ServerLevel)this.level).getChunkSource().chunkMap.broadcast(this, new ClientboundBlockUpdatePacket(blockpos, this.level.getBlockState(blockpos)));
                                this.discard();
                                if (block instanceof Fallable) {
                                    ((Fallable)block).onLand(this.level, blockpos, this.blockState, blockstate, this);
                                }

                                if (this.blockData != null && this.blockState.hasBlockEntity()) {
                                    BlockEntity blockentity = this.level.getBlockEntity(blockpos);
                                    if (blockentity != null) {
                                        CompoundTag compoundtag = blockentity.saveWithoutMetadata();

                                        for(String s : this.blockData.getAllKeys()) {
                                            compoundtag.put(s, this.blockData.get(s).copy());
                                        }

                                        try {
                                            blockentity.load(compoundtag);
                                        } catch (Exception exception) {}

                                        blockentity.setChanged();
                                    }
                                }
                            } else if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.discard();
                                this.callOnBrokenAfterFall(block, blockpos);
                                this.spawnAtLocation(block);
                            }
                        } else {
                            this.discard();
                            if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.callOnBrokenAfterFall(block, blockpos);
                                this.spawnAtLocation(block);
                            }
                        }
                    } else {
                        this.discard();
                        this.callOnBrokenAfterFall(block, blockpos);
                    }
                }
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));

    }



}
