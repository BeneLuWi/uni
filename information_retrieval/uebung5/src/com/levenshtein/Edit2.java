package com.levenshtein;

/*
Benedikt Lüken-Winkels
1138844
 */

public class Edit2 extends Edit {


    public int d(char a, char b) {

        int i, j;
        String[][] groups = {
                {"qay12", "pöüä"},
                {"wsx3", "ol0"},
                {"edc4", "ik9"},
                {"rfvtgb56", "zhnujm78"},
                {" ", " "}
        };


        boolean aleft = false, bleft = false;
        int afinger = -1, bfinger = -1;

        for (i = 0; i < 5; i++) {
            for (j = 0; j < 2; j++) {
                if (groups[i][j].indexOf(a) != -1 && groups[i][j].indexOf(b) != -1) return 0;

                if (groups[i][0].indexOf(a) != -1) aleft = true;
                if (groups[i][0].indexOf(b) != -1) bleft = true;

                if (groups[i][0].indexOf(a) != -1) afinger = i;
                if (groups[i][0].indexOf(b) != -1) bfinger = i;
            }
        }

        if(aleft == bleft) return 1;

        if(afinger == bfinger) return 1;

        return 2;
    }
}

