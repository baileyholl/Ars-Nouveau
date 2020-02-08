package com.hollingsworth.craftedmagic.items;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.api.CraftedMagicAPI;
import com.hollingsworth.craftedmagic.capability.ManaCapability;
import com.hollingsworth.craftedmagic.network.Networking;
import com.hollingsworth.craftedmagic.network.PacketOpenGUI;
import com.hollingsworth.craftedmagic.spell.SpellResolver;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SpellBook extends Item {
    public static final String BOOK_MODE_TAG = "mode";



    public SpellBook(){
        super(new Item.Properties().maxStackSize(1).group(ArsNouveau.itemGroup));
        setRegistryName("spell_book");        // The unique name (within your mod) that identifies this item

        //setUnlocalizedName(ExampleMod.MODID + ".spell_book");     // Used for localization (en_US.lang)
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!worldIn.isRemote && worldIn.getGameTime() % 20 == 0 && !stack.hasTag()) {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt(SpellBook.BOOK_MODE_TAG, 0);
            stack.setTag(tag);
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

//    @Override
//    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
//        System.out.println("On right click");
//        return super.onItemRightClick(worldIn, playerIn, handIn);
//    }
//
//    @Override
//    public ActionResultType onItemUse(ItemUseContext context) {
//        System.out.println("On item use");
//        return super.onItemUse(context);
//    }
//
//    @Override
//    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
//        System.out.println("Interacted with entity");
//        return super.itemInteractionForEntity(stack, playerIn, target, hand);
//    }
//
//    /**
//     * Gets the block or object that is being moused over.
//     */
//    public void getMouseOver(Entity entity, World world) {
//
//        if (entity != null) {
//            double blockReach = 5.0;
//            Vec3d vec3d = entity.getEyePosition(0);
//            boolean flag = false;
//            int i = 3;
//            double d1 = blockReach;
//            if (this.mc.playerController.extendedReach()) {
//                d1 = 6.0D;
//                blockReach = d1;
//            } else {
//                if (blockReach > 3.0D) {
//                    flag = true;
//                }
//
//                blockReach = blockReach;
//            }
//
//            d1 = d1 * d1;
//            if (this.mc.objectMouseOver != null) {
//                d1 = this.mc.objectMouseOver.getHitVec().squareDistanceTo(vec3d);
//            }
//
//            Vec3d vec3d1 = entity.getLook(1.0F);
//            Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReach, vec3d1.y * blockReach, vec3d1.z * blockReach);
//            float f = 1.0F;
//            AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vec3d1.scale(blockReach)).grow(1.0D, 1.0D, 1.0D);
//            EntityRayTraceResult entityraytraceresult = ProjectileHelper.func_221273_a(entity, vec3d, vec3d2, axisalignedbb, (p_215312_0_) -> {
//                return !p_215312_0_.isSpectator() && p_215312_0_.canBeCollidedWith();
//            }, d1);
//            if (entityraytraceresult != null) {
//                Entity entity1 = entityraytraceresult.getEntity();
//                Vec3d vec3d3 = entityraytraceresult.getHitVec();
//                double d2 = vec3d.squareDistanceTo(vec3d3);
//                if (flag && d2 > 9.0D) {
//                    this.mc.objectMouseOver = BlockRayTraceResult.createMiss(vec3d3, Direction.getFacingFromVector(vec3d1.x, vec3d1.y, vec3d1.z), new BlockPos(vec3d3));
//                } else if (d2 < d1 || this.mc.objectMouseOver == null) {
//                    this.mc.objectMouseOver = entityraytraceresult;
//                    if (entity1 instanceof LivingEntity || entity1 instanceof ItemFrameEntity) {
//                        this.mc.pointedEntity = entity1;
//                    }
//                }
//            }
//        }
//    }

        /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if(!playerIn.getEntityWorld().isRemote) {
            System.out.println("Touched Entity");

            ArrayList<AbstractSpellPart> spell_r = getCurrentRecipe(stack);
            int totalCost = spell_r.stream().mapToInt(AbstractSpellPart::getManaCost).sum();
            if(!spell_r.isEmpty()) {

                ManaCapability.getMana(playerIn).ifPresent(mana -> {
                    System.out.println(totalCost);
                    if(totalCost <= mana.getCurrentMana() || playerIn.isCreative()) {
                        SpellResolver resolver = new SpellResolver(spell_r);
                        resolver.onCastOnEntity(stack, playerIn, target, hand);
                        mana.removeMana(totalCost);
                        System.out.println(mana.getCurrentMana());
                    }else{
                        System.out.println("Not enough mana");
                    }
                });
            }
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(worldIn.isRemote || !stack.hasTag()){
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        System.out.println("Right click");
        if(getMode(stack.getTag()) == 0 && !playerIn.isSneaking() && playerIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerIn;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->player), new PacketOpenGUI(stack.getTag()));
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        if (playerIn.isSneaking() && stack.hasTag()){
            changeMode(stack);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }


        SpellResolver resolver = new SpellResolver(getCurrentRecipe(stack));
        resolver.onCast(stack, playerIn, worldIn);
//        if(!spell_r.isEmpty()) {
//            ManaCapability.getMana(playerIn).ifPresent(mana -> {
//                int totalCost = ManaUtil.calculateCost(spell_r);
//                if(totalCost <= mana.getCurrentMana() || playerIn.isCreative()) {
//                    SpellResolver resolver = new SpellResolver(spell_r);
//                    resolver.onCast(stack, playerIn, worldIn);
//                    mana.removeMana(totalCost);
//
//                }else{
//                    System.out.println("Not enough mana");
//                }
//            });
//
//        }
        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
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

        if(worldIn.isRemote || !stack.hasTag() || getMode(stack.getTag()) == 0 || playerIn.isSneaking()) return ActionResultType.PASS;

        SpellResolver resolver = new SpellResolver(getCurrentRecipe(stack));
        resolver.onCastOnBlock(context);
//        int totalCost = ManaUtil.calculateCost(spell_r);
//        if(!spell_r.isEmpty()){
//            ManaCapability.getMana(playerIn).ifPresent(mana -> {
//                if ( (totalCost <= mana.getCurrentMana() || playerIn.isCreative())) {
//                    SpellResolver resolver = new SpellResolver(spell_r);
//                    resolver.onCastOnBlock(context);
//                    mana.removeMana(totalCost);
//
//                    SoundEvent event = new SoundEvent(new ResourceLocation(ArsNouveau.MODID, "cast_spell"));
//                    worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, event, SoundCategory.BLOCKS,
//                            4.0F, (1.0F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.2F) * 0.7F);
//                }
//            });
//
//        }

        return ActionResultType.PASS;
    }

    public ArrayList<AbstractSpellPart> getCurrentRecipe(ItemStack stack){
        return SpellBook.getRecipeFromTag(stack.getTag(), getMode(stack.getTag()));
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
        return tag.getInt(SpellBook.BOOK_MODE_TAG);
    }

    public void setMode(ItemStack stack, int mode){
        stack.getTag().putInt(SpellBook.BOOK_MODE_TAG, mode);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        if(stack != null && stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            tooltip.add(new StringTextComponent(SpellBook.getSpellName(stack.getTag())));
        }
    }
}
