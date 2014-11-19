/**
*
*/
package ee.ioc.cs.vsle.table;

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
