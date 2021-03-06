/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.app.services.freemarker.doctemplate;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.JDOHelper;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.freemarker.dom.spi.FreeMarkerTemplateLoader;
import org.isisaddons.module.freemarker.dom.spi.TemplateSource;

import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.docs.DocumentTemplateRepository;
import org.incode.module.documents.dom.types.DocumentType;
import org.incode.module.documents.dom.types.DocumentTypeRepository;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class FreeMarkerTemplateLoaderUsingDocTemplateRepository implements FreeMarkerTemplateLoader {

    @Override
    public TemplateSource templateSourceFor(final String documentTypeReference, final String atPath) {
        final DocumentType documentType = documentTypeRepository.findByReference(documentTypeReference);

        final DocumentTemplate documentTemplate = lookupDocumentTemplate(documentType, atPath);

        final String templateChars = documentTemplate.asChars();
        return new TemplateSource(templateChars, (long)JDOHelper.getVersion(documentTemplate));
    }

    private DocumentTemplate lookupDocumentTemplate(
            final DocumentType documentType, final String atPath) {
        final List<DocumentTemplate> templates = documentTemplateRepository
                .findByTypeAndApplicableToAtPathAndCurrent(documentType, atPath);
        if(templates.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format(
                            "Could not find any templates for document type ref: %s, atPath: %s",
                            documentType.getReference(), atPath));
        }
        return templates.get(0);
    }

    @Inject
    private DocumentTypeRepository documentTypeRepository;

    @Inject
    private DocumentTemplateRepository documentTemplateRepository;

}
