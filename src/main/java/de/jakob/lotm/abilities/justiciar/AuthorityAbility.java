package de.jakob.lotm.abilities.justiciar;

import de.jakob.lotm.abilities.core.AbilityUsedEvent;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.data.Location;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

import java.util.*;
import java.util.stream.Collectors;

public class AuthorityAbility extends SelectableAbility {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public AuthorityAbility(String id) {
        super(id, 3f, "authority");
        interactionRadius = 15;
        hasOptimalDistance = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("justiciar", 9));
    }

    @Override
    protected float getSpiritualityCost() {
        return 30;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.authority.strip_defense",
                "ability.lotmcraft.authority.slow",
                "ability.lotmcraft.authority.armor_remove"
        };
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        switch (abilityIndex) {
            case 0 -> stripDefense(level, entity);
            case 1 -> slow(level, entity);
            case 2 -> armorRemove(level, entity);
        }
    }

    private int scaledRadius(LivingEntity caster) {
        return 15 * (int) Math.max(multiplier(caster) / 4, 1);
    }

    private int scaledDuration(LivingEntity caster) {
        return (int) Math.max(multiplier(caster) / 4, 1);
    }

    private void applyStunIfEnhanced(LivingEntity caster, LivingEntity target) {
        if (BeyonderData.getSequence(caster) <= 4) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40 * scaledDuration(caster), 127, false, false));
            target.setDeltaMovement(Vec3.ZERO);
        }
    }

    private void spawnAuthorityPulse(ServerLevel level, Vec3 center, int radius) {
        Location loc = new Location(center, level);

        ParticleUtil.createParticleSpirals(
                ParticleTypes.FLAME, loc,
                0.5, radius, 3.0, 1.8, 1.5,
                25, 3, 4
        );

        ServerScheduler.scheduleForDuration(0, 2, 25, () -> {
            ParticleUtil.spawnCircleParticles(level, ParticleTypes.FLAME, center, radius, 48);
            ParticleUtil.spawnCircleParticles(level, ParticleTypes.END_ROD, center, radius * 0.6, 24);
            ParticleUtil.spawnSphereParticles(level, ParticleTypes.FLAME, center, radius * 0.3, 6);
        }, level);
    }

    private void stripDefense(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) level;
        int radius = scaledRadius(entity);
        int scale = scaledDuration(entity);
        Vec3 center = entity.position();

        spawnAuthorityPulse(serverLevel, center, radius);
        serverLevel.playSound(null, entity.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.2f, 0.7f);
        serverLevel.playSound(null, entity.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.8f, 0.5f);

        AbilityUtil.getNearbyEntities(entity, serverLevel, center, radius).forEach(target -> {
            List<Holder<MobEffect>> toRemove = target.getActiveEffects().stream()
                    .filter(e -> e.getEffect().value().getCategory() == MobEffectCategory.BENEFICIAL)
                    .map(MobEffectInstance::getEffect)
                    .collect(Collectors.toList());
            toRemove.forEach(target::removeEffect);
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 8 * scale, 0));
            applyStunIfEnhanced(entity, target);

            Vec3 targetPos = target.position().add(0, 1, 0);
            ServerScheduler.scheduleForDuration(0, 3, 20, () ->
                            ParticleUtil.spawnSphereParticles(serverLevel, ParticleTypes.FLAME, targetPos, 0.8, 4),
                    serverLevel);
        });

        NeoForge.EVENT_BUS.post(new AbilityUsedEvent(serverLevel, center, entity, this, interactionFlags, radius, 20 * 2));
    }

    private void slow(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) level;
        int radius = scaledRadius(entity);
        int scale = scaledDuration(entity);
        Vec3 center = entity.position();

        spawnAuthorityPulse(serverLevel, center, radius);
        serverLevel.playSound(null, entity.blockPosition(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS, 0.9f, 0.6f);

        AbilityUtil.getNearbyEntities(entity, serverLevel, center, radius).forEach(target -> {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 20 * scale, 1));
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 8 * scale, 0));
            applyStunIfEnhanced(entity, target);

            Location targetLoc = new Location(target.position().add(0, 0.1, 0), serverLevel);
            ParticleUtil.createParticleSpirals(
                    ParticleTypes.FLAME, targetLoc,
                    0.3, 0.8, 2.0, 1.0, 1.2,
                    30, 2, 3
            );
        });

        NeoForge.EVENT_BUS.post(new AbilityUsedEvent(serverLevel, center, entity, this, interactionFlags, radius, 20 * 2));
    }

    private void armorRemove(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) level;
        int radius = scaledRadius(entity);
        int scale = scaledDuration(entity);
        Vec3 center = entity.position();

        spawnAuthorityPulse(serverLevel, center, radius);
        serverLevel.playSound(null, entity.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0f, 0.5f);
        serverLevel.playSound(null, entity.blockPosition(), SoundEvents.ARMOR_EQUIP_IRON.value(), SoundSource.PLAYERS, 1.5f, 0.4f);

        AbilityUtil.getNearbyEntities(entity, serverLevel, center, radius).forEach(target -> {
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 8 * scale, 0));
            applyStunIfEnhanced(entity, target);

            if (random.nextDouble() < 0.4) {
                List<EquipmentSlot> equippedSlots = new ArrayList<>();
                for (EquipmentSlot slot : ARMOR_SLOTS) {
                    if (!target.getItemBySlot(slot).isEmpty()) {
                        equippedSlots.add(slot);
                    }
                }

                Collections.shuffle(equippedSlots, random);
                int toStrip = Math.min(2, equippedSlots.size());
                for (int i = 0; i < toStrip; i++) {
                    ItemStack stripped = target.getItemBySlot(equippedSlots.get(i)).copy();
                    target.setItemSlot(equippedSlots.get(i), ItemStack.EMPTY);
                    target.spawnAtLocation(stripped);
                }

                Vec3 targetPos = target.position().add(0, 1, 0);
                ServerScheduler.scheduleForDuration(0, 2, 15, () -> {
                    ParticleUtil.spawnCircleParticles(serverLevel, ParticleTypes.FLAME, targetPos, 0.6, 12);
                    ParticleUtil.spawnParticles(serverLevel, ParticleTypes.LAVA, targetPos, 3, 0.4);
                }, serverLevel);
            }
        });

        NeoForge.EVENT_BUS.post(new AbilityUsedEvent(serverLevel, center, entity, this, interactionFlags, radius, 20 * 2));
    }
}