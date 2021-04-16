package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.common.items.RunicChalk;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class RuneBlock extends ModBlock{
    public RuneBlock() {
        super(defaultProperties().doesNotBlockMovement().notSolid().hardnessAndResistance(0f,0f), LibBlockNames.RUNE);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(handIn);

        if(!worldIn.isRemote && stack.getItem() instanceof RunicChalk){
            if(((RuneTile)worldIn.getTileEntity(pos)).isTemporary){
                ((RuneTile)worldIn.getTileEntity(pos)).isTemporary = false;
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.rune.setperm"));
                return ActionResultType.SUCCESS;
            }
        }
        if(!(stack.getItem() instanceof SpellParchment) || worldIn.isRemote)
            return ActionResultType.SUCCESS;
        List<AbstractSpellPart> recipe = SpellParchment.getSpellRecipe(stack);
        if(recipe == null || recipe.isEmpty())
            return ActionResultType.SUCCESS;

        if(!(recipe.get(0) instanceof MethodTouch)){
            PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.rune.touch"));
            return ActionResultType.SUCCESS;
        }
        ((RuneTile)worldIn.getTileEntity(pos)).setRecipe(recipe);
        PortUtil.sendMessage(player, "Spell set.");
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        super.tick(state, worldIn, pos, rand);
        List<Entity> entities = worldIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() +1, pos.getZ()).grow(1));
        if(!entities.isEmpty() && worldIn.getTileEntity(pos) instanceof RuneTile)
            ((RuneTile) worldIn.getTileEntity(pos)).castSpell(entities.get(0));
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityCollision(state, worldIn, pos, entityIn);
        if(worldIn.getTileEntity(pos) instanceof RuneTile)
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16D, 0.5D, 16D);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }


    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RuneTile();
    }

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }
}
