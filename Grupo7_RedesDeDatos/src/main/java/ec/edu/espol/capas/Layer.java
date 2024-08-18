package ec.edu.espol.capas;

import pool.DataPool;


public abstract class Layer {
    short level;
    String dataGram;
    
    DataPool<char[]> poolInSuperior;
    DataPool<char[]> poolOutSuperior;
    
    DataPool<char[]> poolInInferior;
    DataPool<char[]> poolOutInferior;

    public Layer(short level, String dataGram) {
        this.level = level;
        this.dataGram = dataGram;
        
        this.poolInSuperior = new DataPool(100);
        this.poolOutSuperior = new DataPool(100);

        this.poolInInferior = new DataPool(100);
        this.poolOutInferior = new DataPool(100);
    }

    public abstract char[] encapsulation(char[] data, boolean urgentFlag, char[] urgentData);
    public abstract char[] encapsulation(char[] data);
    public abstract char[] desencapsulation(char[] data);
    public abstract char[] generateHeader(char[] data);
    public abstract char[] generateHeader(char[] data, char[] urgentData);
    public abstract char[] generateTrailer(char[] data);

    public short getLevel() {
        return level;
    }
    
    public DataPool<char[]> getPoolInSuperior() {
        return poolInSuperior;
    }

    public DataPool<char[]> getPoolOutSuperior() {
        return poolOutSuperior;
    }

    public DataPool<char[]> getPoolInInferior() {
        return poolInInferior;
    }

    public DataPool<char[]> getPoolOutInferior() {
        return poolOutInferior;
    }
    
    public void sendDataInferior(char[] data) throws InterruptedException {
        poolOutInferior.add(this.encapsulation(data));
    }

    public void sendDataEqual(char[] data) throws InterruptedException {
        poolOutInferior.add(data);
    }
    
    public void sendDataSuperior(char[] data) throws InterruptedException {
        poolOutSuperior.add(this.desencapsulation(data));
    }

    public char[] receiveDataInferior() throws InterruptedException {
        char[] receivedMessage = poolInInferior.take();
        return receivedMessage;
    }
    
    public char[] receiveDataEqual() throws InterruptedException {
        char[] receivedMessage = poolInSuperior.take();
        return receivedMessage;
    }
    
    public char[] receiveDataSuperior() throws InterruptedException {
        char[] receivedMessage = poolInSuperior.take();
        return receivedMessage;
    }
}
