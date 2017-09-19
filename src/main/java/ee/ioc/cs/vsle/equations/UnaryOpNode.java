package ee.ioc.cs.vsle.equations;

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

class UnaryOpNode extends ExpNode {
	// An expression node to represent a unary minus operator + sin, cos, log,  abs, tan.
	ExpNode operand;
	String meth;
	String sub;

	UnaryOpNode(ExpNode operand, String meth) {
		this.operand = operand;
		this.meth = meth;
	}

	@Override
	void getExpressions() {
		operand.getExpressions(sub);
	}

	@Override
	void getExpressions(String upper) {
	    
		if ( Function.contains( meth ) ) {
			operand.getExpressions( Function.valueOf( meth ).getOpposite() + "(" + upper + ")");
		} else {
			operand.getExpressions(meth + "(" + upper + ")");
		}
	}

	@Override
	void getVars() {
		operand.getVars();
	}

	void reverse() {
		sub = operand.inFix();
	}

	@Override
	void decorate() {
		reverse();
		operand.decorate();
	}

	@Override
	String inFix() {
		String a = operand.inFix();

		if (!meth.equals("-")) {
			return Function.valueOf( meth ).getFunction() + "(" + a + ")";
		}
		return meth + "(" + a + ")";
	}

}
