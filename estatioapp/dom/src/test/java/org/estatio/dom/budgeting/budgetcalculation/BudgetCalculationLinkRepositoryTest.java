/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.estatio.dom.budgeting.budgetcalculation;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.lease.LeaseTermForServiceCharge;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationLinkRepositoryTest {

    FinderInteraction finderInteraction;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    BudgetCalculationLinkRepository budgetCalculationLinkRepository;

    @Before
    public void setup() {
        budgetCalculationLinkRepository = new BudgetCalculationLinkRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected <T> T uniqueMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.UNIQUE_MATCH);
                return null;
            }

            @Override
            protected List<BudgetCalculationLink> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderInteraction.FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }

    public static class FindByLeaseTerm extends BudgetCalculationLinkRepositoryTest {

        @Test
        public void happyCase() {

            LeaseTermForServiceCharge leaseTerm = new LeaseTermForServiceCharge();
            budgetCalculationLinkRepository.findByLeaseTerm(leaseTerm);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculationLink.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByLeaseTerm");
            assertThat(finderInteraction.getArgumentsByParameterName().get("leaseTerm")).isEqualTo((Object) leaseTerm);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }

    public static class FindByBudgetCalculationAndLeaseTerm extends BudgetCalculationLinkRepositoryTest {

        @Test
        public void happyCase() {

            BudgetCalculation budgetCalculation = new BudgetCalculation();
            LeaseTermForServiceCharge leaseTerm = new LeaseTermForServiceCharge();
            budgetCalculationLinkRepository.findByBudgetCalculationAndLeaseTerm(budgetCalculation, leaseTerm);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.UNIQUE_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculationLink.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByBudgetCalculationAndLeaseTerm");
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetCalculation")).isEqualTo((Object) budgetCalculation);
            assertThat(finderInteraction.getArgumentsByParameterName().get("leaseTerm")).isEqualTo((Object) leaseTerm);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(2);
        }

    }

    public static class CreateBudgetCalculationLink extends BudgetCalculationLinkRepositoryTest {

        @Mock
        private DomainObjectContainer mockContainer;

        @Before
        public void setupData() {
            budgetCalculationLinkRepository = new BudgetCalculationLinkRepository();
            budgetCalculationLinkRepository.setContainer(mockContainer);
        }

        @Test
        public void createBudgetCalculationLink() {

            //given
            BudgetCalculation budgetCalculation = new BudgetCalculation();
            LeaseTermForServiceCharge leaseTermForServiceCharge = new LeaseTermForServiceCharge();

            BudgetCalculationLink budgetCalculationLink = new BudgetCalculationLink();

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(BudgetCalculationLink.class);
                    will(returnValue(budgetCalculationLink));
                    oneOf(mockContainer).persistIfNotAlready(budgetCalculationLink);
                }

            });

            //when
            BudgetCalculationLink newBudgetCalculationlink = budgetCalculationLinkRepository.createBudgetCalculationLink(budgetCalculation, leaseTermForServiceCharge);

            //then
            assertThat(newBudgetCalculationlink.getBudgetCalculation()).isEqualTo(budgetCalculation);
            assertThat(newBudgetCalculationlink.getLeaseTerm()).isEqualTo(leaseTermForServiceCharge);

        }
    }

}
