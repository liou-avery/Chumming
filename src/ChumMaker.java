import java.io.*;
import java.util.*;

public class ChumMaker {
    final static String namesList = "names.txt";
    final static String pairsList = "pairs.txt";
    final static String finishedPairs = "finished_pairs.txt";
    final static int numWeeks = 9;
    Map<String, Integer> nameMap = new HashMap<>();
    List<String> names = new ArrayList<>();
    boolean[][] paired;

    public void run() {
        readFiles();
        makeNCombinations(numWeeks);
    }

    public void readFiles() {
        readNamesFile(namesList);
        readPairsFile(pairsList);
    }

    public void readNamesFile(String fileName) {
        try {
            File f = new File(fileName);
            Scanner in = new Scanner(f);
            int nameCounter = 0;
            while (in.hasNext()) {
                String name = in.nextLine();
                nameMap.put(name, nameCounter);
                names.add(name);
                nameCounter++;
            }
            paired = new boolean[nameCounter][nameCounter];
            for (int i = 0; i < nameCounter; i++) {
                paired[i][i] = true;
            }
        } catch (NullPointerException e) {
            System.out.println("File not found.");
        } catch (FileNotFoundException fnf) {
            System.out.println("File not found.");
        }
    }

    public void readPairsFile(String fileName) {
        try {
            File f = new File(fileName);
            Scanner in = new Scanner(f);
            while (in.hasNext()) {
                String pairText = in.nextLine();
                String[] pair = pairText.split(" : ");
                if (pair.length != 2) {
                    System.out.println("Invalid pair text: " + pairText);
                } else {
                    if (nameMap.containsKey(pair[0]) && nameMap.containsKey(pair[1])) {
                        int name1 = nameMap.get(pair[0]);
                        int name2 = nameMap.get(pair[1]);
                        paired[name1][name2] = true;
                        paired[name2][name1] = true;
                    } else {
                        System.out.println("Invalid pair: " + pairText);
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("File not found.");
        } catch (FileNotFoundException fnf) {
            System.out.println("File not found.");
        }
    }

    public void makeNCombinations(int n) {
        PrintStream ps = null;
        try {
            File f = new File(finishedPairs);
            f.createNewFile();
            ps = new PrintStream(f);
        } catch (IOException e) {
            System.out.println("IOException");
        }

        for (int i = 0; i < n; i++) {
            List<String> unpairedCopy = new ArrayList<>();
            unpairedCopy.addAll(names);
            List<String> accumulatedPairs = new ArrayList<>();
            if (tryPairing(unpairedCopy, accumulatedPairs)) {
                System.out.println("SUCCESS");
                printToFile(i + 1, accumulatedPairs, ps);
            } else {
                System.out.println("FAILURE");
                break;
            }
        }
    }

    public void printToFile(int num, List<String> pairs, PrintStream ps) {
        ps.println("Week " + num);
        for (String s : pairs) {
            ps.println(s);
        }
        ps.println();
    }

    public boolean tryPairing(List<String> unpaired, List<String> accumulatedPairs) {
        //Base Case START
        if (unpaired.size() == 2) {
            int numName1 = nameMap.get(unpaired.get(0));
            int numName2 = nameMap.get(unpaired.get(1));
            boolean pairWorks = !paired[numName1][numName2];
            if (pairWorks) {
                paired[numName1][numName2] = true;
                paired[numName2][numName1] = true;
                accumulatedPairs.add(unpaired.get(0) + " : " + unpaired.get(1));
                return true;
            }
            return false;
        } else if (unpaired.size() == 3) {
            int numName1 = nameMap.get(unpaired.get(0));
            int numName2 = nameMap.get(unpaired.get(1));
            int numName3 = nameMap.get(unpaired.get(2));
            boolean trioWorks = !paired[numName1][numName2] || !paired[numName1][numName3]
                                    || !paired[numName2][numName3];
            if (trioWorks) {
                paired[numName1][numName2] = true;
                paired[numName2][numName1] = true;

                paired[numName1][numName3] = true;
                paired[numName3][numName1] = true;

                paired[numName2][numName3] = true;
                paired[numName3][numName2] = true;
                accumulatedPairs.add(unpaired.get(0) + " : " + unpaired.get(1) + " : " + unpaired.get(2));
                return true;
            }
            return false;
        }
        //Base Case END

        String name1 = unpaired.remove(0);
        int numName1 = nameMap.get(name1);
        for (int i = 0; i < unpaired.size(); i++) {
            String name2 = unpaired.get(i);
            int numName2 = nameMap.get(name2);
            if (!paired[numName1][numName2]) {
                unpaired.remove(i);
                if (tryPairing(unpaired, accumulatedPairs)) {
                    paired[numName1][numName2] = true;
                    paired[numName2][numName1] = true;
                    accumulatedPairs.add(name1 + " : " + name2);
                    return true;
                } else {
                    unpaired.add(i, name2);
                }
            }
        }
        unpaired.add(0, name1);
        return false;
    }

    public static void main(String[] args) {
        ChumMaker c = new ChumMaker();
        c.run();
    }
}
