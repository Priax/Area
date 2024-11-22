package com.example.area_backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.json.JSONArray;

public class MainFunctions
{
    public List<Tuple<String, String>> getListOfTupleFromObject(Object newObject)
    {
        if (newObject instanceof JSONArray listObject) {
            return (this.convertToListOfTuple(listObject.toList()));
        }
        return (null);
    }

    public String getStringFromTuple(Tuple<String, String> tuple)
    {
        return ("{" + tuple.getLeft() + ", " + tuple.getRight() + "}");
    }

    public Tuple<String, String> getTupleFromString(String tuple)
    {
        if (tuple == null || !tuple.startsWith("{") || !tuple.endsWith("}")) {
            throw new IllegalArgumentException("Format invalide : doit commencer par '{' et se terminer par '}'");
        }
        String content = tuple.substring(1, tuple.length() - 1);
        String[] parts = content.split(",", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Le tuple doit contenir exactement deux éléments");
        }
        String left = parts[0].trim();
        String right = parts[1].trim();

        left = parseValue(left);
        right = parseValue(right);

        return new Tuple<>(left, right);
    }

    private String parseValue(String value)
    {
        if ((value.startsWith("\"") && value.endsWith("\"")) ||
            (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private List<Tuple<String, String>> convertToListOfTuple(List<?> allEmails)
    {
        try {
            return allEmails.stream()
                .filter(item -> item instanceof String)
                .map(item -> {
                    try {
                        return this.getTupleFromString((String) item);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        } catch (Exception e) {
            return (null);
        }
    }

    public List<String> transformListOfTupleToListOfString(List<Tuple<String, String>> oldList)
    {
        List<String> newList = new ArrayList<>();
        for (Tuple<String, String> tuple : oldList) {
            newList.add(this.getStringFromTuple(tuple));
        }
        return (newList);
    }

    private List<String> convertToListOfString(List<?> futureList)
    {
        try {
            return futureList.stream()
                .filter(item -> item instanceof String)
                .map(item -> {
                    try {
                        return ((String) item);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        } catch (Exception e) {
            return (null);
        }
    }

    private List<String> convertToListOfString(String futureList)
    {
        return (Arrays.asList(futureList.replaceAll("\\[|\\]|\"", "").split("\\s*,\\s*")));
    }

    public List<String> getListOfStringFromObject(Object futureList)
    {
        if (futureList instanceof JSONArray listObject) {
            return (this.convertToListOfString(listObject.toList()));
        }
        if (futureList instanceof String stringObject) {
            return (this.convertToListOfString(stringObject));
        }
        return (null);
    }
}
