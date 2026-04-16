package de.jakob.lotm.util.beyonderMap;

import de.jakob.lotm.LOTMCraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.LinkedList;

public record StoredData(String pathway, Integer sequence, HonorificName honorificName,
                         String trueName, LinkedList<MessageType> msgs, LinkedList<HonorificName> knownNames,
                         Boolean modified, Vec3 lastPosition,
                         int charStack,           // single int — replaces CharacteristicStack
                         String[] pathwayHistory  // fixed size 10 — replaces PathwayHistory record
) {

    public static final String NBT_PATHWAY         = "beyonder_map_pathway";
    public static final String NBT_SEQUENCE        = "beyonder_map_sequence";
    public static final String NBT_HONORIFIC_NAME  = "beyonder_map_honorific_name";
    public static final String NBT_TRUE_NAME       = "beyonder_map_true_name";
    public static final String NBT_MESSAGES        = "beyonder_map_messages";
    public static final String NBT_MODIFIED        = "beyonder_map_modified";
    public static final String NBT_CHAR_STACK      = "beyonder_map_char_stack";
    public static final String NBT_PATHWAY_HISTORY = "beyonder_map_pathway_history";

    public static final String NBT_LAST_POSITION_X = "beyonder_map_last_position_x";
    public static final String NBT_LAST_POSITION_Y = "beyonder_map_last_position_y";
    public static final String NBT_LAST_POSITION_Z = "beyonder_map_last_position_z";

    public static final StoredDataBuilder builder = new StoredDataBuilder();

    // ── helpers ──────────────────────────────────────────────────────────────

    public String getShortInfo() {
        return "Path: " + pathway + " -- Seq: " + sequence + " -- TN: " + trueName;
    }

    public String getAllInfo() {
        return "Name: " + trueName
                + "\n--- Path: " + pathway
                + "\n--- Seq: " + sequence
                + "\n--- Honorific Name: " + honorificName.getAllInfo()
                + "\n--- Logout Position: " + (int) lastPosition.x + " " + (int) lastPosition.y + " " + (int) lastPosition.z
                + "\n--- Char stack: " + charStack
                + "\n--- Pathway history: " + getPathwayHistoryInfo()
                + "\n--- Was modified: " + modified;
    }

    private String getPathwayHistoryInfo() {
        StringBuilder sb = new StringBuilder();
        boolean any = false;
        for (int i = 9; i >= 0; i--) {
            String p = pathwayHistory[i];
            if (p != null && !p.isEmpty()) {
                sb.append("\n   Seq ").append(i).append(": ").append(p);
                any = true;
            }
        }
        return any ? sb.toString() : " None";
    }

    public void addMsg(MessageType msg)    { msgs.add(msg); }
    public void removeMsg(MessageType msg) { msgs.removeIf(str -> str.equals(msg)); }

    // ── regression ───────────────────────────────────────────────────────────

    public StoredData regressSeq() { return regressSeq(true); }

    public StoredData regressSeq(boolean respectCharStack) {
        if (respectCharStack && charStack > 0) {
            // Still has stacks — lose one, stay at current sequence
            return builder.copyFrom(this).charStack(charStack - 1).build();
        }

        int newSequence = sequence + 1;
        boolean becomesNonBeyonder = (newSequence == LOTMCraft.NON_BEYONDER_SEQ);

        // Revert pathway from history if a domain-switch was recorded here
        String regressedPathway;
        if (becomesNonBeyonder) {
            regressedPathway = "none";
        } else {
            String historyEntry = pathwayHistory[newSequence];
            regressedPathway = (historyEntry != null && !historyEntry.isEmpty()) ? historyEntry : pathway;
        }

        // Clear history slots that are no longer valid
        String[] clearedHistory;
        if (becomesNonBeyonder) {
            clearedHistory = new String[10];
        } else {
            clearedHistory = Arrays.copyOf(pathwayHistory, 10);
            for (int i = 0; i <= newSequence; i++) {
                clearedHistory[i] = null;
            }
        }

        return builder
                .copyFrom(this)
                .pathway(regressedPathway)
                .sequence(newSequence)
                .honorificName((newSequence >= 3) ? HonorificName.EMPTY : honorificName)
                .charStack(0)   // reset stack on regression
                .pathwayHistory(becomesNonBeyonder ? new String[10] : clearedHistory)
                .build();
    }

    // ── NBT ──────────────────────────────────────────────────────────────────

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString(NBT_PATHWAY, pathway);
        tag.putInt(NBT_SEQUENCE, sequence);
        tag.put(NBT_HONORIFIC_NAME, honorificName.toNBT());
        tag.putString(NBT_TRUE_NAME, trueName);

        ListTag msgList = new ListTag();
        for (MessageType msg : msgs) msgList.add(msg.toNBT());
        tag.put(NBT_MESSAGES, msgList);

        tag.putBoolean(NBT_MODIFIED, modified);

        tag.putDouble(NBT_LAST_POSITION_X, lastPosition.x());
        tag.putDouble(NBT_LAST_POSITION_Y, lastPosition.y());
        tag.putDouble(NBT_LAST_POSITION_Z, lastPosition.z());

        // single int stack
        tag.putInt(NBT_CHAR_STACK, charStack);

        // String[10] pathway history stored as a ListTag of StringTags
        ListTag histList = new ListTag();
        for (String entry : pathwayHistory) {
            histList.add(StringTag.valueOf(entry == null ? "" : entry));
        }
        tag.put(NBT_PATHWAY_HISTORY, histList);

        return tag;
    }

    public static StoredData fromNBT(CompoundTag tag) {
        String path     = tag.getString(NBT_PATHWAY);
        int    seq      = tag.getInt(NBT_SEQUENCE);
        HonorificName name = HonorificName.fromNBT(tag.getCompound(NBT_HONORIFIC_NAME));
        String trueName = tag.getString(NBT_TRUE_NAME);

        LinkedList<MessageType> msgs = new LinkedList<>();
        for (var t : tag.getList(NBT_MESSAGES, Tag.TAG_COMPOUND)) {
            if (t instanceof CompoundTag c) msgs.add(MessageType.fromNBT(c));
        }

        boolean modified = tag.getBoolean(NBT_MODIFIED);

        Vec3 lastPos = new Vec3(
                tag.getDouble(NBT_LAST_POSITION_X),
                tag.getDouble(NBT_LAST_POSITION_Y),
                tag.getDouble(NBT_LAST_POSITION_Z));

        int charStack = tag.getInt(NBT_CHAR_STACK);

        String[] history = new String[10];
        if (tag.contains(NBT_PATHWAY_HISTORY, Tag.TAG_LIST)) {
            ListTag histList = tag.getList(NBT_PATHWAY_HISTORY, Tag.TAG_STRING);
            for (int i = 0; i < Math.min(histList.size(), 10); i++) {
                String val = histList.getString(i);
                history[i] = val.isEmpty() ? null : val;
            }
        }

        return new StoredData(path, seq, name, trueName, msgs,
                new LinkedList<>(), modified, lastPos, charStack, history);
    }
}