package jsimutate;

import jsimutate.inputfile.InputFile;
import mutation.Mutation;
import mutation.MutationContext;
import mutationoperators.*;
import mutationtesting.MutationResult;
import mutationtesting.TestExecutor;
import mutationtesting.test.QnTest;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import picocli.CommandLine;
import qn.QueueNetwork;
import qn.QueueNetworkParser;
import qn.simulation.MeasureResult;
import qn.simulation.Simulation;
import qn.simulation.SimulationResults;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@CommandLine.Command(
        name = "java -jar JSIMutate.jar",
        header = "@|fg(white) java -jar JSIMutate.jar|@",
        //description = "@|fg(white) java -jar JSIMutate.jar|@",
        version = "JSIMutate 1.0", mixinStandardHelpOptions = false)
public class JSIMutate implements Runnable {

    @CommandLine.Parameters(index = "0", description = "path of the json file describing the workloads to consider")
    String jsonPath;

    @CommandLine.Option(names = "-timeout", description = "timeout in minutes", defaultValue = "5")
    int testTimeoutMin;

    @CommandLine.Option(names = "-outFolder", description = "path of folder for experimental results", defaultValue = "./results")
    String resultsFolder;

    @CommandLine.Option(names = "-operators", description = "selected mutation operators: CQSize, CNServers, CQStrat", split = ",", defaultValue = "CQSize,CNServers,CQStrat")
    MUTATION_OPERATORS[] operators;

    @CommandLine.Option(names = "-saveMutants", description = "save the generated mutants")
    boolean saveMutants;

    @CommandLine.Option(names = "-help", usageHelp = true, description = "display this help and exit")
    private boolean help;

    enum MUTATION_OPERATORS {
        CQSize, CNServers, CQStrat
    }

    public static void main(String[] args) {
        run(args);
    }

