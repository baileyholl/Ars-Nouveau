package com.hollingsworth.craftedmagic.items;

import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.api.CraftedMagicAPI;
import com.hollingsworth.craftedmagic.api.Position;
import com.hollingsworth.craftedmagic.network.Networking;
import com.hollingsworth.craftedmagic.network.PacketOpenGUI;
import com.hollingsworth.craftedmagic.spell.SpellResolver;
import com.hollingsworth.craftedmagic.spell.effect.EffectDig;
import com.hollingsworth.craftedmagic.spell.effect.EffectType;
import com.hollingsworth.craftedmagic.spell.method.CastMethod;
import com.hollingsworth.craftedmagic.spell.method.ModifierProjectile;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDispatcher;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spell extends Item {

    public static final String BOOK_MODE_TAG = "mode";


    public Spell(){
        super(new Item.Properties().maxStackSize(1).group(ExampleMod.itemGroup));
        this.addPropertyOverride(new ResourceLocation("age"), new IItemPropertyGetter() {
            @Override
            public float call(ItemStack p_call_1_, @Nullable World p_call_2_, @Nullable LivingEntity p_call_3_) {
                return 0;
            }

            @OnlyIn(Dist.CLIENT)
            private int model;
            @OnlyIn(Dist.CLIENT)
            long lastUpdateTick;
            @OnlyIn(Dist.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {


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
                    if(worldIn.getGameTime() != lastUpdateTick) {
                        if (worldIn.getGameTime() % 10 == 0) {
                            model += 1;
                        }
                        lastUpdateTick = worldIn.getGameTime();
                    }
                }
                return model;
            }
        });
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


    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        if(stack != null && stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            tooltip.add(new StringTextComponent("Mode" + tag.getInt(Spell.BOOK_MODE_TAG)));
            tooltip.add(new StringTextComponent(tag.getString(tag.getInt(Spell.BOOK_MODE_TAG) + "recipe")));
        };
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
//
        if(!worldIn.isRemote && playerIn.getHeldItem(handIn).getTag().getInt(Spell.BOOK_MODE_TAG) == 0 && !playerIn.isSneaking() && playerIn instanceof ServerPlayerEntity) {

            ServerPlayerEntity player = (ServerPlayerEntity) playerIn;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->player), new PacketOpenGUI(playerIn.getHeldItem(handIn).getTag()));

            //            ExampleMod.proxy.openSpellGUI(playerIn.getHeldItem(handIn));
            return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
        }
        if (!worldIn.isRemote &&playerIn.isSneaking() && playerIn.getHeldItem(handIn).hasTag()){
            changeMode(playerIn.getHeldItem(handIn));
            return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
        }
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(!worldIn.isRemote && stack.hasTag()){
            CompoundNBT tag = playerIn.getHeldItem(handIn).getTag();
            if(tag.contains(tag.getInt(Spell.BOOK_MODE_TAG) + "recipe")){

                ArrayList<AbstractSpellPart> spell_r = getRecipeFromTag(stack.getTag(), tag.getInt(Spell.BOOK_MODE_TAG));

                SpellResolver resolver = new SpellResolver(spell_r);
                resolver.onCast(new Position(playerIn.posX, playerIn.posY, playerIn.posZ), worldIn, playerIn);

                SoundEvent event = new SoundEvent(new ResourceLocation(ExampleMod.MODID, "cast_spell"));
                worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ,
                        event, SoundCategory.BLOCKS,
                        4.0F, (1.0F + (worldIn.rand.nextFloat()
                                - worldIn.rand.nextFloat()) * 0.2F) * 0.7F);
            }
//            ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
//            recipe.add(new ModifierProjectile());
//            recipe.add(new EffectDig());
//            SpellResolver resolver = new SpellResolver(recipe);
//            resolver.onCast(new Position(playerIn.posX, playerIn.posY, playerIn.posZ), worldIn, playerIn);
//
//            SoundEvent event = new SoundEvent(new ResourceLocation(ExampleMod.MODID, "cast_spell"));
//            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ,
//                    event, SoundCategory.BLOCKS,
//                    4.0F, (1.0F + (worldIn.rand.nextFloat()
//                            - worldIn.rand.nextFloat()) * 0.2F) * 0.7F);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    public static ArrayList<AbstractSpellPart> getRecipeFromTag(CompoundNBT tag, int spell_slot){
        String recipe = tag.getString(spell_slot + "recipe");
        System.out.println("Contains a spell");
        ArrayList<AbstractSpellPart> spell_r = new ArrayList<>();
        try {
            for (String id : recipe.substring(1, recipe.length() - 1).split(",")) {
                System.out.println(id.trim());
                if (CraftedMagicAPI.getInstance().spell_map.containsKey(id.trim()))
                    spell_r.add(CraftedMagicAPI.getInstance().spell_map.get(id.trim()));
            }
        }catch (Exception e){
            System.out.println("Couldn't parse recipe.");
            return new ArrayList<>();
        }
        return spell_r;
    }
    private void changeMode(ItemStack stack) {
        int mode = stack.getTag().getInt(Spell.BOOK_MODE_TAG) + 1;
        stack.getTag().putInt(Spell.BOOK_MODE_TAG, mode%4);
        
    }



}
