package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.IPlaceBlockResponder;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.List;

public class EffectPlaceBlock extends AbstractEffect {
    public EffectPlaceBlock() {
        super(ModConfig.EffectPlaceBlockID, "Place Block");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof BlockRayTraceResult){
            BlockRayTraceResult result = (BlockRayTraceResult) rayTraceResult;
            if(shooter instanceof PlayerEntity){
                PlayerEntity playerEntity = (PlayerEntity) shooter;
                NonNullList<ItemStack> list =  playerEntity.inventory.mainInventory;
                for(int i = 0; i < 9; i++){
                    ItemStack stack = list.get(i);

                    if(stack.getItem() instanceof BlockItem && world instanceof ServerWorld){
                        BlockItem item = (BlockItem)stack.getItem();
                        if( ActionResultType.SUCCESS == attemptPlace(world, stack, item, result))
                            break;
                    }
//                    System.out.println(list.get(i));
                }
            }else if(shooter instanceof IPlaceBlockResponder){
                ItemStack stack = ((IPlaceBlockResponder) shooter).onPlaceBlock();
                if(!(stack.getItem() instanceof BlockItem))
                    return;
                BlockItem item = (BlockItem) stack.getItem();
                if(world.getBlockState(result.getPos()).getMaterial() != Material.AIR){
                    result = new BlockRayTraceResult(result.getHitVec().add(0, 1, 0), Direction.UP, result.getPos(),false);
                }
                attemptPlace(world, stack, item, result);
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        return nonAirBlockSuccess(rayTraceResult, world);
    }

    public ActionResultType attemptPlace(World world, ItemStack stack, BlockItem item, BlockRayTraceResult result){
        FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((ServerWorld)world);
        fakePlayer.setHeldItem(Hand.MAIN_HAND, stack);
        BlockItemUseContext context = BlockItemUseContext.func_221536_a(new BlockItemUseContext(new ItemUseContext(fakePlayer, Hand.MAIN_HAND, result)), result.getPos(), result.getFace());

        return item.tryPlace(context);
    }

    @Override
    public int getManaCost() {
        return 10;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.DISPENSER;
    }

    @Override
    protected String getBookDescription() {
        return "Places blocks from the casters inventory. If a player is casting this, this spell will place blocks from the hot bar first.";
    }
}
