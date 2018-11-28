package com.app.client;

import java.util.ArrayList;
import java.util.List;

class Parser {

    static String[] parse(String string) {

        List<String> result = new ArrayList<>();
        String[] splitted = string.split(" ");
        result.add(splitted[0]); // command to execute (cd, md etc.)

        StringBuilder sb = new StringBuilder();
        boolean inside = false;
        for (int i = 1; i < splitted.length; i++) {
            if (!splitted[i].startsWith("\"")) {
                if (inside) {
                    sb.append(" ").append(splitted[i]);
                } else {
                    result.add(splitted[i]);
                    continue;
                }
            }
            if (splitted[i].startsWith("\"")) {
                sb.append(splitted[i]);
                sb.deleteCharAt(0);
                inside = true;
            }
            if (splitted[i].endsWith("\"")) {
                sb.deleteCharAt(sb.length()-1);
                result.add(sb.toString());
                sb.delete(0, sb.length());
                inside = false;
            }
        }
        return result.toArray(new String[result.size()]);
    }
}
