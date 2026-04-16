package de.jakob.lotm.util;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.PassiveAbilityHandler;
import de.jakob.lotm.abilities.PassiveAbilityItem;
import de.jakob.lotm.abilities.PhysicalEnhancementsAbility;
import de.jakob.lotm.attachments.*;
import de.jakob.lotm.events.BeyonderDataTickHandler;
import de.jakob.lotm.gamerule.ModGameRules;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.network.packets.toClient.SyncBeyonderDataPacket;
import de.jakob.lotm.network.packets.toClient.SyncLivingEntityBeyonderDataPacket;
import de.jakob.lotm.potions.BeyonderPotion;
import de.jakob.lotm.util.beyonderMap.*;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.helper.TeamUtils;
import de.jakob.lotm.util.helper.marionettes.MarionetteComponent;
import de.jakob.lotm.util.pathways.PathwayInfos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class BeyonderData {
    private static final int[] spiritualityLookup = {150000, 20000, 10000, 5000, 3900, 1900, 1200, 780, 200, 180};
    private static final double[] multiplier = {9, 4.25, 3.25, 2.15, 1.85, 1.4, 1.25, 1.1, 1.0, 1.0};
    private static final double[] sanityDecreaseMultiplier = {.003, .0125, .025, .05, .1, .65, .75, .88, 1.0, 1.0};

    public static final HashMap<String, List<Integer>> implementedRecipes = new HashMap<>();

    public static BeyonderMap beyonderMap;

    static {
        implementedRecipes.put("fool", List.of(new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1}));
        implementedRecipes.put("door", List.of(new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1}));
        implementedRecipes.put("sun", List.of(new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1}));
        implementedRecipes.put("tyrant", List.of(new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1}));
        implementedRecipes.put("darkness", List.of(new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1}));
        implementedRecipes.put("demoness", List.of(new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1}));
        implementedRecipes.put("red_priest", List.of(new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1}));
        implementedRecipes.put("visionary", List.of(new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1}));
        implementedRecipes.put("mother", List.of(new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1}));
        implementedRecipes.put("abyss", List.of(new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1}));
        implementedRecipes.put("wheel_of_fortune", List.of(new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1}));
        implementedRecipes.put("error", List.of(new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1}));

    }

    public static final List<String> pathways = List.of(
            "fool",
            "error",
            "door",
            "visionary",
            "sun",
            "tyrant",
            "white_tower",
            "hanged_man",
            "darkness",
            "death",
            "twilight_giant",
            "demoness",
            "red_priest",
            "hermit",
            "paragon",
            "wheel_of_fortune",
            "mother",
            "moon",
            "abyss",
            "chained",
            "black_emperor",
            "justiciar"
    );

    public static final List<String> implementedPathways = List.of(
            "fool",
            "door",
            "error",
            "sun",
            "tyrant",
            "darkness",
            "demoness",
            "red_priest",
            "mother",
            "mother",
            "abyss",
            "visionary",
            "wheel_of_fortune"
    );

    public static int getHighestImplementedSequence(String pathway) {
        return switch (pathway) {
            case "mother", "darkness", "fool", "wheel_of_fortune", "error", "visionary", "demoness", "red_priest", "sun", "tyrant", "door", "abyss" -> 1;
            default -> 9;
        };
    }

    public static String getSequenceName(String pathway, int sequence) {
        if(!pathwayInfos.containsKey(pathway))
            return "Unknown";

        PathwayInfos infos = pathwayInfos.get(pathway);
        if(sequence == LOTMCraft.NON_BEYONDER_SEQ || sequence >= infos.sequenceNames().length)
            return "Unknown";

        return infos.getSequenceName(sequence);
    }

    public static final HashMap<String, PathwayInfos> pathwayInfos = new HashMap<>();

    public static void initBeyonderMap(ServerLevel level){
        beyonderMap = BeyonderMap.get(level);
        beyonderMap.setLevel(level);
    }

    public static void initPathwayInfos() {
        pathwayInfos.put("fool", new PathwayInfos("fool", 0xFF864ec7, new String[]{"fool", "attendant_of_mysteries", "miracle_invoker", "scholar_of_yore", "bizarro_sorcerer", "marionettist", "faceless", "magician", "clown", "seer"}, new String[]{"error", "door"}));
        pathwayInfos.put("error", new PathwayInfos("error", 0xFF0018b8, new String[]{"error", "worm_of_time", "trojan_horse_of_destiny", "mentor_of_deceit", "parasite", "dream_stealer", "prometheus", "cryptologist", "swindler", "marauder"}, new String[]{"fool", "door"}));
        pathwayInfos.put("door", new PathwayInfos("door", 0xFF89f5f5, new String[]{"door", "key_of_stars", "planeswalker", "wanderer", "secrets_sorcerer", "traveler", "scribe", "astrologer", "trickmaster", "apprentice"}, new String[]{"error", "error"}));
        pathwayInfos.put("visionary", new PathwayInfos("visionary", 0xFFe3ffff, new String[]{"visionary", "author", "discerner", "dream_weaver", "manipulator", "dreamwalker", "hypnotist", "psychiatrist", "telepathist", "spectator"}, new String[]{"sun", "hanged_man", "white_tower", "tyrant"}));
        pathwayInfos.put("sun", new PathwayInfos("sun", 0xFFffad33, new String[]{"sun", "white_angel", "lightseeker", "justice_mentor", "unshadowed", "priest_of_light", "notary", "solar_high_priest", "light_supplicant", "bard"}, new String[]{"visionary", "hanged_man", "white_tower", "tyrant"}));
        pathwayInfos.put("tyrant", new PathwayInfos("tyrant", 0xFF336dff, new String[]{"tyrant", "thunder_god", "calamity", "sea_king", "cataclysmic_interrer", "ocean_songster", "wind_blessed", "seafarer", "folk_of_rage", "sailor"}, new String[]{"sun", "hanged_man", "white_tower", "visionary"}));
        pathwayInfos.put("white_tower", new PathwayInfos("white_tower", 0xFF8cadff, new String[]{"white_tower", "omniscient_eye", "wisdom_angel", "cognizer", "prophet", "mysticism_magister", "polymath", "detective", "student_of_ratiocination", "reader"}, new String[]{"sun", "hanged_man", "visionary", "tyrant"}));
        pathwayInfos.put("hanged_man", new PathwayInfos("hanged_man", 0xFF8a0a0a, new String[]{"hanged_man", "dark_angel", "profane_presbyter", "trinity_templar", "black_knight", "shepherd", "rose_bishop", "shadow_ascetic", "listener", "secrets_supplicant"}, new String[]{"sun", "visionary", "white_tower", "tyrant"}));
        pathwayInfos.put("darkness", new PathwayInfos("darkness", 0xFF3300b5, new String[]{"darkness", "knight_of_misfortune", "servant_of_concealment", "horror_bishop", "nightwatcher", "spirit_warlock", "soul_assurer", "nightmare", "midnight_poet", "sleepless"}, new String[]{"death", "twilight_giant"}));
        pathwayInfos.put("death", new PathwayInfos("death", 0xFF334f23, new String[]{"death", "pale_emperor", "death_consul", "ferryman", "undying", "gatekeeper", "spirit_guide", "spirit_medium", "gravedigger", "corpse_collector"}, new String[]{"darkness", "twilight_giant"}));
        pathwayInfos.put("twilight_giant", new PathwayInfos("twilight_giant", 0xFF944b16, new String[]{"twilight_giant", "hand_of_god", "glory", "silver_knight", "demon_hunter", "guardian", "dawn_paladin", "weapon_master", "pugilist", "warrior"}, new String[]{"death", "darkness"}));
        pathwayInfos.put("demoness", new PathwayInfos("demoness", 0xFFc014c9, new String[]{"demoness", "demoness_of_apocalypse", "demoness_of_catastrophe", "demoness_of_unaging", "demoness_of_despair", "demoness_of_affliction", "demoness_of_pleasure", "witch", "instigator", "assassin"}, new String[]{"red_priest"}));
        pathwayInfos.put("red_priest", new PathwayInfos("red_priest", 0xFFb80000, new String[]{"red_priest", "conqueror", "weather_warlock", "war_bishop", "iron_blooded_knight", "reaper", "conspirer", "pyromaniac", "provoker", "hunter"}, new String[]{"demoness"}));
        pathwayInfos.put("hermit", new PathwayInfos("hermit", 0xFF832ed9, new String[]{"hermit", "knowledge_emperor", "sage", "clairvoyant", "mysticologist", "constellations_master", "scrolls_professor", "warlock", "melee_scholar", "mystery_pryer"}, new String[]{"paragon"}));
        pathwayInfos.put("paragon", new PathwayInfos("paragon", 0xFFf58e40, new String[]{"paragon", "illuminator", "knowledge_master", "arcane_scholar", "alchemist", "astronomer", "artisan", "appraiser", "archeologist", "savant"}, new String[]{"hermit"}));
        pathwayInfos.put("wheel_of_fortune", new PathwayInfos("wheel_of_fortune", 0xFFbad2f5, new String[]{"wheel_of_fortune", "snake_of_mercury", "soothsayer", "chaoswalker", "misfortune_mage", "winner", "calamity_priest", "lucky_one", "robot", "monster"}, new String[]{}));
        pathwayInfos.put("mother", new PathwayInfos("mother", 0xFF6bdb94, new String[]{"mother", "naturewalker", "desolate_matriarch", "pallbearer", "classical_alchemist", "druid", "biologist", "harvest_priest", "doctor", "planter"}, new String[]{"moon"}));
        pathwayInfos.put("moon", new PathwayInfos("moon", 0xFFf5384b, new String[]{"moon", "beauty_goddess", "life-giver", "high_summoner", "shaman_king", "scarlet_scholar", "potions_professor", "vampire", "beast_tamer", "apothecary"}, new String[]{"mother"}));
        pathwayInfos.put("abyss", new PathwayInfos("abyss", 0xFFa3070c, new String[]{"abyss", "filthy_monarch", "bloody_archduke", "blatherer", "demon", "desire_apostle", "devil", "serial_killer", "unwinged_angel", "criminal"}, new String[]{"chained"}));
        pathwayInfos.put("chained", new PathwayInfos("chained", 0xFFb18fbf, new String[]{"chained", "abomination", "ancient_bane", "disciple_of_silence", "puppet", "wraith", "zombie", "werewolf", "lunatic", "prisoner"}, new String[]{"abyss"}));
        pathwayInfos.put("black_emperor", new PathwayInfos("black_emperor", 0xFF181040, new String[]{"black_emperor", "prince_of_abolition", "duke_of_entropy", "frenzied_mage", "ear_of_the_fallen", "mentor_of_disorder", "baron_of_corruption", "briber", "barbarian", "lawyer"}, new String[]{"justiciar"}));
        pathwayInfos.put("justiciar", new PathwayInfos("justiciar", 0xFFfcd99f, new String[]{"justiciar", "hand_of_order", "balancer", "chaos_hunter", "imperative_mage", "disciplinary_paladin", "judge", "interrogator", "sheriff", "arbiter"}, new String[]{"black_emperor"}));
        pathwayInfos.put("placeholder", new PathwayInfos("placeholder", 0xFFfcd99f, new String[]{"", "", "", "", "", "", "", "", "", "",}, new String[]{}));
    }

    public static void setBeyonder(LivingEntity entity, String pathway, int sequence) {
        setBeyonder(entity, pathway, sequence, false, false, true);
    }

    public static void setBeyonder(LivingEntity entity, String pathway, int sequence, boolean skipCheck, boolean clearPathwayHistory, boolean addToPathwayHistory) {
        if(entity.level() instanceof ServerLevel serverLevel) {
            callPassiveEffectsOnRemoved(entity, serverLevel);
        }

        if(entity instanceof ServerPlayer player) {
            if(!skipCheck && !beyonderMap.check(pathway, sequence)) return;

            if(!BeyonderData.getPathway(player).equals(pathway)
                    || BeyonderData.getSequence(player) < sequence)
                beyonderMap.removeHonorificName(player);

            beyonderMap.setStack(player, 0);
        }

        if(Objects.equals(sequence, LOTMCraft.NON_BEYONDER_SEQ)
                || pathway.equals("none")) {
            clearBeyonderData(entity);
            return;
        }

        boolean griefing = !BeyonderData.isBeyonder(entity) || BeyonderData.isGriefingEnabled(entity);

        BeyonderComponent component = entity.getData(ModAttachments.BEYONDER_COMPONENT);
        component.setPathway(pathway);
        component.setSequence(sequence);
        component.setCharacteristicStack(0);
        component.setSpirituality(getMaxSpirituality(sequence));
        component.setDigestionProgress(0);
        component.setGriefingEnabled(griefing);

        if(entity instanceof Player player)
            SpiritualityProgressTracker.setProgress(player.getUUID(), 1.0f);

        BeyonderDataTickHandler.invalidateCache(entity);

        if(clearPathwayHistory) {
            component.setPathwayHistory(new String[10]);
        }
        if(addToPathwayHistory) {
            component.getPathwayHistory()[sequence] = pathway;
        }

        LuckComponent luckComponent = entity.getData(ModAttachments.LUCK_COMPONENT);
        luckComponent.setLuck(0);

        // Sync to client if this is server-side
        if (entity.level() instanceof ServerLevel serverLevel) {

            callPassiveEffectsOnAdd(entity, serverLevel);

            if(entity instanceof ServerPlayer serverPlayer) {
                PacketHandler.syncBeyonderDataToPlayer(serverPlayer);
                beyonderMap.put(serverPlayer);

                SyncBeyonderDataPacket packet = new SyncBeyonderDataPacket(pathway, sequence, 0.0f, false, 0.0f, component.getPathwayHistory(), 0);
                PacketHandler.sendToAllPlayers(packet);

                // Disband team if the leader is no longer eligible (Red Priest seq <= 3).
                // Only applies when this player is actually the leader (has members) — members
                // advancing their own sequence should never trigger a disband.
                TeamComponent teamComp = serverPlayer.getData(ModAttachments.TEAM_COMPONENT.get());
                if (teamComp.memberCount() > 0 && !TeamUtils.isEligibleLeader(serverPlayer)) {
                    TeamUtils.disbandTeam(serverPlayer, serverPlayer.getServer());
                }
            }
            else {
                PacketHandler.syncBeyonderDataToEntity(entity);
            }
        }

    }

    private static void callPassiveEffectsOnRemoved(LivingEntity entity, ServerLevel serverLevel) {
        List<PassiveAbilityItem> passiveAbilities = new ArrayList<>(PassiveAbilityHandler.ITEMS.getEntries().stream().filter(entry -> {
            if (!(entry.get() instanceof PassiveAbilityItem abilityItem))
                return false;
            return abilityItem.shouldApplyTo(entity);
        }).map(entry -> (PassiveAbilityItem) entry.get()).toList());

        for (PassiveAbilityItem ability : passiveAbilities) {
            ability.onPassiveAbilityRemoved(entity, serverLevel);
        }
    }

    private static void callPassiveEffectsOnAdd(LivingEntity entity, ServerLevel serverLevel) {
        List<PassiveAbilityItem> passiveAbilities = new ArrayList<>(PassiveAbilityHandler.ITEMS.getEntries().stream().filter(entry -> {
            if (!(entry.get() instanceof PassiveAbilityItem abilityItem))
                return false;
            return abilityItem.shouldApplyTo(entity);
        }).map(entry -> (PassiveAbilityItem) entry.get()).toList());

        for (PassiveAbilityItem ability : passiveAbilities) {
            ability.onPassiveAbilityGained(entity, serverLevel);
        }
    }

    public static String getPathway(LivingEntity entity) {
        if(entity.level().isClientSide) {
            return ClientBeyonderCache.getPathway(entity.getUUID());
        }
        BeyonderComponent component = entity.getData(ModAttachments.BEYONDER_COMPONENT);
        String pathway = component.getPathway();
        return pathway == null || pathway.isEmpty() ? "none" : pathway;
    }

    public static int getSequence(LivingEntity entity) {
        return getSequence(entity, false);
    }

    public static int getSequence(LivingEntity entity, boolean returnTrueMarionetteLvl) {
        if(entity == null) return LOTMCraft.NON_BEYONDER_SEQ;

        if(entity.level().isClientSide) {
            return ClientBeyonderCache.getSequence(entity.getUUID());
        }

        if (!returnTrueMarionetteLvl) {
            MarionetteComponent marionetteComponent = entity.getData(ModAttachments.MARIONETTE_COMPONENT);
            if(marionetteComponent.isMarionette()) {
                UUID controllerUUID = UUID.fromString(marionetteComponent.getControllerUUID());
                Entity owner = ((ServerLevel) entity.level()).getEntity(controllerUUID);
                if(owner instanceof LivingEntity ownerLiving) {
                    int ownerSequence = getSequence(ownerLiving);

                    BeyonderComponent component = entity.getData(ModAttachments.BEYONDER_COMPONENT);
                    int entitySequence = component.getSequence();

                    if (entitySequence < 0 || entitySequence == LOTMCraft.NON_BEYONDER_SEQ) {
                        return ownerSequence;
                    }
                    return Math.max(entitySequence, ownerSequence);
                }
            }
        }

        BeyonderComponent component = entity.getData(ModAttachments.BEYONDER_COMPONENT);
        return component.getSequence();
    }

    public static float getSpirituality(LivingEntity entity) {
        if(entity.level().isClientSide) {
            return ClientBeyonderCache.getSpirituality(entity.getUUID());
        }
        if(!(entity instanceof Player player))
            return getMaxSpirituality(getSequence(entity));
        float spirituality = entity.getData(ModAttachments.BEYONDER_COMPONENT).getSpirituality();
        float maxSpirituality = getMaxSpirituality(getSequence(entity));

        if(maxSpirituality <= 0) {
            return 0.0f;
        }

        float progress = spirituality / maxSpirituality;
        SpiritualityProgressTracker.setProgress(player.getUUID(), progress);

        return Math.max(0, spirituality);
    }

    public static void reduceSpirituality(LivingEntity entity, float amount) {
        if(!(entity instanceof Player player))
            return;
        float current = getSpirituality(entity);
        entity.getData(ModAttachments.BEYONDER_COMPONENT).setSpirituality(Math.max(0, current - amount));

        float maxSpirituality = getMaxSpirituality(getSequence(entity));

        if(maxSpirituality <= 0) {
            return;
        }

        float progress = (current - amount) / maxSpirituality;
        SpiritualityProgressTracker.setProgress(player.getUUID(), progress);

        // Sync to client if this is server-side
        if (!entity.level().isClientSide() && entity instanceof ServerPlayer serverPlayer) {
            PacketHandler.syncBeyonderDataToPlayer(serverPlayer);
        }
    }

    public static Optional<HonorificName> getHonorificName(LivingEntity entity){
        if(entity.level().isClientSide() || !(entity instanceof ServerPlayer))
            return Optional.empty();

        if(beyonderMap.get(entity).isEmpty()) return Optional.empty();

        StoredData data = beyonderMap.get(entity).get();

        return Optional.of(data.honorificName());
    }

    public static void setHonorificName(LivingEntity entity, HonorificName name){
        if(entity.level().isClientSide()) return;

        beyonderMap.addHonorificName(entity, name);
    }

    public static double getMultiplier(LivingEntity entity) {
        if(!BeyonderData.isBeyonder(entity))
            return 1;

        int sequence = getSequence(entity);
        if (sequence < 0 || sequence >= multiplier.length)
            return 1.0;

        double damageMultiplier = multiplier[sequence];

        MultiplierModifierComponent modifierComponent = entity.getData(ModAttachments.MULTIPLIER_MODIFIER_COMPONENT);

        if(modifierComponent.modifiers.isEmpty())
            return damageMultiplier;

        for(float d : modifierComponent.modifiers.values().stream().map(MultiplierModifierComponent.MultiplierModifier::multiplier).toList()) {
            damageMultiplier *= d;
        }

        return damageMultiplier;
    }


    public static double getMultiplierForSequence(int sequence) {
        return multiplier[sequence];
    }

    public static double getSanityDecreaseMultiplierForSequence(int sequence) {
        return sanityDecreaseMultiplier[sequence];
    }

    public static void incrementSpirituality(LivingEntity entity, float amount) {
        if(!(entity instanceof Player player))
            return;

        float current = getSpirituality(player);
        float newAmount = Math.min(getMaxSpirituality(getSequence(player)), current + amount);
        player.getData(ModAttachments.BEYONDER_COMPONENT).setSpirituality(newAmount);

        float maxSpirituality = getMaxSpirituality(getSequence(player));

        if(maxSpirituality <= 0) {
            return;
        }

        float progress = newAmount / maxSpirituality;
        SpiritualityProgressTracker.setProgress(player.getUUID(), progress);

        // Sync to client if this is server-side
        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PacketHandler.syncBeyonderDataToPlayer(serverPlayer);
        }
    }

    public static float getMaxSpirituality(int sequence) {
        return sequence > -1 && sequence != LOTMCraft.NON_BEYONDER_SEQ && sequence < spiritualityLookup.length ? spiritualityLookup[sequence] : 0.0f;
    }

    public static void setDigestionProgress(LivingEntity entity, float progress) {
        if(!(entity instanceof Player player))
            return;

        player.getData(ModAttachments.BEYONDER_COMPONENT).setDigestionProgress(progress);

        // Sync to client if this is server-side
        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PacketHandler.syncBeyonderDataToPlayer(serverPlayer);
        }
    }

    public static void clearBeyonderData(LivingEntity entity) {
        BeyonderComponent component = entity.getData(ModAttachments.BEYONDER_COMPONENT);
        component.setPathway("none");
        component.setSequence(LOTMCraft.NON_BEYONDER_SEQ);
        component.setSpirituality(0);
        component.setGriefingEnabled(true);
        component.setDigestionProgress(0);

        if(entity instanceof Player player) {
            SpiritualityProgressTracker.removeProgress(player);
            beyonderMap.remove(player);
        }

        // Sync to client if this is server-side
        if (!entity.level().isClientSide()) {
            if(entity instanceof ServerPlayer serverPlayer) {
                // Send empty data to clear client cache
                SyncBeyonderDataPacket packet = new SyncBeyonderDataPacket("none", 10, 0.0f, false, 0.0f, new String[10], 0);
                PacketHandler.sendToPlayer(serverPlayer, packet);
            }
            else {
                SyncLivingEntityBeyonderDataPacket packet =
                        new SyncLivingEntityBeyonderDataPacket(entity.getId(), "none", 10, 0.0f);
                PacketHandler.sendToAllPlayers(packet); // broadcast to all players tracking this entity
            }
        }
    }

    public static boolean isBeyonder(LivingEntity entity) {
        if (entity.level().isClientSide) {
            return ClientBeyonderCache.isBeyonder(entity.getUUID());
        }

        BeyonderComponent component = entity.getData(ModAttachments.BEYONDER_COMPONENT);
        String pathway = component.getPathway();
        int sequence = component.getSequence();

        return pathway != null                                 &&
                !pathway.equalsIgnoreCase("none") &&
                !pathway.isBlank()                             &&
                sequence != LOTMCraft.NON_BEYONDER_SEQ;
    }

    public static void addModifier(LivingEntity entity, String id, double modifier) {
        MultiplierModifierComponent component = entity.getData(ModAttachments.MULTIPLIER_MODIFIER_COMPONENT);
        component.addMultiplier(id, (float) modifier);
    }


    public static void removeModifier(LivingEntity entity, String id) {
        MultiplierModifierComponent component = entity.getData(ModAttachments.MULTIPLIER_MODIFIER_COMPONENT);
        component.removeMultiplier(id);
    }


    public static void addModifierWithTimeLimit(
            LivingEntity entity,
            String id,
            double modifier,
            long millis
    ) {
        MultiplierModifierComponent component = entity.getData(ModAttachments.MULTIPLIER_MODIFIER_COMPONENT);
        int ticks = (int) (millis / 50);
        component.addMultiplierForTime(id, (float) modifier, ticks);
    }

    public static boolean isGriefingEnabled(Player player) {
        if (player.level().isClientSide()) {
            // On client side, read from cache instead of NBT
            return ClientBeyonderCache.isGriefingEnabled(player.getUUID());
        }

        if(!player.level().getGameRules().getBoolean(ModGameRules.ALLOW_GRIEFING)) {
            return false;
        }

        return player.getData(ModAttachments.BEYONDER_COMPONENT).isGriefingEnabled();
    }

    public static float getDigestionProgress(Player player) {
        if(player.level().isClientSide) {
            return ClientBeyonderCache.getDigestionProgress(player.getUUID());
        }

        return player.getData(ModAttachments.BEYONDER_COMPONENT).getDigestionProgress();
    }

    public static int getCharStack(LivingEntity entity) {
        if(entity.level().isClientSide) {
            return ClientBeyonderCache.getCharStack(entity.getUUID());
        }

        return entity.getData(ModAttachments.BEYONDER_COMPONENT).getCharacteristicStack();
    }

    public static String[] getPathwayHistory(LivingEntity entity) {
        if(entity.level().isClientSide) {
            return ClientBeyonderCache.getPathwayHistory(entity.getUUID());
        }

        String[] history = entity.getData(ModAttachments.BEYONDER_COMPONENT).getPathwayHistory();
        if(history == null || history.length < 10) {
            return new String[10];
        }
        return history;
    }

    public static void digest(Player player, float amount, boolean countTowardsCooldown) {
        float current = getDigestionProgress(player);
        float newAmount = Math.min(1.0f, current + amount);
        if(newAmount == 1.0f && current < 1.0f) {
            AbilityUtil.sendActionBar(player, Component.translatable("lotm.digested").withColor(0xbd64d1));
            if(player.level() instanceof ServerLevel serverLevel) {
                ParticleUtil.spawnParticles(serverLevel, ParticleTypes.END_ROD, player.position().add(0, player.getEyeHeight() / 2, 0), 30, .5, player.getEyeHeight() / 2, .5, 0.06);
            }
            else {
                player.playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 1, 1);
            }
            newAmount = 1.01f;
        }
        player.getData(ModAttachments.BEYONDER_COMPONENT).setDigestionProgress(newAmount);

        // Sync to client if this is server-side
        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PacketHandler.syncBeyonderDataToPlayer(serverPlayer);
        }
    }

    public static boolean isGriefingEnabled(LivingEntity entity) {
        if(!(entity instanceof Player player)) {
            return false;
        }
        return isGriefingEnabled(player);
    }

    private static float getRelativeSpirituality(Player player) {
        float maxSpirituality = getMaxSpirituality(getSequence(player));
        if (maxSpirituality <= 0) {
            return 0.0f;
        }
        return getSpirituality(player) / maxSpirituality;
    }

    public static void setGriefingEnabled(Player player, boolean enabled) {
        player.getData(ModAttachments.BEYONDER_COMPONENT).setGriefingEnabled(enabled);

        // Sync to client if this is server-side
        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PacketHandler.syncBeyonderDataToPlayer(serverPlayer);
        }
    }

    public static void addCharStack(LivingEntity player) {
        if (!isBeyonder(player)) return;

        beyonderMap.addStack(player, 1);
        BeyonderComponent component = player.getData(ModAttachments.BEYONDER_COMPONENT);
        component.setCharacteristicStack(component.getCharacteristicStack() + 1);
        component.setDigestionProgress(0);

        recalculateCharStackModifiers(player);
        if (player instanceof ServerPlayer sp) PacketHandler.syncBeyonderDataToPlayer(sp);
    }

    public static void setCharStack(LivingEntity player, int value, boolean ignoreDigestion) {
        if (!isBeyonder(player)) return;

        beyonderMap.setStack(player, value);
        BeyonderComponent component = player.getData(ModAttachments.BEYONDER_COMPONENT);
        component.setCharacteristicStack(value);

        if (!ignoreDigestion)
            component.setDigestionProgress(0);

        recalculateCharStackModifiers(player);
        if (player instanceof ServerPlayer sp) PacketHandler.syncBeyonderDataToPlayer(sp);
    }

    private static final String CHAR_STACK_BOOST_ID = "characteristics_stack_boost";

    public static void recalculateCharStackModifiers(LivingEntity player) {
        if (!isBeyonder(player)) return;

        // Remove any previously applied boost
        removeModifier(player, CHAR_STACK_BOOST_ID);

        int seq    = getSequence(player);
        int stacks = player.getData(ModAttachments.BEYONDER_COMPONENT).getCharacteristicStack();

        if (stacks != 0) {
            addModifier(player, CHAR_STACK_BOOST_ID, getDamageBoostByCharStack(seq, stacks));
        }
    }

    public static float getDamageBoostByCharStack(int seq, int stacks){
        return switch (seq){
            case 9 -> 1.025f;
            case 8 -> 1.05f;
            case 7 -> 1.075f;
            case 6 -> 1.105f;
            case 5 -> 1.15f;
            case 4 -> 1.3f;
            case 3 -> 1.4f;
            case 2 -> 1.5f;
            case 1 -> 1.0f + stacks;
            default -> 0.0f;
        };
    }
}