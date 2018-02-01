package tech.pod.dataset;

public class RunAlg {
    DoAlg doAlg = new DoAlg();
    String[] args;
    RunAlg(String[] args) {
        this.args = args;
    }
    
    void run() {
    doAlg.doAlg(args);
    }
}