import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class sample {

    public static <T> T invokeCustomClassMethods(String className, String methodName) {
        try {
            String javaFilePath = searchFiles(className, System.getProperty("user.dir") + File.separator + "src");
            File javaFile = new File(javaFilePath);

            // Directory for compiled .class files
            String outputDir = System.getProperty("user.dir") + File.separator + "target";
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs();
            }

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
                Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(javaFile));
                // Correctly specify the compiler options
                compiler.getTask(null, fileManager, null, Arrays.asList("-d", outputDir), null, compilationUnits).call();
            }

            // Create URL to the output directory
            URL[] classUrls = { outputDirFile.toURI().toURL() };

            // Use URLClassLoader to load the class
            try (URLClassLoader classLoader = new URLClassLoader(classUrls)) {
                String packageName = getPackageName(javaFilePath);
                Class<?> cls = classLoader.loadClass(packageName + className);
                Method method = cls.getMethod(methodName);
                Object obj = cls.getDeclaredConstructor().newInstance();
                T methodValue = (T) method.invoke(obj);
                Files.deleteIfExists(Paths.get(new File(outputDirFile+File.separator+className+".class").toURI()));
                return methodValue;
            }
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getPackageName(String javaFilePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(javaFilePath))) {
            String firstLine = br.readLine();
            if (firstLine != null && firstLine.startsWith("package")) {
                return firstLine.split("package")[1].trim().replace(";", "")+ ".";
            } else {
                return ""; // Default package
            }
        }
    }

    public static String searchFiles(String fileName, String searchDirectory) {
        Path directory = Path.of(searchDirectory);

        try (Stream<Path> pathStream = Files.walk(directory)) {
            return pathStream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith(fileName))
                    .findFirst()
                    .map(Path::toString)
                    .orElse(null);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        // Example usage
        String result = invokeCustomClassMethods("CustomClass", "body");
        System.out.println(result);
    }
}
