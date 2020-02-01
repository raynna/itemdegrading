package com.rs.game.item.itemdegrading;

/**
* @author -Andreas 1 feb. 2020 13:58:09
* @project 1. Avalon
* 
*/

public class ItemDegradingData {
	
	
	public enum ItemStore {
		
		CORRUPT_STATIUS_HELM(13920, 13922, -1, 24, 48),
		
		AHRIMS_HOOD(4708, 4856, 4860, 1),
		
		AHRIMS_HOOD_100(4856, 4857, 4860, 24),
		
		AHRIMS_HOOD_75(4857, 4858, 4860, 24),
		
		AHRIMS_HOOD_50(4858, 4859, 4860, 24),
		
		AHRIMS_HOOD_25(4859, -1, 4860, 24);
		
		
		private int itemId, degradedItem, brokenId, startTime, time;
		private boolean degradeCombat;
		
		private ItemStore(int itemId, int degradedItem, int time) {
			this(itemId, degradedItem, false, -1, -1, time);
		}
		
		private ItemStore(int itemId, int degradedItem, int brokenId, int time) {
			this(itemId, degradedItem, false, brokenId, -1, time);
		}
		
		private ItemStore(int itemId, int degradedItem, boolean degradeCombat, int brokenId, int time) {
			this(itemId, degradedItem, degradeCombat, brokenId, -1, time);
		}
		
		private ItemStore(int itemId, int degradedItem, int brokenId, int startTime, int time) {
			this(itemId, degradedItem, false, brokenId, startTime, time);
		}
		
		private ItemStore(int itemId, int degradedItem, boolean degradeCombat, int brokenId, int startTime, int time) {
			this.itemId = itemId;
			this.degradedItem = degradedItem;
			this.brokenId = brokenId;
			this.startTime = startTime;
			this.time = time;
		}
		
		public int getItemId() {
			return itemId;
		}
		
		public int getDegradedId() {
			return degradedItem;
		}
		
		public int getTime() {
			return time;
		}
		
		public int getStartTime() {
			return startTime;
		}
		
		public int getBrokenId() {
			return brokenId;
		}

		public boolean isDegradeCombat() {
			return degradeCombat;
		}
	}
	
	
	public void checkItems() {
		
	}

}
