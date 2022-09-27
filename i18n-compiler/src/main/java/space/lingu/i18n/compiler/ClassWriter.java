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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.IOException;

/**
 * @author RollW
 */
public abstract class ClassWriter {
    private final ProcessingEnvironment environment;
    private final ClassName className;

    public ClassWriter(ProcessingEnvironment environment, ClassName className) {
        this.environment = environment;
        this.className = className;
    }

    protected abstract TypeSpec.Builder builder();

    public void write() throws IOException {
        TypeSpec.Builder builder = builder();
        addGenerated(builder);
        addSuppressWarnings(builder);
        addNote(builder);

        JavaFile.builder(className.packageName(), builder.build())
                .addFileComment(NOTE_GENERATE)
                .addFileComment("\n")
                .addFileComment(NOTE_DO_NOT_MODIFY)
                .indent("    ")// four spaces
                .build()
                .writeTo(environment.getFiler());
    }

    private static final String JDK_VERSION = System.getProperty("java.version");

    private void addSuppressWarnings(TypeSpec.Builder builder) {
        AnnotationSpec anno = AnnotationSpec.builder(SuppressWarnings.class)
                .addMember("value", "{$S, $S}", "unchecked", "deprecation")
                .build();
        builder.addAnnotation(anno);
    }

    static final String NOTE_GENERATE = "This File is Generated by Lingu I18n";
    static final String NOTE_DO_NOT_MODIFY = "Note: Do not modify the generated file.";


    private void addGenerated(TypeSpec.Builder builder) {
        ClassName generated;
        if (getJdkVersion() < 9) {
            generated = ClassName.get("javax.annotation", "Generated");
        } else {
            generated = ClassName.get("javax.annotation.processing", "Generated");
        }
        AnnotationSpec anno = AnnotationSpec.builder(generated)
                .addMember("value", "$S",
                        I18nAnnotationProcessor.class.getCanonicalName())
                .build();
        builder.addAnnotation(anno);
    }

    private void addNote(TypeSpec.Builder builder) {
        builder.addJavadoc("This class exists to help with the difficulty of managing keys when there are too many strings in the internationalization. \n<p>\n" + NOTE_DO_NOT_MODIFY);
    }

    private static int getJdkVersion() {
        // higher than 11 we don't take consideration of it
        if (JDK_VERSION.contains("1.7.")) {
            return 7;
        }
        if (JDK_VERSION.contains("1.8.")) {
            return 8;
        }
        if (JDK_VERSION.startsWith("9.")) {
            return 9;
        }
        if (JDK_VERSION.startsWith("10.")) {
            return 10;
        }
        if (JDK_VERSION.startsWith("11.")) {
            return 11;
        }

        return 12;
    }

    public ProcessingEnvironment getEnvironment() {
        return environment;
    }

    public ClassName getClassName() {
        return className;
    }
}