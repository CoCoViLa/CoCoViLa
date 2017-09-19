package ee.ioc.cs.vsle.synthesize;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
