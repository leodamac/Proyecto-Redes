package pool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class DataPool<T> {
    private BlockingQueue<T> queue;
    
    
    public DataPool(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    public void add(T data) throws InterruptedException {
        queue.add(data);
    }

    public T take() throws InterruptedException {
        T item = queue.poll(1, TimeUnit.SECONDS);
        //return queue.take();
        return item;
    }
    
    public T take(int tSeconds) throws InterruptedException {
        T item = queue.poll(tSeconds, TimeUnit.SECONDS);
        //return queue.take();
        return item;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
    
    public int size(){
        return queue.size();
    }
    
    public void addAll(List<T> lista){
        queue.addAll(lista);
    }
    
    
}