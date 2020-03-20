package com.hdpfans.app.utils;

import android.support.v4.util.ObjectsCompat;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class BooleanConvertTypeAdapter extends TypeAdapter<Boolean> {
    @Override
    public void write(JsonWriter out, Boolean value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    @Override
    public Boolean read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        switch (peek) {
            case BOOLEAN:
                return in.nextBoolean();
            case NULL:
                in.nextNull();
                return null;
            case NUMBER:
                return in.nextInt() != 0;
            case STRING:
                String inStr = in.nextString();
                return Boolean.parseBoolean(inStr) || ObjectsCompat.equals("1", inStr);
            default:
                throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
        }
    }
}
