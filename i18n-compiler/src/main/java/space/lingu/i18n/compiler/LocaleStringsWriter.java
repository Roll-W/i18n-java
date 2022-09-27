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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import space.lingu.i18n.I18nLocaleFile;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author RollW
 */
public class LocaleStringsWriter extends ClassWriter {
    private final TypeElement mElement;
    private final Set<String> mKeys;

    public LocaleStringsWriter(ProcessingEnvironment environment,
                               TypeElement element,
                               Set<String> keys) {
        super(environment, ClassName.get(element));
        mElement = element;
        mKeys = keys == null
                ? Collections.emptySet() : keys;
    }

    @Override
    protected TypeSpec.Builder builder() {
        I18nLocaleFile i18nLocaleFile = mElement.getAnnotation(I18nLocaleFile.class);
        final String className = i18nLocaleFile.className().isEmpty()
                ? I18nLocaleFile.DEFAULT_CLASS_NAME
                : i18nLocaleFile.className();
        List<FieldSpec> fieldSpecs;
        try {
            fieldSpecs = createsAllFields();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return TypeSpec.classBuilder(className)
                .addFields(fieldSpecs)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE).build());
    }

    private FieldSpec createStringField(String name, String value) {
        return FieldSpec.builder(ClassName.get(String.class), name, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", value)
                .build();
    }

    private List<FieldSpec> createsAllFields() throws FileNotFoundException {
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        mKeys.forEach(key -> {
            fieldSpecs.add(createStringField(key, key));
        });

        return fieldSpecs;
    }


}
