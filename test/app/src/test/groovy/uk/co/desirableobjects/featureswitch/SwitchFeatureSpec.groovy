package uk.co.desirableobjects.featureswitch

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(FeatureSwitchService)
class SwitchFeatureSpec extends Specification {

    void setup() {
        InnocentClass.metaClass.withFeature = { String feature, overrides = [:], Closure closure -> service.withFeature(feature, overrides, closure) }
        InnocentClass.metaClass.withoutFeature = { String feature, overrides = [:], Closure closure -> service.withoutFeature(feature, overrides, closure) }
    }

    @Unroll
    def 'A feature can be set to enabled = #enabled'() {
        given:
        config.features.eggs.enabled = enabled

        expect:
        service.hasFeature('eggs') == enabled

        where:
        enabled << [true, false]
    }

    def 'When there is no configuration'() {
        given:
        config.clear()

        expect:
        !service.hasFeature('peas')
    }

    def 'When a requested feature does not exist'() {
        given:
        config.features.eggs.enabled = true

        expect:
        !service.hasFeature('dogs')
    }

    def 'checking status for a feature does not add it to config'() {
        when:
        !service.hasFeature('boys')

        then:
        !config.features.boys
    }

    @Unroll
    def 'User can use withFeature in a class which is decorated with it, where feature = #enabled'() {
        given:
        config.features.eggs.enabled = enabled

        expect:
        new InnocentClass().testWith() == enabled

        where:
        enabled << [true, false]
    }

    @Unroll
    def 'User can use withoutFeature in a class which is decorated with it, where feature = #enabled'() {
        given:
        config.features.eggs.enabled = enabled

        expect:
        new InnocentClass().testWithout() == !enabled

        where:
        enabled << [true, false]
    }

    @Unroll
    def 'grailsConfiguration can be overriden enabled = #enabled' (boolean enabled) {
        given:
        config.features.eggs.enabled = !enabled

        expect:
        service.hasFeature('eggs', ['eggs': enabled]) == enabled

        and:
        new InnocentClass().testWithOverride(enabled)

        where:
        enabled << [true, false]
    }
}
