package com.hollingsworth.craftedmagic.items;

import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.api.Position;
import com.hollingsworth.craftedmagic.spell.SpellResolver;
import com.hollingsworth.craftedmagic.spell.effect.EffectDig;
import com.hollingsworth.craftedmagic.spell.method.CastMethod;
import com.hollingsworth.craftedmagic.spell.effect.EffectType;
import com.hollingsworth.craftedmagic.spell.method.ModifierProjectile;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Spell extends Item {
    public static final String CAST_TYPE = "cast_type";
    public static final String SPELL_TYPE = "spell_type";
    public static final String BOOK_MODE_TAG = "mode";
    public CastMethod castMethod;
    public EffectType effectType;



    public Spell(){
        this.addPropertyOverride(new ResourceLocation("age"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            private int model;
            @SideOnly(Side.CLIENT)
            long lastUpdateTick;
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {


                if (entityIn == null && !stack.isOnItemFrame())
                {
                    return 0;
                }
                else {
                    boolean flag = entityIn != null;
                    Entity entity = (Entity) (flag ? entityIn : stack.getItemFrame());
                    if(model == 10){
                        model = 0;
                    }
                    if (worldIn == null) {
                        worldIn = entity.world;
                    }
                    if(worldIn.getWorldTime() != lastUpdateTick) {
                        if (worldIn.getWorldTime() % 10 == 0) {
                            model += 1;
                        }
                        lastUpdateTick = worldIn.getWorldTime();
                    }
                }
                return model;
            }
        });
        setRegistryName("spell_book");        // The unique name (within your mod) that identifies this item
        setUnlocalizedName(ExampleMod.MODID + ".spell_book");     // Used for localization (en_US.lang)
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!worldIn.isRemote && worldIn.getWorldTime() % 20 == 0 && !stack.hasTagCompound()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(Spell.BOOK_MODE_TAG, 0);
            stack.setTagCompound(tag);
        }
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if(stack != null && stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            tooltip.add("Mode" + tag.getInteger(Spell.BOOK_MODE_TAG));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
//
        if(!worldIn.isRemote && playerIn.getHeldItem(handIn).getTagCompound().getInteger(Spell.BOOK_MODE_TAG) == 0 && !playerIn.isSneaking()) {
            ExampleMod.proxy.openSpellGUI();
            return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
        }
        if (!worldIn.isRemote &&playerIn.isSneaking() && playerIn.getHeldItem(handIn).hasTagCompound()){
            changeMode(playerIn.getHeldItem(handIn));
            return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
        }
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(!worldIn.isRemote && stack.hasTagCompound()){
            NBTTagCompound tag = playerIn.getHeldItem(handIn).getTagCompound();
            ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
            recipe.add(new ModifierProjectile());
            recipe.add(new EffectDig());
            SpellResolver resolver = new SpellResolver(recipe);
            resolver.onCast(new Position(playerIn.posX, playerIn.posY, playerIn.posZ), worldIn, playerIn);

            SoundEvent event = new SoundEvent(new ResourceLocation(ExampleMod.MODID, "cast_spell"));
            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ,
                    event, SoundCategory.BLOCKS,
                    4.0F, (1.0F + (worldIn.rand.nextFloat()
                            - worldIn.rand.nextFloat()) * 0.2F) * 0.7F);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private void changeMode(ItemStack stack) {
        int mode = stack.getTagCompound().getInteger(Spell.BOOK_MODE_TAG) + 1;
        stack.getTagCompound().setInteger(Spell.BOOK_MODE_TAG, mode%4);

    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
