package io.github.bananapuncher714;

import java.util.ArrayList;
import java.util.Random;

public class Dungeon extends Maze {
	protected String startChar = "s", endChar = "e";
	private int maxTries = 100;
	private int size = 124;
	private int padding = 6;
	private int roomNoTouch = 3;
	private int roomMaxStretch = 5;
	private int roomMinSize = 5;
	private DungeonRoom start = null, end;
	private boolean startDefined = false;
	
	private int sx, sy, rsx, rsy;
	
	private ArrayList< DungeonRoom > rooms = new ArrayList< DungeonRoom >();

	public Dungeon( int x, int y, String[] chars, int startx, int starty, int roomsizex, int roomsizey ) {
		super( x, y, chars );
		startDefined = true;
		sx = startx;
		sy = starty;
		rsx = roomsizex;
		rsy = roomsizey;
	}
	
	public Dungeon( int x, int y, String[] chars ) {
		super( x, y, chars );
	}
	
	public Dungeon( int x, int y ) {
		super( x, y );
	}
	
	private boolean overlap( DungeonRoom room ) {
		for ( DungeonRoom r : rooms ) {
			if ( room.x - roomNoTouch < r.x + r.width && room.x + room.width + roomNoTouch > r.x && room.y - roomNoTouch < r.y + r.height && room.y + room.height + roomNoTouch > r.y ) return true;
		}
		return false;
	}
	
	private void createRooms() {
		if ( startDefined ) {
			for ( int i = 0; i < rsx; i++ ) {
				for ( int j = 0; j < rsy; j++ ) {
					int x = sx + i;
					int y = sy + j;
					if ( charIsValid( x, y ) ) {
						map[ x ][ y ] = hallChar;
					}
				}
			}
			start = new DungeonRoom( sx, sy, rsx, rsy );
			rooms.add( start );
		}
		Random r = new Random();
		for ( int i = 0; i < maxTries; i++ ) {
			DungeonRoom room = new DungeonRoom( r.nextInt( size - ( 2 * padding ) - ( roomMaxStretch + roomMinSize ) ) + padding, r.nextInt( size - ( 2 * padding ) - ( roomMaxStretch + roomMinSize ) ) + padding, r.nextInt( roomMaxStretch ) + roomMinSize, r.nextInt( roomMaxStretch ) + roomMinSize );
			if ( rooms.size() == 0 || !overlap( room ) ) {
				rooms.add( room );
				for ( int x = 0 ; x < room.width; x++ ) {
					for ( int y = 0 ; y < room.height; y++ ) {
						map[ x + room.x ][ y + room.y ] = hallChar;
					}
				}
			}
		}
	}
	
	@SuppressWarnings("serial")
	private void connectRooms() {
		Random rng = new Random();
		int[] door;
		ArrayList< int[] > possibleEntrances;
		for ( DungeonRoom r : rooms ) {
			possibleEntrances = new ArrayList< int[] >() {};
			for ( int w = 0 ; w < r.width; w++ ) {
				if ( getChar( r.x + w, r.y - 2 ) == hallChar && isValidPath( new int[] { r.x + w, r.y - 1 }, 2, true, false ) ) {
					possibleEntrances.add( new int[] { r.x + w, r.y - 1 } );
				}
				if ( getChar( r.x + w, r.y + r.height + 1 ) == hallChar && isValidPath( new int[] { r.x + w, r.y + r.height }, 2, true, false ) ) {
					possibleEntrances.add( new int[] { r.x + w, r.y + r.height } );
				}
			}
			
			for ( int h = 0 ; h < r.height; h++ ) {
				if ( getChar( r.x - 2, r.y + h ) == hallChar && isValidPath( new int[] { r.x - 1, r.y + h }, 2, true, false ) ) {
					possibleEntrances.add( new int[] { r.x - 1, r.y + h } );
				}
				if ( getChar( r.x + r.width + 1, r.y + h ) == hallChar && isValidPath( new int[] { r.x + r.width, r.y + h }, 2, true, false ) ) {
					possibleEntrances.add( new int[] { r.x + r.width, r.y + h } );
				}
				
			}
			
			if ( possibleEntrances.size() == 0 ) {
				System.out.println( "A room with no entrances has been found!" );
			} else {
				do {
					door = possibleEntrances.get( rng.nextInt( possibleEntrances.size() - 1 ) );
					possibleEntrances.remove( door );
					if ( isValidPath( door, 2, true, false ) ) {
						map[ door[ 0 ] ][ door[ 1 ] ] = hallChar;
						r.addRoom( door );
						nodes.add( door );
					}
				} while ( rng.nextInt( 8 ) == 0 && possibleEntrances.size() > 0 );
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void trimEnds( int max ) {
		boolean deadEndFound;
		ArrayList< int[] > newNodes = ( ArrayList< int[] > ) nodes.clone();
		while ( nodes.size() > max ) {
			deadEndFound = false;
			for ( int[] coord: nodes ) {
				if ( isValidPath( coord, 1, true, false ) ) {
					map[ coord[ 0 ] ][ coord[ 1 ] ] = wallChar;
					newNodes.remove( coord );
					deadEndFound = true;
				}
			}
				nodes = ( ArrayList< int[] > ) newNodes.clone();
			if ( !deadEndFound ) {
				return;
			}
		}
	}
	
	private int[] getValidStartPoint() {
		Random r = new Random();
		int x = size, y = size;
		while ( !isValidPath( new int[] { x, y }, 0, false, false ) ) {
			x = r.nextInt( map.length - 1 ) - 1;
			y = r.nextInt( map[ x ].length - 1 ) - 1;
		}
		return new int[] { x, y };
	}
	
	private void setStartAndFinish() {
		Random r = new Random();
		if ( start == null ) start = rooms.get( r.nextInt( rooms.size() ) );
		end = rooms.get( r.nextInt( rooms.size() ) );
		map[ ( int ) ( start.x + start.width *.5 ) ][ ( int ) ( start.y + start.height * .5 ) ] = startChar;
		while ( start == end ) {
			end = rooms.get( r.nextInt( rooms.size() ) );
		}
		map[ ( int ) ( end.x + end.width *.5 ) ][ ( int ) ( end.y + end.height * .5 ) ] = endChar; 
	}
	
	public void generateDungeon() {
		createRooms();
		super.hallwayChance = 1;
		super.cornerChance = 2;
		super.generateMaze( false, getValidStartPoint() , false );
		setStartAndFinish();
		connectRooms();
		trimEnds( 2500 );
	}
	
	@Override
	protected boolean decide( int[] c ) {
		return super.decide( c );
	}
	
	public DungeonRoom getStart() {
		return start;
	}
	
	public DungeonRoom getEnd() {
		return end;
	}

}

