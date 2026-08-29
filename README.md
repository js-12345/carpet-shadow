# Carpet Shadow

Carpet extension to fix various inconsistencies (/bugs) related to shadow item stacks in normal gameplay

## Carpet Settings

 - ```shadowItemMode```

    what should happen to shadow items when saved and re-loaded from disk:
    - ```UNLINK```    (vanilla default = dupe them)
    - ```PERSIST```   (shadow items will re-link once re-load)
    - ```VANISH```    (shadow items will disappear when re-load)

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

    inventory updates from shadow items will propagate to the other shadow item stacks even if in other invenotories
    
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
  - Bundle add/remove ( To be tested )
- Fragility Fixes
  - Player Pick-Up shadow stacks from Item Entities
  - Mouse Pickup and Place shadow stacks
  - Shift Click shadow stacks ( will only transfer them entirely w/o merging )
  - Quick Craft (dragging of items in inventory) with/to shadow stacks ( simply disallowed )
  - To delete a shadow item left click on same shadow item while to be deleted item is held on cursor; Currently also: put in crafting grid and exit ui, deletes also tooltip and shadow id
  - Hoppers
  - Droppers
- Propagation of inventory updates after the world tick

### Commands

- ```/carpetShadowItemDelete``` to remove all loaded shadow items with the same id select it in hotbar and execute

## Known Bugs

- [x] **General**
  - [x] **MINOR**: this mod only tracks shadow items that get generated with update suppression and the item swap operation, if another method of generating a shadow item is found this mod will not work. (That's the nature of this mod)
- [ ] **Persistence**
  - [ ] **MINOR**: when the mod generates a new shadow ID it only performs a check on the currently loaded IDs so there is a very small chance of overlapping IDs
  - [ ] **MINOR**: if two instances of a shadow item get unloaded with different amounts, the stack count will be of the first instance to get loaded back
- [ ] **Fragility**
  - [ ] **MAJOR**: opening creative inventory will unlink and duplicate all the shadow stacks in the current inventory
- [ ] **Tooltips**
  - [x] **MINOR**: on servers the tooltips are sent to the clients as LORE nbt tags; this mod will strip them down on the clientside but non modded clients will see some de-synced lores while performing item movements/splits in inventories (cant be fixed cause otherwise no tooltips on non carpet shadow clients)
  - [ ] **MAJOR (Unconfirmed)**: non modded creative inventory might behave strangely if tooltips are active on server
- [ ] **Unlinking/Duping**
  - [ ] **MINOR**: on enchanting
  - [x] **MINOR**: on renaming/anvil and smithing table (currently just disallowed)
  - [ ] **MAJOR**: on recipe book clicks and crafting ui, some stuff (unlinking) maybe intended?

## TODO

- add persistent saving/tracking of all shadow items (also ones that aren't loaded yet), track in creation and 'delete' from tracking on ```setCount(<=0)```
- check all screen handlers for problems/errors with shadow items
