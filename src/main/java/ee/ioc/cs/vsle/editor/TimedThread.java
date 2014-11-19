package ee.ioc.cs.vsle.editor;

public class TimedThread extends Thread {

  private long startTime = -1;

  public synchronized void start() {
    startTime = System.currentTimeMillis();
    super.start();
  }
  
  public long getStartTime() {
    checkStart();
    return startTime;
  }
  
  public long getElapsedTime() {
    checkStart();
    return System.currentTimeMillis() - startTime;
  }
  
  private void checkStart() {
    if( startTime == -1 ) { 
      throw new IllegalStateException("Thread not started yet!"); 
    }
  }
}
