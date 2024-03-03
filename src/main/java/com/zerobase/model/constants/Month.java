package com.zerobase.model.constants;

public enum Month {

    JAN("Jan", 1),
    FEB("Feb",2),
    MAR("Mar", 3),
    APR("Apr", 4),
    May("May",5),
    JUN("Jun", 6),
    JUL("Jul", 7),
    AUG("Aug",8),
    SEP("Sep", 9),
    OCT("Oct", 10),
    NOV("Nov",11),
    DEC("Dec", 12);


    private String st;
    private int num;
    Month(String st, int num){
        this.st = st;
        this.num = num;
    }

 public static int strToNum(String s) {
        for (Month m : Month.values()){
            if(m.st.equals(s)){
               return m.num;
            }
        }
    return -1;
 }

}