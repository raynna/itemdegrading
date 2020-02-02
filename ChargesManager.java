package com.rs.game.item.itemdegrading;

import java.io.Serializable;
import java.util.HashMap;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Graphics;
import com.rs.game.item.Item;
import com.rs.game.item.itemdegrading.ItemDegrade.DegradeType;
import com.rs.game.item.itemdegrading.ItemDegrade.ItemStore;
import com.rs.game.player.Player;
import com.rs.game.player.content.ItemConstants;
import com.rs.utils.Utils;

/**
 * @author -Andreas 1 feb. 2020 13:58:09
 * @project 1. Avalon
 * 
 */

public class ChargesManager implements Serializable {

	private static final long serialVersionUID = -5978513415281726450L;

	private transient Player player;

	private HashMap<Integer, Integer> charges;

	public ChargesManager() {
		charges = new HashMap<Integer, Integer>();
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	private static ItemStore[] data = ItemStore.values();

	public void process() {
		Item[] items = player.getEquipment().getItems().getContainerItems();
		for (int slot = 0; slot < items.length; slot++) {
			Item item = items[slot];
			if (item == null)
				continue;
			int defaultCharges = -1;
			for (ItemStore data : data) {
				if (data == null || data.getCurrentItem().getId() != item.getId())
					continue;
				player.sm("process");
				defaultCharges = data.getTime().getTicks();
				if (defaultCharges == -1)
					continue;
				if (data.getType() == DegradeType.WEAR)
					degrade(item.getId(), defaultCharges, slot);
				else if (data.getType() == DegradeType.IN_COMBAT
						&& player.getAttackedByDelay() > Utils.currentTimeMillis())
					degrade(item.getId(), defaultCharges, slot);
			}
		}
	}

	public void die() {
		die(null, null);
	}

	public void die(Integer[] slots, Integer[] slots2) {
		Item[] equipItems = player.getEquipment().getItems().getContainerItems();
		Item[] invItems = player.getInventory().getItems().getContainerItems();

		if (slots == null) {
			for (int slot = 0; slot < equipItems.length; slot++) {
				if (equipItems[slot] != null && degradeCompletly(equipItems[slot]))
					player.getEquipment().getItems().set(slot, null);
			}
			for (int slot = 0; slot < invItems.length; slot++) {
				if (invItems[slot] != null && degradeCompletly(invItems[slot]))
					player.getInventory().getItems().set(slot, null);
			}
		} else {
			for (int slot : slots) {
				if (slot >= 16) {
					if (invItems[slot - 16] != null && degradeCompletly(invItems[slot - 16]))
						player.getInventory().getItems().set(slot - 16, null);
				} else {
					if (equipItems[slot - 1] != null && degradeCompletly(equipItems[slot - 1]))
						player.getEquipment().getItems().set(slot - 1, null);
				}
			}
			for (int slot : slots2) {
				if (slot >= 16) {
					if (invItems[slot - 16] != null && degradeCompletly(invItems[slot - 16]))
						player.getInventory().getItems().set(slot - 16, null);
				} else {
					if (equipItems[slot - 1] != null && degradeCompletly(equipItems[slot - 1]))
						player.getEquipment().getItems().set(slot - 1, null);
				}
			}
		}
	}

	public static final String REPLACE = "##";

	public void checkPercentage(String message, int id, boolean reverse) {
		int charges = getCharges(id);
		int maxCharges = ItemConstants.getItemDefaultCharges(id);
		int percentage = reverse ? (charges == 0 ? 0 : (100 - (charges * 100 / maxCharges)))
				: charges == 0 ? 100 : (charges * 100 / maxCharges);
		player.getPackets().sendGameMessage("Charges: " + REPLACE.replace("##", "") + String.valueOf(percentage) + "%");
	}

	public void checkCharges(String message, int id) {
		player.getPackets().sendGameMessage(message.replace(REPLACE, String.valueOf(getCharges(id))));
	}

	public int getCharges(int id) {
		Integer c = charges.get(id);
		return c == null ? 0 : c;
	}

	public void resetCharges(int id) {
		charges.remove(id);
	}

	/*
	 * return disapear;
	 */
	public boolean degradeCompletly(Item item) {
		int defaultCharges = -1;
		for (ItemStore data : data) {
			if (data == null)
				continue;
			if (data.getCurrentItem().getId() == item.getId()) {
				defaultCharges = data.getTime().getTicks();
				if (defaultCharges == -1)
					return false;
				charges.remove(item.getId());
				int newId = data.getTime().getTicks();
				if (newId != -1)
					item.setId(newId);
				else
					return data.getTime().getTotalTime() == -1 ? false : true;
			}
		}
		return false;
	}

	private void degrade(int itemId, int defaultCharges, int slot) {
		ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
		Integer c = charges.remove(itemId);
		Item newItem = null;
		for (ItemStore data : data) {
			if (data == null || data.getCurrentItem().getId() != itemId)
				continue;
			if (c == null || c == 0)
				c = data.getTime().getTicks();
			c--;
			player.sm("Ticks: " + c);
			if (c > 0) {
				charges.put(itemId, c);
			} else {
				if (data.getDegradedItem() == null) {
					if (data.getBrokenItem() != null) {
						newItem = data.getBrokenItem();
					}
				} else {
					if (itemId != data.getDegradedItem().getId()) {
						newItem = data.getDegradedItem();
					}
				}
				if (newItem == null) {
					if (itemDef.getName().contains("(deg)")) {
						if (slot == 0)
							player.gfx(new Graphics(1859));
						if (slot == 4)
							player.gfx(new Graphics(1861));
						if (slot == 7)
							player.gfx(new Graphics(1860));
					}
					player.getPackets().sendGameMessage("Your " + itemDef.getName() + " turned into dust.");
				} else {
					player.getPackets().sendGameMessage("Your " + itemDef.getName() + " degraded.");
				}
				player.getEquipment().getItems().set(slot, newItem);
				player.getEquipment().refresh(slot);
				player.getAppearence().generateAppearenceData();
			}
		}
	}

}
