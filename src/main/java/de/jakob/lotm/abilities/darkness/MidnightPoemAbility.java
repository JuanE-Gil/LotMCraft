package de.jakob.lotm.abilities.darkness;

import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.abilities.core.interaction.InteractionHandler;
import de.jakob.lotm.attachments.LuckComponent;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.effect.ModEffects;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.network.packets.toServer.AbilitySelectionPacket;
import de.jakob.lotm.particle.ModParticles;
import de.jakob.lotm.sound.ModSounds;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.data.Location;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.DamageLookup;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MidnightPoemAbility extends SelectableAbility {

    private final DustParticleOptions dust = new DustParticleOptions(new Vector3f(250 / 255f, 40 / 255f, 64 / 255f), 2.5f);
    private final DustParticleOptions dustBig = new DustParticleOptions(new Vector3f(250 / 255f, 40 / 255f, 64 / 255f), 10f);

    public MidnightPoemAbility(String id) {
        super(id, 4f);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("darkness", 8));
    }

    @Override
    protected float getSpiritualityCost() {
        return 19;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[] {
                "ability.lotmcraft.midnight_poem.lullaby",
                "ability.lotmcraft.midnight_poem.wilt",
                "ability.lotmcraft.midnight_poem.console"
        };
    }

    @Override
    public void nextAbility(LivingEntity entity){
        if(getAbilityNames().length == 0)
            return;

        if(!selectedAbilities.containsKey(entity.getUUID())) {
            selectedAbilities.put(entity.getUUID(), 0);
        }

        int selectedAbility = selectedAbilities.get(entity.getUUID());
        int entitySeq = AbilityUtil.getSeqWithArt(entity, this);

        selectedAbility++;
        if(selectedAbility >= getAbilityNames().length) {
            selectedAbility = 0;
        }

        if((entitySeq >= 4 && selectedAbility >= 2)){
            selectedAbility = 0;
        }

        selectedAbilities.put(entity.getUUID(), selectedAbility);
        PacketHandler.sendToServer(new AbilitySelectionPacket(getId(), selectedAbility));
    }

    @Override
    public void previousAbility(LivingEntity entity){
        if(getAbilityNames().length == 0)
            return;

        if(!selectedAbilities.containsKey(entity.getUUID())) {
            selectedAbilities.put(entity.getUUID(), 0);
        }

        int selectedAbility = selectedAbilities.get(entity.getUUID());
        selectedAbility--;
        if(selectedAbility <= -1) {
            selectedAbility = getAbilityNames().length - 1;
        }

        int entitySeq = AbilityUtil.getSeqWithArt(entity, this);
        if((entitySeq >= 4 && selectedAbility >= 2)){
            selectedAbility = 0;
        }

        selectedAbilities.put(entity.getUUID(), selectedAbility);
        PacketHandler.sendToServer(new AbilitySelectionPacket(getId(), selectedAbility));
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        switch (abilityIndex) {
            case 0 -> lullaby(level, entity);
            case 1 -> wilt(level, entity);
            case 2 -> console(level,entity);
        }
    }

    private void wilt(Level level, LivingEntity entity) {
        if(level.isClientSide)
            return;

        level.playSound(null, entity.blockPosition(), ModSounds.MIDNIGHT_POEM.get(), entity.getSoundSource(), 1.0f, 1.0f);

        ParticleUtil.spawnParticles((ServerLevel) level, dustBig, entity.getEyePosition().subtract(0, .4, 0), 800, 7, 0);
        ParticleUtil.spawnParticles((ServerLevel) level, ModParticles.CRIMSON_LEAF.get(), entity.position().subtract(0, .2, 0), 500, 7, .01, 7, 0.07);

        // Wilt damage is reduced by nearby light_source interactions
        Location loc = new Location(entity.position(), level);
        int seq = AbilityUtil.getSeqWithArt(entity, this);
        float damageMult = InteractionHandler.isInteractionPossible(loc, "light_source", seq) ? 0.4f : 1f;

        AbilityUtil.damageNearbyEntities((ServerLevel) level, entity, 20 * (int) (Math.max(multiplier(entity)/20,1)), DamageLookup.lookupDamage(8, 1.1) *
                multiplier(entity)
                * damageMult, entity.getEyePosition(), true, false, ModDamageTypes.source(level, ModDamageTypes.DARKNESS_GENERIC, entity));
    }

    private void lullaby(Level level, LivingEntity entity) {
        if(level.isClientSide)
            return;

        level.playSound(null, entity.blockPosition(), ModSounds.MIDNIGHT_POEM.get(), entity.getSoundSource(), 1.0f, 1.0f);
        List<LivingEntity> targets = AbilityUtil.getNearbyEntities(entity, (ServerLevel) level, entity.position(), 35 * (int)(Math.max(multiplier(entity)/20,1)));

        int duration = (int) (20 * 5 * multiplier(entity));

        int seq = AbilityUtil.getSeqWithArt(entity, this);

        targets.forEach(target -> {
            int actualDuration = AbilityUtil.isTargetSignificantlyStronger(seq, BeyonderData.getSequence(target)) ? 35 : AbilityUtil.isTargetSignificantlyWeaker(seq, BeyonderData.getSequence(target)) ? 20 * 25 : duration;
            target.addEffect(new MobEffectInstance(ModEffects.ASLEEP, actualDuration, 1, false, false, true));

            ServerScheduler.scheduleForDuration(0, 3, actualDuration, () -> {
                target.setDeltaMovement(new Vec3(0, 0, 0));
                target.hurtMarked = true;
            });
        });

        ParticleUtil.spawnParticles((ServerLevel) level, dustBig, entity.getEyePosition().subtract(0, .4, 0), 800, 7, 0);
        ParticleUtil.spawnParticles((ServerLevel) level, ModParticles.CRIMSON_LEAF.get(), entity.position().subtract(0, .2, 0), 500, 7, .01, 7, 0.07);

        ServerScheduler.scheduleForDuration(0, 2, duration, () -> {
            targets.forEach(target -> {
                if(target.isAlive())
                    ParticleUtil.spawnParticles((ServerLevel) level, dust, target.getEyePosition().subtract(0, .4, 0), 1, .5, 0);
            });
        }, null, (ServerLevel) level, () -> AbilityUtil.getTimeInArea(entity, new Location(entity.position(), level)));
    }

    private  void console (Level level, LivingEntity entity)
    {
        if(level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        level.playSound(null, entity.blockPosition(), ModSounds.MIDNIGHT_POEM.get(), entity.getSoundSource(), 1.0f, 1.0f);

        ParticleUtil.spawnParticles((ServerLevel) level, dustBig, entity.getEyePosition().subtract(0, .4, 0), 800, 7, 0);
        ParticleUtil.spawnParticles((ServerLevel) level, ModParticles.CRIMSON_LEAF.get(), entity.position().subtract(0, .2, 0), 500, 7, .01, 7, 0.07);


        LuckComponent luckComponent = entity.getData(ModAttachments.LUCK_COMPONENT);
        if(luckComponent.getLuck() < 0) {
            int amplifier = Math.min(Math.round(multiplier(entity) * 750), 3000);
            luckComponent.addLuckWithMax(amplifier, 3000);
        }
        entity.getData(ModAttachments.SANITY_COMPONENT).increaseSanityAndSync((0.15f*(int)Math.max(multiplier(entity)/20,1)), entity);
        // Wilt damage is reduced by nearby light_source interactions
        Location loc = new Location(entity.position(), level);
        int seq = AbilityUtil.getSeqWithArt(entity, this);


        AbilityUtil.getNearbyEntities(entity, serverLevel, entity.getEyePosition(), 10 * (int) (Math.max(multiplier(entity)/10,1))).forEach(e ->
        {
            e.getData(ModAttachments.SANITY_COMPONENT).increaseSanityAndSync((float) (0.15f*(int)Math.max(multiplier(entity)/20,1)), e);
            LuckComponent luckC = e.getData(ModAttachments.LUCK_COMPONENT);
            if(luckC.getLuck() < 0) {
                int amplifier = Math.min(Math.round(multiplier(entity) * 750), 3000);
                luckC.addLuckWithMax(amplifier, 3000);
            }
        });
    }
}
