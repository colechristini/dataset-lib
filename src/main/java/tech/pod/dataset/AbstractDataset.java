package tech.pod.dataset;
import java.io.File;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

class AbstractDataset implements dataset {
    private static final Logger logger = Logger.getLogger(AbstractDataset.class.getName());
    Deque < int[] > CommandStack = new ConcurrentLinkedDeque < int[] > ();
    List < String > preStringMap = new ArrayList < String > ();
    List < List < Double > > preNumMap = new ArrayList < ArrayList < Double > > ();

    AbstractDataset() {}

    //Import

    void importText(File file) {
        String fileAsString = new String(Files.readAllBytes(Paths.get(files[i])));

        preStringMap.clear();
        preStringMap.append(fileAsString);

    }

    void importTextBatch(File[] files) {
        preStringMap.clear();
        for (int i = 0; i < files.length; i++) {
            String fileAsString = new String(Files.readAllBytes(Paths.get(files[i])));

            preStringMap.append(fileAsString);
        }
    }

    void appendText(File file, int pointer) {
        String fileAsString = new String(File.readAllBytes(Paths.get(file)));

        preStringMap.add(fileAsString, pointer);
    }

    void appendTextBatch(File[] files, int[] pointer) {
        for (int i = 0; i < files.length; i++) {
            String fileAsString = new String(Files.readAllBytes(Paths.get(files[i])));

            preStringMap.add(fileAsString, pointer[i]);
        }
    }

    void importNum(File file) {
        String fileAsString = new String(Files.readAllBytes(Paths.get(file)));
        List < String > temp = new ArrayList < String > ();
        temp.append(split(fileAsString, ","));
        preNumMap.append(Double.ParseDouble(temp));
    }

    void importNumBatch(File[] files) {

        preStringMap.clear();
        for (int i = 0; i < files.length; i++) {
            String fileAsString = new String(Files.readAllBytes(Paths.get(files[i])));
            List < String > temp = new ArrayList < String > ();
            temp.append(split(fileAsString, ","));
            preNumMap.get(i).append(Double.ParseDouble(temp));
            temp.clear();

        }
    }

    void appendNum(File file) {
        String fileAsString = new String(Files.readAllBytes(Paths.get(file)));
        List < String > temp = new ArrayList < String > ();
        temp.append(split(fileAsString, ","));
        preNumMap.append(Double.ParseDouble(temp));
    }

    void appendNumBatch(File[] files, int[] pointer) {

        for (int i = 0; i < files.length(); i++) {
            String fileAsString = new String(Files.readAllBytes(Paths.get(file)));
            List < String > temp = new ArrayList < String > ();
            temp.append(split(fileAsString, ","));
            preNumMap.append(Double.ParseDouble(temp));
            temp.clear();
        }
    }

    //Clean

    void smooth(int width, int[]...thresholds) {
        int max = thresholds[1];
        if (thresholds.length == 2) {
            for (int i = thresholds[0]; i < max; i++) {
                if (i > 1 || i == 1) {
                    int v;
                    v = preNumMap.get(i);
                    for (int z = 0; z < width; z++) {
                        v += preNumMap.get(i + z);
                        v += preNumMap.get(i - z);

                    }
                    v /= width;
                    preNumMap.set(i, v);
                }
            }}
           else if (thresholds.length == 1) {   int[] temp = CommandStack.pop(thresholds[0]);
                for (int i = temp[0]; i < temp[1]; i++) {
                    if (i > 1 || i == 1) {
                        int v;
                        v = preNumMap.get(i);
                        for (int z = 0; z < width; z++) {
                            v += preNumMap.get(i + z);
                            v += preNumMap.get(i - z);
    
                        }
                        v /= width;
                        preNumMap.set(i, v);
                    }
                }
        } else {
            for (int q = 0; q < CommandStack.length; q++) {
                int[] temp = CommandStack.pop(q);
                for (int i = temp[0]; i < temp[1]; i++) {
                    if (i > 1 || i == 1) {
                        int v;
                        v = preNumMap.get(i);
                        for (int z = 0; z < width; z++) {
                            v += preNumMap.get(i + z);
                            v += preNumMap.get(i - z);

                        }
                        v /= width;
                        preNumMap.set(i, v);
                    }
                }

            }
        }
    }
    void lengthenData(int factor, int...selection) {
        if (selection.length == 2) {
            for (int i = selection[0]; i < selection[1]; i++) {
                for (int x = 0; x < factor; x++) {
                    preNumMap.add(i + x, 0);

                }
            }

        } else if (selection.length == 1) {
                int temp[] = CommandStack.pop(selection[0]);
                for (int i = temp[0]; i < temp[1]; i++) {
                    for (int x = 0; x < factor; x++) {
                        preNumMap.add(i + x, 0);

                    }
                }
            } else {
                int temp[];
                for (int z = 0; z < commandStack.length; z++) {
                    temp = CommandStack.pop(selection[z]);
                    for (int i = temp[0]; i < temp[1]; i++) {
                        for (int x = 0; x < factor; x++) {
                            preNumMap.add(i + x, 0);

                        }
                    }
                }
            }
        }
    

