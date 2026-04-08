# Visionary Pathway Abilities

## Active Abilities

---

### Story Writing
**Sequence Requirement:** 1
**Spirituality Cost:** 1500
**Cooldown:** 60 seconds
*(Cannot be used by NPCs)*

Five selectable modes. Requires an active story book target to use most modes.

**Mode 0 — Write Player**
- Opens a screen to select a player as the story target.
- Creates a **Story Book** item for that player (3 uses).

**Mode 1 — Write Target**
- Targets the nearest entity within **20 blocks**.
- Creates a **Story Book** item for that entity (3 uses).

**Mode 2 — Disaster**
- Triggers the currently selected **Disaster Fantasia** mode at the story target's position.
- Consumes 1 book use.

**Mode 3 — Assault**
- Triggers **Group Incite** (from Manipulation) centered on the story target.
- Consumes 1 book use.

**Mode 4 — Guidance**
- Sends the story target a compulsion to move to a chosen location (**85 block range**).
- Sanity drains every second after 30s, ramping ×1.5 every 30s — ends after the 4th ramp or on arrival.
- Consumes 1 book use.

---

### Disaster Fantasia
**Sequence Requirement:** 1
**Spirituality Cost:** 2500
**Cooldown:** 30 seconds

Two selectable modes. **Target range:** 150 blocks.

**Mode 0 — Earthquake**
- Spawns an earthquake calamity at the target location.

**Mode 1 — Meteor Shower**
- Spawns **7 meteors** scattered within a **50 block radius** of the target location.

---

### Dream Maze
**Sequence Requirement:** 2
**Spirituality Cost:** 1000
**Cooldown:** 60 seconds
*(Cannot be copied, replicated, or used by NPCs)*

Two selectable modes.

**Mode 0 — Self**
- Sends the caster into their own Dream Maze dimension.
- If already inside, ejects them back.

**Mode 1 — Others**
- Sends all **sleeping** entities within **25 blocks** into the caster's Dream Maze.
- Cannot be used from inside the maze.
- Targets find an exit by interacting with **oak doors** inside the maze.

---

### Dream Weave
**Sequence Requirement:** 3
**Spirituality Cost:** 750
**Cooldown:** 20 seconds

Two selectable modes. **Target range:** 20 blocks.

**Mode 0 — Strong**
- Spawns **1 BeyonderNPC** at the target's position, **1 sequence below the caster**.
- The NPC is passive until the target attacks it, at which point it turns hostile.
- Lasts **10 seconds**, then discards.

**Mode 1 — Weak**
- Spawns **3 BeyonderNPCs** arranged in a triangle around the target, **3 sequences below the caster**.
- Same passive-until-attacked behavior.
- Lasts **5 seconds**, then discards.

---

### Mind Invasion
**Sequence Requirement:** 4
**Spirituality Cost:** 1200
**Cooldown:** 10 seconds
*(Cannot be stolen, cannot be used in artifacts — not yet implemented)*

- Currently sends a "not implemented" message on use.

---

### Mental Plague
**Sequence Requirement:** 4
**Spirituality Cost:** 1200
**Cooldown:** 20 seconds
*(Cannot be copied or replicated)*

- **Target range:** 30 blocks.
- Fails if the target is significantly stronger than the caster.
- Applies **Mental Plague** effect (level 4):
  - **Duration:** 10 minutes normally; reduced to **2 minutes** if a purification interaction is active at the target.

---

### Manipulation
**Sequence Requirement:** 4
**Spirituality Cost:** 750
**Cooldown:** 30 seconds

Two selectable modes. **Target range:** 20 blocks.

**Mode 0 — Group Incite**
- Causes all nearby entities within **20 blocks** to attack the target for **10 seconds**.
- Bypasses Beyonder players/mobs of weaker sequence; non-Beyonder mobs always affected.

**Mode 1 — Control**
- Marks a single target (weaker sequence only) as a marionette for **7 seconds**.
- Gives the caster a **Marionette Controller** item (movement-only).
- *(Players only)*

---

### Virtual Persona
**Sequence Requirement:** 4
**Spirituality Cost:** 500
**Cooldown:** 3 seconds
*(Cannot be used by NPCs, copied, replicated, or used in artifacts)*

Two selectable modes. **Target range (others):** 20 blocks.

**Mode 0 — Self**
- Adds **1 Virtual Persona stack** to the caster (max 10).

**Mode 1 — Others**
- Adds **1 Virtual Persona stack** to the target.
- If no target is found and the caster is **sequence 3 or below**, spawns a **Visionary Avatar** (2 sequences weaker than the caster) instead.

---

### Dream Traversal
**Sequence Requirement:** 5
**Spirituality Cost:** 60
**Cooldown:** 1 second

Three selectable modes. **Target range:** 20–200 blocks depending on mode.
- At sequence 4 or higher, target must be **asleep** for Jump and Hide.

**Mode 0 — Jump**
- **Range:** 200 blocks.
- Teleports the caster to the target's position.
- If currently hiding inside a host, switches to the new host without breaking hide.

**Mode 1 — Hide**
- **Range:** 20 blocks.
- Parasites into the target — caster becomes invisible and floats above the host.
- Caster is ejected if the host dies, is no longer asleep (at sequence 4+), or is removed.
- Toggle off to cancel.

**Mode 2 — Guidance**
- **Range:** 200 blocks.
- Reveals the target's **pathway and sequence** (if caster is stronger) or **pathway only** (if weaker).
- Only works on Beyonder targets.

