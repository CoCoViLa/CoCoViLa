package ee.ioc.cs.vsle.util;

import ee.ioc.cs.vsle.editor.*;

/**
 * <p>Title: DebugPrinter</p>
 * <p>Description: Debug information printer. Central debug information printing
 *                 utility to allow filtering of printed information etc.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Aulo Aasmaa, Ando Saabas
 * @version 1.0
 */
public class db {

	/**
	 * Print out any object.
	 * @param o Object - object to be printed.
	 */
	public static void p(Object o) {
		if (RuntimeProperties.debugInfo == 1) {
			System.out.println(o);
		}
	} // p

	/**
	 * Print out boolean values.
	 * @param b boolean - boolean value to be printed.
	 */
	public static void p(boolean b) {
		if (RuntimeProperties.debugInfo == 1) {
			System.out.println(b);
		}
	} // p

	/**
	 * Print out integer values.
	 * @param i int - integer value to be printed.
	 */
	public static void p(int i) {
		if (RuntimeProperties.debugInfo == 1) {
			System.out.println(i);
		}
	} // p

	/**
	 * Print out float values.
	 * @param f float - float value to be printed.
	 */
	public static void p(float f) {
		if (RuntimeProperties.debugInfo == 1) {
			System.out.println(f);
		}
	} // p

   	public static void p(double f) {
		if (RuntimeProperties.debugInfo == 1) {
			System.out.println(f);
		}
	} // p
}