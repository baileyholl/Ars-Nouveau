package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
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
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if(shooter != null){
            Vector3d origLoc = shooter.position;
            if(isNotFakePlayer(shooter)) {
                shooter.teleportTo(entity.getX(), entity.getY(), entity.getZ());
            }
            if(entity instanceof LivingEntity && isNotFakePlayer((LivingEntity)entity)) {
                entity.teleportTo(origLoc.x(), origLoc.y(), origLoc.z());
            }
        }
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult result, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, result.getBlockPos(), result,  spellStats.getBuffCount(AugmentAOE.INSTANCE),  spellStats.getBuffCount(AugmentPierce.INSTANCE));
        BlockState origState = world.getBlockState(result.getBlockPos());
        PlayerEntity playerEntity = getPlayer(shooter, (ServerWorld) world);
        List<ItemStack> list = playerEntity.inventory.items;
        List<IItemHandler> handlers = new ArrayList<>();

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
                    || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, pos1)){
                continue;
            }
            if(isRealPlayer(shooter) && spellContext.castingTile == null) {
                firstBlock = swapFromInv(list, origState, world, pos1, result, shooter, 9, firstBlock);
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
                            if(attemptPlace(stack, world, pos1, result, shooter)) {
                                shouldBreak = true;
                                break;
                            }
                        }
                    }
                    if(shouldBreak)
                        break;
                }

            }
        }
    }

    public Block swapFromInv(List<ItemStack> inventory, BlockState origState, World world, BlockPos pos1, BlockRayTraceResult result, LivingEntity shooter, int slots, Block firstBlock){
        for(int i = 0; i < slots; i++){
            ItemStack stack = inventory.get(i);
            if(stack.getItem() instanceof BlockItem && world instanceof ServerWorld){
                BlockItem item = (BlockItem)stack.getItem();
                if(item.getBlock() == origState.getBlock())
                    continue;
                if(firstBlock == null){
                    firstBlock = item.getBlock();
                }else if(item.getBlock() != firstBlock)
                    continue;
                if(attemptPlace(stack, world, pos1, result, shooter))
                    break;
            }
        }
        return firstBlock;
    }

    public boolean attemptPlace(ItemStack stack, World world, BlockPos pos1, BlockRayTraceResult result, LivingEntity shooter){
        BlockItem item = (BlockItem)stack.getItem();
        ItemStack tool = LootUtil.getDefaultFakeTool();
        tool.enchant(Enchantments.SILK_TOUCH, 1);
        FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((ServerWorld)world);
        fakePlayer.setItemInHand(Hand.MAIN_HAND, stack);
        BlockItemUseContext context = BlockItemUseContext.at(new BlockItemUseContext(new ItemUseContext(fakePlayer, Hand.MAIN_HAND, result)), pos1.relative(result.getDirection().getOpposite()), result.getDirection());
        BlockState placeState = item.getBlock().getStateForPlacement(context);
        Block.dropResources(world.getBlockState(pos1), world, pos1, world.getBlockEntity(pos1), shooter,tool);
        destroyBlockSafelyWithoutSound(world, pos1, false, shooter);

        if(placeState != null){
            item.place(context);
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
    public int getManaCost() {
        return 50;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
