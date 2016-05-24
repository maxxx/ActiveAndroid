package com.activeandroid.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Maxim Smirnov on 24.05.16.
 */
public class Convert {

    /**
     * @param arr - list like "1, 2, 3"
     * @return - string "1, 2, 3"
     */
    public static String listToString(List<?> arr) {
        if (!arr.isEmpty()) {
            StringBuilder nameBuilder = new StringBuilder();

            for (Object n : arr) {
                nameBuilder.append(n.toString()).append(", ");
            }

            nameBuilder.deleteCharAt(nameBuilder.length() - 1);
            nameBuilder.deleteCharAt(nameBuilder.length() - 1);

            return nameBuilder.toString();
        } else {
            return "";
        }
    }

    /**
     * @param str - like "1, 2, 3"
     * @param sep - ", " by default
     * @return - list of "1" "2" "3"
     */
    public static ArrayList<String> strToList(String str, String sep) {
        String[] arr = str.split(sep);
        return arrToList(arr);
    }

    public static <T> ArrayList<T> arrToList(T[] arr) {
        return new ArrayList<T>(Arrays.asList(arr));
    }

    public static <T> T[] listToArr(List<T> arr) {
        final T t = arr.get(0);
        final T[] res = (T[]) Array.newInstance(t.getClass(), arr.size());
        for (int i = 0; i < arr.size(); i++) {
            res[i] = arr.get(i);
        }
        return res;
    }
}
