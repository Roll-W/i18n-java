/*
 * Copyright (C) 2022 Lingu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package space.lingu.i18n.configuration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Only reads keys from properties. (Value is just incidental.)
 *
 * @author RollW
 */
public class ConfigurationKeysReader {
    private static final String SEPARATOR = "=";

    private static final String COMMENT = "#";
    private static final String CONTINUE = "\\";

    private final Map<String, String> keyValues;
    private final BufferedReader reader;

    public ConfigurationKeysReader(File file) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(file));
        keyValues = readKeyValues();
    }

    public ConfigurationKeysReader(InputStream stream) {
        reader = new BufferedReader(new InputStreamReader(stream));
        keyValues = readKeyValues();
    }

    private Map<String, String> readKeyValues() {
        Map<String, String> map = new HashMap<>();
        AtomicBoolean contact = new AtomicBoolean(false);
        reader.lines().forEach(s -> {
            if (s.startsWith(COMMENT)) {
                return;
            }
            if (s.trim().isEmpty()) {
                return;
            }
            contact.set(s.endsWith(CONTINUE));
            StringValue value = readLine(s, contact.get());
            if (value == null) {
                return;
            }
            if (map.containsKey(value.key)) {
                throw new IllegalArgumentException("duplicated key " + value.key);
            }
            map.put(value.key, value.value);
        });
        return map;
    }

    public Set<String> keys() {
        return keyValues.keySet();
    }

    public static class StringValue {
        final String key;
        final String value;

        private StringValue(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private StringValue readLine(String line, boolean contact) {
        int start = line.indexOf(SEPARATOR);
        if (start < 0) {
            throw new IllegalArgumentException("Not found key value pattern.");
        }
        if (contact) {
            return null;
        }
        final String key = line.substring(0, start).trim();
        final String value = line.substring(start + 1, line.length() -1).trim();
        return new StringValue(key, value);
    }

}
