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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.List;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;
import net.minecraft.item.Item.Properties;

public class SpellBook extends Item implements ISpellTier, IScribeable, IDisplayMana, IAnimatable {

    public static final String BOOK_MODE_TAG = "mode";
    public static final String UNLOCKED_SPELLS = "spells";
    public static final int SEGMENTS = 10;
    public Tier tier;


    public SpellBook(Tier tier){
        super(new Item.Properties().stacksTo(1).tab(ArsNouveau.itemGroup).setISTER(() -> SpellBookRenderer::new));
        this.tier = tier;
    }
    
    public SpellBook(Properties properties, Tier tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!stack.hasTag())
            stack.setTag(new CompoundNBT());

        if(!worldIn.isClientSide && worldIn.getGameTime() % 5 == 0 && !stack.hasTag()) {
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
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
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
        if(result instanceof BlockRayTraceResult && worldIn.getBlockEntity(((BlockRayTraceResult) result).getBlockPos()) instanceof ScribesTile)
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        if(result instanceof BlockRayTraceResult && !playerIn.isShiftKeyDown()){
            if(worldIn.getBlockEntity(((BlockRayTraceResult) result).getBlockPos()) != null &&
                    !(worldIn.getBlockEntity(((BlockRayTraceResult) result).getBlockPos()) instanceof IntangibleAirTile
                    ||(worldIn.getBlockEntity(((BlockRayTraceResult) result).getBlockPos()) instanceof PhantomBlockTile))) {
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
            }
        }


        if(worldIn.isClientSide || !stack.hasTag()){
            return new ActionResult<>(ActionResultType.CONSUME, stack);
        }
        // Crafting mode
        if(getMode(stack.getTag()) == 0 && playerIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerIn;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->player), new PacketOpenSpellBook(stack.getTag(), getTier().ordinal(), getUnlockedSpellString(player.getItemInHand(handIn).getTag())));
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
        if(!(player.getItemInHand(handIn).getItem() instanceof SpellBook))
            return false;

        List<AbstractSpellPart> spellParts = SpellBook.getUnlockedSpells(player.getItemInHand(handIn).getTag());
        int unlocked = 0;
        for(AbstractSpellPart spellPart : spellParts){
            if(SpellBook.unlockSpell(stack.getTag(), spellPart))
                unlocked++;
        }
        PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.spell_book.copied", unlocked));
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
    public UseAction getUseAnimation(ItemStack stack) {
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
            return new TranslationTextComponent("ars_nouveau.spell_book.create_mode").getString();
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
    public void appendHoverText(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if(stack != null && stack.hasTag()) {
            tooltip.add(new StringTextComponent(SpellBook.getSpellName(stack.getTag())));

            tooltip.add(new TranslationTextComponent("ars_nouveau.spell_book.select", KeyBinding.createNameSupplier(ModKeyBindings.OPEN_SPELL_SELECTION.getKeyBinding().getName()).get().getString()));
            tooltip.add(new TranslationTextComponent("ars_nouveau.spell_book.craft", KeyBinding.createNameSupplier(ModKeyBindings.OPEN_BOOK.getKeyBinding().getName()).get().getString()));
        }
    }

    @Override
    public Tier getTier() {
        return this.tier;
    }

    @Override
    public void registerControllers(AnimationData data) {

    }
    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
