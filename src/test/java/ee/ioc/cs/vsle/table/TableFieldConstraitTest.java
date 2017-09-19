/**
*
*/
package ee.ioc.cs.vsle.table;

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

import org.junit.*;

import static ee.ioc.cs.vsle.table.TableFieldConstraint.*;

/**
* @author pavelg
*
*/
public class TableFieldConstraitTest {

    @Test
    public void range() {
      //1-5
        Range rc = new Range();
        rc.setMin( 1 );
        rc.setMax( 5 );
        Assert.assertFalse( rc.verify( 0 ) );
        Assert.assertTrue(rc.verify( 1 ));
        Assert.assertTrue(rc.verify( 2 ));
        Assert.assertTrue(rc.verify( 5 ));
        Assert.assertFalse(rc.verify( 6 ));
        //>=1
        rc = new Range();
        rc.setMin( 1 );
        Assert.assertFalse(rc.verify( 0 ));
        Assert.assertTrue(rc.verify( 1 ));
        Assert.assertTrue(rc.verify( 2 ));
        //<=5
        rc = new Range();
        rc.setMax( 5 );
        Assert.assertTrue(rc.verify( 2 ));
        Assert.assertTrue(rc.verify( 5 ));
        Assert.assertFalse(rc.verify( 6 ));
    }

    @Test
    public void list() {
        List lc = new List();
        lc.setValueList( new Object[] { 1, 3, 5, 7 } );
        Assert.assertFalse( lc.verify( 0 ) );
        Assert.assertTrue( lc.verify( 1 ) );
        Assert.assertFalse( lc.verify( 4 ) );
        Assert.assertTrue( lc.verify( 5 ) );
        Assert.assertFalse( lc.verify( 100 ) );
    }
}
