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

package space.lingu.i18n.compiler;

import space.lingu.i18n.I18n;
import space.lingu.i18n.configuration.ConfigurationKeysReader;

import javax.annotation.processing.Messager;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author RollW
 */
public class ResourcesReader {
    private final String path;
    private final String prefix;
    private final String suffix;

    public ResourcesReader(String folderPath, String prefix, String suffix) {
        path = Objects.requireNonNull(
                I18n.class.getClassLoader().getResource(""), "Resource path null").getPath()
                + "/" + folderPath;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public Set<String> load() throws FileNotFoundException {
        Set<String> strings = new HashSet<>();
        File parent = new File(path);
        if (!parent.exists()) {
            return Collections.emptySet();
        }
        File[] files = parent.listFiles();
        if (files == null) {
            return Collections.emptySet();
        }
        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }
            String name = file.getName();
            if (!name.startsWith(prefix)) {
                continue;
            }
            if (!name.endsWith(suffix)) {
                continue;
            }
            ConfigurationKeysReader reader = new ConfigurationKeysReader(file);
            strings.addAll(reader.keys());
        }
        return strings;
    }

}
