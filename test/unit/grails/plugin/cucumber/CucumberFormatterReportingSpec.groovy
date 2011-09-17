/*
 * Copyright 2011 Martin Hauner
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

package grails.plugin.cucumber

import gherkin.formatter.model.Result
import junit.framework.AssertionFailedError


@SuppressWarnings("GroovyPointlessArithmetic")
class CucumberFormatterReportingSpec extends GherkinSpec {
    def report = Mock (FeatureReport)
    def uat = formatter (report)


    def "(re)-init feature report for each feature to create a feature wise log" () {
        given:
        def featureA = featureStub (FEATURE_NAME_A)
        def featureB = featureStub (FEATURE_NAME_B)

        when:
        uat.feature (featureA)
        uat.feature (featureB)

        then:
        1 * report.startFeature (FEATURE_NAME_A)
        1 * report.startFeature (FEATURE_NAME_B)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "finishes feature report before initializing a new feature report" () {
        given:
        def featureA = featureStub (FEATURE_NAME_A)
        def featureB = featureStub (FEATURE_NAME_B)

        when:
        uat.feature (featureA)
        uat.feature (featureB)

        then:
        2 * report.startFeature (_)
        1 * report.endFeature ()
    }

    def "report test start for each scenario" () {
        given:
        def scenarioA = scenarioStub (SCENARIO_NAME_A)
        def scenarioB = scenarioStub (SCENARIO_NAME_B)

        when:
        uat.scenario (scenarioA)
        uat.scenario (scenarioB)

        then:
        1 * report.startScenario (SCENARIO_NAME_A)
        1 * report.startScenario (SCENARIO_NAME_B)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "report test end for previous scenario before each new scenario" () {
        given:
        def scenarioA = scenarioStub (SCENARIO_NAME_A)
        def scenarioB = scenarioStub (SCENARIO_NAME_B)

        when:
        uat.scenario (scenarioA)
        uat.scenario (scenarioB)

        then:
        2 * report.startScenario (_)
        1 * report.endScenario ()
    }

    def "report test end for last scenario" () {
        def featureStub = featureStub (FEATURE_NAME_A)
        def scenarioStubA = scenarioStub (SCENARIO_NAME_A)

        when:
        uat.feature (featureStub)
        uat.scenario (scenarioStubA)
        uat.finish ()

        then:
        1 * report.endScenario ()
    }


    def "report end feature for last feature" () {
        def featureStub = featureStub (FEATURE_NAME_A)
        def scenarioStubA = scenarioStub (SCENARIO_NAME_A)

        when:
        uat.feature (featureStub)
        uat.scenario (scenarioStubA)
        uat.finish ()

        then:
        1 * report.endFeature ()
    }

    def "reports step failures" () {
        def result = Mock (Result)
        def failure = new AssertionFailedError ()
        result.error >> failure

        when:
        uat.step (stepStub ())
        uat.result (result)

        then:
        1 * report.addFailure (failure)
    }

    def "reports step errors" () {
        def result = Mock (Result)
        def error = new Throwable ()
        result.error >> error

        when:
        uat.step (stepStub ())
        uat.result (result)

        then:
        1 * report.addError (error)
    }

}