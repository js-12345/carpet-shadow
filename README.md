# Carpet Shadow
carpet extension to fix various bugs related to shadow item stacks

## Carpet Settings

 - **shadowItemMode**
    what should happen to shadow items when saved and re-loaded from disk?
    - UNLINK    ( vanilla default = dupe them )
    - PERSIST   ( shadow items will re-link once re-load )
    - VANISH    ( shadow items will disappear when re-load ) 

 - **shadowItemIdSize**

   changes the length of the shadow stack unique identifiers, smaller can lead to more id collisions

- **shadowSuppressionGeneration**

  enables the shadow item creation via update suppression

- **shadowCraftingGeneration**

  enables the shadow item creation via a crafting recipe; put item to shadow and an ender chest into crafting grid

 - **shadowItemTooltip**

    shows the shadow stack unique identifier when hovering over a shadow stack with the mouse
    
 - **shadowItemFragilityFixes**

    various fixes to item related actions that might unlink/dupe/delete the shadow stacks
    
 - **shadowItemUpdateFix**

    inventory updates from shadow items will propagate to the other shadow item stacks even if in other invenotories
    
 - **shadowItemPreventCombine**

    by default instances of the same shadow stack won't merge on inventory actions; this option expands that behavior and prevents merging operations between any shadow stack
    
    **PS:** *this option only has an effect if **shadowItemFragilityFixes** is active*

- **shadowItemUseFix**

    prevents desync between client and server when using a fast refilling shadow stack

- **shadowItemDropFix**

  allows for correct item dropping while preserving the shadow item


## Feature List

 - [x] Shadow Item Persistence/Removal
	 - [x] Server Restarts
	 - [x] Player Join/Leave
	 - [x] Chunk Unload/Reload
	 - [x] Shulker Box Break/Place
	 - [x] Bundle add/remove ( To be tested )
 - [x] Fragility Fixes
	 - [x] Player Pick-Up shadow stacks from Item Entities
	 - [x] Mouse Pickup and Place shadow stacks
	 - [x] Shift Click shadow stacks ( will only transfer them entirely w/o merging )
	 - [x] Quick Craft (dragging of items in inventory) with/to shadow stacks ( simply disallowed )
     - [x] To delete a shadow item left click on same shadow item while to be deleted item is held on cursor; Currently also: put in crafting grid and exit ui, deletes also tooltip and shadow id
	 - [x] Hoppers
		 - [x] Fail to pull from shadow stacks
	 - [x] Droppers
		 - [x] Unlinking on transfer to Inventory
 - [x] Inventory Updates
 	 - [x] propagation of updates after the world tick   

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
   - [ ] **MINOR**: on renaming/anvil
   - [ ] **MINOR**: stone cutter?
   - [ ] **MAJOR**: on recipe book clicks and crafting ui, some stuff (unlinking) maybe intended?
