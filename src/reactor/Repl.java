/**
 * 
 */
package reactor;

import java.util.Scanner;

/**
 * @author tom
 *
 */
public class Repl implements Runnable {
	volatile boolean stop;
	
	/**
	 * ctor
	 */
	public Repl() {
		stop=false;
	}
	
	/**
	 * do not stop
	 * @return the inverse of stop
	 */
	public boolean dontStop() {
		return !stop;
	}
	
	@Override
	public void run() {
		final Scanner sc = new Scanner(System.in);
		String in = sc.next();
		while (!in.equals("print")) {
			in = sc.next();
		}
		if (in.equals("print")) {
			Stats.print();
			stop=true;
		}
		return;
	}

}
