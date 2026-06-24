package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.common.block.RotatingSpellTurret;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public class TempSpellTurretTile extends RotatingTurretTile {
    private int duration;
    private int castInterval;
    private int castCooldown;

    public TempSpellTurretTile(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public TempSpellTurretTile(BlockPos pos, BlockState state) {
        this(BlockRegistry.TEMP_SPELL_TURRET_TILE.get(), pos, state);
    }

    public void configure(int duration, int castInterval) {
        this.duration = duration;
        this.castInterval = castInterval;
        this.castCooldown = castInterval;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            duration--;
            castCooldown--;
            if (duration <= 0) {
                level.destroyBlock(worldPosition, false);
            } else if (castCooldown <= 0) {
                this.shootSpell();
                this.castCooldown = castInterval;
            }
        }
    }

    @Override
    public void shootSpell() {
        BlockPos pos = worldPosition;
        if (spellCaster.getSpell().isEmpty() || !(level instanceof ServerLevel level))
            return;
        Networking.sendToNearbyClient(level, pos, new PacketOneShotAnimation(pos));
        Position iposition = RotatingSpellTurret.getDispensePosition(pos, this);
        FakePlayer fakePlayer = uuid != null
                ? FakePlayerFactory.get(level, new GameProfile(uuid, ""))
                : ANFakePlayer.getPlayer(level);
        fakePlayer.setPos(pos.getX(), pos.getY(), pos.getZ());
        EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(level, spellCaster.getSpell(), fakePlayer, new TileCaster(this, SpellContext.CasterType.TURRET)));
        if (resolver.castType != null && RotatingSpellTurret.ROT_TURRET_BEHAVIOR_MAP.containsKey(resolver.castType)) {
            RotatingSpellTurret.ROT_TURRET_BEHAVIOR_MAP.get(resolver.castType).onCast(resolver, level, pos, fakePlayer, iposition, orderedByNearest()[0].getOpposite());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putInt("duration", duration);
        tag.putInt("castInterval", castInterval);
        tag.putInt("castCooldown", castCooldown);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        duration = tag.getInt("duration");
        castInterval = tag.getInt("castInterval");
        castCooldown = tag.getInt("castCooldown");
    }
}
