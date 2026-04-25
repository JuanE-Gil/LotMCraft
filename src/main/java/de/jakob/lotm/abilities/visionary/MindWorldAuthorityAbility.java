package de.jakob.lotm.abilities.visionary;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.common.AngelFlightAbility;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.attachments.DisabledAbilitiesComponent;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class MindWorldAuthorityAbility extends SelectableAbility {
    private MindWorldAuthorityEnvisioning envisioningToggle;

    public MindWorldAuthorityAbility(String id) {
        super(id, 1f);

        canBeCopied = false;
        canBeReplicated = false;
        cannotBeStolen = true;
        canBeUsedByNPC = false;
        canBeUsedInArtifact = false;
        doesNotIncreaseDigestion = true;
        canBeShared = false;

        envisioningToggle = null;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.mind_world_authority_ability.envisioning",
                "ability.lotmcraft.mind_world_authority_ability.seal_mind_world"
        };
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int selectedAbility) {
        switch (selectedAbility){
            case 0 -> envisioning(level, entity);
            case 1 -> sealMindWorld(level, entity);
        }
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("visionary", 0));
    }

    @Override
    protected float getSpiritualityCost() {
        return 1000;
    }

    private void envisioning(Level level, LivingEntity player){
        if(level.isClientSide) return;

        if(envisioningToggle == null)
            envisioningToggle = (MindWorldAuthorityEnvisioning) LOTMCraft.abilityHandler.getById("mind_world_authority_envisioning");

        if(envisioningToggle == null) return;

        envisioningToggle.useAbility((ServerLevel) level, player);
    }

    private void sealMindWorld(Level level, LivingEntity entity){
        if(!(level instanceof ServerLevel serverLevel)) return;
    }

}
