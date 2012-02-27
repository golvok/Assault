/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.util;

/**
 * a utility to block a thread until a value is ready.
 * useful for UI input.
 * @author matt
 */
public class ThreadBlocker<T> {

	private Thread waitingThread = null;
	private T value = null;
	private boolean valueReady = false;
	private boolean canceled = false;

	public ThreadBlocker() {
	}

	synchronized public T WaitUntilValue() {
		value = null;
		valueReady = false;
		canceled = false;
		waitingThread = Thread.currentThread();
		synchronized (waitingThread) {
			while (!valueReady) {
				try {
					waitingThread.wait();
				} catch (InterruptedException ex) {
				}
				if (canceled) {
					break;
				}
			}
		}
		waitingThread = null;
		valueReady = false;
		canceled = false;
		T tmp = value;
		value = null;
		return tmp;
	}

	public void notifyWaitingThreadWith(T value) {
		if (isAWaitingThread()) {
			//System.out.println("got value " + value + ". Waiting on lock");
			synchronized (waitingThread) {
				//System.out.println("got lock");
				this.value = value;
				valueReady = true;
				canceled = false;
				waitingThread.notify();

			}
		}
	}

	public void cancelWaitingThread() {
		if (isAWaitingThread()) {
			synchronized (waitingThread) {
				valueReady = false;
				value = null;
				canceled = true;
				waitingThread.notify();
			}
		}
	}

	public boolean isAWaitingThread() {
		return waitingThread != null;
	}
}
