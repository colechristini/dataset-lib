package tech.pod.dataset.ims;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
public class IndexKey extends Comparable {
    Object[] list = new Object[8];

    IndexKey(String title, String tags, BasicFileAttributes fileAttributes, String hashCode) {
        list[0] = title;
        list[1] = tags;
        UUID uuid = UUID.randomUUID();
        list[2] = uuid;
        list[3] = fileAttributes;
        list[4] = hashCode;
        list[5] = fileAttributes.creationTime();
        list[6] = fileAttributes.lastAccessTime();
        list[7] = 0;
    }
    @Override
    public int compareTo(IndexKey i) {
        if (list[6].after(i.getLastAccessTime())) {
            return 1;
        } else if (list[6].before(i.getLastAccessTime())) {
            return 0;
        } else if (list[6].equals(i.getLastAccessTime())) {
            return 2;
        }
    }
    public String getTitle() {

        return list[0];
    }
    public String getHashCode() {
        return list[4];
    }

    public UUID getUUID() {
        return list[2];
    }
    public String getTags() {
        return list[1];
    }

    public Object[] getAll() {
        return list;
    }
    public int duplicateCheck(IndexKey i) {
        final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Date d = new Date();
        list[6] = sdf.format(d);
        if (list[0] != i.getTitle() && list[4] != i.getHashCode() && list[2] != i.getUUID()) {
            return 0;
        } else if (list[0] == i.getTitle() && list[4] != i.getHashCode() && list[2] != i.getUUID()) {
            return 1;
        } else if (list[0] != i.getTitle() && list[4] == i.getHashCode() && list[2] != i.getUUID()) {
            return 2;
        } else if (list[0] == i.getTitle() && list[4] == i.getHashCode() && list[2] != i.getUUID()) {
            return 3;
        } else if (list[0] == i.getTitle() && list[4] == i.getHashCode() && list[2] == i.getUUID()) {
            return 4;
        }
    }
    void incrementCounter() {
        list[7]++;
    }
    public Date getLastAccessTime() {
        return list[6];
    }
    public Date getCreationTime() {
        return list[5];
    }
}