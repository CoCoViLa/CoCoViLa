package ee.ioc.cs.vsle.ccl;

/**
 * <p>Title: ee.ioc.cs.editor.ccl.CompileException</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Aulo Aasmaa, Ando Saabas
 * @version 1.0
 */
public class CompileException
	extends Throwable {

	/**
	 * Exception description.
	 */
	public String excDesc;

	/**
	 * Class constructor.
	 * @param msg String - exception message.
	 */
	CompileException(String msg) {
		excDesc = msg;
	} // ee.ioc.cs.editor.ccl.CompileException

}