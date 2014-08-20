/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bigstone
 */
public class ThreadPool {

  private BlockingQueue taskQueue = null;
  private List<PoolThread> threads = new ArrayList<PoolThread>();
  private boolean isStopped = false;

  public boolean isTaskFinished(){
      return taskQueue.getQueueLength() <= 0;
  }
  
  public ThreadPool(int noOfThreads, int maxNoOfTasks){
    taskQueue = new BlockingQueue(maxNoOfTasks);

    for(int i=0; i<noOfThreads; i++){
      threads.add(new PoolThread(taskQueue));
    }
    for(PoolThread thread : threads){
      thread.start();
    }
  }

  public void excute(Runnable task){
    if(this.isStopped) throw
      new IllegalStateException("ThreadPool is stopped");

      try {
          this.taskQueue.enqueue(task);
      } catch (InterruptedException ex) {
          Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, ex);
      }
  }

  public synchronized void stop(){
    this.isStopped = true;
    for(PoolThread thread : threads){
      thread.close();
    }
  }

}
