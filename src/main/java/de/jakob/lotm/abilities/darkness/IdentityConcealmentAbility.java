package de.jakob.lotm.abilities.darkness;

import com.google.common.util.concurrent.AtomicDouble;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.dimension.ModDimensions;
import de.jakob.lotm.effect.ModEffects;
import de.jakob.lotm.rendering.effectRendering.EffectManager;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.data.Location;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.TemporaryChunkLoader;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import de.jakob.lotm.util.shapeShifting.NameUtils;
import de.jakob.lotm.util.shapeShifting.ShapeShiftingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.*;

public class IdentityConcealmentAbility extends SelectableAbility {
    public IdentityConcealmentAbility(String id) {
        super(id, 5);
        this.canBeCopied = false;
        canBeReplicated = false;
        canBeUsedInArtifact = false;
        autoClear = false;
        cannotBeStolen = true;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("darkness", 2));
    }

    @Override
    protected float getSpiritualityCost() {
        return 3000;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.identityconcealment.conceal_identity",
                "ability.lotmcraft.identityconcealment.remove_conceal_identity"
        };
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {

        if(!(entity instanceof Player)) abilityIndex = 0;
        switch(abilityIndex) {
            case 0 -> concealIdentity(level,entity);
            case 1 -> removeConcealIdentity(level,entity);
        }
    }
    private void concealIdentity(Level level, LivingEntity entity) {
        if(level.isClientSide) return;

        if(!(entity instanceof ServerPlayer player)) return;

        level.playSound(null,
                player.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.BLOCKS,
                10.0f,
                1.0f);
        LivingEntity targetEntity = AbilityUtil.getTargetEntity(entity, 16, 2);
        String playerName = "§1";
        if(targetEntity == null){
            //NameUtils.setPlayerName(player, playerName);
            AttributeInstance attribute = player.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
            if (attribute != null) {
                attribute.setBaseValue(0);
            }
            return;
        }
        //NameUtils.setPlayerName((ServerPlayer) targetEntity, playerName);
        AttributeInstance attribute = targetEntity.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
        if (attribute != null) {
            attribute.setBaseValue(0);
        }


    };
    private void removeConcealIdentity(Level level, LivingEntity entity) {
        if(level.isClientSide) return;

        if(!(entity instanceof ServerPlayer player)) return;

        level.playSound(null,
                player.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.BLOCKS,
                10.0f,
                1.0f);
        LivingEntity targetEntity = AbilityUtil.getTargetEntity(entity, 16, 2);
        if(targetEntity == null){
            //ShapeShiftingUtil.resetShape(player);
            AttributeInstance attribute = player.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
            if (attribute != null) {
                attribute.setBaseValue(64);
            }
            return;
        };
        //ShapeShiftingUtil.resetShape((ServerPlayer) targetEntity);
        AttributeInstance attribute = targetEntity.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
        if (attribute != null) {
            attribute.setBaseValue(64);
        }

    };

}