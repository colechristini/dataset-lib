package tech.pod.dataset.ims;

import java.util.ArrayList;
import java.util.List;

public class Index {
    List < IndexKey > IndexKeyStore = new ArrayList < IndexKey > ();
    String[] tierOrdering;

    IndexKeyStore(String[] tierOrdering) {
        this.tierOrdering = tierOrdering;
    }
}