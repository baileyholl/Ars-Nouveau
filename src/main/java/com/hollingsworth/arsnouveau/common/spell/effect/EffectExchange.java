package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.hollingsworth.arsnouveau.api.util.BlockUtil.destroyBlockSafelyWithoutSound;

public class EffectExchange extends AbstractEffect {
    public static EffectExchange INSTANCE = new EffectExchange();

    private EffectExchange() {
        super(GlyphLib.EffectExchangeID, "Exchange");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if(shooter != null){
            Vec3 origLoc = shooter.position;
            if(isNotFakePlayer(shooter)) {
                shooter.teleportTo(entity.getX(), entity.getY(), entity.getZ());
            }
            if(entity instanceof LivingEntity && isNotFakePlayer((LivingEntity)entity)) {
                entity.teleportTo(origLoc.x(), origLoc.y(), origLoc.z());
            }
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult result, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, result.getBlockPos(), result,  spellStats.getBuffCount(AugmentAOE.INSTANCE),  spellStats.getBuffCount(AugmentPierce.INSTANCE));
        BlockState origState = world.getBlockState(result.getBlockPos());
        Player playerEntity = getPlayer(shooter, (ServerLevel) world);
        List<ItemStack> list = playerEntity.inventory.items;
        List<IItemHandler> handlers = new ArrayList<>();
        ANFakePlayer fakePlayer = ANFakePlayer.getPlayer((ServerLevel) world);
        if(spellContext.castingTile instanceof IPlaceBlockResponder && spellContext.castingTile instanceof IPickupResponder) {
            handlers = ((IPlaceBlockResponder) spellContext.castingTile).getInventory();
        }

        if(shooter instanceof IPlaceBlockResponder && shooter instanceof IPickupResponder)
            handlers = ((IPlaceBlockResponder) shooter).getInventory();

        Block firstBlock = null;
        for(BlockPos pos1 : posList) {
            BlockState state = world.getBlockState(pos1);

            if(!canBlockBeHarvested(spellStats, world, pos1) || origState.getBlock() != state.getBlock() ||
                    world.getBlockState(pos1).getMaterial() != Material.AIR && world.getBlockState(pos1).getBlock() == BlockRegistry.INTANGIBLE_AIR
                    || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos1)){
                continue;
            }
            if(isRealPlayer(shooter) && spellContext.castingTile == null) {
                firstBlock = swapFromInv(list, origState, world, pos1, result, shooter, 9, firstBlock, (Player) fakePlayer);
            } else if((spellContext.castingTile instanceof IPlaceBlockResponder && spellContext.castingTile instanceof IPickupResponder) || (shooter instanceof IPlaceBlockResponder && shooter instanceof IPickupResponder)){
                boolean shouldBreak = false;
                for(IItemHandler i : handlers){
                    for(int slot = 0; slot < i.getSlots(); slot++){
                        ItemStack stack = i.getStackInSlot(slot);
                        if(stack.getItem() instanceof BlockItem){
                            BlockItem item = (BlockItem)stack.getItem();
                            if(item.getBlock() == origState.getBlock())
                                continue;
                            if(firstBlock == null){
                                firstBlock = item.getBlock();
                            }else if(item.getBlock() != firstBlock)
                                continue;
                            ItemStack extracted = i.extractItem(slot, 1, false);
                            if(attemptPlace(extracted, world, pos1, result, shooter, fakePlayer)) {
                                shouldBreak = true;
                                break;
                            }else{
                                i.insertItem(slot, extracted, false);
                            }
                        }
                    }
                    if(shouldBreak)
                        break;
                }

            }
        }
    }

    public Block swapFromInv(List<ItemStack> inventory, BlockState origState, Level world, BlockPos pos1, BlockHitResult result, LivingEntity shooter, int slots, Block firstBlock, Player fakePlayer){
        for(int i = 0; i < slots; i++){
            ItemStack stack = inventory.get(i);
            if(stack.getItem() instanceof BlockItem && world instanceof ServerLevel){
                BlockItem item = (BlockItem)stack.getItem();
                if(item.getBlock() == origState.getBlock())
                    continue;
                if(firstBlock == null){
                    firstBlock = item.getBlock();
                }else if(item.getBlock() != firstBlock)
                    continue;
                if(attemptPlace(stack, world, new BlockPos(pos1), result, shooter, fakePlayer))
                    break;
            }
        }
        return firstBlock;
    }

    public boolean attemptPlace(ItemStack stack, Level world, BlockPos pos1, BlockHitResult result, LivingEntity shooter, Player fakePlayer){
        BlockItem item = (BlockItem)stack.getItem();
        ItemStack tool = LootUtil.getDefaultFakeTool();
        tool.enchant(Enchantments.SILK_TOUCH, 1);
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPlaceContext context = BlockPlaceContext.at(new BlockPlaceContext(new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND, result)), pos1.relative(result.getDirection().getOpposite()), result.getDirection());
        BlockState placeState = item.getBlock().getStateForPlacement(context);
        Block.dropResources(world.getBlockState(pos1), world, pos1, world.getBlockEntity(pos1), shooter,tool);
        destroyBlockSafelyWithoutSound(world, pos1, false, shooter);
        if(placeState != null){
            world.setBlock(pos1, placeState, 3);
            item.getBlock().setPlacedBy(world, pos1, placeState, shooter, stack);
            BlockItem.updateCustomBlockEntityTag(world,
                    shooter instanceof Player ? (Player) shooter :
                            fakePlayer, pos1, stack);
            stack.shrink(1);
            return true;
        }
        return false;
    }


    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentPierce.INSTANCE,
                AugmentAOE.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "When used on blocks, exchanges the blocks in the players hotbar for the blocks hit as if they were mined with silk touch. Can be augmented with AOE, and Amplify is required for swapping blocks of higher hardness. "
        + "When used on entities, the locations of the caster and the entity hit are swapped.";
    }

    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentExtractID);
    }

    @Override
    public int getDefaultManaCost() {
        return 50;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
