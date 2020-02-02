package com.rs.game.item.itemdegrading;

import java.util.concurrent.TimeUnit;

import com.rs.game.item.Item;

/**
 * @author -Andreas 1 feb. 2020 13:58:09
 * @project 1. Avalon
 * 
 */

public class ItemDegrade {

	public enum DegradeType {

		WEAR(0),

		IN_COMBAT(1),

		AT_HIT(2);

		private int type;

		private DegradeType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}

	public enum ItemStore {

		CORRUPT_STATIUS_HELM(new Item(13920), new Item(13922), new DegradeTime(TimeUnit.SECONDS, 30)),

		CORRUPT_STATIUS_HELM_DEG(new Item(13922), null, new DegradeTime(TimeUnit.MINUTES, 15)),

		VESTA_PLATEBODY(new Item(13887), new Item(13889), DegradeType.IN_COMBAT, new DegradeTime(TimeUnit.SECONDS, 30)),

		VESTA_PLATEBODY_DEG(new Item(13889), null, DegradeType.IN_COMBAT, new DegradeTime(TimeUnit.MINUTES, 30)),

		AHRIMS_HOOD(new Item(4708), new Item(4856), DegradeType.IN_COMBAT, new Item(4860),
				new DegradeTime(TimeUnit.SECONDS, 1)),

		AHRIMS_HOOD_100(new Item(4856), new Item(4857), DegradeType.IN_COMBAT, new Item(4860),
				new DegradeTime(TimeUnit.MINUTES, 120)),

		AHRIMS_HOOD_75(new Item(4857), new Item(4858), DegradeType.IN_COMBAT, new Item(4860),
				new DegradeTime(TimeUnit.MINUTES, 120)),

		AHRIMS_HOOD_50(new Item(4858), new Item(4859), DegradeType.IN_COMBAT, new Item(4860),
				new DegradeTime(TimeUnit.MINUTES, 120)),

		AHRIMS_HOOD_25(new Item(4859), null, DegradeType.IN_COMBAT, new Item(4860),
				new DegradeTime(TimeUnit.MINUTES, 120)),

		CRYSTAL_BOW(new Item(4212), new Item(4213), DegradeType.AT_HIT, 200);

		private Item currentItem, degradedItem, brokenItem;
		private DegradeType type;
		private DegradeTime time;
		private int hits;

		private ItemStore(Item currentItem, Item degradedItem, DegradeType type, DegradeTime time) {// non corrupt
			this(currentItem, degradedItem, type, null, time, -1);
		}

		private ItemStore(Item currentItem, Item degradedItem, DegradeTime time) {// corrupt
			this(currentItem, degradedItem, DegradeType.WEAR, null, time, -1);
		}

		private ItemStore(Item currentItem, Item degradedItem, DegradeType type, Item brokenItem, DegradeTime time) {// barrows
			this(currentItem, degradedItem, type, brokenItem, time, -1);
		}

		private ItemStore(Item currentItem, Item degradedItem, DegradeType type, int hits) {// crystal
			this(currentItem, degradedItem, type, null, null, hits);
		}

		private ItemStore(Item currentItem, Item degradedItem, DegradeType type, Item brokenItem, DegradeTime time,
				int hits) {
			this.currentItem = currentItem;
			this.degradedItem = degradedItem;
			this.type = type;
			this.brokenItem = brokenItem;
			this.time = time;
			this.hits = hits;
		}

		public Item getCurrentItem() {
			return currentItem;
		}

		public Item getDegradedItem() {
			return degradedItem;
		}

		public Item getBrokenItem() {
			return brokenItem;
		}

		public DegradeType getType() {
			return type;
		}

		public DegradeTime getTime() {
			return time;
		}

		public int getHits() {
			return hits;
		}
	}

	public static int getSecondsInTicks(int ticks) {
		return (int) (ticks * 0.6);
	}

}
