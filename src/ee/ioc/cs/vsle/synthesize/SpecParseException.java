package ee.ioc.cs.vsle.synthesize;

public class SpecParseException
	extends Throwable {
	public String excDesc;
	SpecParseException(String s) {
		excDesc = s;
	}
}
