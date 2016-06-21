package sk.ab.herbs.backend.service;

/*
 * Copyright (C) 2012 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import okio.Buffer;
import retrofit.Converter;

final class GsonConverter<T> implements Converter<T> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final TypeAdapter<T> typeAdapter;

    GsonConverter(TypeAdapter<T> typeAdapter) {
        this.typeAdapter = typeAdapter;
    }

    @Override
    public T fromBody(ResponseBody body) throws IOException {
        Reader in = body.charStream();
        try {
            JsonReader jsonReader = new JsonReader(in);
            jsonReader.setLenient(true);
            return typeAdapter.read(jsonReader);
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public RequestBody toBody(T value) {
        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
        try {
            typeAdapter.toJson(writer, value);
            writer.flush();
        } catch (IOException e) {
            throw new AssertionError(e); // Writing to Buffer does no I/O.
        }
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }
}