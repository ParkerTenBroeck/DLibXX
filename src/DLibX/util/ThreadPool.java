package DLibX.util;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Class to concurrently process tasks in a queue, based on priority
 *
 * @author  Marcus Minhorst
 * @version 1.0
 */

public class ThreadPool {
    private long seq;
    private Worker[][] pool;
    private PriorityBlockingQueue<Task> tasks;
    private boolean running;

    /**
     * Makes a thread pool
     *
     * @param size Number of threads per priority level
     *
     * @throws IllegalArgumentException if {@code size.length == 0}
     * @throws IllegalArgumentException if any element in {@code size == 0}
     */

    public ThreadPool(int... size) {
        if (size.length == 0) {
            throw new IllegalArgumentException("Must specify at least one pool");
        }

        this.pool = new Worker[size.length][];
        this.seq = 0l;
        this.tasks = new PriorityBlockingQueue<>();

        for (int i = 0; i < size.length; i++) {
            if (size[i] <= 0) {
                throw new IllegalArgumentException("Pool size must be greater than 0");
            }
            this.pool[i] = new Worker[size[i]];
        }
    }

    /**
     * Starts proccessing jobs
     *
     * @throws IllegalStateException if pool has already started processing jobs
     */

    public synchronized void start() {
        if (running) {
            throw new IllegalStateException("Pool already started");
        }
        running = true;
        for (int i = 0; i < pool.length; i++) {
            for (int j = 0; j < pool[i].length; j++) {
                pool[i][j] = new Worker(i);
                pool[i][j].start();
            }
        }
    }

    /**
     * Stops proccessing jobs
     *
     * @throws IllegalStateException if pool is not processing jobs
     */

    public synchronized void stop() {
        if (!running) {
            throw new IllegalStateException("Pool already stopped");
        }
        for (Worker[] sub: pool) {
            for (Worker worker: sub) {
                worker.stopWorker();
            }
        }
        running = false;
    }

    /**
     * Adds task to pool with priority of zero
     *
     * @param task The task
     */

    public synchronized void addTask(Runnable task) {
        addTask(task, 0);
    }

    /**
     * Adds task to pool, sorted by priority
     *
     * @param task The task
     * @param priority The priority
     */

    public synchronized void addTask(Runnable task, int priority) {
        tasks.add(new Task(task, priority, seq));
        seq++;
    }

    /**
     * Checks if the pool has a task registered
     *
     * @param task The task
     *
     * @return true if it has the task, and it is not and has not been processed
     */

    public synchronized boolean hasTask(Runnable task) {
        return tasks.contains(task);
    }

    /**
     * Removes task from pool
     *
     * @param task The task
     */

    public synchronized void removeTask(Runnable task) {
        tasks.remove(task);
    }

    /**
     * Returns number of remaining tasks
     *
     * @return number of remaining tasks
     */

    public synchronized int tasksRemaining() {
        return tasks.size();
    }

    /**
     * Checks if any tasks are currently running
     *
     * @return true if there are tasks running
     */

    public synchronized boolean isIdle() {
        for (Worker[] sub: pool) {
            for (Worker worker: sub) {
                if (!worker.isIdle()) return false;
            }
        }
        return true;
    }

    private class Worker extends Thread {
        final int priority;
        boolean idle = true;
        volatile boolean running = true;

        Worker(int priority) {
            this.priority = priority;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    Task task = tasks.take();
                    if (priority == 0 || priority <= task.priority) {
                        idle = false;
                        task.run();
                        idle = true;
                    } else {
                        tasks.put(task);
                        Thread.sleep(10);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            idle = true;
        }

        public boolean isIdle() {
            return idle;
        }

        public void stopWorker() {
            running = false;
        }
    }

    private class Task implements Runnable, Comparable<Task> {
        final long id;
        final int priority;
        final Runnable task;

        Task(Runnable task, int priority, long id) {
            this.task = task;
            this.priority = priority;
            this.id = id;
        }

        @Override
        public int compareTo(Task that) {
            if (this.priority == that.priority) {
                return (this.id > that.id)? 1: -1;
            }
            return (this.priority > that.priority)? -1: 1;
        }

        @Override
        public boolean equals(Object that) {
            return this.task == ((Task)that).task;
        }

        @Override
        public int hashCode() {
            return this.task.hashCode();
        }

        @Override
        public void run() {
            task.run();
        }
    }
}
