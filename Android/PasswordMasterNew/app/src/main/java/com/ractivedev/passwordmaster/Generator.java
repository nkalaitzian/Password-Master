package com.ractivedev.passwordmaster;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by pasto on 11-Mar-18.
 */

public class Generator {

    /**
     * The default lowercase letters String array.
     */
    public final static String[] DEFAULT_LOWERCASE = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    /**
     * The default uppercase letters String array.
     */
    public final static String[] DEFAULT_UPPERCASE = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    /**
     * The default numbers String array.
     */
    public final static String[] DEFAULT_NUMBERS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    /**
     * The default symbols String array.
     */
    public final static String[] DEFAULT_SYMBOLS = {"!", "@", "#", "$", "%", "^", "&", "*", "-", "_", "=", "+", "[", "]", "(", ")", "?", "|"};

    private static String[] lowercase = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private static String[] uppercase = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private static String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static String[] symbols = {"!", "@", "#", "$", "%", "^", "&", "*", "-", "_", "=", "+", "[", "]", "(", ")", "?", "|"};

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

    public static String getLowercaseString() {
        StringBuilder sb = new StringBuilder();
        for(String s : lowercase){
            if(!s.equals("")) {
                sb.append(",").append(s);
            }
        }
        return sb.toString().replaceFirst(",","");
    }

    private static String getLowercaseCommalessString() {
        StringBuilder sb = new StringBuilder();
        for(String s : lowercase){
            if(!s.equals("")) {
                sb.append(s);
            }
        }
        return sb.toString();
    }

    public static void setLowercase(String[] lowercase) {
        Generator.lowercase = lowercase;
    }

    public static String[] getUppercase() {
        return uppercase;
    }

    public static String getUppercaseString() {
        StringBuilder sb = new StringBuilder();
        for(String s : uppercase){
            if(!s.equals("")) {
                sb.append(",").append(s);
            }
        }
        return sb.toString().replaceFirst(",","");
    }

    private static String getUppercaseCommalessString() {
        StringBuilder sb = new StringBuilder();
        for(String s : uppercase){
            if(!s.equals("")) {
                sb.append(s);
            }
        }
        return sb.toString();
    }

    public static void setUppercase(String[] uppercase) {
        Generator.uppercase = uppercase;
    }

    public static String[] getNumbers() {
        return numbers;
    }

    public static String getNumbersString() {
        StringBuilder sb = new StringBuilder();
        for(String s : numbers){
            if(!s.equals("")) {
                sb.append(",").append(s);
            }
        }
        return sb.toString().replaceFirst(",","");
    }

    private static String getNumbersCommalessString() {
        StringBuilder sb = new StringBuilder();
        for(String s : numbers){
            if(!s.equals("")) {
                sb.append(s);
            }
        }
        return sb.toString();
    }

    public static void setNumbers(String[] numbers) {
        Generator.numbers = numbers;
    }

    public static String[] getSymbols() {
        return symbols;
    }

    public static String getSymbolsString() {
        StringBuilder sb = new StringBuilder();
        for(String s : symbols){
            if(!s.equals("")) {
                sb.append(",").append(s);
            }
        }
        return sb.toString().replaceFirst(",","");
    }

    private static String getSymbolsCommalessString() {
        StringBuilder sb = new StringBuilder();
        for(String s : symbols){
            if(!s.equals("")) {
                sb.append(s);
            }
        }
        return sb.toString();
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
        try {
            Generator.length = new Integer(length);
        } catch (NumberFormatException ex) {
            Generator.length = 8;
        }
    }

    public static void setLength(int length) {
        if(length < 0){
            length = 8;
        }
        Generator.length = length;
    }

    public static void setDefault() {
        setLength(8);
        setLowercase(DEFAULT_LOWERCASE);
        setUppercase(DEFAULT_UPPERCASE);
        setNumbers(DEFAULT_NUMBERS);
        setSymbols(DEFAULT_SYMBOLS);
        setLowercaseSelected(true);
        setUppercaseSelected(false);
        setSymbolsSelected(false);
        setNumbersSelected(true);
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



        String result = shufflePassword();
        if(result.contains("---")){
            return createPassword();
        } else {
            return result;
        }
    }

    public static void setFromString(String string) {
        if (!string.contains("Generator")) {
            return;
        }
        string = string.replaceAll("Generator", "");
        string = string.replaceAll("\\{", "");
        string = string.replaceAll("\\}", "");
        String[] stringArray = string.split(",");
        for (String str : stringArray) {
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
            } else if (str.contains("length")) {
                str = str.replace("length=", "");
                setLength(str);
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

    public static String getString() {
        String string = "Generator{";
        string += "lowercase=" + getLowercaseCommalessString() + ",lowercaseSelected=" + isLowercaseSelected();
        string += ",uppercase=" + getUppercaseCommalessString() + ",uppercaseSelected=" + isUppercaseSelected();
        string += ",numbers=" + getNumbersCommalessString() + ",numbersSelected=" + isNumbersSelected();
        string += ",symbols=" + getSymbolsCommalessString() + ",symbolsSelected=" + isSymbolsSelected();
        string += ",length=" + getLength() + "}";
        return string;
    }

    public static String shufflePassword() {
        if(password == null){
            return "";
        }
        if(password.isEmpty()){
            return "";
        }
        StringBuilder output = new StringBuilder(password.size());
        while (!password.isEmpty()) {
            int randPicker = random.nextInt(password.size());
            output.append(password.remove(randPicker));
        }
        for(String s:output.toString().split("")){
            password.add(s);
        }
        return output.toString();
    }
}
