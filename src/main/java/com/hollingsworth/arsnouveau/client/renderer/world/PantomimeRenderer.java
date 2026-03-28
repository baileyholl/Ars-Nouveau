package com.hollingsworth.arsnouveau.client.renderer.world;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.method.MethodPantomime;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

// 1.21.11: LevelRenderer.renderLineBox removed; use ShapeRenderer.renderShape + RenderTypes.LINES
public class PantomimeRenderer {
    public static void renderOutline(PoseStack poseStack) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (!(heldItem.getItem() instanceof ICasterTool)) return;

        var caster = SpellCasterRegistry.from(heldItem);
        if (caster == null) return;
        Spell selectedSpell = caster.getSpell();

        if (!(selectedSpell.getCastMethod() instanceof MethodPantomime pantomime)) return;
        SpellStats stats = new SpellStats.Builder()
                .setAugments(selectedSpell.getAugments(0, player))
                .addItemsFromEntity(player)
                .build(MethodPantomime.INSTANCE, null, player.level(), player, SpellContext.fromEntity(selectedSpell, player, heldItem));
        if (!stats.isSensitive()) return;

        BlockPos pos = pantomime.findPosition(player, stats).getBlockPos();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().position();

        poseStack.pushPose();
        poseStack.translate(-projectedView.x + pos.getX(), -projectedView.y + pos.getY(), -projectedView.z + pos.getZ());

        var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        var consumer = bufferSource.getBuffer(RenderTypes.LINES);
        int color = ARGB.color(200, 255, 255, 100);
        ShapeRenderer.renderShape(poseStack, consumer, Shapes.block(), 0, 0, 0, color, 1.5f);
        bufferSource.endLastBatch();

        poseStack.popPose();
    }
}
