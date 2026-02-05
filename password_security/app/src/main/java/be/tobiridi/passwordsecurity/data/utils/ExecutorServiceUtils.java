package be.tobiridi.passwordsecurity.data.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * Utils class to centralize common task and exception management when use a {@link ExecutorService}.
 */
public final class ExecutorServiceUtils {
    /**
     * Execute a {@link Runnable} task to the {@link ExecutorService}.
     * @param command The runnable task.
     * @return {@code true} if the command success {@code false} if an exception occurred.
     */
    public static boolean executeRunnable(ExecutorService executor, Runnable command) {
        try {
            executor.execute(command);
            return true;
        } catch (NullPointerException | RejectedExecutionException e) {
            return false;
        }
    }

    /**
     * Execute a {@link Callable} task to the {@link ExecutorService}.
     * @param callable The callable task.
     * @return The result of the callable task.
     */
    public static <T> T executeCallable(ExecutorService executor, Callable<T> callable) {
        try {
            return executor.submit(callable).get();
        } catch (NullPointerException | RejectedExecutionException e) {
            //the callable parameter is null or invalid
            throw new RuntimeException(e);
        } catch (CancellationException | ExecutionException | InterruptedException e) {
            //if the callable task has been cancelled or interrupted
            throw new RuntimeException(e);
        }
    }
}
