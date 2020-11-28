package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IntangibleAirTile;
import com.hollingsworth.arsnouveau.common.block.tile.PhantomBlockTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.capability.CasterCapability;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Caster extends ModItem implements IScribeable {

    public Caster(Properties properties, String registry){
        super(properties, registry);
    }

    public Caster(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return CasterCapability.createProvider(new SpellCaster(stack));
    }

    @Override
    public boolean onScribe(World world, BlockPos pos, PlayerEntity player, Hand handIn, ItemStack stack) {
        ItemStack heldStack = player.getHeldItem(handIn);
        ISpellCaster caster = getCaster(stack);

        if(caster == null)
            return false;

        if(!((heldStack.getItem() instanceof SpellBook) || (heldStack.getItem() instanceof SpellParchment)) || heldStack.getTag() == null)
            return false;

        if(heldStack.getItem() instanceof SpellBook) {
            List<AbstractSpellPart> spellParts = SpellBook.getRecipeFromTag(heldStack.getTag(), SpellBook.getMode(heldStack.getTag()));
            CasterCapability.getCaster(stack).ifPresent(cas ->{
                cas.setSpell(new Spell(spellParts));
            });
            PortUtil.sendMessage(player, new StringTextComponent("Set spell"));
            return true;
        }else if(heldStack.getItem() instanceof SpellParchment){
            caster.setSpell(new Spell(SpellParchment.getSpellRecipe(heldStack)));
            return true;
        }
        return false;
    }

    public ISpellCaster getCaster(ItemStack stack){
        return CasterCapability.getCaster(stack).orElse(null);
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        ISpellCaster caster = getCaster(stack);

        if(caster == null || worldIn.isRemote)
            return super.onItemRightClick(worldIn, playerIn, handIn);

        RayTraceResult result = playerIn.pick(5, 0, false);
        if(result instanceof BlockRayTraceResult && worldIn.getTileEntity(((BlockRayTraceResult) result).getPos()) instanceof ScribesTile)
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        if(result instanceof BlockRayTraceResult && !playerIn.isSneaking()){
            if(worldIn.getTileEntity(((BlockRayTraceResult) result).getPos()) != null &&
                    !(worldIn.getTileEntity(((BlockRayTraceResult) result).getPos()) instanceof IntangibleAirTile
                            ||(worldIn.getTileEntity(((BlockRayTraceResult) result).getPos()) instanceof PhantomBlockTile))) {
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
            }
        }

        SpellResolver resolver = new SpellResolver(caster.getSpell().recipe, new SpellContext(caster.getSpell(), playerIn));
        EntityRayTraceResult entityRes = MathUtil.getLookedAtEntity(playerIn, 25);

        if(entityRes != null && entityRes.getEntity() instanceof LivingEntity){
            resolver.onCastOnEntity(stack, playerIn, (LivingEntity) entityRes.getEntity(), handIn);
            return new ActionResult<>(ActionResultType.CONSUME, stack);
        }

        if(result instanceof BlockRayTraceResult){
            ItemUseContext context = new ItemUseContext(playerIn, handIn, (BlockRayTraceResult) result);
            resolver.onCastOnBlock(context);
            return new ActionResult<>(ActionResultType.CONSUME, stack);
        }

        resolver.onCast(stack,playerIn,worldIn);
        return new ActionResult<>(ActionResultType.CONSUME, stack);
    }
}
