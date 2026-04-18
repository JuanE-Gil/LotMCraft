package de.jakob.lotm.item.custom;

import de.jakob.lotm.attachments.ApotheosisComponent;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.gamerule.ModGameRules;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.beyonderMap.StoredData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

import static de.jakob.lotm.util.BeyonderData.beyonderMap;

public class UniquenessItem extends Item {

    private final String pathway;

    public UniquenessItem(Properties properties, String pathway) {
        super(properties);
        this.pathway = pathway;
    }

    public String getPathway() {
        return pathway;
    }
}