package io.github.bananapuncher714;

import java.util.ArrayList;

public class DungeonRoom {
	protected int x, y, height, width;
	protected ArrayList< int[] > entrances = new ArrayList< int[] >();
	
	public DungeonRoom( int x, int y, int width, int height ) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	protected void addRoom( int[] room ) {
		entrances.add( room );
	}

}