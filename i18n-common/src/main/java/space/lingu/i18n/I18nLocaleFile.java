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

package space.lingu.i18n;

import java.lang.annotation.*;

/**
 * @author RollW
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface I18nLocaleFile {
    /**
     * Path of folders in resources directory.
     * <p>
     * Since the program does not look for files recursively,
     * if you put files in multiple folders, you need to manually
     * write to all the folders.
     * <p>
     * Default is the root of resources folder.
     *
     * @return folders in resources
     */
    String[] value() default {""};

    /**
     * Generated class name.
     *
     * @return generated class name
     */
    String className() default DEFAULT_CLASS_NAME;

    /**
     * Prefix of file, to excludes some files.
     *
     * @return Prefix of file.
     */
    String prefix() default DEFAULT_PREFIX;

    /**
     * Suffix of file, to excludes some files.
     *
     * @return Prefix of file.
     */
    String suffix() default DEFAULT_SUFFIX;


    String DEFAULT_CLASS_NAME = "LocaleStrings";

    String DEFAULT_PREFIX = "messages";

    String DEFAULT_SUFFIX = ".properties";
}
