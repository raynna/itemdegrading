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
