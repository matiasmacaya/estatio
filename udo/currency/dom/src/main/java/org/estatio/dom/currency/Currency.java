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
package org.estatio.dom.currency;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.WithNameUnique;
import org.estatio.dom.WithReferenceComparable;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.apptenancy.ApplicationTenancyConstants;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobal;
import org.estatio.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the {@link Currency} of some amount of money.
 * 
 * <p>
 * Used for taxes, indices, invoices, charges.
 */
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Currency_reference_UNQ", members = "reference"),
        @javax.jdo.annotations.Unique(
                name = "Currency_name_UNQ", members = "name")
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.currency.Currency "
                        + "WHERE reference == :reference"),
        @javax.jdo.annotations.Query(
                name = "matchByReferenceOrDescription", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.currency.Currency "
                        + "WHERE reference.matches(:searchArg) "
                        + "|| description.matches(:searchArg)")
})
@DomainObject(editing = Editing.DISABLED, autoCompleteRepository = CurrencyRepository.class, autoCompleteAction = "autoComplete", bounded = true)
public class Currency
        extends UdoDomainObject2<Currency>
        implements WithReferenceComparable<Currency>, WithReferenceUnique, WithNameUnique, WithApplicationTenancyGlobal {

    public Currency() {
        super("reference");
    }

    public String title() {
        return TitleBuilder.start()
                .withReference(getReference())
                .withName(getName())
                .toString();
    }

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(
                ApplicationTenancyConstants.GLOBAL_PATH);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.REFERENCE)
    @Property(regexPattern = RegexValidation.Currency.REFERENCE)
    @Getter @Setter
    private String reference;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.NAME)
    @Getter @Setter
    private String name;

}
