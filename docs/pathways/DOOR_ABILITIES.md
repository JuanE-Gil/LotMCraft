# Door Pathway Abilities

## Recording and Replicating

The Door pathway has two signature mechanics for borrowing abilities from other Beyonders:

- **Recording** (Sequence 6): Watches for an ability being used within 15 blocks. On success (**50% chance**), stores a single-use copy of that ability. Fails if the ability cannot be copied (`canBeCopied = false`) or if the caster is more than 2 sequences weaker than the ability's owner.

- **Replicating** (Sequence 2): Watches for an ability being used within 15 blocks. On success (**1 in 6 chance**), stores an unlimited-use copy of that ability. Fails if the ability cannot be replicated (`canBeReplicated = false`) or if the caster is more than 2 sequences weaker than the ability's owner.

Recorded copies have 1 use. Replicated copies have unlimited uses.

---

## Active Abilities

---

### Space-Time Storm
**Sequence Requirement:** 1
**Spirituality Cost:** 1700
**Cooldown:** 12 seconds
*(Cannot be copied)*

- **Target Range:** 60 blocks; **Damage Radius:** 35 blocks
- **Duration:** 12 seconds
- **Hit Interval:** Every 2 ticks
- **Hits:** **120 hits** over the full duration (~62 base DPS)
- **Damage:** **~6.2 damage per hit** (space damage type).
- Randomly converts blocks within the radius to Void or Air if griefing is enabled.

---

### Black Hole
**Sequence Requirement:** 1
**Spirituality Cost:** 1500
**Cooldown:** 2 minutes
*(Cannot be copied)*

- **Target Range:** 27 blocks
- Spawns a **Black Hole entity** with a **10-block radius**.
- **Hit Interval:** Every tick
- **Damage:** **~5.7 damage per tick** (space damage type).
- Continuously pulls entities toward the black hole center.

---

### Player Teleportation
**Sequence Requirement:** 1
**Spirituality Cost:** 1200
**Cooldown:** 2 seconds
*(Cannot be copied, replicated, or used in artifacts)*

- Teleports the caster directly to a selected online player.
- **Blocked if:**
  - The target has a **concealment power of 14 or higher**.
  - The target is in a special dimension (Sefirah Castle, Space, Concealment World, or World Creation).

---

### Distortion Field
**Sequence Requirement:** 2
**Spirituality Cost:** 1400
**Cooldown:** 40 seconds
*(Cannot be copied)*

- Creates a **barrier ring** (radius 40, 18 blocks tall) lasting **40 seconds**.
- **Effect Interval:** Every 6 ticks
- Each interval, all entities within range:
  - Are **randomly teleported ±8 blocks**.
  - Have a **1-in-15 chance** of having their abilities **disabled for 4 seconds**.
- The caster maintains a 4.5-block clear zone around themselves (barrier blocks continuously removed).

---

### Space Distortion
**Sequence Requirement:** 2
**Spirituality Cost:** 1000
**Cooldown:** 20 seconds
*(Cannot be copied)*

- **Target Range:** 27 blocks; **Pull Radius:** 70 blocks
- **Duration:** 60 seconds
- **Effect Interval:** Every 2 ticks (600 intervals total)
- Each interval, pulls all entities within 70 blocks toward the target point at **4% velocity per interval**.

---

### Pocket Dimension
**Sequence Requirement:** 2
**Spirituality Cost:** 1000
**Cooldown:** 2 seconds
*(Cannot be copied, replicated, or used in artifacts)*

- Teleports the caster to their **personal hollow sphere** (radius 22) in the Space dimension.
- On first visit, a grass floor is generated inside the sphere.
- A **return portal** is spawned at the center of the sphere.

---

### Area Miniaturization
**Sequence Requirement:** 2
**Spirituality Cost:** 1200
**Cooldown:** 2 seconds
*(Cannot be copied; requires griefing to be enabled)*

- **Radius:** 20×9 ellipsoid
- Removes all blocks in the target area (up to **10,000 blocks**) and stores them as an **Excavated Area Item**.
- The item can be used to restore the area.

