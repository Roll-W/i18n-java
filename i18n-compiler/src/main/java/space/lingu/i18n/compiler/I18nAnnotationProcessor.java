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

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import space.lingu.i18n.I18nLocaleFile;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author RollW
 */
@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes({"space.lingu.i18n.I18nLocaleFile"})
public class I18nAnnotationProcessor extends AbstractProcessor {
    private ProcessingEnvironment processingEnvironment;

    public I18nAnnotationProcessor() {
        super();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnvironment = processingEnv;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedClasses = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element annotatedClass : annotatedClasses) {
                TypeElement classElement = (TypeElement) annotatedClass;
                I18nLocaleFile i18nLocaleFile =
                        annotatedClass.getAnnotation(I18nLocaleFile.class);

                Set<String> allKeys = new HashSet<>();
                for (String folder : i18nLocaleFile.value()) {
                    ResourcesReader resourcesReader = new ResourcesReader(
                            folder,
                            i18nLocaleFile.prefix(),
                            i18nLocaleFile.suffix());
                    try {
                        allKeys.addAll(resourcesReader.load());
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                LocaleStringsWriter writer = new LocaleStringsWriter(
                        processingEnvironment,
                        classElement,
                        allKeys
                );
                try {
                    writer.write();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return roundEnv.processingOver();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}
