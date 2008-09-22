package prealpha.util;

import java.nio.IntBuffer;
import java.util.Arrays;

public abstract class ArrayUtil {
	
	/**
	 * Removes all Occurences of the given ints from the given Array
	 * @param input the array to remove from
	 * @param toRemove the int values to remove
	 * @return
	 */
	public static int[] removeAllOccurences( int[] input, int... toRemove ) {
		if ( input == null || toRemove == null ) return null;

		IntBuffer buff = IntBuffer.allocate(input.length);
		
		for ( int r : toRemove ) {		
			buff.clear();
			
			int x = 0;
			
			for ( int i = 0; i < input.length; i++ ) {
				if ( input[i] != r ) buff.put(input[i]);
				else x++;
			}		
			
			input = new int[input.length-x];
			
			for ( int i = 0; i < input.length; i++ ) {
				input[i] = buff.get(i);
			}			
		}
		return input;	
	}
	
	/**
	 * add the give int values to the array
	 * @param input
	 * @param toAdd
	 * @return
	 */
	public static int[] addToArray( int[] input, int... toAdd ) {
//		System.out.println(Arrays.toString(toAdd)+"x"+toAdd.length);
		if ( input == null ) return toAdd;
		if ( toAdd == null ) return input;
		
		IntBuffer buff = IntBuffer.allocate(input.length + toAdd.length);
		buff.put(input);		
		
		for ( int i : toAdd ) buff.put(i);
		
		return buff.array();
	}
	
	/**
	 * remove the given int values from the array
	 * @param input
	 * @param toRemove
	 * @return
	 */
	public static int[] removeFromArray( int[] input, int... toRemove ) {
//		System.out.println(Arrays.toString(toAdd)+"x"+toAdd.length);
		if ( input == null ) return null;
		if ( toRemove == null ) return input;
		
		IntBuffer buff = IntBuffer.allocate(input.length - toRemove.length);
		
		for ( int r : toRemove ) {
			for ( int i : input ) {
				if ( i != r ) buff.put(i);
			}
		}

		return buff.array();
	}
	
	public static void main(String[] args) {
		int[] i = { 1,2,3,4,5 };
		
		System.out.println(Arrays.toString(i));
		
		i = ArrayUtil.removeAllOccurences(i, 3,5,0);
		
		System.out.println(Arrays.toString(i));
		
		i = ArrayUtil.addToArray(i, 0,1,0,1);
		
//		i = ArrayUtil.removeFromArray(i, 2);
		
		System.out.println(Arrays.toString(i));
	}

}