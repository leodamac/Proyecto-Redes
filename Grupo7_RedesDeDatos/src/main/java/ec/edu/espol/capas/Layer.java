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
        
        this.poolInSuperior = new DataPool(1000);
        this.poolOutSuperior = new DataPool(1000);

        this.poolInInferior = new DataPool(1000);
        this.poolOutInferior = new DataPool(1000);
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
        if(data.length < 5){
            poolOutInferior.add(data);
        }else{
            //poolOutInferior.add(this.encapsulation(data));
            poolOutInferior.add(this.encapsulation(data));
        }
    }

    public void sendDataEqual(char[] data) throws InterruptedException {
        poolOutInferior.add(data);
    }
    
    public void sendDataSuperior(char[] data) throws InterruptedException {
        if(data.length < 5){
            poolOutSuperior.add(data);
        }else{
            //poolOutSuperior.add(this.desencapsulation(data));
            poolOutSuperior.add(this.desencapsulation(data));
        }
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
    
    public int doChecksum(char[] data){
        int checksum = 0;
        for (char c : data) {
            checksum += c;
        }
        return checksum;
    }
}
