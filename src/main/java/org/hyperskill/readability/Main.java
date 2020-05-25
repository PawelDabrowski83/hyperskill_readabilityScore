package org.hyperskill.readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

        private static final String TEXT_LOADED = "The text is:";
        private static final String WORD_COUNT = "Words: %d" + System.lineSeparator();
        private static final String SENTENCE_COUNT = "Sentences: %d" + System.lineSeparator();
        private static final String CHARACTER_COUNT = "Characters: %d" + System.lineSeparator();
        private static final String SYLLABLES_COUNT = "Syllables: %d" + System.lineSeparator();
        private static final String POLYSYLLABLES_COUNT = "Polysyllables: %d" + System.lineSeparator();
        private static final String COMMAND_LINE = "Enter the score you want to calculate (ARI, FK, SMOG, CL, all):";
        private static final String RESULT_ARI = "Automated Readability Index: %f (about %d year olds)." + System.lineSeparator();
        private static final String RESULT_FK = "Flesch–Kincaid readability tests: %f (about %d year olds)." + System.lineSeparator();
        private static final String RESULT_SMOG = "Simple Measure of Gobbledygook: %f (about %d year olds)." + System.lineSeparator();
        private static final String RESULT_CL = "Coleman–Liau index: %f (about %d year olds)." + System.lineSeparator();
        private static final Pattern vowelsPattern = Pattern.compile("[aeiou]+");
        private static final Pattern vowelYPattern = Pattern.compile("[^aeiou](y)");

        public static void main(String[] args) {
            StringBuilder builder = new StringBuilder();
            String fileName = "";
            if (args.length > 0) {
                fileName = args[0];
            }


            File file = new File(fileName);

            try (Scanner fileScanner = new Scanner(file)) {
                while (fileScanner.hasNextLine()) {
                    builder.append(fileScanner.nextLine());
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
                return;
            }

            String text = builder.toString();
            System.out.println(TEXT_LOADED);
            System.out.println(text);
            System.out.println();




            String[] sentences = text.trim().split("[?!.]");
            int sentenceCount = sentences.length;
            int wordSum = 0;
            int syllableSum = 0;
            int polysyllablicSum = 0;
            for (String sentence : sentences) {
                String[] words = sentence.trim().toLowerCase().split("\\s+");
                for (String word : words) {
                    syllableSum += countSyllables(word);
                    if (isPolysyllablic(word)) {
                        polysyllablicSum++;
                    }
                }
                wordSum += sentence.trim().split("\\s+").length;
            }

            System.out.printf(WORD_COUNT, wordSum);
            System.out.printf(SENTENCE_COUNT, sentences.length);
            String textWithoutSpaces = text.replaceAll("\\s+", "");
            System.out.printf(CHARACTER_COUNT, textWithoutSpaces.length());
            System.out.printf(SYLLABLES_COUNT, syllableSum);
            System.out.printf(POLYSYLLABLES_COUNT, polysyllablicSum);

            System.out.println(COMMAND_LINE);
            String command;
            try (Scanner scanner = new Scanner(System.in)) {
                command = scanner.next();
            }
            switch (command) {
                case "ARI":
                    calculateFinal(RESULT_ARI, calculateARI(textWithoutSpaces.length(), wordSum, sentenceCount));
                    break;
                case "FK":
                    calculateFinal(RESULT_FK, calculateFK(wordSum, sentenceCount, syllableSum));
                    break;
                case "SMOG":
                    calculateFinal(RESULT_SMOG, calculateSMOG(polysyllablicSum, sentenceCount));
                    break;
                case "CL":
                    calculateFinal(RESULT_CL, calculateCL(textWithoutSpaces.length(), wordSum, sentenceCount));
                    break;
                case "all":
                    calculateFinal(RESULT_ARI, calculateARI(textWithoutSpaces.length(), wordSum, sentenceCount));
                    calculateFinal(RESULT_FK, calculateFK(wordSum, sentenceCount, syllableSum));
                    calculateFinal(RESULT_SMOG, calculateSMOG(polysyllablicSum, sentenceCount));
                    calculateFinal(RESULT_CL, calculateCL(textWithoutSpaces.length(), wordSum, sentenceCount));
                    break;
                default:
                    System.out.println("Command uncognized");
            }


        }

        public static int countSyllables(String word) {
            String tempWord = word.toLowerCase();
            // cut final E (voiceless)
            if (word.matches("\\w*e\\b") && word.matches("\\w*[^bl]e")) {
                tempWord = tempWord.replaceAll("e\\b", "");
            }

            int result = 0;
            // check for Y as a vowel
            if (tempWord.matches("\\b\\w*[^aeiou](y)")) {
                result += (int) vowelYPattern.matcher(tempWord).results().count();
            }
            result += (int) vowelsPattern.matcher(tempWord).results().count();
            return result == 0 ? 1: result;

        }
        public static boolean isPolysyllablic(String word) {
            return countSyllables(word) > 2;
        }

        public static double calculateARI(int characters, int words, int sentences) {
            return 4.71 * characters / words + 0.5 * words / sentences - 21.43;
        }

        public static double calculateFK(int words, int sentences, int syllables) {
            return 0.39 * ((double) words / sentences) + 11.8 * ((double) syllables / words) - 15.59;
        }

        public static double calculateSMOG(int polysyllables, int sentences) {
            return 1.043 * Math.sqrt(polysyllables * ((double) 30 / sentences)) + 3.1291;
        }

        public static double calculateCL(int characters, int words, int sentences) {
            return 0.0588 * ((double) characters / words) * 100 - 0.296 * ((double) sentences / words) * 100 - 15.8;
        }

        public static int getAgeGroup(int score) {
            switch (score) {
                case 1:
                case 2:
                    return score + 5;
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                    return score + 6;
                case 13:
                    return 24;
                default:
                    return 0;
            }
        }

        public static void calculateFinal(String message, double score) {
            System.out.printf(message, score, getAgeGroup((int) Math.ceil(score)));
        }
    }

