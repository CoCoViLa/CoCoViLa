package ee.ioc.cs.vsle.classeditor;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ArcCalcTest {
	
	private static final int ARC_HEIGHT = 2000;
	private static final int ARC_WIDTH = 2000;
	private static final int CENTER_X = 1000;
	private static final int CENTER_Y = 1000;
	private static final int DEGREES_45 = 45;
	
	private MouseOps mouseOps;

	@Before
	public void setUp() {
		mouseOps = new MouseOps(null);
		
		mouseOps.startX = 0;
		mouseOps.startY = 0;
		mouseOps.arcHeight = ARC_WIDTH;
		mouseOps.arcWidth = ARC_HEIGHT;
	}

	@Test
	public void whenYCenteredLineStartAngleCalculatedCorrectly() {
		mouseOps.calculateStartAngle(ARC_WIDTH, CENTER_Y);
		assertEquals(0, mouseOps.arcStartAngle);

		mouseOps.calculateStartAngle(0, CENTER_Y);
		assertEquals(180, mouseOps.arcStartAngle);
	}

	@Test
	public void whenXCenteredStartAngleCalculatedCorrectly() {
		mouseOps.calculateStartAngle(CENTER_X, ARC_HEIGHT);
		assertEquals(-90, mouseOps.arcStartAngle);

		mouseOps.calculateStartAngle(CENTER_X, 0);
		assertEquals(90, mouseOps.arcStartAngle);
	}

	@Test
	public void whenXYInCornerStartAngleCalculatedCorrectly() {
		mouseOps.calculateStartAngle(ARC_WIDTH, ARC_HEIGHT);
		assertEquals(-DEGREES_45, mouseOps.arcStartAngle);

		mouseOps.calculateStartAngle(0, ARC_HEIGHT);
		assertEquals(-DEGREES_45 * 3, mouseOps.arcStartAngle);

		mouseOps.calculateStartAngle(0, 0);
		assertEquals(DEGREES_45 * 3, mouseOps.arcStartAngle);

		mouseOps.calculateStartAngle(ARC_WIDTH, 0);
		assertEquals(DEGREES_45, mouseOps.arcStartAngle);
	}
}
