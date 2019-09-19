package com.josieAlan.videohdplayerjosie.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;

public class MyNumFormatter {
    private static String TAG = "MyNumFormatter";

    public static String getFormattedInForeign(final long amount) {
        return numDifferentiation(amount);
    }

    public static String getFormatted(double Value) {
        return numDifferentiation(Value);
    }

    private static double RoundTo2Decimals(double amount) {
        DecimalFormat df2 = new DecimalFormat("###.##");
        return Double.valueOf(df2.format(amount));
    }

    private static String RoundTo2DecimalsA(double amount) {
        DecimalFormat df2 = new DecimalFormat("###.##");
        return String.valueOf(df2.format(amount));
    }

    public static String getNumFormat11(String Value) {
        Double amt = Double.valueOf(Value);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance((new Locale("en", "IN")));
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        formatter.setDecimalFormatSymbols(symbols);
        String stramt = formatter.format(amt);
        String[] finalamt = stramt.split(Pattern.quote("."));
        String toreturn = finalamt[0].replaceAll("\\s+", "");
        return toreturn;
    }

    private static String numDifferentiation(double val) {
        try {

            String returnVal = "";

            double d;
            String iteration;

            if (val >= 1000000000000L) {
                d = val / 1000000000000L;
                iteration = "T";
            } else if (val >= 10000000) {
                d = val / 10000000;
                iteration = "C";
            } else if (val >= 100000) {
                d = val / 100000;
                iteration = "L";
            } else {
                return RoundTo2DecimalsA(val);
            }

            boolean isRound = (d * 10) % 10 == 0;
            if (isRound) {
                String[] numberD = String.valueOf(d).split(Pattern.quote("."));
                if (Integer.valueOf(numberD[1]) > 0)
                    returnVal = d + " " + iteration;
                else
                    returnVal = ((long) d) + " " + iteration;
            } else {
                double dd = RoundTo2Decimals(d);
               /* String[] numberD = String.valueOf(dd).split(Pattern.quote("."));
                if(Integer.valueOf(numberD[1]) > 0)
                    returnVal = dd  + " " + iteration;
                else*/
                returnVal = (dd) + " " + iteration;
            }

            return returnVal;

        } catch (Exception e) {

            e.printStackTrace();
            return String.valueOf(val);
        }
    }
}
