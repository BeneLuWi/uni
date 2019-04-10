package com.levenshtein;

/*
Benedikt Lüken-Winkels
1138844
 */

public class Main {

    public static void main(String[] args) {

        String s1 = "Schnuppervier";
        String s2 = "Schuppentier";

        Edit1 edit1 = new Edit1();

        Edit2 edit2 = new Edit2();

        int[][] result1 = edit1.levenshtein(s1, s2);
        for(int j = 0; j <= s2.length(); j++){
            for(int i = 0; i <= s1.length(); i++){
                System.out.print(result1[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("------------------");

        int[][] result2 = edit2.levenshtein(s1, s2);
        for(int j = 0; j <= s2.length(); j++){
            for(int i = 0; i <= s1.length(); i++){
                System.out.print(result2[i][j] + " ");
            }
            System.out.println();
        }

    }
}