---

### Replicating
**Sequence Requirement:** 2
**Spirituality Cost:** 0
**Cooldown:** 8 seconds
*(Cannot be copied or replicated)*

- Watches for any ability used within **15 blocks** over the next **5 seconds**.
- **1 in 6 chance** to successfully replicate the observed ability as an **unlimited-use copy**.
- Fails if the ability has `canBeReplicated = false`, or if the caster is more than 2 sequences weaker than the ability's owner.

---

### Sealing
**Sequence Requirement:** 3
**Spirituality Cost:** 500
**Cooldown:** 2 seconds
*(Cannot be copied)*

- **Target Range:** 20 blocks; **Radius:** 5 blocks
- Only affects enemies that are not significantly stronger than the caster.
- **Effect on Beyonders** (lower sequence than caster):
  - Abilities disabled for **14 seconds**.
  - Applies a **0.5× damage modifier** for 14 seconds.
  - Applies **Slowness (Level 101)** — complete immobility — every 4 ticks for 14 seconds.
- **Effect on non-Beyonder mobs:** AI disabled.

---

### Waypoint
**Sequence Requirement:** 3
**Spirituality Cost:** 900
**Cooldown:** 1 second
*(Cannot be copied, replicated, or used in artifacts)*

Three selectable modes:

**Mode 0 — Teleport**
- Teleports the caster to their currently selected waypoint (cross-dimension capable).

**Mode 1 — Set Waypoint**
- Saves the caster's current position as a waypoint.

**Mode 2 — Delete Waypoint**
- Deletes the currently selected waypoint.

*Hold the ability to view the current waypoint's coordinates. Shift+Hold to cycle through saved waypoints.*

---

### Wandering
**Sequence Requirement:** 3
**Spirituality Cost:** 200
**Cooldown:** 1 second
*(Cannot be copied, replicated, or used in artifacts)*

- Cycles the caster through available dimensions (excludes Sefirah Castle and Concealment World).
- Applies **Slow Falling** for **5 seconds** on arrival in each dimension.

---

### Space Tearing
**Sequence Requirement:** 3
**Spirituality Cost:** 800
**Cooldown:** 2 seconds
*(Cannot be copied)*

- **Target Range:** 27 blocks
- Spawns a **Space Collapse entity** at the target location.
- **Hit Interval:** Every 4 ticks
- **Damage:** **~4.4 damage per hit** (space damage type).

---

### Conceptualization
**Sequence Requirement:** 3
**Spirituality Cost:** 2 per tick (toggle)
*(Cannot be copied, replicated, or used in artifacts)*

- Toggles sustained flight for the caster (speed 0.225).
- Resets fall distance for **2.5 seconds** after deactivating, preventing fall damage.

---

### Space Concealment
**Sequence Requirement:** 4
**Spirituality Cost:** 100
**Cooldown:** 1 second
*(Cannot be copied)*

Three selectable modes:

**Mode 0 — Conceal Location**
- **Range:** 20 blocks
- Creates a **5-block barrier cube** (Apprentice Door) at the target location lasting **30 seconds**.
- Repairs every 5 ticks if damaged.

**Mode 1 — Conceal Self**
- Creates a **5-block barrier cube** around the caster's current position.

**Mode 2 — Collapse All**
- Collapses all active concealed spaces.
- Deals **~22.4 damage** to entities inside each space and clears the blocks.

---

### Exile
**Sequence Requirement:** 4
**Spirituality Cost:** 500
**Cooldown:** 10 seconds
*(Cannot be copied)*

- **Target Range:** 20 blocks
- Creates an **Exile Doors entity** at the target location lasting **20 seconds**.

---

### Door Substitution
**Sequence Requirement:** 4
**Spirituality Cost:** 90
**Cooldown:** 10 seconds
*(Cannot be copied, replicated, or used in artifacts)*

- Gives the caster an **Oak Door item** (up to **5 doors** held at a time).
- When the caster takes damage (except Losing Control damage):
  - The damage is **completely cancelled**.
  - One door is **consumed**.
  - The caster is **teleported** up to 7 blocks away randomly.

