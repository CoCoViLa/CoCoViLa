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

import java.util.*;

/**
 * <p>Title: ee.ioc.cs.editor.synthesize.ClassList</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Ando Saabas
 * @version 1.0
 */
public class ClassList
	extends LinkedHashSet<AnnotatedClass> {

	/**
	 * Class constructor.
	 */
	public ClassList() {
		super();
	} // ee.ioc.cs.editor.synthesize.ClassList

	public ClassList(Collection<AnnotatedClass> acs) {
		super(acs);
	}

	/**
	 * Returns the Annotated Class of a specified type.
	 * @param type String - type of the Annotated Class to be returned.
	 * @return AnnotatedClass - AnnotatedClass of a specified type.
	 */

	public AnnotatedClass getType(String type) {
		for (AnnotatedClass ac : this ) {

			if ( ac.getName().equals(type) ) {
				return ac;
			}
		}
		return null;
	} // getType

	public boolean containsType( String type ) {
		return getType( type ) != null;
	}

}
