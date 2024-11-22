package com.example.area_backend;

import org.json.JSONException;
import org.json.JSONObject;

public class ParserJson
{
    public ParserJson() {}

    public JSONObject parseToJson(String elementToParse)
    {
        try {
            JSONObject json = new JSONObject(elementToParse);
            return (json);
        } catch (JSONException e) {
            System.err.println("Failed to Parse this string : \"" + elementToParse + "\" to json: " + e);
        }
        return (null);
    }

    public String parseToString(JSONObject elementToParse)
    {
        return (elementToParse != null ? elementToParse.toString() : null);
    }
}
