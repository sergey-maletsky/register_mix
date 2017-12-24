package com.firstlinesoftware.rmrs.server.servlets;

/**
 * Created by cybertron on 24.08.17.
 */
public class Main {
    public static void main (String args[]) {
        int i = 5;
        	while (i > 2 ) {
            		System.out.print(i);
            		if (i < 4)
                			try {	throw new ArithmeticException ("ex"); }

            			catch (ArithmeticException e) {
                				System.out.print(i);
                				break;
                			}
                            finally { System.out.print(i); }
            		i--;
            	}
    }

    public int methods () throws Exception, ClassNotFoundException {
        int i = 0;
        if (i == 0)
            throw new ClassCastException("first");
        if (i == 2)
            throw new ClassNotFoundException("second");

        return 1;
    }

}
