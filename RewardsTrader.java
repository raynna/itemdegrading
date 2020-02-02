package com.rs.game.player.dialogues.npcs;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.item.itemdegrading.ItemDegrade.ItemStore;
import com.rs.game.player.dialogues.Dialogue;
import com.rs.utils.Utils;

/**
 * @author -Andreas 2 feb. 2020 14:40:02
 * @project 1. Avalon
 * 
 */

public class RewardsTrader extends Dialogue {

	private int npcId;
	private int chatType;
	private int itemId;

	private int REGULAR = 0, RECHARGE = 1, USE_ITEM = 2, FULLY_CHARGED = 3, WRONG_ITEM = 4;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		itemId = (Integer) parameters[1];
		chatType = (Integer) parameters[2];
		if (chatType == WRONG_ITEM) {
			stageName = "end";
			sendEntityDialogue(SEND_2_TEXT_CHAT,
					new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
							"You can't recharge " + ItemDefinitions.getItemDefinitions(itemId).getName() + "." },
					IS_NPC, npcId, 9827);
		}
		if (chatType == FULLY_CHARGED) {
			stageName = "end";
			sendEntityDialogue(SEND_2_TEXT_CHAT,
					new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
							ItemDefinitions.getItemDefinitions(itemId).getName() + " is already fully charged." },
					IS_NPC, npcId, 9827);
		}
		if (chatType == USE_ITEM) {
			stageName = "USE_ITEM_1";
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
					"Fixing it is possible, yes, The cost is up to you." }, IS_NPC, npcId, 9827);
		}
		//TODO REGULAR CHATDIALOGUE
		//TODO RECHARGE CHATDIALOGUE
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stageName == "USE_ITEM_1") {
			stageName = "USE_ITEM_2";
			int price = player.getCharges().getPercentage(itemId, true) * 20000;
			int token = player.getCharges().getPercentage(itemId, true) * 200;
			sendOptionsDialogue("Pick your chosen price to pay:", Utils.getFormattedNumber(price, ',') + " gp.",
					Utils.getFormattedNumber(token, ',') + " tokens.", "No way!");
		}
		if (stageName == "USE_ITEM_2") {
			int price = player.getCharges().getPercentage(itemId, true) * 20000;
			int token = player.getCharges().getPercentage(itemId, true) * 200;
			if (componentId == OPTION_1) {
				stageName = "end";
				if (player.canBuy(price)) {
					sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
							"All done. Moveon. Please..." }, IS_NPC, npcId, 9827);
					for (ItemStore item : ItemStore.values()) {
						if (item == null)
							continue;
						if (item.getCurrentItem().getId() == itemId) {
							player.getInventory().deleteItem(itemId, 1);
							player.getInventory().addItem(itemId, 1);
							player.getCharges().setCharges(itemId, item.getHits());
						}
					}
				} else {
					sendEntityDialogue(SEND_2_TEXT_CHAT,
							new String[] { NPCDefinitions.getNPCDefinitions(npcId).name, "You can't afford that." },
							IS_NPC, npcId, 9827);
				}
			} else if (componentId == OPTION_2) {
				stageName = "end";
				if (player.getAvalonPoints() >= token) {
					player.setAvalonPoints(player.getAvalonPoints() - token);
					sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
							"All done. Moveon. Please..." }, IS_NPC, npcId, 9827);
					for (ItemStore item : ItemStore.values()) {
						if (item == null)
							continue;
						if (item.getCurrentItem().getId() == itemId) {
							player.getInventory().deleteItem(itemId, 1);
							player.getInventory().addItem(itemId, 1);
							player.getCharges().setCharges(itemId, item.getHits());
						}
					}
				} else {
					sendEntityDialogue(SEND_2_TEXT_CHAT,
							new String[] { NPCDefinitions.getNPCDefinitions(npcId).name, "You can't afford that." },
							IS_NPC, npcId, 9827);
				}
			} else if (componentId == OPTION_3) {
				stageName = "end";
			}
		} else if (stageName == "end")
			end();

	}

	@Override
	public void finish() {

	}
}