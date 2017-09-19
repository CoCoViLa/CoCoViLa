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

class StringStack {
    String e;

    StringStack(String e) {
        this.e = e;
    }

    char peek() {
        return peek(0);
    }

    char peek(int n) {
        if (e.length() > n) {
            return e.charAt(n);
        }
        return '#';
    }
    
    void skipBlanks() {
        // Skip past any spaces and tabs on the current line of input.
        // Stop at a non-blank character or end-of-line.
        int i = 0;
        char ch;
        while ((ch = peek(i)) == ' ' || ch == '\t') {
            i++;
        }
        e = e.substring(i);
    }
    
    char getAnyChar() {
        char ch = e.charAt(0);

        e = e.substring(1, e.length());
        return ch;
    }

    double getString() {
        int i = 0;

        while (i < e.length() && Character.isDigit(e.charAt(i))) {
            i++;
        }
        double d = (Integer.parseInt(e.substring(0, i)));

        e = e.substring(i, e.length());
        return d;
    }

    char getChar() {
        char ch = peek();

        while (ch == ' ' || ch == '\n') {
            getAnyChar();
            ch = peek();
        }
        return getAnyChar();
    }

    boolean isEmpty() {
        return e.length() == 0;
    }
}

