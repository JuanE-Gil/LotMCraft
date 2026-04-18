package de.jakob.lotm.abilities.visionary.prophecy.actions;

import de.jakob.lotm.abilities.visionary.prophecy.TokenStream;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextEnum;
import de.jakob.lotm.abilities.visionary.prophecy.actions.implementations.DropItemAction;
import de.jakob.lotm.util.BeyonderData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class ActionsHelper {

    private static @Nullable ActionsEnum getType(String str){
        return switch (str){
            case "drop" -> ActionsEnum.DROP_ITEM;
            default -> null;
        };
    }

    private static ActionContextEnum getContextType(ActionsEnum value){
        return switch (value){
            case ActionsEnum.DROP_ITEM -> ActionContextEnum.ITEM;
        };
    }

    public static @Nullable ActionBase deduceActionWithContext(String str){
        TokenStream stream = new TokenStream(str);

        if(stream.match("if"))
            stream.next();

        String nick = stream.peek();
        UUID id = BeyonderData.beyonderMap.getKeyByName(nick);

        stream = moveToThen(stream);

        var actionType = getType(Objects.requireNonNull(stream.next()));
        if(actionType == null) return null;

        var contextType = getContextType(actionType);
        if(contextType == null) return null;

        var context = ActionContextBase.create(contextType, id);
        context.fillFromStream(stream);

        return ActionBase.create(actionType, context);
    }

    private static TokenStream moveToThen(TokenStream stream){
        if(stream.match("then")) return stream;
        else {
            stream.next();
            return moveToThen(stream);
        }
    }

    public static Item getItemFromString(String input) {
        ResourceLocation id = ResourceLocation.tryParse(input);

        if (id == null) {
            return null; // invalid format
        }

        return BuiltInRegistries.ITEM.getOptional(id).orElse(null);
    }
}
