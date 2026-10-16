# Carpet Shadow

Carpet extension to fix various inconsistencies (/bugs) related to shadow item stacks in normal gameplay

## Carpet Settings

- ```shadowItemCommand```

  Enables/disables and set the permissions for the /shadowItem command:

  Allowed options: `true`, `false`, `ops`, `0`, `1`, `2`, `3`, `4`


 - ```shadowTrackUntrackedItems```

    in a version change of this mod stuff got reworked, now every shadow item is always tracked on disk.
    This option controls what should happen if old/legacy version if shadow items are found/to be loaded:
    - ```TRUE``` to add them to the tracking
    - ```FALSE``` to not add them (this would lead to a duplication of the item)

 - ```shadowItemIdSize```

   changes the length of the shadow stack unique identifiers, smaller can lead to more id collisions

- ```shadowSuppressionGeneration```

  enables the shadow item creation via update suppression

- ```shadowCraftingGeneration```

  enables the shadow item creation via a crafting recipe; put item to shadow and an ender chest into crafting grid

 - ```shadowItemTooltip```

    shows the shadow stack unique identifier when hovering over a shadow stack with the mouse
    
 - ```shadowItemFragilityFixes```

    various fixes to item related actions that might unlink/dupe/delete the shadow stacks
    
 - ```shadowItemUpdateFix```

    inventory updates from shadow items will propagate to the other shadow item stacks even if in other inventories
    
 - ```shadowItemPreventCombine```

    by default instances of the same shadow stack won't merge on inventory actions; this option expands that behavior and prevents merging operations between any shadow stack
    
    **PS:** *this option only has an effect if ```shadowItemFragilityFixes``` is active*

- ```shadowItemUseFix```

    prevents desync between client and server when using a fast refilling shadow stack

- ```shadowItemDropFix```

  allows for correct item dropping while preserving the shadow item


## Feature List

### Shadow Item Behaviour

- Shadow Item Persistence/Removal
  - Server Restarts
  - Player Join/Leave
  - Chunk Unload/Reload
  - Shulker Box Break/Place
- Fragility Fixes
  - Player Pick-Up shadow stacks from Item Entities
  - Mouse Pickup and Place shadow stacks
  - Shift Click shadow stacks ( will only transfer them entirely w/o merging )
  - Quick Craft (dragging of items in inventory) with/to shadow stacks ( simply disallowed )
  - To delete a shadow item left click on same shadow item while to be deleted item is held on cursor
  - Hoppers
  - Droppers
- Propagation of inventory updates after the world tick

### Commands

- ```/shadowItem delete``` to remove all loaded shadow items with the same id select it in hotbar and execute
- ```/shadowItem delete <shadow_id>``` to remove all loaded shadow items with the shadow_id
- ```/shadowItem create``` to create a new shadow item from selected hotbar slot and puts it into offhand (has to be free)
- ```/shadowItem info``` to printout a list of all currently tracked shadow items
- ```/shadowItem unlink``` to remove the shadow link of currently held item (keep remaining shadow items tracked)
- ```/shadowItem get <shadow_id>``` to get a tracked shadow item into currently selected slot (needs to be empty) with specific shadow_id

## Known Bugs

- [x] **General**
  - [x] **MINOR**: this mod only tracks shadow items that get generated with update suppression and the item swap operation, if another method of generating a shadow item is found this mod will not work. (That's the nature of this mod)
- [x] **Persistence**
  - [x] **MINOR**: when the mod generates a new shadow ID it only performs a check on the currently loaded IDs so there is a very small chance of overlapping IDs
  - [x] **MINOR**: if two instances of a shadow item get unloaded with different amounts, the stack count will be of the first instance to get loaded back
- [x] **Tooltips**
  - [x] **MINOR**: on servers the tooltips are sent to the clients as LORE nbt tags; this mod will strip them down on the clientside but non modded clients will see some de-synced lore while performing item movements/splits in inventories (cant be fixed cause otherwise no tooltips on non carpet shadow clients)
- [ ] **Unlinking/Duping/Vanishing**
  - [x] **MINOR**: on enchanting
  - [x] **MINOR**: on renaming/anvil and smithing table (currently just disallowed)
  - [ ] **MAJOR**: on recipe book clicks and crafting ui, some stuff (unlinking) maybe intended?
  - [x] **MAJOR**: Bundle
  - [ ] **MINOR**: weird stuff on horse screen handler happening

### Currently out of scope

- opening creative inventory will unlink and duplicate all the shadow stacks in the current inventory (out of scope currently)
- (Unconfirmed) non modded creative inventory might behave strangely if tooltips are active on server (out of scope currently)

## TODO

- add metadata to shadow item loading???
- check all screen handlers for problems/errors with shadow items
