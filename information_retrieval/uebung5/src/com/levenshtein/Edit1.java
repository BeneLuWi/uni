package com.levenshtein;

/*
Benedikt Lüken-Winkels
1138844
 */

public class Edit1 extends Edit{


    public int d(char a, char b){
        if(a == b) return 0;
        return 1;
    }

}
