public class Property<T> {
    String name,type;
    T value;
    boolean setImmutable;
    Propery(String name, String type, T value,boolean setImmutable){
        this.name=name;
        this.type=type;
        this.value=value;
        this.setImmutable=setImmutable;
    }
    public T getValue(){
        return value;
    }
    public void setValue(T value) throws UnsupportedOperationException{
        if(!setImmutable){
            this.value=value;
        }
        else{
            throw new UnsupportedOperationException();
        }
    }
    public String getName(){
        return name;
    }
    public String getType(){
        return type;
    }
}