    void cleanWords(String[] words,int z, int...selection) {
        List < List < String >> temp = new ArrayList < ArrayList < String >> ();
        temp.get(0).append(split(preStringMap.get(z)," "));
        for (int i = 0; i < temp.get(0).length; i++) {
            words[i]= String.format("%1$" + 1 + "s", temp.get(0).get(i));
            words[i]= String.format("%1$-" + 1 + "s", temp.get(0).get(i));
        }
        for (int i = 0; i < temp.get(0).length; i++) {
            temp.get(0).set(i, String.format("%1$" + 1 + "s", temp.get(0).get(i)));
            temp.get(0).set(String.format("%1$-" + 1 + "s", temp.get(0).get(i)));
        }
        if(selection.length()==2){
            
        }
    }

    void cleanNouns() {}

    void cleanVerbs() {}

    void cleanAdjectives() {}

    void cleanAdverbs() {}

    void cleanPrepositions() {}

    void filterCustom(String regex) {

    }

    //Filter

    void filterThreshold(int min, int max) {}

    void filterStandardDeviation(int max) {}

    void filterSlope(int min, int max) {}

    void lateralThreshold(int min, int max) {}

    //Sel/Del

    void select(int start, int end) {}

    void selectFromFilter() {}

    void pushToCommandStack() {}

    void delete() {}

    //Transform

    void clamp() {}

    void lateralClamp(int min, int max) {}

    void transform(int amount) {}

    void lateralTransform(int amount) {}


    //Reduce

    void reduceOccurences() {}

    //Index

    void indexBySchema(Schema schema) {}

    void indexTreeBySchema(int length, int branches, Schema schema) {}

    void indexMatrixBySchema(int length, int depth, Schema schema) {}

    void indexAbsolute(int length) {}

    void indexTreeAbsolute(int branches, int length) {}

    void indexMatrixAbsolute(int length, int depth) {}

    //Generate

    void genConsonantSet() {}

    void genVowelSet() {}

    void genPhonemeSet() {}

    //Same?

    void genMetahphoneSet() {}

    void genCoordinateSet() {}

    void genCoordinateSetMatrix() {}

    void genMatrixSet() {}

    void storeGenSet() {}

    void storeGenSetBatch() {}
    //Map

    void mapInt() {}

    void mapIntMatrix() {}

    void mapStrings() {}

    void mapStringMatrix() {}

    void mapFloat() {}

    void mapFloatMatrix() {}



    //Search

    void searchRegex(String regex) {}

    void searchAbsoluteString(String str) {}

    void searchAbsoluteInt(int num) {}

    void searchAbsoluteFloat(float num) {}

    void searchRangeNum(int min, int max) {}

    void SearchFuzzy(String word) {}

    void searchMultStrings(String[] words) {}

    void searchMultStringsFuzzy(String[] words) {}

    void searchMultAbsoluteInts(int[] nums) {}

    void searchMultAbsoluteFloats(int[] nums) {}

    void searchMultRangeNum(int[] mins, int[] maxs) {}

    //Query

    void queryAll() {}

    void query() {}

    void queryTop() {}



}