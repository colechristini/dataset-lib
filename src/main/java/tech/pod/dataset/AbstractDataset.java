package main.java.tech.pod.dataset;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class AbstractDataset extends Thread implements dataset {
 Deque<int[]> CommandStack=new ConcurrentLinkedDeque<int[]>();
 List < String > preStringMap = new ArrayList < String > ();
 List < Double > preNumMap = new ArrayList < Double > ();
 List < List < Double > > preNumMapMatrix = new ArrayList < ArrayList< Double > > ();

 AbstractDataset() {}

 //Import

 void importText(File file) {
    String fileAsString = new String(Files.readAllBytes(Paths.get(files[i])));
    
  preStringMap.clear();
  preStringMap.append(fileAsString);

 }

 void importTextBatch(File[] files) {
  preStringMap.clear();
  for(int i=0;i<files.length;i++){
    String fileAsString = new String(Files.readAllBytes(Paths.get(files[i])));

   preStringMap.append(fileAsString);
  }
 }

 void appendText(File file, int pointer) {
    String fileAsString = new String(Files.readAllBytes(Paths.get(file)));

  preStringMap.add(fileAsString, pointer);
 }

 void appendTextBatch(File[] files, int[] pointer) {
for(int i=0;i<files.length;i++){
    String fileAsString = new String(Files.readAllBytes(Paths.get(files[i])));

   preStringMap.add(fileAsString, pointer[i]);
  }
}

 void importNum(File file) {
    String fileAsString = new String(Files.readAllBytes(Paths.get(file)));
    List<String> temp = new ArrayList<String>();
    temp.append(split(fileAsString,","));
    preNumMap.append(Double.ParseDouble(temp));
 }

 void importNumBatch(File[] files) {
     
    preStringMap.clear();
    for(int i=0;i<files.length;i++){
    String fileAsString = new String(Files.readAllBytes(Paths.get(files[i])));
     List<String> temp = new ArrayList<String>();
     temp.append(split(fileAsString,","));
     preNumMap.get(i).append(Double.ParseDouble(temp));
     temp.clear();
     
    }
}

 void appendNum(File file, int[] pointer) {
    String fileAsString = new String(Files.readAllBytes(Paths.get(file)));
    List<String> temp = new ArrayList<String>();
    temp.append(split(fileAsString,","));
    preNumMap.add(Double.ParseDouble(temp),pointer);
 }

 void appendNumBatch(File[] files, int[] pointer) {
   
  for(int i=0;i<files.length();i++){
     String fileAsString = new String(Files.readAllBytes(Paths.get(file)));
     List<String> temp = new ArrayList<String>();
     temp.append(split(fileAsString,","));
     preNumMap.add(Double.ParseDouble(temp),pointers[i]);
     temp.clear();
    }
}

 //Clean

void smooth(int width,int[]... thresholds){
int max=thresholds[1];
if(thresholds.length==2){
for(int i=thresholds[0];i<max;i++){
if(i>1 || i==1){
    int v;
    v=preNumMap.get(i);
    for(int z=0;z<width;z++){
        v+=preNumMap.get(i+z);
        v+=preNumMap.get(i-z);
        
            }
    v/=width;
    preNumMap.set(i,v);
        }
    }
}

    else{
        for(int q=0;q<CommandStack.length;q++){int[] temp=CommandStack.pop(q);
        for(int i=temp[0];i<temp[1];i++){
            if(i>1 || i==1){
                int v;
                v=preNumMap.get(i);
                for(int z=0;z<width;z++){
                    v+=preNumMap.get(i+z);
                    v+=preNumMap.get(i-z);
                    
                }
                v/=width;
                preNumMap.set(i,v);
                }
            }

        }
    }
}
 void lengthenData(int factor) {}

 void cleanWords(String[] words) {}

 void cleanArticles(String[] articleList) {}

 void cleanNouns() {}

 void cleanVerbs() {}

 void cleanAdjectives() {}

 void cleanAdverbs() {}

 void cleanPrepositions() {}

 void filterCustom(String regex) {}

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

 void reducePattern(String genPattern) {}

 void reduceSection(int min, int max) {}




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