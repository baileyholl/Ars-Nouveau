package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import com.hollingsworth.arsnouveau.client.renderer.item.SpellBookRenderer;
import com.hollingsworth.arsnouveau.common.capability.ANPlayerDataCap;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class SpellBook extends Item implements IAnimatable, ICasterTool {

    public SpellTier tier;

    public SpellBook(SpellTier tier){
        super(new Item.Properties().stacksTo(1).tab(ArsNouveau.itemGroup));
        this.tier = tier;
    }
    
    public SpellBook(Properties properties, SpellTier tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        CapabilityRegistry.getMana(playerIn).ifPresent(iMana -> {
            if(iMana.getBookTier() < this.tier.value){
                iMana.setBookTier(this.tier.value);
            }
            IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(playerIn).orElse(new ANPlayerDataCap());
            if(iMana.getGlyphBonus() < cap.getKnownGlyphs().size()){
                iMana.setGlyphBonus(cap.getKnownGlyphs().size());
            }
        });
        ISpellCaster caster = getSpellCaster(stack);

        return caster.castSpell(worldIn, playerIn, handIn, new TranslatableComponent("ars_nouveau.invalid_spell"));
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
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(final ItemStack stack, @Nullable final Level world, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslatableComponent("ars_nouveau.spell_book.select", KeyMapping.createNameSupplier(ModKeyBindings.OPEN_SPELL_SELECTION.getName()).get()));
        tooltip.add(new TranslatableComponent("ars_nouveau.spell_book.craft", KeyMapping.createNameSupplier(ModKeyBindings.OPEN_BOOK.getName()).get()));
        tooltip.add(new TranslatableComponent("tooltip.ars_nouveau.caster_level", getTier().value).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
    }

    public SpellTier getTier() {
        return this.tier;
    }

    @Override
    public void registerControllers(AnimationData data) {}

    AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @NotNull
    @Override
    public ISpellCaster getSpellCaster(ItemStack stack) {
        return new BookCaster(stack);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IItemRenderProperties() {
            private final BlockEntityWithoutLevelRenderer renderer = new SpellBookRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer;
            }
        });
    }

    public static class BookCaster extends SpellCaster{

        public BookCaster(ItemStack stack){
            super(stack);
        }

        public BookCaster(CompoundTag tag){
            super(tag);
        }

        @Override
        public int getMaxSlots() {
            return 10;
        }
    }
}
