package me.DMan16.DMan16Utils.Interfaces;

@FunctionalInterface
public interface ExceptionalRunnable {
    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see     Thread#run()
     */
    void run() throws Exception;
}
