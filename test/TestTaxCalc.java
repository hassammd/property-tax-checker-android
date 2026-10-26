import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * Plain-JVM test for TaxCalc: compiles TaxCalc.java from source and
 * checks every figure visible in the user's screenshots, plus the
 * non-filer buying slabs.
 */
public class TestTaxCalc {
    static int passed = 0, failed = 0;

    static void check(String name, Object got, Object want) {
        if (got.equals(want)) { passed++; System.out.println("PASS " + name + " = " + got); }
        else { failed++; System.out.println("FAIL " + name + " got=" + got + " want=" + want); }
    }

    public static void main(String[] args) throws Exception {
        String srcDir = args[0];
        File out = new File("test-classes");
        out.mkdirs();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int rc = compiler.run(null, null, null, "-d", out.getPath(),
                srcDir + "/com/hatch/propertytax/TaxCalc.java");
        if (rc != 0) throw new RuntimeException("javac failed: " + rc);
        Class<?> c = new URLClassLoader(new URL[]{out.toURI().toURL()})
                .loadClass("com.hatch.propertytax.TaxCalc");
        Method rate = c.getMethod("rate", boolean.class, boolean.class, long.class);
        Method tax = c.getMethod("tax", long.class, double.class);

        // Screenshot examples (buying=true, selling=false; filer=true/false)
        check("Buy 10M filer rate", rate.invoke(null, true, true, 10_000_000L), 1.25);
        check("Buy 10M filer tax", tax.invoke(null, 10_000_000L, 1.25), 125_000L);
        check("Sell 25M filer tax", tax.invoke(null, 25_000_000L, 2.75), 687_500L);
        check("Buy 50M non-filer rate (screenshot history)", rate.invoke(null, true, false, 50_000_000L), 14.5);
        check("Buy 50M non-filer tax", tax.invoke(null, 50_000_000L, 14.5), 7_250_000L);
        check("Sell 18M filer tax", tax.invoke(null, 18_000_000L, 2.75), 495_000L);
        // Other slabs
        check("Buy 10M non-filer rate", rate.invoke(null, true, false, 10_000_000L), 10.5);
        check("Buy 10M non-filer tax", tax.invoke(null, 10_000_000L, 10.5), 1_050_000L);
        check("Buy 60M non-filer rate", rate.invoke(null, true, false, 60_000_000L), 14.5);
        check("Buy 120M non-filer rate", rate.invoke(null, true, false, 120_000_000L), 18.5);
        check("Buy 120M non-filer tax", tax.invoke(null, 120_000_000L, 18.5), 22_200_000L);
        check("Buy 100M non-filer rate", rate.invoke(null, true, false, 100_000_000L), 18.5);
        check("Sell 25M non-filer rate", rate.invoke(null, false, false, 25_000_000L), 11.5);
        check("Sell 25M non-filer tax", tax.invoke(null, 25_000_000L, 11.5), 2_875_000L);
        check("Sell filer rate", rate.invoke(null, false, true, 1_000_000L), 2.75);

        System.out.println("RESULT passed=" + passed + " failed=" + failed);
        if (failed > 0) System.exit(1);
    }
}
