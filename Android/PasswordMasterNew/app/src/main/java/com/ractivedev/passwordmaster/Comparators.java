package com.ractivedev.passwordmaster;

import java.util.Comparator;

/**
 * Created by pasto on 11-Feb-18.
 */

public class Comparators {

    public static class LoginComparatorDescending implements Comparator<Login>
    {
        @Override
        public int compare(Login x, Login y)
        {
            String[] a = x.getTitle().split("");
            String[] b = y.getTitle().split("");
            if((x.getFavorite() && y.getFavorite()) || (!x.getFavorite() && !y.getFavorite())){
                return a[0].compareToIgnoreCase(b[0]);
            } else if(x.getFavorite()){
                return -1;
            } else {
                return 1;
            }
        }
    }
}
