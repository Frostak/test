package main;

import static java.util.stream.Collectors.toCollection;

import entity.Package;
import entity.Tax;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    private static final String Q = "quit";
    private static final int interval = 60000;
    private static boolean feeFileInitialize;

    public static void main(String[] args) throws IOException {
        List<Package> packageList = new ArrayList<>();
        List<Tax> feeList = new ArrayList<>();
        String initFile = null;
        String feesFile = null;

        try {
            initFile = args[0];
            feesFile = args[1];
        } catch (Exception ignored) {
            System.out.println("No arguments set for program run.");
        }

        if (feesFile != null) {
            System.out.println("Fee file provided ... Initializing...");
            feeFileInitialize = initializeFeeFile(feesFile, feeList);
        }

        if (initFile != null) {
            System.out.println("Init file provided ... Initializing...");
            initializePackageList(initFile, feeList, packageList);
        }

        class Demo extends TimerTask {

            public void run() {
                packageList.sort(Comparator.reverseOrder());
                packageList.forEach(aPackage -> System.out.println(aPackage.toString(feeFileInitialize)));
                System.out.println("***********");
            }
        }
        Timer timer = new Timer();
        timer.schedule(new Demo(), 0, interval);

        InputStreamReader streamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        while (true) {
            System.out.println("Please write 'quit' if you wish to exit.");
            System.out.println("For inserting new package write 'insert'.");

            String input = bufferedReader.readLine();

            if (Q.equals(input)) {
                System.exit(1);
            } else {
                System.out.println("Inserting ...");
                processInsert(packageList, feeList, bufferedReader);
                System.out.println("Inserting ... Done.");
            }
        }
    }

    private static boolean initializeFeeFile(String feesFile, List<Tax> feeList) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getFileLocation(feesFile)));
            String currentLine = reader.readLine();

            while (currentLine != null) {
                insertFee(feeList, currentLine);

                currentLine = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void insertFee(List<Tax> feeList, String readLine) {
        String[] toSplitString = readLine.split("\\s+");

        double weight = Double.parseDouble(toSplitString[0]);
        double fee = Double.parseDouble(toSplitString[1]);

        feeList.add(new Tax(weight, fee));
    }

    private static void initializePackageList(String initFile, List<Tax> feeList, List<Package> packageList) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getFileLocation(initFile)));
            String currentLine = reader.readLine();

            while (currentLine != null) {
                insertSinglePackage(packageList, feeList, currentLine);

                currentLine = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static InputStream getFileLocation(String file) {
        return Main.class.getClassLoader().getResourceAsStream(file + ".txt");
    }

    private static void processInsert(List<Package> packageList, List<Tax> feeList, BufferedReader bufferedReader) {
        try {
            String readLine = bufferedReader.readLine();
            insertSinglePackage(packageList, feeList, readLine);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void insertSinglePackage(List<Package> packageList, List<Tax> feeList, String readLine) {
        String[] toSplitString = readLine.split("\\s+");

        double weight = Double.parseDouble(toSplitString[0]);
        int postalCode = Integer.parseInt(toSplitString[1]);

        Package newPackage = new Package(weight, postalCode);

        AtomicBoolean shouldInsert = new AtomicBoolean(true);
        packageList.forEach(currentPackage -> {
            if (currentPackage.getPostalCode() == postalCode) {
                currentPackage.setWeight(currentPackage.getWeight() + weight);
                calculateFee(currentPackage, feeList);
                shouldInsert.set(false);
            }
        });

        if (shouldInsert.get()) {
            calculateFee(newPackage, feeList);
            packageList.add(newPackage);
        }
    }

    private static void calculateFee(Package currentPackage, List<Tax> feeList) {
        if (feeFileInitialize) {
            double weight = currentPackage.getWeight();

            currentPackage.setFee(feeList.stream()
                    .filter(tax -> weight >= tax.getWeight())
                    .limit(1)
                    .collect(toCollection(ArrayList::new)).get(0).getFee());
        }
    }
}
