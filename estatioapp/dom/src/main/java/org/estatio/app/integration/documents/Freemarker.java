/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.app.integration.documents;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;

import org.incode.module.documents.dom.docs.DocumentRepository;
import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.rendering.RendererToChars;
import org.incode.module.documents.dom.types.DocumentType;

import freemarker.template.TemplateException;

public class Freemarker implements RendererToChars {

    @Override
    public String renderToChars(
            final DocumentTemplate documentTemplate, final Object dataModel)
            throws IOException {

        final DocumentType documentType = documentTemplate.getType();
        final String typeRef = documentType.getReference();
        final String atPath = documentTemplate.getAtPath();

        try {
            return freeMarkerService.render(typeRef, atPath, dataModel);
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }

    @Inject
    private DocumentRepository documentRepository;
    @Inject
    private ClockService clockService;
    @Inject
    private FreeMarkerService freeMarkerService;

}
