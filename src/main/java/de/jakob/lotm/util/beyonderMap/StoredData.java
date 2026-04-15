package de.jakob.lotm.util.beyonderMap;

import de.jakob.lotm.LOTMCraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;

public record StoredData(String pathway, Integer sequence, HonorificName honorificName,
                         String trueName, LinkedList<MessageType> msgs, LinkedList<HonorificName> knownNames,
                         Boolean modified, Vec3 lastPosition, CharacteristicStack charStack,
                         PathwayHistory pathwayHistory) {

    public static final String NBT_PATHWAY = "beyonder_map_pathway";
    public static final String NBT_SEQUENCE = "beyonder_map_sequence";
    public static final String NBT_HONORIFIC_NAME = "beyonder_map_honorific_name";
    public static final String NBT_TRUE_NAME = "beyonder_map_true_name";
    public static final String NBT_MESSAGES = "beyonder_map_messages";
    public static final String NBT_KNOWN_NAMES = "beyonder_map_known_names";
    public static final String NBT_MODIFIED = "beyonder_map_modified";
    public static final String NBT_CHAR_STACK = "beyonder_map_char_stack";
    public static final String NBT_PATHWAY_HISTORY = "beyonder_map_pathway_history";

    public static final String NBT_LAST_POSITION_X = "beyonder_map_last_position_x";
    public static final String NBT_LAST_POSITION_Y = "beyonder_map_last_position_y";
    public static final String NBT_LAST_POSITION_Z = "beyonder_map_last_position_z";

    public static StoredDataBuilder builder = new StoredDataBuilder();

    public String getShortInfo() {
        return "Path: " + pathway
                + " -- Seq: " + sequence
                + " -- TN: " + trueName;
    }

    public String getAllInfo(){
        return "Name: " + trueName
                + "\n--- Path: " + pathway
                + "\n--- Seq: " + sequence
                + "\n--- Honorific Name: " + honorificName.getAllInfo()
                + "\n--- Logout Position: " + (int) lastPosition.x + " " + (int) lastPosition.y + " " + (int) lastPosition.z
                + "\n--- Characteristics stack: " + getCharStackInfo()
                + "\n--- Pathway history: " + pathwayHistory.getInfo()
                + "\n--- Was modified: " + modified
                ;
    }

    private String getCharStackInfo() {
        StringBuilder sb = new StringBuilder();
        // Walk from most powerful (1) to weakest (9), tracking which pathway was active.
        // The switch records history[prevSeq]=prevPathway (e.g. history[5]="fool" means seq 5+ was fool).
        // Start with current pathway, then switch to historical once we reach the recorded sequence.
        // e.g. history[5]="fool", current="error" → seq 1-4 show "error", seq 5-9 show "fool"
        String activePathway = pathway;
        for (int i = 1; i <= 9; i++) {
            String historyPathway = pathwayHistory.get(i);
            if (historyPathway != null) {
                activePathway = historyPathway;
            }
            sb.append("\n   Seq ").append(i).append(" (").append(activePathway).append("): ").append(charStack.get(i));
        }
        return sb.toString();
    }

    public void addMsg(MessageType msg){
        msgs.add(msg);
    }

    public void removeMsg(MessageType msg){
        msgs.removeIf(str -> str.equals(msg));
    }

    public StoredData regressSeq() {
        return regressSeq(true);
    }

    public StoredData regressSeq(boolean respectCharStack){
        int currentStack = charStack.get(sequence);

        if (respectCharStack && currentStack > 0) {
            // Has stacks: stay at the same sequence, lose one stack, reset digestion to full
            return builder
                    .copyFrom(this)
                    .charStack(charStack.set(sequence, currentStack - 1))
                    .build();
        }

        // No stacks (or ignoring stacks): drop a sequence
        int newSequence = sequence + 1;
        boolean becomesNonBeyonder = newSequence == LOTMCraft.NON_BEYONDER_SEQ;

        // If a domain-switch was recorded for the sequence being restored to, revert to that pathway
        String regressedPathway;
        if (becomesNonBeyonder) {
            regressedPathway = "none";
        } else {
            String historyPathway = pathwayHistory.get(newSequence);
            regressedPathway = (historyPathway != null) ? historyPathway : pathway;
        }

        // Clear history at newSequence (just used) and all sequences more powerful than it (numerically
        // lower), since those belonged to the switched pathway and are no longer relevant
        PathwayHistory clearedHistory = pathwayHistory;
        if (!becomesNonBeyonder) {
            for (int i = 0; i <= newSequence; i++) {
                clearedHistory = clearedHistory.clear(i);
            }
        }

        return builder
                .copyFrom(this)
                .pathway(regressedPathway)
                .sequence(newSequence)
                .honorificName((newSequence >= 3) ? HonorificName.EMPTY : honorificName)
                .charStack(becomesNonBeyonder ? charStack.clear() : charStack.clear(sequence))
                .pathwayHistory(becomesNonBeyonder ? pathwayHistory.clear() : clearedHistory)
                .build();
    }


    public CompoundTag toNBT(){
        CompoundTag tag = new CompoundTag();

        tag.putString(NBT_PATHWAY, pathway);
        tag.putInt(NBT_SEQUENCE, sequence);

        tag.put(NBT_HONORIFIC_NAME, honorificName.toNBT());

        tag.putString(NBT_TRUE_NAME, trueName);

        ListTag list = new ListTag();
        for (MessageType value : msgs) {
            list.add(value.toNBT());
        }

        tag.put(NBT_MESSAGES, list);

//        ListTag list2 = new ListTag();
//        for (MessageType value : knownNames) {
//            list2.add(value.toNBT());
//        }
//
//        tag.put(NBT_KNOWN_NAMES, list2);

        tag.putBoolean(NBT_MODIFIED, modified);

        tag.putDouble(NBT_LAST_POSITION_X, lastPosition.x());
        tag.putDouble(NBT_LAST_POSITION_Y, lastPosition.y());
        tag.putDouble(NBT_LAST_POSITION_Z, lastPosition.z());

        tag.put(NBT_CHAR_STACK, charStack.toNBT());
        tag.put(NBT_PATHWAY_HISTORY, pathwayHistory.toNBT());

        return tag;
    }

    public static StoredData fromNBT(CompoundTag tag){
        String path = tag.getString(NBT_PATHWAY);
        Integer seq = tag.getInt(NBT_SEQUENCE);
        HonorificName name = HonorificName.fromNBT(tag.getCompound(NBT_HONORIFIC_NAME));
        String trueName = tag.getString(NBT_TRUE_NAME);

        LinkedList<MessageType> list = new LinkedList<>();
        for (var t : tag.getList(NBT_MESSAGES, Tag.TAG_COMPOUND)) {
            if(t instanceof CompoundTag compTag)
                list.add(MessageType.fromNBT(compTag));
        }

        LinkedList<HonorificName> names = new LinkedList<>();
//        for (var t : tag.getList(NBT_KNOWN_NAMES, Tag.TAG_COMPOUND)) {
//            if(t instanceof CompoundTag compTag)
//                names.add(HonorificName.fromNBT(compTag));
//        }

        Boolean modified = tag.getBoolean(NBT_MODIFIED);

        Vec3 vec = new Vec3(tag.getDouble(NBT_LAST_POSITION_X),
                tag.getDouble(NBT_LAST_POSITION_Y),
                tag.getDouble(NBT_LAST_POSITION_Z));

        CharacteristicStack stack = CharacteristicStack.fromNBT(tag.getCompound(NBT_CHAR_STACK));
        PathwayHistory history = tag.contains(NBT_PATHWAY_HISTORY) ? PathwayHistory.fromNBT(tag.getCompound(NBT_PATHWAY_HISTORY)) : new PathwayHistory();

        return new StoredData(path, seq, name, trueName, list, names, modified, vec, stack, history);
    }

}
