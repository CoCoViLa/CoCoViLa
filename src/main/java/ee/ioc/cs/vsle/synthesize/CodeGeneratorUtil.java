package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.util.TypeToken;

public class CodeGeneratorUtil {

	public static String getAnyTypeSubstitution(String name, String type) {
  	return new StringBuilder("((").append(getAnyCastType(type)).append(")").append(name).append(")").toString();
  }
  
	public static String getAnyCastType(String type) {
		TypeToken token = TypeToken.getTypeToken(type);
		if(token.isPrimitive())
			type = token.getObjType();
		return type;
  }
}
