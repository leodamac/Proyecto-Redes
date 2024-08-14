package pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class DataPool<T> {
    private BlockingQueue<T> queue;

    public DataPool(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    public void add(T data) throws InterruptedException {
        queue.put(data);
    }

    public T take() throws InterruptedException {
        return queue.take();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}