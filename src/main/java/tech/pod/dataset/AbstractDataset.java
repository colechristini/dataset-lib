package main.java.tech.pod.dataset;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class AbstractDataset implements dataset {
 ConcurrentLinkedDeque CommandStack;
 List < String > preStringMap = new ArrayList < String > ();
 List < Double > preNumMap = new ArrayList < Double > ();
 List < List < Double >> preNumMatrixMap = new ArrayList < List < Double >> ();
 AbstractDataset() {}

 //Import

 void importTextSingle(File file) {
  InputStream is = new FileInputStream(file);
  BufferedReader buf = new BufferedReader(new InputStreamReader(is));

  String line = buf.readLine();
  StringBuilder sb = new StringBuilder();

  while (line != null) {
   sb.append(line).append("\n");
   line = buf.readLine();
  }

  String fileAsString = sb.toString();
  preStringMap.clear();
  preStringMap.append(fileAsString);

 }

 void importTextBatch(File[] files) {
  preStringMap.clear();
  for (File i: files) {
   InputStream is = new FileInputStream(i);
   BufferedReader buf = new BufferedReader(new InputStreamReader(is));

   String line = buf.readLine();
   StringBuilder sb = new StringBuilder();

   while (line != null) {
    sb.append(line).append("\n");
    line = buf.readLine();
   }

   String fileAsString = sb.toString();

   preStringMap.append(fileAsString);
  }
 }

 void appendText(File file, int pointer) {
  InputStream is = new FileInputStream(file);
  BufferedReader buf = new BufferedReader(new InputStreamReader(is));

  String line = buf.readLine();
  StringBuilder sb = new StringBuilder();

  while (line != null) {
   sb.append(line).append("\n");
   line = buf.readLine();
  }

  String fileAsString = sb.toString();

  preStringMap.add(fileAsString, pointer);
 }

 void appendTextBatch(File[] files, int[] pointer) {

  for (int i = 0; i < files.length; i++) {
   InputStream is = new FileInputStream(files[i]);
   BufferedReader buf = new BufferedReader(new InputStreamReader(is));

   String line = buf.readLine();
   StringBuilder sb = new StringBuilder();

   while (line != null) {
    sb.append(line).append("\n");
    line = buf.readLine();
   }

   String fileAsString = sb.toString();

   preStringMap.add(fileAsString, pointer[i]);
  }
 }

 void importNum(File file) {}

 void importNumBatch(File[] files) {}

 void appendNum(File file, int[] pointer) {}

 void appendNumBatch(File[] file, int[] pointer) {}

 void importNumSets(File file) {}

 void importNumSetsBatch(File[] files) {}

 void appendNumSets(File file, int[] pointer) {}

 void appendNumSetsBatch(File[] file, int[] pointer) {}

 //Clean

 void cleanNoise(int variance) {}

 void smoothPeaks() {}

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