package io.github.bananapuncher714;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonGenMain extends JavaPlugin implements Listener {
	private Map< Location, Material > dungeonParts = new HashMap< Location, Material >();
	private int maxIterations = 100, layers = 4;
	private boolean fillBlanks = true, skipWalls = true;
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				if ( !dungeonParts.isEmpty() ) {
					int iterations = 0;
					System.out.println( "Blocks Left: " + dungeonParts.size() );
					for ( Iterator< Location > it = dungeonParts.keySet().iterator(); it.hasNext(); ) {
						Location part = it.next();
						part.getBlock().setType( dungeonParts.get( part ) );
						it.remove();
						if ( iterations++ == maxIterations ) {
							break;
						}
					}
				}
			}
		}, 4, 4 );
	}
	
	public boolean onCommand( CommandSender s, Command c, String l, String[] a ) {
		Random r = new Random();
		if ( c.getName().equalsIgnoreCase( "maze" ) ) {
			Maze maze = new Maze( 120, 120 );
			maze.generateMaze( true, new int[] { 1, 1 }, true );
			if ( !( s instanceof Player ) ) {
				maze.printMaze();
			} else {
				String[][] dungeonMap = maze.getMap();
				Location start = ( ( Player ) s ).getLocation();
				int blockx = 0;
				for ( String[] row : dungeonMap ) {
					int blocky = 0;
					for ( String column : row ) {
						if ( column.equalsIgnoreCase( maze.wallChar ) ) {
							for ( int y = 0; y < 3; y ++ ) {
								if ( r.nextInt( 2 ) == 0 ) { 
									dungeonParts.put( start.clone().add( blockx, y, blocky ), Material.MOSSY_COBBLESTONE );
								} else {
									dungeonParts.put( start.clone().add( blockx, y, blocky ), Material.COBBLESTONE );
								}
							}
						} else if ( column.equalsIgnoreCase( maze.answerChar ) ) {
							dungeonParts.put( start.clone().add( blockx, -1, blocky ), Material.STAINED_CLAY );
						} else {
							dungeonParts.put( start.clone().add( blockx, -1, blocky ), Material.STONE );
						}
						blocky ++;
					}
					blockx ++;
				}
			}
		}
		if ( c.getName().equalsIgnoreCase( "dungeon" ) ) {
			DungeonRoom startr = null;
			DungeonRoom endr = null;
			for ( int i = 0; i < layers; i++ ) {
				Dungeon dungeon;
				if ( i == 0 || endr == null) dungeon = new Dungeon( 124, 124 );
				else dungeon = new Dungeon( 124, 124, new String[] {" ","█","."}, endr.x, endr.y, endr.width, endr.height );
				dungeon.generateDungeon();
				startr = dungeon.getStart();
				endr = dungeon.getEnd();
				if ( !( s instanceof Player ) ) {
					dungeon.printMaze();
				} else {
					String[][] dungeonMap = dungeon.getMap();
					Location start = (( Player ) s ).getLocation();
					int blockx = 0;
					for ( String[] row : dungeonMap ) {
						int blocky = 0;
						for ( String column : row ) {
							if ( column.equalsIgnoreCase( dungeon.wallChar ) ) {
								if ( !skipWalls ) {
									if ( ( dungeon.isValidPath( new int[] { blockx, blocky }, 1, false, true ) || blockx == 0 || blockx == dungeonMap.length - 1 || blocky == 0 || blocky == dungeonMap[ blockx ].length - 1 ) || !fillBlanks ) {
										for ( int y = 0; y < 3; y ++ ) {
											if ( r.nextInt( 2 ) == 0 ) { 
												dungeonParts.put( start.clone().add( blockx, y + i * 4, blocky ), Material.MOSSY_COBBLESTONE );
											} else {
												dungeonParts.put( start.clone().add( blockx, y + i * 4, blocky ), Material.COBBLESTONE );
											}
										}
									}
								}
							} else if ( column.equalsIgnoreCase( dungeon.startChar ) ) {
								dungeonParts.put( start.clone().add( blockx, -1 + i * 4, blocky ), Material.GOLD_BLOCK );
							} else if ( column.equalsIgnoreCase( dungeon.endChar ) ) { 
								dungeonParts.put( start.clone().add( blockx, -1 + i * 4, blocky ), Material.EMERALD_BLOCK );
							} else {
								dungeonParts.put( start.clone().add( blockx, -1 + i * 4, blocky ), Material.STONE );
							}
							blocky ++;
						}
						blockx ++;
					}
				}
			}
		}
		return true;
	}
}

