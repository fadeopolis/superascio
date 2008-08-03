package prealpha;
import java.io.IOException;


public class Test {
	public static void main(String [] args) {
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
