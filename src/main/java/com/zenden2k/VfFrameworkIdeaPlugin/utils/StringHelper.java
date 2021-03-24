package com.zenden2k.VfFrameworkIdeaPlugin.utils;

import java.util.ArrayList;

public class StringHelper {

    public static class StringMatch {
        private final String s;
        private final int startPos;
        private final int length;

        public StringMatch(String s, int startPos, int length) {
            this.s = s;
            this.startPos = startPos;
            this.length = length;
        }

        public String getValue() {
            return s;
        }

        public int getStartPos() {
            return startPos;
        }

        public int getLength() {
            return length;
        }
    }

    /*
        Разбивает строку по разделителю и возвращает позиции подстрок
     */
    public static StringMatch[] splitString(String input, String separator) {
        int startIndex = 0;
        final ArrayList<StringMatch> res = new ArrayList<>();
        int pos;
        do {
            pos = input.indexOf(",", startIndex);
            String alias;
            if (pos == -1) {
                alias = input.substring(startIndex);
            } else {
                alias = input.substring(startIndex, pos);
            }
            res.add(new StringMatch(alias, startIndex, alias.length()));
            startIndex = pos+1;
        } while ( pos != -1);

        return res.toArray(new StringMatch[0]);
    }
}
