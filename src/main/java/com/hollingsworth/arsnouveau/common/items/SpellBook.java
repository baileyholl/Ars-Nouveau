package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOpenGUI;
import com.hollingsworth.arsnouveau.common.spell.SpellResolver;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class SpellBook extends Item implements ISpellTier {
    public static final String BOOK_MODE_TAG = "mode";
    public static final String UNLOCKED_SPELLS = "spells";
    public static final int SEGMENTS = 10;
    public Tier tier;


    public SpellBook(Tier tier){
        super(new Item.Properties().maxStackSize(1).group(ArsNouveau.itemGroup));
        this.tier = tier;
        //setUnlocalizedName(ExampleMod.MODID + ".spell_book");     // Used for localization (en_US.lang)
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!stack.hasTag())
            stack.setTag(new CompoundNBT());
        if(!worldIn.isRemote && worldIn.getGameTime() % 5 == 0 && !stack.hasTag()) {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt(SpellBook.BOOK_MODE_TAG, 0);
            StringBuilder starting_spells = new StringBuilder();
            ArsNouveauAPI.getInstance().getStartingSpells().forEach(s-> starting_spells.append(",").append(s.getTag().trim()));
            tag.putString(SpellBook.UNLOCKED_SPELLS, starting_spells.toString());
            stack.setTag(tag);
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

        /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if(!playerIn.getEntityWorld().isRemote) {
            SpellResolver resolver = new SpellResolver(getCurrentRecipe(stack));
            resolver.onCastOnEntity(stack, playerIn, target, hand);

        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(worldIn.isRemote || !stack.hasTag()){
            //spawnParticles(playerIn.posX, playerIn.posY + 2, playerIn.posZ, worldIn);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }

        if(getMode(stack.getTag()) == 0 && playerIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerIn;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->player), new PacketOpenGUI(stack.getTag(), getTier().ordinal(), getUnlockedSpellString(player.getHeldItem(handIn).getTag())));
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        SpellResolver resolver = new SpellResolver(getCurrentRecipe(stack));
        resolver.onCast(stack, playerIn, worldIn);


        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    public static void spawnParticles(double posX, double posY, double posZ, World world){
        BlockPos pos = new BlockPos(posX, posY, posZ);
        VoxelShape shape = world.getBlockState(pos).getShape(world, pos);
        double yOffset = 0.0;
        yOffset = shape.isEmpty() ? yOffset : shape.getBoundingBox().maxY/2;
        for(int i =0; i < 5; i++) {
            double d0 = posX + world.rand.nextFloat();
            double d1 = posY + world.rand.nextFloat() + yOffset;
            double d2 = posZ + world.rand.nextFloat();
            world.addParticle(ParticleTypes.POOF, d0, d1, d2, 0.0, 0.1, 0.0);

        }
    }
    /*
    Called on block use. TOUCH ONLY
     */
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {

        World worldIn = context.getWorld();
        PlayerEntity playerIn = context.getPlayer();
        Hand handIn = context.getHand();
        BlockPos blockpos = context.getPos();
        BlockPos blockpos1 = blockpos.offset(context.getFace());
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(getMode(stack.getTag()) == 0 && playerIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerIn;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->player), new PacketOpenGUI(stack.getTag(), getTier().ordinal(),getUnlockedSpellString(context.getItem().getTag()) ));
            return ActionResultType.SUCCESS;
        }
        if(worldIn.isRemote || !stack.hasTag() || getMode(stack.getTag()) == 0 ) {
          //  spawnParticles(blockpos.getX(), blockpos.getY(), blockpos.getZ(), worldIn);
            return ActionResultType.FAIL;
        }
        SpellResolver resolver = new SpellResolver(getCurrentRecipe(stack));
        resolver.onCastOnBlock(context);

        return ActionResultType.SUCCESS;
    }

    public ArrayList<AbstractSpellPart> getCurrentRecipe(ItemStack stack){
        return SpellBook.getRecipeFromTag(stack.getTag(), getMode(stack.getTag()));
    }

    public static ArrayList<AbstractSpellPart> getRecipeFromTag(CompoundNBT tag, int r_slot){
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        String recipeStr = getRecipeString(tag, r_slot);
        if (recipeStr.length() <= 3) // Account for empty strings and '[,]'
            return recipe;
        String[] recipeList = recipeStr.substring(1, recipeStr.length() - 1).split(",");
        for(String id : recipeList){
            if (ArsNouveauAPI.getInstance().getSpell_map().containsKey(id.trim()))
                recipe.add(ArsNouveauAPI.getInstance().getSpell_map().get(id.trim()));
        }
        return recipe;
    }



    public static void setSpellName(CompoundNBT tag, String name, int slot){
        tag.putString(slot + "_name", name);
    }

    public static String getSpellName(CompoundNBT tag, int slot){
        if(slot == 0)
            return "Create Mode";
        return tag.getString( slot+ "_name");
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

    public static ArrayList<AbstractSpellPart> getUnlockedSpells(CompoundNBT tag){
        return SpellRecipeUtil.getSpellsFromString(tag.getString(SpellBook.UNLOCKED_SPELLS));
    }

    public static String getUnlockedSpellString(CompoundNBT tag){
        return tag.getString(SpellBook.UNLOCKED_SPELLS);
    }

    public static void unlockSpell(CompoundNBT tag, String spellTag){
        String newSpells = tag.getString(SpellBook.UNLOCKED_SPELLS) + "," + spellTag;
        tag.putString(SpellBook.UNLOCKED_SPELLS, newSpells);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        if(stack != null && stack.hasTag()) {
            tooltip.add(new StringTextComponent(SpellBook.getSpellName(stack.getTag())));
            tooltip.add(new StringTextComponent("Press " + ModKeyBindings.OPEN_SPELL_SELECTION.getKeyBinding().getLocalizedName() + " to quick select"));
            tooltip.add(new StringTextComponent("Press " + ModKeyBindings.OPEN_BOOK.getKeyBinding().getLocalizedName() + " to quick craft"));

        }
    }

    @Override
    public Tier getTier() {
        return this.tier;
    }


}
