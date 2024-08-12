package ec.edu.espol.capas;


abstract class Layer {
    short level;
    String dataGram;
    char[] capsule;
    public abstract char[] encapsulation(char[] data, boolean urgentFlag, char[] urgentData);
    public abstract char[] encapsulation(char[] data);
    public abstract char[] desencapsulation(char[] data);
    public abstract char[] generateHeader(char[] data);
    public abstract char[] generateHeader(char[] data, char[] urgentData);
    public abstract char[] generateTrailer(char[] data);
    
}
