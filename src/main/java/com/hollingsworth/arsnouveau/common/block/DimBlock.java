package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.DimTile;
import com.hollingsworth.arsnouveau.common.world.dimension.VoidChunkGenerator;
import net.commoble.infiniverse.internal.DimensionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class DimBlock extends TickableModBlock{

    public DimBlock(Properties properties) {
        super(properties);
    }

    public DimBlock(){
        super(defaultProperties().noOcclusion());
    }

    public static LevelStem createDimension(MinecraftServer server)
    {
        return new LevelStem(getDimensionTypeHolder(server), new VoidChunkGenerator(server));
    }

    public static Holder<DimensionType> getDimensionTypeHolder(MinecraftServer server)
    {
        return server.registryAccess() // get dynamic registries
                .registryOrThrow(Registries.DIMENSION_TYPE)
                .getHolderOrThrow(ArsNouveau.DIMENSION_TYPE_KEY);
    }
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level instanceof ServerLevel serverLevel) {
            var key = ResourceKey.create(Registries.DIMENSION, ArsNouveau.prefix( "test"));
            var newLevel = DimensionManager.INSTANCE.getOrCreateLevel(serverLevel.getServer(), key, () ->{
                return createDimension(serverLevel.getServer());
            });
            player.teleportTo(newLevel, 7, 2, 7, Set.of(), player.getYRot(), player.getXRot());
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DimTile(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
