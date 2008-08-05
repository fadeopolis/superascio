package prealpha;
import java.io.IOException;
import java.util.ArrayList;
import prealpha.util.Util;

public class Test {
	public static void main(String [] args) {
		ArrayList<Object> a = new ArrayList<Object>();
		Object o = new Object();
		
		for ( Object po : a ) Util.shout(po);
		
		while (true) {
			try {
				if ( System.in.read() != 0 ) {
					System.out.println("GAME OVER");
					System.exit(0); 
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