---

### Blink
**Sequence Requirement:** 5
**Spirituality Cost:** 20
**Cooldown:** ~0 seconds
*(Cannot be copied)*

- **Range:** 8 blocks
- Short-range teleport to the targeted block.
- Registers a **blink_escape interaction** in a 3-block radius for ~2 seconds after blinking.

---

### Traveler's Door
**Sequence Requirement:** 5
**Spirituality Cost:** 70
**Cooldown:** 3 seconds
*(Cannot be copied, replicated, or used in artifacts)*

Two selectable modes:

**Mode 0 — Coordinates**
- Opens a UI to enter target X/Y/Z coordinates.
- Creates a **door** at the caster's position leading to those coordinates.
- Door lasts **10 seconds**.

**Mode 1 — Spirit World**
- Creates a **door** leading to the Spirit World.
- Door lasts **10 seconds**.

---

### Recording
**Sequence Requirement:** 6
**Spirituality Cost:** 0
**Cooldown:** 8 seconds
*(Cannot be copied or replicated)*

- Watches for any ability used within **15 blocks** over the next **5 seconds**.
- **50% chance** to successfully record the observed ability as a **single-use copy**.
- Fails if the ability has `canBeCopied = false`, or if the caster is more than 2 sequences weaker than the ability's owner.

---

### Spells
**Sequence Requirement:** 8
**Spirituality Cost:** 15
**Cooldown:** 0.8 seconds

Four selectable modes:

**Mode 0 — Wind**
- **Range:** 10 blocks
- Pushes the target away continuously for **6 seconds**.

**Mode 1 — Electric Shock**
- **Range:** 15 blocks (projectile)
- **Hits:** **1 hit** on contact
- **Damage:** **~6.8 damage per hit**.

**Mode 2 — Freeze**
- **Range:** 4 blocks
- **Hits:** **1 hit** per cast
- **Damage:** **~7 damage per hit**.
- Applies **Slowness (Level 3, 2 seconds)** on hit.

**Mode 3 — Flash**
- **Range:** 12 blocks; **Radius:** 5 blocks
- Applies **Blindness** and **Slowness** for **5 seconds** to all entities in range.

---

### Door Opening
**Sequence Requirement:** 9
**Spirituality Cost:** 12
**Cooldown:** 1 second
*(Cannot be used by NPCs)*

- **Range:** 25 blocks
- Opens an **Apprentice Door** in or on the targeted solid block (must have air exposure on the facing side).
- The door lasts **10 seconds**.

---

## Passive Abilities

---

### Physical Enhancements (Door)
**Sequence Requirement:** 9

Provides passive buffs that scale with the caster's current sequence. Includes Fire Resistance at higher sequences. No Night Vision at any sequence.

| Sequence | Strength | Resistance | Speed | Bonus Health | Regeneration | Other |
|----------|----------|------------|-------|--------------|--------------|-------|
| 9        | —        | —          | +2    | —            | +1           | — |
| 8        | +2       | +4         | +2    | +5           | +2           | — |
| 7        | +2       | +4         | +2    | +5           | +2           | — |
| 6        | +2       | +6         | +2    | +7           | +2           | — |
| 5        | +2       | +8         | +2    | +9           | +2           | Fire Resistance +1 |
| 4        | +3       | +13        | +4    | +16          | +3           | Fire Resistance +2 |
| 3        | +3       | +14        | +4    | +17          | +3           | Fire Resistance +3 |
| 2        | +4       | +17        | +5    | +25          | +4           | Fire Resistance +3 |
| 1        | +4       | +18        | +5    | +30          | +4           | Fire Resistance +4 |
| 0        | +5       | +18        | +5    | +20          | +5           | Fire Resistance +5 |

---

### Void Immunity
**Sequence Requirement:** 3

- **Passive:** Blocks all damage from falling out of the world (void damage above Y=−1000).

---

### Spirit World Awareness
**Sequence Requirement:** 5

- **Passive:** While in the Spirit World, the caster's equivalent **Overworld coordinates** are displayed in the action bar.
