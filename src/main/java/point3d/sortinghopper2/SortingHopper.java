package point3d.sortinghopper2;
import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.sothatsit.usefulsnippets.L;

/**
 * Sorting Hopper plugin. Allows the creation of a special hopper that
 * "sorts", or only accepts a particular input.
 */
public class SortingHopper extends JavaPlugin {

	private static final String TAG = "SortingHopper";
  	public static final Logger mclog = Logger.getLogger("minecraft"); 
  	private Rules rules;
  	private Sorter sorter;
  	private static boolean debug = true;
  	
  	
	@Override
	public void onEnable() {
		L.setLogger(this.getLogger());
		L.i(TAG,"Enabling SortingHopper2");
		this.rules = new Rules(this);
		this.sorter = new Sorter(this);

		PluginManager pm = getServer().getPluginManager();

		this.loadConf();
		debug=getConfig().getBoolean("debug");

		this.getCommand("sortinghopper").setExecutor(new CommandListener(this));

		final PlayerListener playerListener = new PlayerListener(this);
		pm.registerEvents(playerListener, this);

		final HopperListener hopperListener = new HopperListener(this);
		pm.registerEvents(hopperListener, this);

		if (getConfig().getBoolean("replacedrops")) {
			final BreakListener breakListener = new BreakListener(this);
			pm.registerEvents(breakListener, this);
		} else {
			if(Material.matchMaterial(this.getConfig().getString("add_drop")) != null) {
				ItemStack drop = new ItemStack(Material.matchMaterial(this.getConfig().getString("add_drop")));
				final BreakListenerGentle breakListener = new BreakListenerGentle(this, drop);
				pm.registerEvents(breakListener, this);
			}
		}

		//Config setting renamed, checking old name for compatibility
		if(getConfig().getBoolean("check_lore")){
			final PlaceListener placeListener = new PlaceListener(this);
			pm.registerEvents(placeListener, this);
		}

		if (getConfig().getBoolean("preventitempickup")) {
			final PickupListener pickupListener = new PickupListener(this);
			pm.registerEvents(pickupListener, this);
		} else if(getConfig().getBoolean("sortitempickup")) {
			final PickupListenerFilter pickupListener = new PickupListenerFilter(this);
			pm.registerEvents(pickupListener, this);
		}

		if (getConfig().getBoolean("crafting.enabled")) {
			final ServerLoadListener serverloadistener = new ServerLoadListener(this);
			pm.registerEvents(serverloadistener, this);
		}

		rules.loadAndBackup();
		TagUtil.loadSortingTags();
		L.i(TAG,"SortingHopper2 started!");
	}

	@Override
	public void onDisable() {
			L.i(TAG, "Saving rules...");
			rules.saveRules();
	}

	public void reload(){
		this.reloadConfig();
		TagUtil.loadSortingTags();
		sorter.reload();
	}

	private void loadConf(){
		this.saveDefaultConfig();

		File itemgroupsyml = new File(this.getDataFolder(), "itemgroups.yml");
		if (!itemgroupsyml.exists()) {
			this.saveResource("itemgroups.yml", false);
		}
		sorter.reload();
	}

	public Rules getRules(){
		return this.rules;
	}
}