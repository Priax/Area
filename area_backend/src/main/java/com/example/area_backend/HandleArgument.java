package com.example.area_backend;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleArgument
{
    public HandleArgument() {};

    public Map<String, String> fillOutWithArg(Map<String, String> arguments, String[] keys, String[] values)
    {
        int size = keys.length;
        for (int i = 0; i < size; i++) {
            arguments.put(keys[i], values[i]);
        }
        return (arguments);
    }

    public String[] fillWithArgument(Map<String, String> arguments, String []values)
    {
        int size = values.length;
        for (int i = 0; i < size; i++) {
            values[i] = this.replaceVariable(arguments, values[i]);
        }
        return (values);
    }

    public String[] fillWithArgument(Map<String, String> arguments, String []values, boolean []keepAuthor)
    {
        int size = values.length;
        for (int i = 0; i < size; i++) {
            values[i] = this.replaceVariable(arguments, values[i], keepAuthor[i]);
        }
        return (values);
    }

    private String replaceVariable(Map<String, String> arguments, String values, boolean keepAuthor)
    {
        Pattern pattern = Pattern.compile("\\$(\\w+)");
        Matcher matcher = pattern.matcher(values);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String variableName = matcher.group(1);
            String fullVariableName = "$" + variableName;
            String replacement = arguments.getOrDefault(fullVariableName, fullVariableName);
            replacement = putDestinationBefore(fullVariableName, replacement, keepAuthor);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String replaceVariable(Map<String, String> arguments, String values)
    {
        Pattern pattern = Pattern.compile("\\$(\\w+)");
        Matcher matcher = pattern.matcher(values);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String variableName = matcher.group(1);
            String fullVariableName = "$" + variableName;
            String replacement = arguments.getOrDefault(fullVariableName, fullVariableName);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String putDestinationBefore(String value, String argument, boolean keepAuthor)
    {
        if (!keepAuthor) {
            return (argument);
        }
        if (value.equals("$Author") || value.equals("$User")) {
            return ("@" + argument);
        } else {
            return ("#" + argument);
        }
    }
}
