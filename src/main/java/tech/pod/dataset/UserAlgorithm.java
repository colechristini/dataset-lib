package tech.pod.dataset;
import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import javax.lang.model.*;
import javax.annotation.processing.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.util.Arrays;
public class UserAlgorithm {
    String globalLogger;
    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    UserAlgorithm(String globalLogger) {
        this.globalLogger = globalLogger;
    }

    void runAlg(String[] dependencies, String[] code, String[] args) {
        Logger logger = null;
        if (globalLogger != null) {
            logger=Logger.getLogger(globalLogger);
        } else {
            logger=Logger.getLogger(ImportThread.class.getName());
        }
        logger.entering(getClass().getName(), "runAlg()");
        File file = new File("./doAlg.java");
        try {
            List < String > lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
            for (int i = 0; i < code.length; i++) {
                lines.add(7 + i, code[i]);
            }
            for (String i: dependencies) {
                lines.add(0, i);
            }
            Files.write(Paths.get(file.getAbsolutePath()), lines, Charset.defaultCharset());
        } catch (IOException e) {
            logger.logp(Level.WARNING, "UserAlgorithm", "runAlg()", "IOException", e);
            e.printStackTrace();
        }
        final DiagnosticCollector < JavaFileObject > diagnostics = new DiagnosticCollector < JavaFileObject > ();
        final StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);
        File fileb;
        try {
            fileb = new File(Compiler.class.getResource("./DoAlg.java").toURI());
        } catch (URISyntaxException e) {
            logger.logp(Level.WARNING, "UserAlgorithm", "runAlg()", "URISyntaxException", e);
            e.printStackTrace();
        }

        final Iterable < ? extends JavaFileObject > sources = manager.getJavaFileObjectsFromFiles(Arrays.asList(fileb));
        final CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, sources);
        task.call();
        logger.logp(Level.INFO, "UserAlgorithm", "runAlg()", "Compiled");
        String p = String.join(" ", args);
        ProcessBuilder pb = new ProcessBuilder("java -classpath ./DoAlg.class RunAlg" + p);
        pb.inheritIO();
        pb.directory(new File("bin"));
        try {
            pb.start();
            logger.logp(Level.INFO, "UserAlgorithm", "runAlg()", "Started algorithm");
        } catch (IOException e) {
            logger.logp(Level.WARNING, "UserAlgorithm", "runAlg()", "IOException", e);
            e.printStackTrace();
        }
        logger.logp(Level.INFO, "UserAlgorithm", "runAlg()", "Completed program");
    }
}