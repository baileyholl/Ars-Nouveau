package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectPlaceBlock extends AbstractEffect {
    public static EffectPlaceBlock INSTANCE = new EffectPlaceBlock();

    private EffectPlaceBlock() {
        super(GlyphLib.EffectPlaceBlockID, "Place Block");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats);
        BlockHitResult result = rayTraceResult;
        for(BlockPos pos1 : posList) {
            BlockPos hitPos = result.isInside() ? pos1 : pos1.relative(result.getDirection());
            if(spellContext.castingTile instanceof IPlaceBlockResponder){
                ItemStack stack = ((IPlaceBlockResponder) spellContext.castingTile).onPlaceBlock();
                if(stack.isEmpty() || !(stack.getItem() instanceof BlockItem))
                    return;

                BlockItem item = (BlockItem) stack.getItem();
                FakePlayer fakePlayer = ANFakePlayer.getPlayer((ServerLevel) world);
                fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);

                // Special offset for touch
                boolean isTouch = spellContext.getSpell().recipe.get(0) instanceof MethodTouch;
                BlockState blockTargetted = isTouch ? world.getBlockState(hitPos.relative(result.getDirection().getOpposite())) : world.getBlockState(hitPos.relative(result.getDirection()));
                if(blockTargetted.getMaterial() != Material.AIR)
                    continue;
                // Special offset because we are placing a block against the face we are looking at (in the case of touch)
                Direction direction = isTouch ? result.getDirection().getOpposite() : result.getDirection();
                BlockPlaceContext context = BlockPlaceContext.at(new BlockPlaceContext(new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND, result)),
                        hitPos.relative(direction), direction);
                item.place(context);
            }else if(shooter instanceof IPlaceBlockResponder){
                ItemStack stack = ((IPlaceBlockResponder) shooter).onPlaceBlock();
                if(stack.isEmpty() || !(stack.getItem() instanceof BlockItem))
                    return;
                BlockItem item = (BlockItem) stack.getItem();
                if(world.getBlockState(hitPos).getMaterial() != Material.AIR){
                    result = new BlockHitResult(result.getLocation().add(0, 1, 0), Direction.UP, result.getBlockPos(),false);
                }
                attemptPlace(world, stack, item, result);
            }else if(shooter instanceof Player){
                Player playerEntity = (Player) shooter;
                NonNullList<ItemStack> list =  playerEntity.inventory.items;
                if(!world.getBlockState(hitPos).getMaterial().isReplaceable())
                    continue;
                for(int i = 0; i < 9; i++){
                    ItemStack stack = list.get(i);
                    if(stack.getItem() instanceof BlockItem && world instanceof ServerLevel){
                        BlockItem item = (BlockItem)stack.getItem();

                        BlockHitResult resolveResult = new BlockHitResult(new Vec3(hitPos.getX(), hitPos.getY(), hitPos.getZ()), result.getDirection(), hitPos, false);
                        InteractionResult resultType = attemptPlace(world, stack, item, resolveResult);
                        if(InteractionResult.FAIL != resultType)
                            break;
                    }
                }
            }
        }
    }

    @Override
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        return nonAirBlockSuccess(rayTraceResult, world);
    }

    public static InteractionResult attemptPlace(Level world, ItemStack stack, BlockItem item, BlockHitResult result){
        FakePlayer fakePlayer = ANFakePlayer.getPlayer((ServerLevel) world);
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPlaceContext context = BlockPlaceContext.at(new BlockPlaceContext(new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND, result)), result.getBlockPos(), result.getDirection());
        return item.place(context);
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

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Places blocks from the casters inventory. If a player is casting this, this spell will place blocks from the hot bar first.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
