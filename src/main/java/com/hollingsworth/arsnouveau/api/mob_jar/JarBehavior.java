package com.hollingsworth.arsnouveau.api.mob_jar;

import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JarBehavior<T extends Entity> {

    public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, MobJarTile tile){
        T entity = entityFromJar(tile);
        if (entity.getType().is(EntityTags.INTERACT_JAR_BLACKLIST)) return;

        if (entity instanceof LivingEntity livingEntity) {
            ItemStack handItem = player.getItemInHand(handIn);
            InteractionResult result = handItem.getItem().interactLivingEntity(handItem, player, livingEntity, handIn);
            if (result != InteractionResult.PASS) {
                syncClient(tile);
                return;
            }
        }

        if (entity instanceof Mob mob) {
            mob.interact(player, handIn);
            syncClient(tile);
        }
    }

    public void tick(MobJarTile tile){
    }

    /**
     * A helper method to sync the jar change to the client, also sends a block update.
     */
    public void syncClient(MobJarTile tile){
        tile.updateBlock();
    }

    public @NotNull T entityFromJar(MobJarTile tile){
        return (T) tile.getEntity();
    }

    public boolean isEntityBaby(Entity entity){
        if(!(entity instanceof AgeableMob ageableMob))
            return false;
        return ageableMob.isBaby();
    }

    /**
     * @return Renderer scale for the entity in the jar.
     */
    public Vec3 scaleOffset(MobJarTile pBlockEntity){
        return new Vec3(0,0,0);
    }
    /**
     * @return Renderer translation for the entity in the jar.
     */
    public Vec3 translate(MobJarTile pBlockEntity){
        return new Vec3(0,0,0);
    }

    public boolean shouldUsePartialTicks(MobJarTile pBlockEntity){
        return false;
    }

    /**
     *
     * @return Light level the jar should give off
     */
    public int lightLevel(MobJarTile pBlockEntity){
        return 0;
    }

    public int getAnalogPower(MobJarTile tile) {
        return tile.getEntity() == null ? 0 : 15;
    }

    public void onRedstonePower(MobJarTile tile) {
        T entity = entityFromJar(tile);
        if (entity instanceof Mob mob) {
            mob.playAmbientSound();
        }
    }

    public int getSignalPower(MobJarTile tile) {
        return 0;
    }

    public void getTooltip(MobJarTile tile, List<Component> tooltips){

    }

    public boolean isPowered(MobJarTile tile) {
        return tile.getBlockState().getValue(BlockStateProperties.POWERED);
    }
}
