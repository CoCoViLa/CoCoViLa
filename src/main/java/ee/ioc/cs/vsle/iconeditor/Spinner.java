package ee.ioc.cs.vsle.iconeditor;

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

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerListModel;

/**
 * Module: LineWidthSpinner
 * User: AASMAAUL
 * Date: 15.01.2004
 * Time: 13:27:30
 */
public class Spinner extends JSpinner {

	/**
	 * Spinner constructor.
	 * @param start - spinner starting value (first element).
	 * @param end - spinner end value (last element).
	 * @param step - spinner element step.
	 *
	public Spinner(double start, double end, double step) {
		String[] elements = setElements(start, end, step);
		SpinnerModel model = new SpinnerListModel(elements);
		this.setModel(model);
	}

	/**
	 * Spinner constructor.
	 * @param start - spinner starting value (first element).
	 * @param end - spinner end value (last element).
	 * @param step - spinner element step.
	 */
	public Spinner(int start, int end, int step, int initial) {
		String[] elements = setElements(start, end, step);
		SpinnerModel model = new SpinnerListModel(elements);
		model.setValue(Integer.toString(initial));
		this.setModel(model);
	}

	/**
	 * Set spinner elements.
	 * @param start - spinner starting value (first element).
	 * @param end - spinner end value (last element).
	 * @param step - spinner element step.
	 * @return String[] - array of spinner elements.
	 */
	private String[] setElements(int start, int end, int step) {
		int elementCount = ((end - start) / step) + 1;
		start = start - step;
		String[] elements = new String[elementCount];

		for (int i = 0; i < elements.length; i++) {
			elements[i] = String.valueOf(start += step);

		}
		return elements;
	}

	/**
	 * Set spinner elements.
	 * @param start - spinner starting value (first element).
	 * @param end - spinner end value (last element).
	 * @param step - spinner element step.
	 * @return String[] - array of spinner elements.
	 */
	private String[] setElements(double start, double end, double step) {
		int elementCount = (int) ((end - start) / step) + 1;
		start = start - step;
		String[] elements = new String[elementCount];

		for (int i = 0; i < elements.length; i++) {
			elements[i] = String.valueOf(start += step);

		}
		return elements;
	}

} // end of class