    protected static void run(String[] args) {
        int exitCode = new CommandLine(new JSIMutate()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        InputFile inputFile = null;
        try {
            inputFile = InputFile.readInputFile(jsonPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not open json file at: " + jsonPath);
            System.exit(1);
        }

        Collection<QnTest> tests = inputFile.buildTestSuite();
        Map<QnTest, SimulationResults> originalResults = new HashMap<>();

        String jsimgFile = inputFile.getJsimgFile();
        Path pathJsimgfile = Paths.get(jsimgFile);
        jsimgFile = Paths.get(jsonPath).getParent().resolve(pathJsimgfile).normalize().toString();
        //String modelFolder = pathJsimgfile.toAbsolutePath().getParent().toString();
        String modelFolder = Paths.get(jsimgFile).toAbsolutePath().getParent().normalize().toString();
        //System.out.println(modelFolder);

        System.out.println("----------------------\nRunning the workloads on the original network " + jsimgFile);
        for (QnTest test : tests) {
            System.out.println("-- Running workload \"" + test.getTestName() + "\"");
            try {
                Document testDoc = TestExecutor.applyTest(jsimgFile, test);
                assert testDoc != null : jsimgFile;
                Simulation.runSimulation(testDoc, modelFolder, testTimeoutMin * 60);
                originalResults.put(test, Simulation.readSimulationResults(modelFolder + "/" + testDoc.getRootElement().getAttributeValue("name")));
            } catch (Exception e) {
                System.out.println("Workload " + test.getTestName() + " failed on the original model. We can not perform mutation analysis.");
                e.printStackTrace();
                System.exit(1);
            }
        }

        MutationContext context = new MutationContext();
        CompoundOperator co = new CompoundOperator(context);
        List<MUTATION_OPERATORS> listOperators = Arrays.stream(operators).collect(Collectors.toList());
        if (listOperators.contains(MUTATION_OPERATORS.CNServers)) {
            co.addOperator(new ChangeNumberServers(context));
            co.addOperator(new ChangeNumberServersM1(context));
            co.addOperator(new ChangeNumberServersP(context));
            co.addOperator(new ChangeNumberServersP1(context));
        }
        if (listOperators.contains(MUTATION_OPERATORS.CQSize)) {
            co.addOperator(new ChangeQueueSize(context));
            co.addOperator(new ChangeQueueSizeM1(context));
            co.addOperator(new ChangeQueueSizeP(context));
            co.addOperator(new ChangeQueueSizeP1(context));
        }
        if (listOperators.contains(MUTATION_OPERATORS.CQStrat)) {
            co.addOperator(new ChangeQueueingStrategy(context));
        }
        List<MutationResult> mutationResults = new ArrayList<>();

        try {
            QueueNetwork qn = QueueNetworkParser.parse(jsimgFile);
            co.identifyMutants(qn);

            List<Mutation> mutations = new ArrayList<Mutation>(context.getMutations());

            XMLOutputter out = new XMLOutputter();
            int counter = 0;
            String mutantsFolder = resultsFolder + "/mutants/";
            if (saveMutants) {
                Files.createDirectories(Paths.get(mutantsFolder));
            }
            for (Mutation m : mutations) {
                System.out.println("\n----------------------\nRunning the workloads on mutant: " + m.getDescription() + " at " + m.getLocation());
                Document mutantDoc = co.generateMutant(qn, m);

                if (saveMutants) {
                    String prefix = Paths.get(jsimgFile).getFileName().toString().replaceAll(".jsimg", "");
                    String fileNameNoExtension = prefix + "_" + m.getOperator() + "_" + counter;
                    String prefixMutant = mutantsFolder + "/" + fileNameNoExtension;
                    String fullPath = prefixMutant + ".jsimg";
                    String fileNameWithExtension = fileNameNoExtension + ".jsimg";
                    setNameInDocument(mutantDoc, fileNameWithExtension);
                    out.output(mutantDoc, new FileWriter(fullPath));
                    counter++;
                }

                for (QnTest test : tests) {
                    //System.out.println("----------------------\nRunning " + test.getTestName() + " on mutant: " + m.getDescription() + " at " + m.getLocation());
                    System.out.println("-- Running workload \"" + test.getTestName() + "\"");
                    Document testMutantDoc = TestExecutor.applyTest(mutantDoc, test);

                    ExecutorService service = Executors.newSingleThreadExecutor();
                    long testTime = System.currentTimeMillis();
                    Callable<Boolean> simulationTask = () -> {
                        try {
                            Simulation.runSimulation(testMutantDoc, modelFolder, testTimeoutMin * 60);
                        } catch (Exception e) {
                            return Boolean.FALSE;
                        }
                        return Boolean.TRUE;
                    };
                    boolean error;
                    Future<Boolean> futureTask = service.submit(simulationTask);
                    try {
                        error = !futureTask.get(testTimeoutMin, TimeUnit.MINUTES);
                        testTime = (System.currentTimeMillis() - testTime);
                    } catch (TimeoutException timeoutException) {
                        testTime = (System.currentTimeMillis() - testTime);
                        futureTask.cancel(true);
                        System.out.println("Terminated because of timeout. Simulation time " + (testTime / 60000.0) + " mins.");
                        continue;
                    } catch (InterruptedException | ExecutionException e) {
                        testTime = (System.currentTimeMillis() - testTime);
                        System.out.println("Terminated because an error occurred. Simulation time " + (testTime / 60000.0) + " mins.");
                        continue;
                    } finally {
                        service.shutdownNow();
                    }
                    if (error) {
                        System.out.println("Terminated because an error occurred. Test time " + (testTime / 60000.0) + " mins.");
                        continue;
                    }

                    SimulationResults mutantResult = Simulation.readSimulationResults(modelFolder + "/" + testMutantDoc.getRootElement().getAttributeValue("name"));
                    mutationResults.add(new MutationResult(m, originalResults.get(test), mutantResult, test.getTestName()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error while running mutation analysis.");
            e.printStackTrace();
        }

        try {
            saveResults(jsimgFile, mutationResults);
        } catch (Exception e) {
            System.err.println("Error while exporting results");
            e.printStackTrace();
        }

    }

    private void saveResults(String modelName, Collection<MutationResult> mutationResults) throws IOException {
        System.out.println("----------------------\n\nSaving results in " + resultsFolder);
        File outputDir = new File(resultsFolder);
        if ((outputDir.exists() && !outputDir.isDirectory()) || (!outputDir.exists() && !outputDir.mkdirs())) {
            throw new IOException("Could not make output directory");
        }
        String prefix = Paths.get(modelName).getFileName().toString().replaceAll(".jsimg", "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String resultsTime = simpleDateFormat.format(new GregorianCalendar(Locale.getDefault()).getTime());

        prefix = resultsTime + "_" + prefix;

        //Raw results
        try (FileWriter fw = new FileWriter(resultsFolder + File.separator + prefix + "_results.tsv");
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("workload\tlocation\toperator\t"
                    + MeasureResult.printHeaderFixedPart("") + "\t"
                    + MeasureResult.printHeaderVariablePart("orig_") + "\t"
                    + MeasureResult.printHeaderVariablePart("mut_")
                    + "\ttimeoutMut" +
                    "\tsignificant_difference\n");
            for (MutationResult result : mutationResults) {
                bw.write(result.printMetrics());
            }
        }

        List<String> tests = mutationResults.stream().map(MutationResult::getTestName).distinct().sorted().collect(Collectors.toList());
        List<String> measures = mutationResults.stream()
                .flatMap(mr -> mr.getMutantResult().getMeasuresResults().stream().filter(Objects::nonNull)
                        .map(measureResult -> measureResult.getMeasureType() + "_"
                                + (("".equals(measureResult.getClassField()) || measureResult.getClassField() == null) ? "NA" : measureResult.getClassField()) + "_"
                                + (("".equals(measureResult.getStation()) || measureResult.getStation() == null) ? "NA" : measureResult.getStation())))
                .distinct().sorted().collect(Collectors.toList());
        List<Mutation> mutations = mutationResults.stream().map(MutationResult::getMutation).distinct().sorted((m1, m2) -> {
            if (!m1.getLocation().equals(m2.getLocation())) {
                return m1.getLocation().compareTo(m2.getLocation());
            } else {
                return m1.getOperator().compareTo(m2.getOperator());
            }
        }).collect(Collectors.toList());

        //Killed by workload matrix
        try (FileWriter fw = new FileWriter(resultsFolder + File.separator + prefix + "_results_triggeredByWorkload_matrix.tsv");
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("workload\t");
            for (int i = 0; i < mutations.size(); i++) {
                Mutation m = mutations.get(i);
                bw.write(m.getOperator() + "_" + m.getLocation());
                if (i == mutations.size() - 1) {
                    bw.write("\n");
                } else {
                    bw.write("\t");
                }
            }
            for (String test : tests) {
                bw.write(test + "\t");
                for (int i = 0; i < mutations.size(); i++) {
                    Mutation m = mutations.get(i);
                    boolean killed = mutationResults.stream()
                            .filter(mr -> mr.getMutation().equals(m) && mr.getTestName().equals(test))
                            .anyMatch(MutationResult::isKilled);
                    bw.write(String.valueOf(killed));
                    if (i == mutations.size() - 1) {
                        bw.write("\n");
                    } else {
                        bw.write("\t");
                    }
                }
            }
        }

        //Killed by metric matrix
        try (FileWriter fw = new FileWriter(resultsFolder + File.separator + prefix + "_results_triggeredByMetric_matrix.tsv");
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("measureDescr\t");
            for (int i = 0; i < mutations.size(); i++) {
                Mutation m = mutations.get(i);
                bw.write(m.getOperator() + "_" + m.getLocation());
                if (i == mutations.size() - 1) {
                    bw.write("\n");
                } else {
                    bw.write("\t");
                }
            }
            for (String measure : measures) {
                bw.write(measure + "\t");
                for (int i = 0; i < mutations.size(); i++) {
                    Mutation m = mutations.get(i);
                    boolean killed = mutationResults.stream()
                            .filter(mr -> mr.getMutation().equals(m)).anyMatch(mr -> mr.isKilledWithMeasure(measure));
                    bw.write(String.valueOf(killed));
                    if (i == mutations.size() - 1) {
                        bw.write("\n");
                    } else {
                        bw.write("\t");
                    }
                }
            }
        }
    }

    private static void setNameInDocument(Document doc, String name) {
        Element rootElement = doc.getRootElement();
        rootElement.setAttribute("name", name);
        rootElement.getChild("sim").setAttribute("name", name);
    }
}
