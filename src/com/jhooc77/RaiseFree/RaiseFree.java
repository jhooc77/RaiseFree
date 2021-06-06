package com.jhooc77.RaiseFree;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import net.aw.server.AWCore.AWCore;
import net.aw.server.AWCore.util.Gui;
import net.aw.server.AWCore.util.ItemUtil;

public class RaiseFree extends JavaPlugin implements Listener {
	
	private static final String PREFIX = "§f§l[ §6§l프리키우기 §f§l] §r";

	private ItemStack exerciseButton;
	private ItemStack moneyButton;
	
	private static Map<UUID, Free> frees = new HashMap<UUID, Free>();
	
	private static Map<UUID, String> check = new HashMap<UUID, String>();
	
	@Override
	public void onEnable() {
		
		exerciseButton = ItemUtil.Builder.builder().setDisplayName("§e운동시키기").setMaterial(Material.IRON_SWORD).build();
		moneyButton = ItemUtil.Builder.builder().setDisplayName("§e용돈주기").setMaterial(Material.EMERALD).build();
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginCommand("프리키우기").setExecutor((sender, command, label, args) -> {
			if (!(sender instanceof Player)) return false;
			Player player = (Player) sender;
			Inventory mainScreen = getServer().createInventory(null, 45, "프리키우기");
			for(int i : new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43 ,44}) {
				mainScreen.setItem(i, ItemUtil.PANE);
			}
			mainScreen.setItem(20, exerciseButton);
			mainScreen.setItem(21, moneyButton);
			Inventory exerciseScreen = getServer().createInventory(null, 45, "프리키우기");
			for(int i : new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43 ,44}) {
				exerciseScreen.setItem(i, ItemUtil.PANE);
			}
			Gui mainGui = new Gui(mainScreen, true);
			Gui exerciseGui = new Gui(exerciseScreen, true);
			mainGui.addGuiCallback(exerciseButton, action -> {
				exerciseGui.open(player);
			});
			mainGui.addGuiCallback(moneyButton, action -> {
				player.sendMessage(PREFIX + "프리에게 얼마를 줄까? (채팅창에 입력해주세요!)");
				player.closeInventory();
				check.put(player.getUniqueId(), "돈주기");
			});
			mainGui.open(player);
			
			
			return true;
		});
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (check.containsKey(event.getPlayer().getUniqueId())) {
			Player player = event.getPlayer();
			String type = check.get(player.getUniqueId());
			check.remove(player.getUniqueId());
			switch(type) {
			case"돈주기":
				try {
					int money = Integer.parseInt(event.getMessage());
					if (AWCore.getAWCore().getEconomyManager().getBalance(player) < money) {
						player.sendMessage(PREFIX + "돈이 부족합니다!");
						break;
					}
					AWCore.getAWCore().getEconomyManager().withdrawPlayer(player, money);
					frees.get(player.getUniqueId()).addMoney(money);
					player.sendMessage(PREFIX + "프리에게 돈을 주었습니다!");
				} catch (Exception e) {
					player.sendMessage(PREFIX + "제대로 다시 입력해주세요!");
				}
				break;
			}
		}
	}
	
	@EventHandler
	public void onExit(PlayerQuitEvent event) {
		check.remove(event.getPlayer().getUniqueId());
		
	}
	
	public static class Free implements ConfigurationSerializable {
		
		private UUID owner;
		
		private String nickName = "프리";
		
		private int maxHealth = 10;
		private int health = maxHealth;
		
		private int power = 1;
		private int intelligence = 1;
		private int dexterity = 1;
		
		private int money = 0;
		
		public Free(Player owner) {
			this.owner = owner.getUniqueId();
		}

		public String getNickName() {
			return nickName;
		}

		public int getHealth() {
			return health;
		}

		public int getMaxHealth() {
			return maxHealth;
		}

		public int getPower() {
			return power;
		}

		public int getIntelligence() {
			return intelligence;
		}

		public int getDexterity() {
			return dexterity;
		}

		public int getMoney() {
			return money;
		}

		public void setNickName(String nickName) {
			this.nickName = nickName;
		}

		public void setHealth(int health) {
			this.health = health;
		}

		public void setMaxHealth(int maxHealth) {
			this.maxHealth = maxHealth;
		}

		public void setPower(int power) {
			this.power = power;
		}

		public void setIntelligence(int intelligence) {
			this.intelligence = intelligence;
		}

		public void setDexterity(int dexterity) {
			this.dexterity = dexterity;
		}

		public void setMoney(int money) {
			this.money = money;
		}

		public void addHealth(int health) {
			this.health += health;
		}

		public void addMaxHealth(int maxHealth) {
			this.maxHealth += maxHealth;
		}

		public void addPower(int power) {
			this.power += power;
		}

		public void addIntelligence(int intelligence) {
			this.intelligence += intelligence;
		}

		public void addDexterity(int dexterity) {
			this.dexterity += dexterity;
		}

		public void addMoney(int money) {
			this.money += money;
		}
		
		public void create() {
			frees.put(owner, this);
		}

		@Override
		public Map<String, Object> serialize() {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("owner", owner.toString());
			map.put("nickName", nickName);
			map.put("maxHealth", maxHealth);
			map.put("health", health);
			map.put("power", power);
			map.put("intelligence", intelligence);
			map.put("dexterity", dexterity);
			map.put("money", money);
			return map;
		}
		
		public Free(Map<String, Object> map) {
			owner = UUID.fromString((String) map.get("owner"));
			nickName = (String) map.get("nickName");
			maxHealth = (int) map.get("maxHealth");
			health = (int) map.get("health");
			power = (int) map.get("power");
			intelligence = (int) map.get("intelligence");
			dexterity = (int) map.get("dexterity");
			money = (int) map.get("money");
			create();
		}
		
	}

}
