package de.jakob.lotm.abilities.visionary;

import de.jakob.lotm.abilities.core.AbilityUsedEvent;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.abilities.tyrant.TorrentialDownpourAbility;
import de.jakob.lotm.abilities.wheel_of_fortune.calamities.Earthquake;
import de.jakob.lotm.abilities.wheel_of_fortune.calamities.Meteor;
import de.jakob.lotm.entity.custom.ability_entities.MeteorEntity;
import de.jakob.lotm.rendering.effectRendering.EffectManager;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.data.Location;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.DamageLookup;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Vector3f;

import java.util.*;

public class DisasterFantasiaAbility extends SelectableAbility {

    private static final Earthquake EARTHQUAKE = new Earthquake();
    private static final Meteor METEOR = new Meteor();
    private static final int METEOR_COUNT = 25;
    private static final double METEOR_RADIUS = 50.0;

    public DisasterFantasiaAbility(String id) {
        super(id, 11f);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("visionary", 1));
    }

    @Override
    public float getSpiritualityCost() {
        return 2500;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.disaster_fantasia.earthquake",
                "ability.lotmcraft.disaster_fantasia.meteor"
        };
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        Vec3 targetPos = AbilityUtil.getTargetLocation(entity, 150, 3);
        float multiplier = multiplier(entity);
        boolean griefing = BeyonderData.isGriefingEnabled(entity);

        switch (abilityIndex) {
            case 0 -> EARTHQUAKE.spawnCalamity(serverLevel, targetPos, multiplier, griefing, 65, (float) DamageLookup.lookupDamage(1, .4f), entity);
            case 1 -> spawnMeteorShower(serverLevel, targetPos, multiplier, griefing);
        }
    }

    private void spawnMeteorShower(ServerLevel level, Vec3 center,
                                          float multiplier, boolean griefing) {
        Random rand = new Random();
        for (int i = 0; i < METEOR_COUNT; i++) {
            ServerScheduler.scheduleDelayed(i * 4, () -> {
                double angle = rand.nextDouble() * 2 * Math.PI;
                double distance = rand.nextDouble() * METEOR_RADIUS;
                double offsetX = Math.cos(angle) * distance;
                double offsetZ = Math.sin(angle) * distance;
                Vec3 meteorPos = new Vec3(center.x + offsetX, center.y, center.z + offsetZ);


                MeteorEntity meteor = new MeteorEntity(level, 2.5f,  (float) DamageLookup.lookupDamage(1, .75f) * multiplier, 3, null, griefing, 13, 12);
                meteor.setPosition(meteorPos);
                level.addFreshEntity(meteor);
            }, level, () -> AbilityUtil.getTimeInArea(null, new Location(center, level)));
        }
    }
}