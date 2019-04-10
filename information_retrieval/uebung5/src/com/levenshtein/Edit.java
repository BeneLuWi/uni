package com.levenshtein;

/*
Benedikt Lüken-Winkels
1138844
 */

abstract class Edit {


    public abstract int d(char a, char b);

    private int edit(int[][] result, int i, int j, char a, char b){

        if(i == 0 && j == 0) return 0;

        if(i == 0) return j;

        if(j == 0) return i;

        return Math.min(result[i-1][j] + 1, Math.min(result[i][j-1] + 1, result[i-1][j-1] + this.d(a,b)));

    }

    // for lines 0
    private int edit(int i, int j){

        if(i == 0 && j == 0) return 0;

        if(i == 0) return j;

        if(j == 0) return i;

        return -1;
    }



    public int[][] levenshtein(String s1, String s2){

        String tmp;
        if (s1.length() < s2.length()) {
            tmp = s1;
            s1 = s2;
            s2 = tmp;
        }

        int[][] result = new int[s1.length() + 1 ][s2.length() + 1];

        int i, j;

        for(i = 0, j = 0; i <= s1.length(); i++){
            result[i][j] = edit(i, j);
        }

        for(i = 0, j = 0; j <= s2.length(); j++){
            result[i][j] = edit(i, j);
        }



        for(j = 1; j <= s2.length(); j++){
            for(i = 1; i <= s1.length(); i++){
                result[i][j] = edit(result, i, j, s1.charAt(i-1), s2.charAt(j-1));
            }
        }

/*
            i = j = 1;
            while (i <= s1.length() && j <= s2.length()){

            result[i][j] = edit(result, i, j, s1.charAt(i-1), s2.charAt(j-1));

            if(i == j) i++;

            else if(i > j){ i--; j++;}

            // i < j
            else  i++;
        }
*/
        return result;
    }

}