---

### Nightmare Spectator
**Sequence Requirement:** 5
**Spirituality Cost:** 110
**Cooldown:** 1.5 seconds
*(Cannot be copied)*

- **Target range:** 200 blocks. Target must be **asleep**.
- **Damage:** **~`DamageLookup(5, 1.1)`** × multiplier per hit (~18–20 damage at sequence 5).
- Applies **Losing Control** effect (level 1, 4 seconds).
- Decreases target sanity by **~8.25% × (multiplier/2)**.

---

### Sleep Inducement
**Sequence Requirement:** 5
**Spirituality Cost:** 90
**Cooldown:** 2 seconds
*(Cannot be copied)*

- **Target range:** 80 blocks.
- Only works if the caster's sequence is **weaker** than the target's (higher number).
- Applies **Asleep** effect (level 1, 12 seconds).
- Animates particle lines from caster to target.

---

### Battle Hypnosis
**Sequence Requirement:** 6
**Spirituality Cost:** 150
**Cooldown:** 2 seconds
*(Cannot be copied or replicated)*

- **Target range:** 20 blocks.
- If the target is charmed and the caster's sequence is equal or lower, removes the charm.
- Randomly applies one of three effects:

**Effect 0 — Freeze**
- Stops the target completely for **5 seconds** (movement set to zero, max Slowness).
- Disables ability usage for **3 seconds**.

**Effect 1 — Weaken**
- Applies a **0.4× damage multiplier** debuff for **12 seconds**.
- Moves the target around randomly for **8 seconds** with Weakness level 5.

**Effect 2 — Stop Beyonder Powers**
- Disables ability usage for **9 seconds** (Beyonder targets only; falls back to other effects otherwise).

---

### Dragon Scales
**Sequence Requirement:** 6
**Spirituality Cost:** 2 per tick (toggle)
*(Cannot be copied, replicated, used in artifacts, or stolen)*

- Passively applies **Resistance (Level 1)** every tick while active.

---

### Psychological Invisibility
**Sequence Requirement:** 6
**Spirituality Cost:** 13
**Cooldown:** 180 seconds
*(Cannot be copied or replicated)*

- Makes the caster **invisible** and prevents mobs from targeting them for **60 seconds**.
- Applies **Invisibility** effect (level 20, 60 seconds).

---

### Awe
**Sequence Requirement:** 7
**Spirituality Cost:** 40
**Cooldown:** 10 seconds

- **Radius:** 25 blocks.
- **Damage:** **~`DamageLookup(7, 0.675)`** × multiplier per hit (~11–12 damage at sequence 7).
- Applies **Slowness (Level 11)** and **Weakness (Level 6)** for 10 seconds to all nearby enemies.
- Applies a **0.625× damage multiplier** debuff to Beyonder targets for **10 seconds**.
- Continuously moves affected entities in random directions every 8 ticks for 10 seconds.

---

### Frenzy
**Sequence Requirement:** 7
**Spirituality Cost:** 35
**Cooldown:** 1.5 seconds

- **Target range:** 20 blocks.
- **Damage:** **~`DamageLookup(7, 0.85)`** × multiplier per hit (~12–13 damage at sequence 7).
- Applies **Losing Control** effect with amplifier scaling by relative sequence:
  - Significantly weaker target: level 6
  - Equal/same strength: level 2–4
  - Significantly stronger target: level 1
- Decreases target sanity by **6.5% × multiplier**.

---

### Placate
**Sequence Requirement:** 7
**Spirituality Cost:** 50
**Cooldown:** 7 seconds
*(Cannot be copied or replicated)*

Two selectable modes.

**Mode 0 — Self**
- Restores **15% sanity** to the caster.
- Removes the **Losing Control** effect from the caster.

**Mode 1 — Others**
- **Radius:** 18 blocks.
- Restores **15% sanity** and removes **Losing Control** from all nearby allies.

---

### Telepathy
**Sequence Requirement:** 8
**Spirituality Cost:** 1 per tick (toggle)

- **Range:** 20 blocks.
- Displays the **name** and **sanity percentage** (color-coded) of the looked-at entity in the action bar, updated every 10 ticks.

---

### Spectating
**Sequence Requirement:** 9
**Spirituality Cost:** 0.125 per tick (toggle)
*(Cannot be used by NPCs, copied, or replicated)*

- **Range:** 40 blocks.
- Grants **Night Vision** while active.
- Sends the looked-at entity's ID to the client for visual spectating effects.

---

## Passive Abilities

---

### Physical Enhancements (Visionary)
**Sequence Requirement:** 9

Passive buffs scaling with sequence.

| Sequence | Strength | Resistance | Speed | Bonus Health | Regeneration |
|----------|----------|------------|-------|--------------|--------------|
| 9        | +1       | —          | +2    | —            | +1           |
| 8, 7     | +2       | +4         | +2    | +5           | +2           |
| 6        | +2       | +6         | +2    | +7           | +2           |
| 5        | +2       | +8         | +2    | +9           | +2           |
| 4        | +3       | +13        | +4    | +16          | +3           |
| 3        | +3       | +14        | +4    | +17          | +3           |
| 2        | +4       | +17        | +5    | +25          | +4           |
| 1        | +4       | +18        | +5    | +30          | +4           |
| 0        | +5       | +18        | +5    | +20          | +5           |
