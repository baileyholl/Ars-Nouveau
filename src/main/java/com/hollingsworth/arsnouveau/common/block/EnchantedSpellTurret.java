package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.EnchantedTurretTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class EnchantedSpellTurret extends BasicSpellTurret {

    public EnchantedSpellTurret(Properties properties, String registry) {
        super(properties, registry);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public EnchantedSpellTurret() {
        super(defaultProperties().noOcclusion(), LibBlockNames.ENCHANTED_SPELL_TURRET);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnchantedTurretTile(pos, state);
    }

    public static final DirectionProperty FACING = DirectionalBlock.FACING;
}
