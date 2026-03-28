package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.client.renderer.item.GauntletRenderer;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.client.gui.screens.Screen;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.client.Minecraft;

import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.component.DataComponents;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.tags.BlockTags;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.tags.TagKey;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.InteractionHand;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.InteractionResult;

import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.entity.player.Player;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.Item;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.ItemStack;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.TooltipFlag;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.component.TooltipDisplay;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.component.Tool;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.level.Level;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class EnchantersGauntlet extends ModItem implements ICasterTool, GeoItem, IManaDiscountEquipment {

    private static TagKey<Block>[] blocks;

    public EnchantersGauntlet(Properties properties) {
        super(properties);
    }

    public EnchantersGauntlet() {
        super(ItemsRegistry.newItemProperties().stacksTo(1)
                .component(DataComponentRegistry.SPELL_CASTER, new SpellCaster())
                .component(DataComponents.TOOL, createToolProperties())
        );
    }

    // 1.21.11: Tool.Rule.minesAndDrops takes HolderSet<Block>, not TagKey<Block>
    // BuiltInRegistries.BLOCK is a HolderGetter<Block>; getOrThrow(TagKey) returns HolderSet.Named<Block>
    // Tool constructor adds boolean canDestroyBlocksInCreative as 4th parameter
    // acquireBootstrapRegistrationLookup returns a HolderGetter valid during bootstrap/registration phase
    static Tool createToolProperties() {
        HolderGetter<Block> getter = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        List<Tool.Rule> rules = new ArrayList<>();
        for (TagKey<Block> block : List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_AXE,
                BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.MINEABLE_WITH_HOE)) {
            rules.add(Tool.Rule.minesAndDrops(getter.getOrThrow(block), 8.0F));
        }
        rules.add(Tool.Rule.deniesDrops(getter.getOrThrow(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)));
        rules.add(Tool.Rule.overrideSpeed(getter.getOrThrow(BlockTags.SWORD_EFFICIENT), 1.5F));
        return new Tool(rules, 1.0F, 1, false);
    }

    // 1.21.11: getEnchantmentValue(ItemStack) and isEnchantable(ItemStack) removed from Item interface
    public int getEnchantmentValue(@NotNull ItemStack stack) {
        return 15;
    }

    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        AbstractCaster<?> caster = getSpellCaster(stack);
        return caster.castSpell(worldIn, playerIn, handIn, Component.translatable("ars_nouveau.gauntlet.invalid"));
    }

    @Override
    public boolean isScribedSpellValid(AbstractCaster<?> caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        return spell.mutable().recipe.stream().noneMatch(s -> s instanceof AbstractCastMethod);
    }

    @Override
    public void sendInvalidMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.gauntlet.invalid"));
    }

    @Override
    public int getManaDiscount(ItemStack i, Spell spell) {
        return (int) (spell.getCost() * .25);
    }

    @Override
    public void scribeModifiedSpell(AbstractCaster<?> caster, Player player, InteractionHand hand, ItemStack stack, Spell.Mutable spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodTouch.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
    }

    // 1.21.11: appendHoverText changed to (ItemStack, TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        if (Minecraft.getInstance().hasShiftDown() || !Config.GLYPH_TOOLTIPS.get())
            getInformation(stack, context, tooltip2, flagIn);
        super.appendHoverText(stack, context, display, tooltip2, flagIn);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        AbstractCaster<?> caster = getSpellCaster(pStack);
        if (caster != null && Config.GLYPH_TOOLTIPS.get() && !Minecraft.getInstance().hasShiftDown() && !caster.isSpellHidden() && !caster.getSpell().isEmpty())
            return Optional.of(new SpellTooltip(caster));
        return Optional.empty();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            final GauntletRenderer renderer = new GauntletRenderer();

            @Override
            public software.bernie.geckolib.renderer.GeoItemRenderer<?> getGeoItemRenderer() {
                return renderer;
            }
        });
    }

    // 1.21.11: PICKAXE_DIG, AXE_DIG, SWORD_DIG, SHOVEL_DIG, HOE_DIG removed from ItemAbilities
    static Set<ItemAbility> ACTIONS = Set.of(ItemAbilities.SHEARS_DIG);

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, net.neoforged.neoforge.common.@NotNull ItemAbility itemAbility) {
        return ACTIONS.contains(itemAbility);
    }

}
