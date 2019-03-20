package com.hollingsworth.craftedmagic.items;

import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.spell.SpellResolver;
import com.hollingsworth.craftedmagic.spell.method.MethodType;
import com.hollingsworth.craftedmagic.spell.effect.EffectType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class Spell extends Item {
    public static final String CAST_TYPE = "cast_type";
    public static final String SPELL_TYPE = "spell_type";

    public MethodType methodType;
    public EffectType effectType;



    public Spell(){
        setRegistryName("spell");        // The unique name (within your mod) that identifies this item
        setUnlocalizedName(ExampleMod.MODID + ".spell");     // Used for localization (en_US.lang)
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!worldIn.isRemote && worldIn.getWorldTime() % 20 == 0 && !stack.hasTagCompound()) {
            NBTTagCompound tag = new NBTTagCompound();
            stack.setTagCompound(makeDefaultSpellTag(tag));
        }
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public NBTTagCompound makeDefaultSpellTag(NBTTagCompound tag){
        tag.setString(CAST_TYPE, "Projectile");
        tag.setString(SPELL_TYPE, "Dig");
        return tag;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if(stack != null && stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            tooltip.add("Method:     " + tag.getString(CAST_TYPE));
            tooltip.add("Effect:     " + tag.getString(SPELL_TYPE));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(!worldIn.isRemote && stack.hasTagCompound()){
            NBTTagCompound tag = playerIn.getHeldItem(handIn).getTagCompound();
            SpellResolver resolver = new SpellResolver(tag.getString(CAST_TYPE), tag.getString(SPELL_TYPE));
            resolver.onCast();
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
