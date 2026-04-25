package de.jakob.lotm.entity.custom.ability_entities.death_pathway;

import de.jakob.lotm.util.helper.ParticleUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class UnderworldGateEntity extends Entity {

    public AnimationState openAnimationState = new AnimationState();
    public UnderworldGateEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        if(level().isClientSide) {
            if (this.tickCount == 1) {
                openAnimationState.start(0);
            }
            return;
        }

        ParticleUtil.spawnParticles((ServerLevel) level(), ParticleTypes.SOUL, position().add(getLookAngle().normalize().scale(.85)), 12, .8, 0.25, .8, 0);
        ParticleUtil.spawnParticles((ServerLevel) level(), ParticleTypes.ENCHANT, getEyePosition().subtract(0, .2, 0), 8, 1.5, 1.5, 1.5, 0);
        ParticleUtil.spawnParticles((ServerLevel) level(), ParticleTypes.SMOKE, getEyePosition().subtract(0, .2, 0), 8, 1.5, 1.5, 1.5, 0);

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }
}
