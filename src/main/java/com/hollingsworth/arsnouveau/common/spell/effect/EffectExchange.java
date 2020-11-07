package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.util.BlockUtil.destroyBlockSafelyWithoutSound;

public class EffectExchange extends AbstractEffect {
    public EffectExchange() {
        super(ModConfig.EffectExchangeID, "Exchange");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof BlockRayTraceResult){
            resolveBlockHit(rayTraceResult, world, shooter, augments,spellContext);
        }else if(rayTraceResult instanceof EntityRayTraceResult){
            resolveEntityHit(rayTraceResult, world, shooter, augments, spellContext);
        }
    }

    public void resolveEntityHit(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext){
        EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
        Entity entity = entityRayTraceResult.getEntity();
        if(shooter instanceof PlayerEntity){
            BlockPos origLoc = shooter.getPosition();
            shooter.setPositionAndUpdate(entity.getPosX(), entity.getPosY(), entity.getPosZ());
            entity.setPositionAndUpdate(origLoc.getX(), origLoc.getY(), origLoc.getZ());
        }
    }

    public void resolveBlockHit(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext){
        if(shooter instanceof PlayerEntity){
            int aoeBuff = getBuffCount(augments, AugmentAOE.class);
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, ((BlockRayTraceResult) rayTraceResult).getPos(), (BlockRayTraceResult)rayTraceResult,1 + aoeBuff, 1 + aoeBuff, 1, -1);
            BlockRayTraceResult result = (BlockRayTraceResult) rayTraceResult;
            double maxHardness = getHardness(augments);
            BlockState origState = world.getBlockState(result.getPos());
            Block firstBlock = null;
            for(BlockPos pos1 : posList) {
                BlockState state = world.getBlockState(pos1);
                if(!(state.getBlockHardness(world, pos1) <= maxHardness && state.getBlockHardness(world, pos1) >= 0) || origState.getBlock() != state.getBlock()){
                    continue;
                }

                // Break block
                ItemStack tool = LootUtil.getDefaultFakeTool();
                tool.addEnchantment(Enchantments.SILK_TOUCH, 1);

                PlayerEntity playerEntity = (PlayerEntity) shooter;
                NonNullList<ItemStack> list =  playerEntity.inventory.mainInventory;
                for(int i = 0; i < 9; i++){
                    ItemStack stack = list.get(i);
                    if(stack.getItem() instanceof BlockItem && world instanceof ServerWorld){
                        BlockItem item = (BlockItem)stack.getItem();
                        if(item.getBlock() == origState.getBlock())
                            continue;
                        if(firstBlock == null){
                            firstBlock = item.getBlock();
                        }else if(item.getBlock() != firstBlock)
                            continue;
                        FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((ServerWorld)world);
                        fakePlayer.setHeldItem(Hand.MAIN_HAND, stack);
                        BlockItemUseContext context = BlockItemUseContext.func_221536_a(new BlockItemUseContext(new ItemUseContext(fakePlayer, Hand.MAIN_HAND, result)), pos1, result.getFace());
                        BlockState placeState = item.getBlock().getStateForPlacement(context);
                        Block.spawnDrops(world.getBlockState(pos1), world, pos1, world.getTileEntity(pos1), shooter,tool);
                        destroyBlockSafelyWithoutSound(world, pos1, false, shooter);

                        if(placeState != null && world.getBlockState(pos1).getMaterial() == Material.AIR && world.getBlockState(pos1).getBlock() != BlockRegistry.INTANGIBLE_AIR){

                            world.setBlockState(pos1, placeState, 2);
                            stack.shrink(1);
                            break;
                        }
                    }
                }

            }
            return;
        }
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    protected String getBookDescription() {
        return "When used on blocks, exchanges the blocks in the players hotbar for the blocks hit as if they were mined with silk touch. Can be augmented with AOE, and Amplify is required for swapping blocks of higher hardness. "
        + "When used on entities, the locations of the caster and the entity hit are swapped.";
    }

    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.AugmentExtractID);
    }

    @Override
    public int getManaCost() {
        return 50;
    }
}
