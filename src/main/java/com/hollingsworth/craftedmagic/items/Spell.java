package com.hollingsworth.craftedmagic.items;

import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.api.CraftedMagicAPI;
import com.hollingsworth.craftedmagic.api.Position;
import com.hollingsworth.craftedmagic.api.mana.IMana;
import com.hollingsworth.craftedmagic.capability.ManaCapability;
import com.hollingsworth.craftedmagic.network.Networking;
import com.hollingsworth.craftedmagic.network.PacketOpenGUI;
import com.hollingsworth.craftedmagic.spell.SpellResolver;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Spell extends Item {
    public static final String BOOK_MODE_TAG = "mode";



    public Spell(){
        super(new Item.Properties().maxStackSize(1).group(ExampleMod.itemGroup));
        setRegistryName("spell_book");        // The unique name (within your mod) that identifies this item

        //setUnlocalizedName(ExampleMod.MODID + ".spell_book");     // Used for localization (en_US.lang)
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!worldIn.isRemote && worldIn.getGameTime() % 20 == 0 && !stack.hasTag()) {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt(Spell.BOOK_MODE_TAG, 0);
            stack.setTag(tag);
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }


    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if(!playerIn.getEntityWorld().isRemote) {
            System.out.println("Touched Entity");

            ArrayList<AbstractSpellPart> spell_r = getCurrentRecipe(stack);
            if(!spell_r.isEmpty()) {
                SpellResolver resolver = new SpellResolver(spell_r);
                resolver.onCastOnEntity(stack, playerIn, target, hand);
            }
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isRemote()) {
            System.out.println("Right clicked");
            ManaCapability.getMana(playerIn).ifPresent(mana -> {
                System.out.println(mana.getCurrentMana());
                System.out.println(mana.addMana(50));
            });
        }
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(worldIn.isRemote || !stack.hasTag()){
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }

        if(getMode(stack.getTag()) == 0 && !playerIn.isSneaking() && playerIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerIn;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->player), new PacketOpenGUI(stack.getTag()));
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        if (playerIn.isSneaking() && stack.hasTag()){
            changeMode(stack);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }

        ArrayList<AbstractSpellPart> spell_r = getCurrentRecipe(stack);
        if(!spell_r.isEmpty()) {
            SpellResolver resolver = new SpellResolver(spell_r);
            resolver.onCast(stack, playerIn, worldIn);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
    }

    /*
    Called on block use
     */
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        System.out.println("Spell used");
        World worldIn = context.getWorld();

        PlayerEntity playerIn = context.getPlayer();
        Hand handIn = context.getHand();
        BlockPos blockpos = context.getPos();
        BlockPos blockpos1 = blockpos.offset(context.getFace());
        ItemStack stack = playerIn.getHeldItem(handIn);

        if(worldIn.isRemote || !stack.hasTag() || getMode(stack.getTag()) == 0 || playerIn.isSneaking()) return ActionResultType.PASS;

        ArrayList<AbstractSpellPart> spell_r = getCurrentRecipe(stack);
        if(!spell_r.isEmpty()){

            SpellResolver resolver = new SpellResolver(spell_r);
            resolver.onCastOnBlock(context);

            SoundEvent event = new SoundEvent(new ResourceLocation(ExampleMod.MODID, "cast_spell"));
            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, event, SoundCategory.BLOCKS,
                    4.0F, (1.0F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.2F) * 0.7F);
        }

        return ActionResultType.PASS;
    }

    public ArrayList<AbstractSpellPart> getCurrentRecipe(ItemStack stack){
        return Spell.getRecipeFromTag(stack.getTag(), getMode(stack.getTag()));
    }


    private void changeMode(ItemStack stack) {
        setMode(stack, (getMode(stack.getTag()) + 1) % 4);
    }

    public static ArrayList<AbstractSpellPart> getRecipeFromTag(CompoundNBT tag, int r_slot){
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        String recipeStr = getRecipeString(tag, r_slot);
        if (recipeStr.length() <= 3) // Account for empty strings and '[,]'
            return recipe;
        String[] recipeList = recipeStr.substring(1, recipeStr.length() - 1).split(",");
        for(String id : recipeList){
            if (CraftedMagicAPI.getInstance().spell_map.containsKey(id.trim()))
                recipe.add(CraftedMagicAPI.getInstance().spell_map.get(id.trim()));
        }
        return recipe;
    }

    public static void setSpellName(CompoundNBT tag, String name, int slot){
        tag.putString(slot + "_name", name);
    }

    public static String getSpellName(CompoundNBT tag, int slot){
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
        return tag.getInt(Spell.BOOK_MODE_TAG);
    }

    public void setMode(ItemStack stack, int mode){
        stack.getTag().putInt(Spell.BOOK_MODE_TAG, mode);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        if(stack != null && stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            tooltip.add(new StringTextComponent(Spell.getSpellName(stack.getTag())));
        }
    }
}
