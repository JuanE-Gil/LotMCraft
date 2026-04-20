package de.jakob.lotm.abilities.visionary;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.attachments.ParasitationComponent;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.effect.ModEffects;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.network.packets.toServer.AbilitySelectionPacket;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.AbilityUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class DreamTraversalAbility extends SelectableAbility {
    private static final HashMap<UUID, UUID> hideMap = new HashMap<>();
    private static final HashMap<UUID, Integer> hideSeqMap = new HashMap<>();
    //this is for artifact stuff
    //tbh wonkiest ability for atifact cuz it uses selectable (even though hide is a toggle style - cuz i didnt wanna make a whole other ability for it tbh

    public DreamTraversalAbility(String id) {
        super(id, 1f);
        this.autoClear = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("visionary", 5));
    }

    @Override
    public float getSpiritualityCost() {
        return 60;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.dream_traversal.jump",
                "ability.lotmcraft.dream_traversal.hide"
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

        if((entitySeq > 3 && selectedAbility >= 0)){
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
        if((entitySeq > 3 && selectedAbility >= 0)){
            selectedAbility = 0;
        }

        selectedAbilities.put(entity.getUUID(), selectedAbility);
        PacketHandler.sendToServer(new AbilitySelectionPacket(getId(), selectedAbility));
    }


    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        switch (abilityIndex) {
            case 0 -> jump(level, entity);
            case 1 -> hide(level, entity);
        }
    }

    private boolean requiresAsleep(LivingEntity entity) {
        return AbilityUtil.getSeqWithArt(entity, this) > 3;
    }

    private void jump(Level level, LivingEntity entity) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        LivingEntity target = AbilityUtil.getTargetEntity(entity, 200 * (int) multiplier(entity), 2);

        if (target == null) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.dream_traversal.no_target").withColor(0xFFff124d));
            return;
        }

        if (requiresAsleep(entity) && !target.hasEffect(ModEffects.ASLEEP)) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.dream_traversal.must_be_asleep").withColor(0xFFff124d));
            return;
        }

        // If currently hiding, jump to new host without cancelling hide
        if (hideMap.containsKey(entity.getUUID())) {
            Entity oldHostEntity = serverLevel.getEntity(hideMap.get(entity.getUUID()));
            if (oldHostEntity instanceof LivingEntity oldHost) {
                ParasitationComponent oldParasite = oldHost.getData(ModAttachments.PARASITE_COMPONENT);
                oldParasite.setParasited(false);
                oldParasite.setParasiteUUID(null);
            }

            hideMap.put(entity.getUUID(), target.getUUID());
            ParasitationComponent newParasite = target.getData(ModAttachments.PARASITE_COMPONENT);
            newParasite.setParasited(true);
            newParasite.setParasiteUUID(entity.getUUID());
        }

        entity.teleportTo(target.getX(), target.getY(), target.getZ());
    }

    private void hide(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (hideMap.containsKey(entity.getUUID())) {
            cancelHide(serverLevel, entity);
            clearArtifactScaling(entity);
            return;
        }

        LivingEntity target = AbilityUtil.getTargetEntity(entity, 20 * (int) multiplier(entity), 2);

        if (target == null) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.dream_traversal.no_target").withColor(0xFFff124d));
            return;
        }

        if (requiresAsleep(entity) && !target.hasEffect(ModEffects.ASLEEP)) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.dream_traversal.must_be_asleep").withColor(0xFFff124d));
            return;
        }

        hideMap.put(entity.getUUID(), target.getUUID());
        // Store sequence at cast time for artifact stuff
        hideSeqMap.put(entity.getUUID(), AbilityUtil.getSeqWithArt(entity, this));

        ParasitationComponent parasitationComponent = target.getData(ModAttachments.PARASITE_COMPONENT);
        parasitationComponent.setParasited(true);
        parasitationComponent.setParasiteUUID(entity.getUUID());

        PsychologicalInvisibilityAbility.add(entity, AbilityUtil.getSeqWithArt(entity, this));

        if (entity instanceof Player player) {
            player.setBoundingBox(new AABB(
                    player.getX(), player.getY(), player.getZ(),
                    player.getX(), player.getY(), player.getZ()
            ));
            player.onUpdateAbilities();
            player.hurtMarked = true;
        }
    }

    public static void cancelHide(ServerLevel serverLevel, LivingEntity entity) {
        if (!hideMap.containsKey(entity.getUUID())) return;

        Entity hostEntity = serverLevel.getEntity(hideMap.get(entity.getUUID()));
        if (hostEntity instanceof LivingEntity host) {
            ParasitationComponent parasitationComponent = host.getData(ModAttachments.PARASITE_COMPONENT);
            parasitationComponent.setParasited(false);
            parasitationComponent.setParasiteUUID(null);
        }

        hideMap.remove(entity.getUUID());
        hideSeqMap.remove(entity.getUUID());

       PsychologicalInvisibilityAbility.remove(entity);

        if (entity instanceof Player player) {
            player.setBoundingBox(player.getDimensions(player.getPose()).makeBoundingBox(
                    player.getX(), player.getY(), player.getZ()
            ));
            player.onUpdateAbilities();
            player.hurtMarked = true;
        }
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if(!(event.getLevel() instanceof ServerLevel level)) return;

        LivingEntity entity = event.getEntity();

        if (hideMap.containsKey(entity.getUUID())) {
            DreamTraversalAbility.cancelHide(level, entity);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity.level().isClientSide) return;
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        if (!hideMap.containsKey(entity.getUUID())) return;

        Entity hostEntity = serverLevel.getEntity(hideMap.get(entity.getUUID()));

        // Use the sequence stored at cast time (ie the artifact if used)
        boolean seqUnlocked = hideSeqMap.getOrDefault(entity.getUUID(), 9) <= 3;

        if (hostEntity == null || hostEntity.isRemoved() || !(hostEntity instanceof LivingEntity host)
                || !host.isAlive() || (!seqUnlocked && !host.hasEffect(ModEffects.ASLEEP))) {

            cancelHide(serverLevel, entity);
            return;
        }

        entity.setInvisible(true);
        entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20, 0, false, false, false));

        Vec3 hostPos = host.position();
        Vec3 floatPos = hostPos.add(0, host.getBbHeight() + 0.3, 0);
        entity.teleportTo(floatPos.x, floatPos.y, floatPos.z);
        entity.setDeltaMovement(Vec3.ZERO);

        if (entity instanceof Player player) {
            player.setBoundingBox(new AABB(
                    player.getX(), player.getY(), player.getZ(),
                    player.getX(), player.getY(), player.getZ()
            ));
            player.hurtMarked = true;
        }
    }

    public static boolean isHiding(UUID uuid) {
        return hideMap.containsKey(uuid);
    }
}
