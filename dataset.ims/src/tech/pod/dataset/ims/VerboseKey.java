/*A VerboseKey is an immutable shortened version of an IndexKey used to replace an IndexKey flushed to disk

*/

public class VerboseKey {
    String title;
    String path;


    VerboseKey(String title, String path) {
        this.title = title;
        this.path = path;
    }
    public String getTitle(){
        return title;
    }
    public String getPath(){
        return path;
    }
}