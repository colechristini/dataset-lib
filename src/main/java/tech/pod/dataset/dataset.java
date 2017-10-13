package tech.pod.dataset;

public interface dataset {

    //Import

    void importTextSingle(File file);

    void importTextBatch(File[] files);
    
    void appendText(File file,int[] pointer);

    void appendTextBatch(File[] files,int[] pointer);

    void importNum(File file);

    void importNumBatch(File[] files);

    void appendNum(File file,int[] pointer);

    void appendNumBatch(File[] file,int[] pointer);

    //Clean

    void cleanNoise(int variance);

    void smoothPeaks();

    void lengthenData(int factor);

    void cleanWords(String[] words);

    void cleanArticles(String[] articleList);

    void cleanNouns(String noun);

    void cleanVerbs(String verb);

    void cleanAdjectives(String adjective);

    void cleanAdverbs(String adverb);

    void cleanPrepositions(String preposition);

    //Filter
    
    void filterThreshold(int min, int max);

    void filterStandardDeviation(int max);

    void filterSlope(int min, int max);

    void clamp(int min, int max);

    void lateralThreshold(int min, int max);

    void lateralClamp(int min, int max);

    void transform(int amount);

    void lateralTransform(int amount);
    
    //Reduce

    void reduceOccurences();

    void reducePattern(String genPattern);

    void reduceSection(int min, int max);

    //Index

    void indexBySchema(Schema schema);

    void indexTreeBySchema(int length,int branches,Schema schema);

    void indexMatrixBySchema(int length,int depth,Schema schema);

    void indexAbsolute(int length);

    void indexTreeAbsolute(int branches,int length);

    void indexMatrixAbsolute(int length,int depth);

    //Generate

    void genConsonantSet();

    void genVowelSet();

    void genPhonemeSet();

     //Same?

    void genMetahphoneSet();

    void genCoordinateSet();

    void genCoordinateSetMatrix();
    
    void genMatrixSet();

    void storeGenSet();

    void storeGenSetBatch();
    //Map

    //Search

    //Query

    //Dashboard
}