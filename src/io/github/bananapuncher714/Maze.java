package io.github.bananapuncher714;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Maze {
	protected String hallChar = " ", wallChar = "█", answerChar = ".";
	protected ArrayList< int[] > nodes = new ArrayList< int[] >();
	protected String[][] map;
	
	protected int direc;
	protected int hallwayChance = 3;
	protected int cornerChance = 1;
	
	public Maze( int x, int y ) {
		map = new String[ x ][ y ];
		for ( String[] row : map ) {
			Arrays.fill( row, wallChar );
		}
	}
	
	public Maze( int x, int y, String[] chars ) {
		map = new String[ x ][ y ];
		for ( String[] row : map ) {
			Arrays.fill( row, wallChar );
		}
		hallChar = chars[ 0 ];
		wallChar = chars[ 1 ];
		answerChar = chars[ 2 ];
	}
	
	public String[][] getMap() {
		return map.clone();
	}
	
	public void printMaze() {
		String row;
		for ( String[] i : map ) {
			row = "";
			for ( String j : i ) {
				row = row + j;
			}
			System.out.println( row );
		}
	}
	
	public void generateMaze( boolean showAnswer, int[] start, boolean hasStartAndEnd ) {
		ArrayList< int[] > validPaths = new ArrayList< int[] >();
		HashMap< Integer, ArrayList< int[] > > mazeEnds = new HashMap< Integer, ArrayList< int[] > >();
		
		Random r = new Random();
		
		map[ start[ 0 ] ][ start[ 1 ] ] = hallChar;
		validPaths.add( start );
		if ( hasStartAndEnd ) {
			map[ 0 ][ 1 ] = hallChar;
			validPaths.add( new int[] { 0,1 } );
		}
		
		ArrayList< int[] > tempCoordHolder = new ArrayList< int[] >();
		tempCoordHolder.add( start );
		mazeEnds.put( start.hashCode(), tempCoordHolder );
		
		while ( true ) {
			if ( validPaths.size() == 0 ) {
				break;
			}
			int[] path = validPaths.get( Math.abs( r.nextInt() % validPaths.size() ) );
			if ( newPath( path, mazeEnds, validPaths ) == 0 ) {
				if ( path[ 0 ] != 1 && path[ 0 ] != map.length - 2 && path[ 1 ] != 1 && path[ 1 ] != map[ path[ 0 ] ].length - 2 ) {
					mazeEnds.remove( path.hashCode() );
				}
				validPaths.remove( path );
			}
		}
		
		if ( hasStartAndEnd ) {
			int length = 0;
			ArrayList< ArrayList< int[] > > possibleEnds = new ArrayList< ArrayList< int[] > >();
			for ( int id : mazeEnds.keySet() ) {
				if ( mazeEnds.get( id ).size() > length ) {
					possibleEnds.clear();
					possibleEnds.add( mazeEnds.get( id ) );
					length = mazeEnds.get( id ).size();
				} else if ( mazeEnds.get( id ).size() == length ) {
					possibleEnds.add( mazeEnds.get( id ) );
				}	
			}
			
			ArrayList< int[] > finalEnds = possibleEnds.get( Math.abs( r.nextInt() % possibleEnds.size() ) );
			int[] lastCoord = finalEnds.get( finalEnds.size() - 1 );
			if ( !charIsValid( lastCoord[ 0 ], lastCoord[ 1 ] + 1 ) ) {
				finalEnds.add( new int[] { lastCoord[ 0 ], lastCoord[ 1 ] + 1 } );
			} else if ( !charIsValid( lastCoord[ 0 ], lastCoord[ 1 ] - 1 ) ) {
				finalEnds.add( new int[] { lastCoord[ 0 ], lastCoord[ 1 ] - 1 } );
			} else if ( !charIsValid( lastCoord[ 0 ] + 1, lastCoord[ 1 ] ) ) {
				finalEnds.add( new int[] { lastCoord[ 0 ] + 1, lastCoord[ 1 ] } );
			} else if ( !charIsValid( lastCoord[ 0 ] - 1, lastCoord[ 1 ] ) ) {
				finalEnds.add( new int[] { lastCoord[ 0 ] - 1, lastCoord[ 1 ] } );
			}
			
			lastCoord = finalEnds.get( finalEnds.size() - 1 );
			finalEnds.add( new int[] { 0, 1 } );
			map[ lastCoord[ 0 ] ][ lastCoord[ 1 ] ] = hallChar;
			
			if ( showAnswer ) {
				for ( int[] coord : finalEnds ) {
					map[ coord[ 0 ] ][ coord[ 1 ] ] = answerChar;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected int newPath( int[] path, HashMap< Integer, ArrayList< int[] > > mazeEnds, ArrayList< int[] > validPaths ) {
		int score = 0, x = 0, y = 0;
		int[] coordset;
		for ( int i = 0; i < 4; i++ ) {
			if ( i == 0 ) {
				x = -1;
			} else if ( i == 1 ) {
				x = 1;
			} else if ( i == 2 ) {
				x = 0;
				y = 1;
			} else if ( i == 3 ) {
				y = -1;
			}
			coordset = path.clone();
			coordset[ 0 ] = coordset[ 0 ] + x;
			coordset[ 1 ] = coordset[ 1 ] + y;
			if ( isValidPath( coordset ) ) {
				if ( decide( coordset ) ) {
					mazeEnds.put( coordset.hashCode(), ( ArrayList< int[] > ) mazeEnds.get( path.hashCode() ).clone() );
					mazeEnds.get( coordset.hashCode() ).add( coordset );
					validPaths.add( coordset );
					nodes.add( coordset );
					map[ coordset[ 0 ] ][ coordset[ 1 ] ] = hallChar;
				}
				score ++;
			}

		}
		return score;
	}
	
	protected boolean isValidPath( int[] p ) {
		return isValidPath( p, 1, false, false );
	}
	
	protected boolean isValidPath( int[] p, int amountOfEntrances, boolean isNotWall, boolean atLeast ) {
		int amount = 0, x = p[ 0 ], y = p[ 1 ];
		if ( getChar( x, y ) == wallChar || isNotWall ) {
			if ( getChar( x, y + 1 ) == hallChar ) {
				amount ++;
				direc = 1;
			} if ( getChar( x, y - 1 ) == hallChar ) {
				amount ++;
				direc = 2;
			} if ( getChar( x + 1, y ) == hallChar ) {
				amount ++;
				direc = 10;
			} if ( getChar( x - 1, y ) == hallChar ) {
				amount ++;
				direc = 20;
			} if ( amount == amountOfEntrances || ( atLeast && amount >= amountOfEntrances ) ) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean decide( int[] coordinates ) {
		Random r = new Random();
		boolean s = false;
		if ( direc == 1 && getChar( coordinates[ 0 ], coordinates[ 1 ] + 2 ) == hallChar ) {
			s = true;
		} if ( direc == 2 && getChar( coordinates[ 0 ], coordinates[ 1 ] - 2 ) == hallChar ) {
			s = true;
		} if ( direc == 10 && getChar( coordinates[ 0 ] + 2, coordinates[ 1 ] ) == hallChar ) {
			s = true;
		} if ( direc == 20 && getChar( coordinates[ 0 ] - 2, coordinates[ 1 ] ) == hallChar ) {
			s = true;
		} if ( s ) {
			if ( r.nextInt( hallwayChance ) == 0 ) {
				return true;
			}
			return false;
		} if ( r.nextInt( cornerChance ) == 0 ) {
			return true;
		}
		return false;
	}
	
	protected boolean charIsValid( int x, int y  ) {
		if ( x > 0 && x < map.length - 1 ) {
			if ( y > 0 && y < map[ 0 ].length - 1 ) {
				return true;
			}
		}
		return false;
	}
	
	protected String getChar( int x, int y ) {
		if ( charIsValid( x, y ) ) {
			return map[ x ][ y ];
		}
		return "DEFINITELY NOT";
	}
}