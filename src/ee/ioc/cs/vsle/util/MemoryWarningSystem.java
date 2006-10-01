package ee.ioc.cs.vsle.util;

import javax.management.*;
import java.lang.management.*;
import java.util.*;

public final class MemoryWarningSystem {

	private final static MemoryWarningSystem s_instance = new MemoryWarningSystem();
	
	public static MemoryWarningSystem getInstance() {
		return s_instance;
	}
	
	private final Collection<Listener> listeners =
		new ArrayList<Listener>();

	private MemoryWarningSystem() {
		System.err.println( "Starting Memory Warning System" );
		MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
		NotificationEmitter emitter = (NotificationEmitter) mbean;
		emitter.addNotificationListener(new NotificationListener() {
			public void handleNotification(Notification n, Object hb) {
				if (n.getType().equals(
						MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED)) {
					long maxMemory = tenuredGenPool.getUsage().getMax();
					long usedMemory = tenuredGenPool.getUsage().getUsed();
					System.err.println( "usedMemory: " + usedMemory + " maxMemory: " + maxMemory );
					for (Listener listener : listeners) {
						listener.memoryUsageLow(usedMemory, maxMemory);
					}
				}
			}
		}, null, null);
	}

	public boolean addListener(Listener listener) {
		return listeners.add(listener);
	}

	public boolean removeListener(Listener listener) {
		return listeners.remove(listener);
	}

	private static final MemoryPoolMXBean tenuredGenPool =
		findTenuredGenPool();

	public static void setPercentageUsageThreshold(double percentage) {
		if (percentage <= 0.0 || percentage > 1.0) {
			throw new IllegalArgumentException("Percentage not in range");
		}
		long maxMemory = tenuredGenPool.getUsage().getMax();
		long warningThreshold = (long) (maxMemory * percentage);
		System.err.println( "percentage: " + percentage + " warningThreshold: " + warningThreshold + " maxMemory: " + maxMemory );
		tenuredGenPool.setUsageThreshold(warningThreshold);
	}

	/**
	 * Tenured Space Pool can be determined by it being of type
	 * HEAP and by it being possible to set the usage threshold.
	 */
	private static MemoryPoolMXBean findTenuredGenPool() {
		for (MemoryPoolMXBean pool :
			ManagementFactory.getMemoryPoolMXBeans()) {
			// I don't know whether this approach is better, or whether
			// we should rather check for the pool name "Tenured Gen"?
			if (pool.getType() == MemoryType.HEAP &&
					pool.isUsageThresholdSupported()) {
				return pool;
			}
		}
		throw new AssertionError("Could not find tenured space");
	}

	public interface Listener {
		public void memoryUsageLow(long usedMemory, long maxMemory);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		MemoryWarningSystem.setPercentageUsageThreshold(0.6);

		MemoryWarningSystem mws = new MemoryWarningSystem();
		mws.addListener(new MemoryWarningSystem.Listener() {
			double per = 0.6;
			public void memoryUsageLow(long usedMemory, long maxMemory) {
				System.out.println("Memory usage low!!!");
				double percentageUsed = ((double) usedMemory) / maxMemory;
				System.out.println("percentageUsed = " + percentageUsed);
				per += 0.1;
				MemoryWarningSystem.setPercentageUsageThreshold(per);
			}
		});

		Collection<Double> numbers = new LinkedList<Double>();
		while (true) {
			numbers.add(Math.random());
		}
	}

}
