package tech.pod.dataset;

public class RunAlg {
    DoAlg Do = new DoAlg();
    String[] args;
    RunAlg(String[] args) {
        this.args = args;
    }
    
    void run() {
        Do.doAlg(args);
    }
}