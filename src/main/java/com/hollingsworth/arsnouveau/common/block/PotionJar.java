package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PotionJar extends TickableModBlock {
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
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        PotionJarTile tile = (PotionJarTile) worldIn.getBlockEntity(pos);
        if (tile == null || tile.getCurrentFill() <= 0) return 0;
        int step = (tile.getMaxFill() - 1) / 14;
        return (tile.getCurrentFill() - 1) / step + 1;
    }


    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(worldIn.isClientSide)
            return InteractionResult.SUCCESS;

        PotionJarTile tile = (PotionJarTile) worldIn.getBlockEntity(pos);
        if(tile == null)
            return InteractionResult.SUCCESS;
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
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) { builder.add(ManaJar.fill); }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PotionJarTile(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if(stack.getTag() == null)
            return;
        int mana = stack.getTag().getCompound("BlockEntityTag").getInt("amount");
        tooltip.add( new TextComponent((mana*100) / 10000 + "% full"));
        ItemStack stack1 = new ItemStack(Items.POTION);
        stack1.setTag(stack.getTag().getCompound("BlockEntityTag"));
        PotionUtils.addPotionTooltip(stack1, tooltip, 1.0F);
    }
}
