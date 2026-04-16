package de.jakob.lotm.abilities.justiciar;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.abilities.core.AbilityUsedEvent;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class EyeOfOrderAbility extends Ability {

    public static final List<EyeZone> ACTIVE_ZONES = new CopyOnWriteArrayList<>();

    private static final int TICK_RATE = 10; // Updates 2 times per second
    private static final int DURATION = 20 * 60 * 5; // 5 minutes

    private static final DustParticleOptions GOLD_DUST = new DustParticleOptions(
            new Vector3f(1.0f, 0.9f, 0.3f), 1.0f); // Passive
    private static final DustParticleOptions RED_DUST = new DustParticleOptions(
            new Vector3f(0.9f, 0.2f, 0.1f), 1.0f); // Hostile
    private static final DustParticleOptions BLACK_DUST = new DustParticleOptions(
            new Vector3f(0.05f, 0.05f, 0.05f), 1.0f); // "Evil" players

    public EyeOfOrderAbility(String id) {
        super(id, 10f, "eye_of_order");
        hasOptimalDistance = false;
        postsUsedAbilityEventManually = true;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("justiciar", 8));
    }

    @Override
    protected float getSpiritualityCost() {
        return 120;
    }

    /**
     * Sequence → Radius scaling
     */
    private static int getRadiusForSequence(int seq) {
        return switch (seq) {
            case 8 -> 15;
            case 7 -> 20;
            case 6 -> 25;
            case 5 -> 30;
            case 4 -> 40;
            case 3 -> 50;
            case 2 -> 100;
            case 1 -> 150;
            case 0 -> 250;
            default -> 15;
        };
    }

    @override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        ServerLevel serverlevel = (ServerLevel) level;

        // Forgot to add a way to turn it off lmao
        Optional<EyeZone> existing = ACTIVE_ZONES.stream()
                .filter(z -> z.ownerId.equals(entity.getUUID()))
                .findFirst();

        if (existing.isPresent()) {
            EyeZone zone = existing.get ();
            zone.deactivate();
            cleanupGlow(zone);
            ACTIVE_ZONES.re.ove(zone);
            return;
        }
    }