package com.oauth.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class OptionalUtil {

    public static List<String> spiltToArray(String content, String regex) {
        return Arrays.asList(Optional.ofNullable(content).map(mapper -> mapper.split(regex)).orElse(new String[0]));
    }

    public static Set<String> spiltToSet(String content, String regex) {
        return new HashSet<String>(spiltToArray(content, regex));
    }
}
