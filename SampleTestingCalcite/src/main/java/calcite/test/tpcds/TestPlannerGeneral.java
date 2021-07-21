package calcite.test.tpcds;

import org.apache.calcite.rel.RelNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class TestPlannerGeneral {
    private static final String TEST_DIR_TPCDS = "/TPCDS";
    private static final Logger LOGGER = Logger.getLogger(TestPlannerGeneral.class.getName());

    /**
     * Get all test directories
     *
     * @return list of test directories
     */
    private static List<File> getTestDirectoriesTPCDS() {
        final File directory = new File(TestPlannerGeneral.class.getResource(TEST_DIR_TPCDS).toString().substring(5));
        final List<File> testDirectory = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory() && file.getName().equals("Test06")) {
                testDirectory.add(file);
            }
        }
        return testDirectory;
    }

    /**
     * Get a query in a test directory
     *
     * @param testDirectory given test directory
     * @return the query
     */
    private static String getTestQuery(final File testDirectory) throws IOException {
        for (File file : testDirectory.listFiles()) {
            if (file.isFile() && file.getName().contains("query")) {
                return Files.readString(Paths.get(file.getAbsolutePath()));
            }
        }
        throw new IOException("Query file not found in a directory");
    }


    private static void testOptimizedPlannerTPCDS(final File file) throws Exception {
        final String sqlQuery = getTestQuery(file);
        final SQL2Log sql2Log = new SQL2Log(sqlQuery);
        final RelNode relNode = sql2Log.getLogOptimized();
        System.out.println(relNode.explain());
    }

    public static void main(String[] args){

       //Calcite Planner Test
        getTestDirectoriesTPCDS().stream().forEach(f -> {
            try {
                testOptimizedPlannerTPCDS(f);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

//        //Calcite Volcano Planner Test
//        getTestDirectoriesTPCDS().stream().forEach(f -> {
//            try {
//                testOptimizedPlannerTPCDS(f);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });

    }
    /**
     * Calcite fails to get optimized logical plan
     *
     * @param testFile
     * @return
     */
    private boolean check(String testFile) {
        switch (testFile) {
            case "Test06":
            case "Test07":
            case "Test17":
            case "Test18":
            case "Test22":
            case "Test23-a":
            case "Test25":
            case "Test26":
            case "Test27":
            case "Test30":
            case "Test32":
            case "Test35":
            case "Test39":
            case "Test44":
            case "Test65":
            case "Test81":
            case "Test85":
            case "Test92":
                return false;
            default:
                return true;
        }
    }
}
