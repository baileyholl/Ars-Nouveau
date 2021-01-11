package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.renderer.item.SpellBookRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.IntangibleAirTile;
import com.hollingsworth.arsnouveau.common.block.tile.PhantomBlockTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOpenSpellBook;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class SpellBook extends Item implements ISpellTier, IScribeable, IDisplayMana {

    public static final String BOOK_MODE_TAG = "mode";
    public static final String UNLOCKED_SPELLS = "spells";
    public static final int SEGMENTS = 10;
    public Tier tier;


    public SpellBook(Tier tier){
        super(new Item.Properties().maxStackSize(1).group(ArsNouveau.itemGroup).setISTER(() -> SpellBookRenderer::new));
        this.tier = tier;
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!stack.hasTag())
            stack.setTag(new CompoundNBT());

        if(!worldIn.isRemote && worldIn.getGameTime() % 5 == 0 && !stack.hasTag()) {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt(SpellBook.BOOK_MODE_TAG, 0);
            StringBuilder starting_spells = new StringBuilder();

            if(stack.getItem() == ItemsRegistry.creativeSpellBook){
                ArsNouveauAPI.getInstance().getSpell_map().values().forEach(s -> starting_spells.append(",").append(s.getTag().trim()));
            }else{
                ArsNouveauAPI.getInstance().getDefaultStartingSpells().forEach(s-> starting_spells.append(",").append(s.getTag().trim()));
            }
            tag.putString(SpellBook.UNLOCKED_SPELLS, starting_spells.toString());
            stack.setTag(tag);
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(!stack.hasTag())
            return new ActionResult<>(ActionResultType.SUCCESS, stack);

        ManaCapability.getMana(playerIn).ifPresent(iMana -> {
            if(iMana.getBookTier() < this.tier.ordinal()){
                iMana.setBookTier(this.tier.ordinal());
            }
            if(iMana.getGlyphBonus() < SpellBook.getUnlockedSpells(stack.getTag()).size()){
                iMana.setGlyphBonus(SpellBook.getUnlockedSpells(stack.getTag()).size());
            }
        });

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


        if(worldIn.isRemote || !stack.hasTag()){
            return new ActionResult<>(ActionResultType.CONSUME, stack);
        }
        // Crafting mode
        if(getMode(stack.getTag()) == 0 && playerIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerIn;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->player), new PacketOpenSpellBook(stack.getTag(), getTier().ordinal(), getUnlockedSpellString(player.getHeldItem(handIn).getTag())));
            return new ActionResult<>(ActionResultType.CONSUME, stack);
        }
        SpellResolver resolver = new SpellResolver(new SpellContext(getCurrentRecipe(stack), playerIn)
                .withColors(SpellBook.getSpellColor(stack.getTag(), SpellBook.getMode(stack.getTag()))));
        EntityRayTraceResult entityRes = MathUtil.getLookedAtEntity(playerIn, 25);

        if(entityRes != null && entityRes.getEntity() instanceof LivingEntity){
            resolver.onCastOnEntity(stack, playerIn, (LivingEntity) entityRes.getEntity(), handIn);
            return new ActionResult<>(ActionResultType.CONSUME, stack);
        }

        if(result.getType() == RayTraceResult.Type.BLOCK){
            ItemUseContext context = new ItemUseContext(playerIn, handIn, (BlockRayTraceResult) result);
            resolver.onCastOnBlock(context);
            return new ActionResult<>(ActionResultType.CONSUME, stack);
        }

        resolver.onCast(stack,playerIn,worldIn);
        return new ActionResult<>(ActionResultType.CONSUME, stack);
    }


    @Override
    public boolean onScribe(World world, BlockPos pos, PlayerEntity player, Hand handIn, ItemStack stack) {
        if(!(player.getHeldItem(handIn).getItem() instanceof SpellBook))
            return false;

        List<AbstractSpellPart> spellParts = SpellBook.getUnlockedSpells(player.getHeldItem(handIn).getTag());
        int unlocked = 0;
        for(AbstractSpellPart spellPart : spellParts){
            if(SpellBook.unlockSpell(stack.getTag(), spellPart))
                unlocked++;
        }
        PortUtil.sendMessage(player, new StringTextComponent("Copied " + unlocked + " new spells to the book."));
        return true;
    }


    /**
     * How long it takes to use or consume an item
     */
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }


    public Spell getCurrentRecipe(ItemStack stack){
        return SpellBook.getRecipeFromTag(stack.getTag(), getMode(stack.getTag()));
    }

    public static Spell getRecipeFromTag(CompoundNBT tag, int r_slot){
        String recipeStr = getRecipeString(tag, r_slot);
        return Spell.deserialize(recipeStr);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }

    public static void setSpellName(CompoundNBT tag, String name, int slot){
        tag.putString(slot + "_name", name);
    }

    public static String getSpellName(CompoundNBT tag, int slot){
        if(slot == 0)
            return "Create Mode";
        return tag.getString( slot+ "_name");
    }

    public static void setSpellColor(CompoundNBT tag, ParticleColor.IntWrapper color, int slot){
        tag.putString(slot + "_color", color.serialize());
    }

    public static ParticleColor.IntWrapper getSpellColor(CompoundNBT tag, int slot){
        String key = slot+ "_color";
        if(!tag.contains(key))
            return new ParticleColor.IntWrapper(255, 25, 180);

        return ParticleColor.IntWrapper.deserialize(tag.getString(key));
    }

    public static String getSpellName(CompoundNBT tag){
        return getSpellName( tag, getMode(tag));
    }

    public static String getRecipeString(CompoundNBT tag, int spell_slot){
        return tag.getString(spell_slot + "recipe");
    }

    public static void setRecipe(CompoundNBT tag, String recipe, int spell_slot){
        tag.putString(spell_slot + "recipe", recipe);
    }

    public static int getMode(CompoundNBT tag){
        return tag.getInt(SpellBook.BOOK_MODE_TAG);
    }

    public static void setMode(CompoundNBT tag, int mode){
        tag.putInt(SpellBook.BOOK_MODE_TAG, mode);
    }

    public static List<AbstractSpellPart> getUnlockedSpells(CompoundNBT tag){
        return SpellRecipeUtil.getSpellsFromString(tag.getString(SpellBook.UNLOCKED_SPELLS));
    }

    public static String getUnlockedSpellString(CompoundNBT tag){
        return tag.getString(SpellBook.UNLOCKED_SPELLS);
    }

    public static boolean unlockSpell(CompoundNBT tag, AbstractSpellPart spellPart){
        if(containsSpell(tag, spellPart))
            return false;
        String newSpells = tag.getString(SpellBook.UNLOCKED_SPELLS) + "," + spellPart.getTag();
        tag.putString(SpellBook.UNLOCKED_SPELLS, newSpells);
        return true;
    }

    public static void unlockSpell(CompoundNBT tag, String spellTag){
        String newSpells = tag.getString(SpellBook.UNLOCKED_SPELLS) + "," + spellTag;
        tag.putString(SpellBook.UNLOCKED_SPELLS, newSpells);
    }

    public static boolean containsSpell(CompoundNBT tag, AbstractSpellPart spellPart){
        return SpellBook.getUnlockedSpells(tag).contains(spellPart);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        if(stack != null && stack.hasTag()) {
            tooltip.add(new StringTextComponent(SpellBook.getSpellName(stack.getTag())));

            tooltip.add(new StringTextComponent("Press " + KeyBinding.getDisplayString(ModKeyBindings.OPEN_SPELL_SELECTION.getKeyBinding().getKeyDescription()).get().getString()+ " to quick select"));
            tooltip.add(new StringTextComponent("Press " + KeyBinding.getDisplayString(ModKeyBindings.OPEN_BOOK.getKeyBinding().getKeyDescription()).get().getString() + " to quick craft"));
        }
    }

    @Override
    public Tier getTier() {
        return this.tier;
    }

}
