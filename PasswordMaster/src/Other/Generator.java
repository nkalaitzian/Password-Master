/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Other;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author pasto
 */
public class Generator {
    
    public final static String[] defaultLowercase = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    public final static String[] defaultUppercase = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    public final static String[] defaultNumbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    public final static String[] defaultSymbols = {"!", "@", "#", "$", "%", "^", "&", "*", "-", "_", "=", "+", "[", "]", "{", "}", "(", ")", "?", "|"};

    static String[] lowercase = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    static String[] uppercase = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    static String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    static String[] symbols = {"!", "@", "#", "$", "%", "^", "&", "*", "-", "_", "=", "+", "[", "]", "{", "}", "(", ")", "?", "|"};
    
    static boolean lowercaseSelected = true,
            uppercaseSelected = false,
            numbersSelected = true,
            symbolsSelected = false;

    private static ArrayList<String> password;
    
    private static int length = 8;

    private static final Random random = new Random();

    public static String[] getLowercase() {
        return lowercase;
    }

    public static void setLowercase(String[] lowercase) {
        Generator.lowercase = lowercase;
    }

    public static String[] getUppercase() {
        return uppercase;
    }

    public static void setUppercase(String[] uppercase) {
        Generator.uppercase = uppercase;
    }

    public static String[] getNumbers() {
        return numbers;
    }

    public static void setNumbers(String[] numbers) {
        Generator.numbers = numbers;
    }

    public static String[] getSymbols() {
        return symbols;
    }

    public static void setSymbols(String[] symbols) {
        Generator.symbols = symbols;
    }

    public static boolean isLowercaseSelected() {
        return lowercaseSelected;
    }

    public static void setLowercaseSelected(boolean lowercaseSelected) {
        Generator.lowercaseSelected = lowercaseSelected;
    }

    public static boolean isUppercaseSelected() {
        return uppercaseSelected;
    }

    public static void setUppercaseSelected(boolean uppercaseSelected) {
        Generator.uppercaseSelected = uppercaseSelected;
    }

    public static boolean isNumbersSelected() {
        return numbersSelected;
    }

    public static void setNumbersSelected(boolean numbersSelected) {
        Generator.numbersSelected = numbersSelected;
    }

    public static boolean isSymbolsSelected() {
        return symbolsSelected;
    }

    public static void setSymbolsSelected(boolean symbolsSelected) {
        Generator.symbolsSelected = symbolsSelected;
    }

    public static ArrayList<String> getPassword() {
        return password;
    }

    public static void setPassword(ArrayList<String> password) {
        Generator.password = password;
    }

    public static int getLength() {
        return length;
    }

    public static void setLength(String length) {
        try{
            Generator.length = new Integer(length);
        } catch(NumberFormatException ex){
            Generator.length = 8;
        }
    }
    
    public static void setLength(int length){
        Generator.length = length;
    }
    
    public static void setDefault(){
        setLength(8);
        setLowercase(defaultLowercase);
        setUppercase(defaultUppercase);
        setNumbers(defaultNumbers);
        setSymbols(defaultSymbols);
    }
    
    private static void getRandomLowercaseLetters(int counter) {
        for (int i = 0; i < counter; i++) {
            int index = random.nextInt(lowercase.length - 1);
            password.add(lowercase[index]);
        }
    }

    private static void getRandomUppercaseLetters(int counter) {
        for (int i = 0; i < counter; i++) {
            int index = random.nextInt(uppercase.length - 1);
            password.add(uppercase[index]);
        }
    }

    private static void getRandomSymbols(int counter) {
        for (int i = 0; i < counter; i++) {
            int index = random.nextInt(symbols.length - 1);
            password.add(symbols[index]);
        }
    }

    private static void getRandomNumbers(int counter) {
        for (int i = 0; i < counter; i++) {
            int index = random.nextInt(numbers.length - 1);
            password.add(numbers[index]);
        }
    }

    public static String createPassword() {
        password = new ArrayList();
        int selectedArrays = 0;
        if (isLowercaseSelected()) {
            selectedArrays++;
        }
        if (isUppercaseSelected()) {
            selectedArrays++;
        }
        if (isSymbolsSelected()) {
            selectedArrays++;
        }
        if (isNumbersSelected()) {
            selectedArrays++;
        }

        int counter = getLength() / selectedArrays;

        if (isLowercaseSelected()) {
            getRandomLowercaseLetters(counter);
        }
        if (isUppercaseSelected()) {
            getRandomUppercaseLetters(counter);
        }
        if (isSymbolsSelected()) {
            getRandomSymbols(counter);
        }
        if (isNumbersSelected()) {
            getRandomNumbers(counter);
        }

        if (password.size() < getLength()) {
            getRandomLowercaseLetters(getLength() - password.size());
        }

        StringBuilder output = new StringBuilder(password.size());
        while (!password.isEmpty()) {
            int randPicker = random.nextInt(password.size());
            output.append(password.remove(randPicker));
        }
        return output.toString();
    }
    
    public static void setFromString(String string) {
        if(!string.contains("Generator")){
            return;
        }
        string = string.replaceAll("Generator", "");
        string = string.replaceFirst("\\{", "");
        String[] stringArray = string.split(",");
        for(String str: stringArray){
            if (str.contains("lowercase=")) {
                str = str.replace("lowercase=", "");
                setLowercase(str.split(""));
            } else if (str.contains("uppercase=")) {
                str = str.replace("uppercase=", "");
                setUppercase(str.split(""));
            } else if (str.contains("numbers=")) {
                str = str.replace("numbers=", "");
                setNumbers(str.split(""));
            } else if (str.contains("symbols=")) {
                str = str.replace("symbols=", "");
                setSymbols(str.split(""));
            } else if (str.contains("length")){
                str = str.replace("length=", "");
                setLength(str.replace("\\}", ""));
            } else if (str.contains("lowercaseSelected=")) {
                str = str.replace("lowercaseSelected=", "");
                setLowercaseSelected(Boolean.valueOf(str));
            } else if (str.contains("uppercaseSelected=")) {
                str = str.replace("uppercaseSelected=", "");
                setUppercaseSelected(Boolean.valueOf(str));
            } else if (str.contains("numbersSelected=")) {
                str = str.replace("numbersSelected=", "");
                setNumbersSelected(Boolean.valueOf(str));
            } else if (str.contains("symbolsSelected=")) {
                str = str.replace("symbolsSelected=", "");
                setSymbolsSelected(Boolean.valueOf(str));
            }
        }
    }
    
    public static String getString(){
        String string = "Generator{";
        StringBuilder sb = new StringBuilder();
        for (String s : getLowercase()) {
            sb.append(s);
        }
        string += "lowercase=" + sb.toString() + ",lowercaseSelected=" + isLowercaseSelected();
        sb = new StringBuilder();
        for (String s : getUppercase()) {
            sb.append(s);
        }
        string += ",uppercase=" + sb.toString() + ",uppercaseSelected=" + isUppercaseSelected();
        sb = new StringBuilder();
        for (String s : getNumbers()) {
            sb.append(s);
        }
        string += ",numbers=" + sb.toString() + ",numbersSelected=" + isNumbersSelected();
        sb = new StringBuilder();
        for (String s : getSymbols()) {
            sb.append(s);
        }
        string += ",symbols=" + sb.toString() + ",symbolsSelected=" + isSymbolsSelected();
        string += ",length="+getLength() + "}";
        return string;
    }
}
