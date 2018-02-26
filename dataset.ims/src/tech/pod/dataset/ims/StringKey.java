package tech.pod.dataset.ims;
import java.io.Serializable;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
/*StringKey is the reference IndexKey implementation, using all methods, along with implementing Comparable and Serializable.
It also supports a more intelligent duplicate check.
*/
public class StringKey implements IndexKey, Comparable, Serializable {
    static final long serialVersionUID=0L;
    ConcurrentHashMap<String,Object> parameters=new ConcurrentHashMap<String,Object>();
    StringKey(Property title, Property tags, Property fileAttributes, Property hashCode, Property importTime, Property indexLoc,Property filepath,Property wordOccurences) {
        parameters.put("title", title.getValue());
        parameters.put("tags", tags.getValue());
        UUID uuid = UUID.randomUUID();
        parameters.put("UUID", uuid);
        parameters.put("fileAttributes", fileAttributes).getValue();
        parameters.put("hashCode", hashCode.getValue());
        parameters.put("creationTime", fileAttributes.getValue().creationTime());
        parameters.put("importTime", importTime.getValue());
        parameters.put("lastAccessTime", fileAttributes.getValue().lastAccessTime());
        parameters.put("accessCount", 0);
        parameters.put("accessAverage", parameters.get("accessCount")/parameters.get("lastAccessTime").getTime()-parameters.get("importTime").getTime);
        parameters.put("locationTier", 0);
        parameters.put("filePath", filepath.getValue());
        parameters.put("wordOccurences", wordOccurences.getValue());
        parameters.put("hexHash", Integer.toHexString(this.hashCode()));
    }
    @Override
    public int compareTo(StringKey i) {
        Date d=new Date();
        long l=d.getTime();
        long l2=(Date)parameters.get("importTime").getTime();
        long aa= (long)parameters.get("accessCounter") /  l- l2;
        parameters.replace("accessCounter",aa);
        if ((int) parameters.get("accessCounter") > i.getAccessAverage()) {
            return 1;
        } else if ((int) parameters.get("accessCounter") < i.getAccessAverage()) {
            return 0;
        } else if ((int) parameters.get("accessCounter") == i.getAccessAverage()) {
            return 2;
        }
    }
    public String getTitle() {

        return parameters.get("title");
    }
    public String getHashCode() {
        return parameters.get("hashCode");
    }

    public UUID getUUID() {
        UUID uuid=parameters.get("UUID");
        return uuid;
    }
    public String getTags() {
        return parameters.get("tags");
    }

    public Object[] getAll() {
        return list;
    }
    public int duplicateCheck(StringKey i) {
       /* final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
       // final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Date d = new Date();
        parameters.get("lastAccessTime") = sdf.format(d);*/
        // Should it update the last access time on duplicate check?
        this.update();
        if (parameters.get("title") != i.getTitle() && parameters.get("hashCode") != i.getHashCode() && parameters.get("UUID") != i.getUUID()) {
            return 0;
        } else if (parameters.get("title") == i.getTitle() && parameters.get("hashCode") != i.getHashCode() && parameters.get("UUID") != i.getUUID()) {
            return 1;
        } else if (parameters.get("title") != i.getTitle() && parameters.get("hashCode") == i.getHashCode() && parameters.get("UUID") != i.getUUID()) {
            return 2;
        } else if (parameters.get("title") == i.getTitle() && parameters.get("hashCode") == i.getHashCode() && parameters.get("UUID") != i.getUUID()) {
            return 3;
        } else if (parameters.get("title") == i.getTitle() && parameters.get("hashCode") == i.getHashCode() && parameters.get("UUID") == i.getUUID()) {
            return 4;
        }
        return 5;
    }
    void incrementCounter() {
        int ac=(int)parameters.get("accessCounter")+1;
        parameters.replace("accessCounter",ac);
        parameters.replace("lastAccessTime", new Date());
    }
    public Date getLastAccessTime() {
        return parameters.get("lastAccessTime");
    }
    public Date getCreationTime() {
        return parameters.get("creationTime");
    }
    public int getAccessAverage() {
        return parameters.get("accessCounter");
    }
    public void update() {
        Date d=new Date();
        long l=d.getTime();
        long l2=(Date)parameters.get("importTime").getTime();
        long aa= (long)parameters.get("accessCounter") /  l- l2;
        parameters.replace("accessCounter",aa);
    }
    public int getLocation() {
        return list[10];
    }
   
    public void setLocation(int location) {
        list[10] = location;

    }
    public void setLocationTier(int locationTier) {
        parameters.replace("locationTier",locationTier);
    }
    public int getLocationTier() {
        return parameters.get("locationTier");
    }
   public void setTitle(String title) {
        parameters.replace("title", title);
    }
    public String getPath(){
        return parameters.get("filepath");
    }
    public void setPath(String path){
        parameters.replace("filepath",path);
    }

    public Date getImportTime(){
        return parameters.get("lastAccessTime");
    }

    public ConcurrentHashMap wordOccurences(){
        return parameters.get("wordOccurences");
    }

    public String getKeyHash(){
        return (String)parameters.get("hexHash");
    }
    public Object getProperty(String property){
        return parameters.get(property);
    }
    public<E> void addProperty(String name,E value ){
        parameters.put(name, value);
    }
    public<E> void replaceProperty(String name, E value){
        parameters.replace(name, value);
    }
}