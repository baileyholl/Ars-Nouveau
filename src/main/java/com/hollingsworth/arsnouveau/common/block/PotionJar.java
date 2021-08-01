package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.AbstractBlock.Properties;

public class PotionJar extends ModBlock{
    public PotionJar(Properties properties, String registry) {
        super(properties, registry);
    }

    public PotionJar(String registry){
        super(registry);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
        PotionJarTile tile = (PotionJarTile) worldIn.getBlockEntity(pos);
        if (tile == null || tile.getCurrentFill() <= 0) return 0;
        int step = (tile.getMaxFill() - 1) / 14;
        return (tile.getCurrentFill() - 1) / step + 1;
    }


    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(worldIn.isClientSide)
            return ActionResultType.SUCCESS;

        PotionJarTile tile = (PotionJarTile) worldIn.getBlockEntity(pos);
        if(tile == null)
            return ActionResultType.SUCCESS;
        ItemStack stack = player.getItemInHand(handIn);
        Potion potion = PotionUtils.getPotion(stack);

        if(stack.getItem() == Items.POTION && potion != Potions.EMPTY ) {
            if (tile.getAmount() == 0) {

                tile.setPotion(stack);
                tile.addAmount(100);
                if(!player.isCreative()) {
                    player.addItem(new ItemStack(Items.GLASS_BOTTLE));
                    stack.shrink(1);
                }

            }else if(tile.isMixEqual(stack) && tile.getCurrentFill() < tile.getMaxFill()){

                tile.addAmount(100);
                if(!player.isCreative()) {
                    player.addItem(new ItemStack(Items.GLASS_BOTTLE));
                    stack.shrink(1);
                }

            }
            worldIn.sendBlockUpdated(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);
        }

        if(stack.getItem() == Items.GLASS_BOTTLE && tile.getCurrentFill() >= 100){
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(potionStack, tile.getPotion());
            PotionUtils.setCustomEffects(potionStack, tile.getCustomEffects());
            player.addItem(potionStack);
            player.getItemInHand(handIn).shrink(1);
            tile.addAmount(-100);
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<net.minecraft.block.Block, BlockState> builder) { builder.add(ManaJar.fill); }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PotionJarTile();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if(stack.getTag() == null)
            return;
        int mana = stack.getTag().getCompound("BlockEntityTag").getInt("amount");
        tooltip.add( new StringTextComponent((mana*100) / 10000 + "% full"));
        ItemStack stack1 = new ItemStack(Items.POTION);
        stack1.setTag(stack.getTag().getCompound("BlockEntityTag"));
        PotionUtils.addPotionTooltip(stack1, tooltip, 1.0F);
    }
}
