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
        super(defaultProperties().noCollission().noOcclusion().strength(0f,0f), LibBlockNames.RUNE);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack stack = player.getItemInHand(handIn);

        if(!worldIn.isClientSide && stack.getItem() instanceof RunicChalk){
            if(((RuneTile)worldIn.getBlockEntity(pos)).isTemporary){
                ((RuneTile)worldIn.getBlockEntity(pos)).isTemporary = false;
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.rune.setperm"));
                return ActionResultType.SUCCESS;
            }
        }
        if(!(stack.getItem() instanceof SpellParchment) || worldIn.isClientSide)
            return ActionResultType.SUCCESS;
        List<AbstractSpellPart> recipe = SpellParchment.getSpellRecipe(stack);
        if(recipe == null || recipe.isEmpty())
            return ActionResultType.SUCCESS;

        if(!(recipe.get(0) instanceof MethodTouch)){
            PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.rune.touch"));
            return ActionResultType.SUCCESS;
        }
        ((RuneTile)worldIn.getBlockEntity(pos)).setRecipe(recipe);
        PortUtil.sendMessage(player, "Spell set.");
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        super.tick(state, worldIn, pos, rand);
        List<Entity> entities = worldIn.getEntitiesOfClass(Entity.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() +1, pos.getZ()).inflate(1));
        if(!entities.isEmpty() && worldIn.getBlockEntity(pos) instanceof RuneTile)
            ((RuneTile) worldIn.getBlockEntity(pos)).castSpell(entities.get(0));
    }

    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
        if(worldIn.getBlockEntity(pos) instanceof RuneTile)
            worldIn.getBlockTicks().scheduleTick(pos, this, 1);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return Block.box(0.0D, 0.0D, 0.0D, 16D, 0.5D, 16D);
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

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }
}
