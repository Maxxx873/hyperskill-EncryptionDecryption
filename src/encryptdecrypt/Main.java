package encryptdecrypt;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

interface CodingAlgorithm {

    String code(String mode, int key, String message);

}

class Coder {

    private CodingAlgorithm algorithm;

    public Coder(CodingAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public CodingAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm( CodingAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String code(String mode, int key, String message) {return this.algorithm.code(mode, key, message); }

}

class UnicodeAlgorithm implements CodingAlgorithm {

    @Override
    public String code(String mode, int key, String message) {
        String result = new String();

        switch (mode) {
            case "dec":
                for (int i = 0; i < message.length(); i++) {
                    result += (char)((int)message.charAt(i) - key);
                }
                return result;
            case "enc":
                for (int i = 0; i < message.length(); i++) {
                    result += (char)((int)message.charAt(i) + key);
                }
                return result;
            default:
                return null;
        }
    }
}

class ShiftAlgorithm implements CodingAlgorithm {

    @Override
    public String code(String mode, int key, String message) {
        String alphabetLowCase = "abcdefghijklmnopqrstuvwxyz";
        String alphabetUpCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String alphabet = null;
        String cyphertext = new String();
        int cypherIndex = 0;
        switch (mode) {
            case "enc":
                for (int i = 0; i < message.length(); i++) {
                    if (alphabetLowCase.indexOf(message.charAt(i)) > 0) {
                        alphabet = alphabetLowCase;
                    }
                    if (alphabetUpCase.indexOf(message.charAt(i)) > 0) {
                        alphabet = alphabetUpCase;
                    }
                    int index = alphabet.indexOf(message.charAt(i));
                    if (index < 0) {
                        cyphertext += message.charAt(i);
                        continue;
                    }
                    cypherIndex = index + key;
                    if (cypherIndex > alphabet.length()-1) {
                        cypherIndex = cypherIndex % alphabet.length();
                    }
                    cyphertext += alphabet.charAt(cypherIndex);
                }
                return cyphertext;
            case "dec":
                for (int i = 0; i < message.length(); i++) {
                    if (alphabetLowCase.indexOf(message.charAt(i)) > 0) {
                        alphabet = alphabetLowCase;
                    }
                    if (alphabetUpCase.indexOf(message.charAt(i)) > 0) {
                        alphabet = alphabetUpCase;
                    }
                    int index = alphabet.indexOf(message.charAt(i));
                    if (index < 0) {
                        cyphertext += message.charAt(i);
                        continue;
                    }
                    cypherIndex = index - key;
                    if (cypherIndex < 0 ) {
                        cypherIndex += alphabet.length();
                    }
                    cyphertext += alphabet.charAt(cypherIndex);
                }
                return cyphertext;
            default:
                return null;
        }

    }
}

public class Main {
    public static void main(String[] args) {
        String mode = "enc";
        String message = "";
        String pathToFileIn = "";
        String pathToFileOut = "";
        String algorithm = "shift";
        int key = 0;
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-mode")) {
                mode = args[i + 1];
            }
            if (args[i].equals("-key") ) {
                key = Integer.parseInt(args[i + 1]);
            }
            if (args[i].equals("-data")) {
                message = args[i + 1];
            }
            if (args[i].equals("-alg")) {
                algorithm = args[i + 1];
            }
            if (args[i].equals("-in")) {
                if (message.length() == 0) {
                    pathToFileIn = args[i + 1];
                    File fileIn = new File(pathToFileIn);
                    try (Scanner scanner = new Scanner(fileIn)) {
                        while (scanner.hasNext()) {
                            message = message + scanner.nextLine();
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("Error: No file found: " + pathToFileIn);
                    }

                }
            }
            if (args[i].equals("-out")) {
                pathToFileOut = args[i + 1];
            }
        }

        Coder coder = null;
        switch (algorithm) {
            case "shift":
                coder = new Coder(new ShiftAlgorithm());
                break;
            case "unicode":
                coder = new Coder(new UnicodeAlgorithm());
                break;
            default:
                coder = new Coder(new ShiftAlgorithm());
                break;
        }


        if (pathToFileOut.length() == 0) {
            System.out.println(coder.code(mode, key, message));

        } else {
            File fileOut = new File(pathToFileOut);
            try (FileWriter writer = new FileWriter(fileOut)) {
                writer.write(coder.code(mode, key, message));
                }
            catch (IOException e) {
                System.out.printf("Error An exception occurs %s", e.getMessage());
            }

        }
    }


}