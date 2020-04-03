package com.app.budbank.utils;

import android.util.Patterns;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextUtils {
    public static boolean isValidEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean isEmpty(String value) {
        if(value == null || value.equals("")) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(EditText value) {
        if(value == null || getValue(value).equals("")) {
            return true;
        }
        return false;
    }


    public static boolean isEmpty(CharSequence value) {
        if(value == null || value.equals("")) {
            return true;
        }
        return false;
    }

    public static String getValue(EditText editText) {
        return editText.getText().toString();
    }

    public static int getIntValue(String string) {
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double getDoubleValue(String string) {
        try {
            return Double.parseDouble(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String formatCard(String cardNumber) {
        if (cardNumber == null) return null;
        char delimiter = ' ';
        return cardNumber.replaceAll(".{4}(?!$)", "$0" + delimiter);
    }

    public static String amountFormatting(String amount) {
        double amountParsed = TextUtils.getDoubleValue(amount);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(amountParsed);
    }

    public static boolean stringValidation(String input, String givenPattern) {
        String s = givenPattern.substring(1, givenPattern.length() - 2);
        final Pattern pattern = Pattern.compile(s);
        Matcher m = pattern.matcher(input);
        return m.find() && m.group().equals(input);
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
