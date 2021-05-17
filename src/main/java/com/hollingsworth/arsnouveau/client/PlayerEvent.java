package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.MappingUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.particle.engine.ParticleEngine;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class PlayerEvent {

    private static final Minecraft minecraft = Minecraft.getInstance();

    @SubscribeEvent
    public static void onTick(final TickEvent.RenderTickEvent evt) {
        ParticleEngine.getInstance().tick();
    }

    @SubscribeEvent
    public static void onBlock(final PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity entity = event.getPlayer();
        if(!event.getWorld().isClientSide || event.getHand() != Hand.MAIN_HAND || event.getWorld().getBlockState(event.getPos()).getBlock() instanceof ScribesBlock)
            return;
        if(entity.getItemInHand(event.getHand()).getItem() instanceof SpellBook){
            event.setCanceled(true);
            ObfuscationReflectionHelper.setPrivateValue(FirstPersonRenderer.class, minecraft.getItemInHandRenderer(), 1f, MappingUtil.getEquippedProgressMainhand());
        }
    }

    @SubscribeEvent
    public static void onTooltip(final ItemTooltipEvent event){
        ItemStack stack = event.getItemStack();
        int level = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT, stack);
        if(level > 0 && stack.hasTag() && stack.getTag().contains("spell")){
            Spell spell = new Spell(SpellParchment.getSpellRecipe(stack));
            event.getToolTip().add(new StringTextComponent(spell.getDisplayString()));
        }
    }

    @SubscribeEvent
    public static void onItem(final PlayerInteractEvent.RightClickItem event) {
        PlayerEntity entity = event.getPlayer();
        if(!event.getWorld().isClientSide || event.getHand() != Hand.MAIN_HAND)
            return;
        if(entity.getItemInHand(event.getHand()).getItem() instanceof SpellBook){
            event.setCanceled(true);
            ObfuscationReflectionHelper.setPrivateValue(FirstPersonRenderer.class, minecraft.getItemInHandRenderer(), 1f, MappingUtil.getEquippedProgressMainhand());
        }
    }

    @SubscribeEvent
    public static void scrying(final TickEvent.RenderTickEvent evt) {
//        if(evt.phase == TickEvent.Phase.END && minecraft.level != null && minecraft.player != null){
//            if(minecraft.player.hasEffect(ModPotions.SCRYING_EFFECT)){
//                ClientWorld world = minecraft.level;
//                ClientPlayerEntity entity = minecraft.player;
//                for(BlockPos p : BlockPos.betweenClosed(entity.blockPosition().offset(10, 10, 10), entity.blockPosition().offset(-10, -10, -10))){
//                   //System.out.println(p.toString());
//                    if(world.getBlockState(p).getBlock() == Blocks.DIAMOND_BLOCK) {
//                        System.out.println("block");
//                        world.addParticle(ParticleTypes.FLAME, p.getX(), p.getY() + 1, p.getZ(), 0, 0, 0);
//                      //  Tessellator.getInstance().getBuilder().vertex(p.getX(), p.getY() + 1, p.getZ()).vertex(p.getX(), p.getY() + 2, p.getZ()).endVertex();
//                    }
////                    int sX = yourX;
////                    int sY = yourY;
////                    int sZ = yourZ;
////// Usually the player
//////Interpolating everything back to 0,0,0. These are transforms you can find at RenderEntity class
////                    double d0 = entity.xOld + (entity.getX() - entity.xOld) * (double)evt.renderTickTime;
////                    double d1 = entity.yOld + (entity.getY() - entity.yOld) * (double)evt.renderTickTime;
////                    double d2 = entity.zOld + (entity.getZ() - entity.zOld) * (double)evt.renderTickTime;
//////Apply 0-our transforms to set everything back to 0,0,0
////                    Tessellator.getInstance().getBuilder().sortQuads(-d0, -d1, -d2);
////                    StructureTileEntityRenderer
//////Your render function which renders boxes at a desired position. In this example I just copy-pasted the one on TileEntityStructureRenderer
////                    renderBox(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), sX, sY, sZ, sX + 1, sY + 1, sZ + 1);
//////When you are done rendering all your boxes reset the offsets. We do not want everything that renders next to still be at 0,0,0 :)
////                    Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0);
//
//
//                }
//
//            }
//
//        }
    }
    @SubscribeEvent
    public static void playerRender(final RenderPlayerEvent event)
    {

    }

    @SubscribeEvent
    public static void onRenderWorldLast(final RenderWorldLastEvent event)
    {
        final PlayerEntity playerEntity = Minecraft.getInstance().player;

        if (playerEntity == null)
            return;
        Vector3d vector3d = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        ClientWorld world = Minecraft.getInstance().level;

        double xView = vector3d.x();
        double yView = vector3d.y();
        double zView = vector3d.z();
       // System.out.println(playerEntity.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG));
        CompoundNBT tag = ClientInfo.persistentData;
        if(!tag.contains("an_scrying"))
            return;

        for(BlockPos p : BlockPos.betweenClosed(playerEntity.blockPosition().offset(10, 10, 10), playerEntity.blockPosition().offset(-10, -10, -10))){
            if(world.getBlockState(p).getBlock().getRegistryName().toString().equals(tag.getString("an_scrying"))) {
                final VoxelShape boundingShape = VoxelShapes.block();
                ParticleUtil.spawnTouch(world, p);
                WorldRenderer.renderVoxelShape(
                        event.getMatrixStack(),
                        Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES),
                        boundingShape,
                        p.getX() - xView, p.getY() - yView, p.getZ() - zView,
                        0.15F, .15f, .15f, 0.25F
                );
                //  Tessellator.getInstance().getBuilder().vertex(p.getX(), p.getY() + 1, p.getZ()).vertex(p.getX(), p.getY() + 2, p.getZ()).endVertex();
            }
        }

    }

}
