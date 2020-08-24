package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class ScribesBlock extends ModBlock{
    public ScribesBlock() {
        super(ModBlock.defaultProperties().notSolid(), LibBlockNames.SCRIBES_BLOCK);
    }
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;


    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(handIn != Hand.MAIN_HAND)
            return ActionResultType.PASS;
        System.out.println("Activated");
        if(!world.isRemote && world.getTileEntity(pos) instanceof ScribesTile && !player.isSneaking()) {
            ScribesTile tile = (ScribesTile) world.getTileEntity(pos);
            if (tile.stack != null && player.getHeldItem(handIn).isEmpty()) {
                ItemEntity item = new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), tile.stack);
                world.addEntity(item);
                tile.stack = null;
            } else if (!player.inventory.getCurrentItem().isEmpty()) {
                if(tile.stack != null){
                    ItemEntity item = new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), tile.stack);
                    world.addEntity(item);
                }

                tile.stack = player.inventory.decrStackSize(player.inventory.currentItem, 1);

            }
            world.notifyBlockUpdate(pos, state, state, 2);
        }
        if(!world.isRemote &&  world.getTileEntity(pos) instanceof ScribesTile && player.isSneaking()){
            ItemStack stack = ((ScribesTile) world.getTileEntity(pos)).stack;
            if(stack == null || !(player.getHeldItem(handIn).getItem() instanceof SpellBook))
                return ActionResultType.FAIL;

            if(stack.getItem() instanceof SpellBook){
                ArrayList<AbstractSpellPart> spellParts = SpellBook.getUnlockedSpells(player.getHeldItem(handIn).getTag());
                int unlocked = 0;
                for(AbstractSpellPart spellPart : spellParts){
                    if(SpellBook.unlockSpell(stack.getTag(), spellPart))
                        unlocked++;
                }
                player.sendMessage(new StringTextComponent("Copied " + unlocked + " new spells to the book."));
            }else if(stack.getItem() instanceof SpellParchment){
                if(SpellBook.getMode(player.getHeldItem(handIn).getTag()) == 0){
                    player.sendMessage(new StringTextComponent("Set your spell book to a spell."));
                    return ActionResultType.FAIL;
                }

                SpellParchment.setSpell(stack, SpellBook.getRecipeString(player.getHeldItem(Hand.MAIN_HAND).getTag(), SpellBook.getMode(player.getHeldItem(handIn).getTag())));
                player.sendMessage(new StringTextComponent("Spell inscribed."));
            }else{
                return ActionResultType.FAIL;
            }
        }

        return ActionResultType.SUCCESS;
    }


    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            world.setBlockState(pos, state.with(FACING, getFacingFromEntity(pos, entity)), 2);
        }
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        Vec3d vec = entity.getPositionVec();
        Direction direction = Direction.getFacingFromVector((float) (vec.x - clickedBlock.getX()), (float) (vec.y - clickedBlock.getY()), (float) (vec.z - clickedBlock.getZ()));
        if(direction == Direction.UP || direction == Direction.DOWN)
            direction = Direction.NORTH;
        return direction;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ScribesTile();
    }

}
