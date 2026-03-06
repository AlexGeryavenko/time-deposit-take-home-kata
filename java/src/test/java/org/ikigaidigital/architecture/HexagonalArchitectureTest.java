package org.ikigaidigital.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "org.ikigaidigital", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_application_or_adapter =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..application..", "..adapter..")
            .because("Domain layer must be independent of application and adapter layers");

    @ArchTest
    static final ArchRule application_should_not_depend_on_adapter =
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..adapter..")
            .because("Application layer must not depend on adapter layer");

    @ArchTest
    static final ArchRule adapter_in_should_not_depend_on_adapter_out =
        noClasses().that().resideInAPackage("..adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.out..")
            .because("Inbound adapters must not depend on outbound adapters");

    @ArchTest
    static final ArchRule adapter_out_should_not_depend_on_adapter_in =
        noClasses().that().resideInAPackage("..adapter.out..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.in..")
            .because("Outbound adapters must not depend on inbound adapters");

    @ArchTest
    static final ArchRule port_in_should_only_contain_interfaces =
        classes().that().resideInAPackage("..application.port.in..")
            .should().beInterfaces()
            .because("Driving ports (port.in) must be interfaces");

    @ArchTest
    static final ArchRule port_out_should_only_contain_interfaces =
        classes().that().resideInAPackage("..application.port.out..")
            .should().beInterfaces()
            .because("Driven ports (port.out) must be interfaces");

    @ArchTest
    static final ArchRule controllers_should_reside_in_adapter_in_rest_controller =
        classes().that().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
            .should().resideInAPackage("..adapter.in.rest.controller..")
            .because("REST controllers must reside in adapter.in.rest.controller");

    @ArchTest
    static final ArchRule jpa_entities_should_reside_in_adapter_out_persistence_entity =
        classes().that().areAnnotatedWith(jakarta.persistence.Entity.class)
            .should().resideInAPackage("..adapter.out.persistence.entity..")
            .because("JPA entities must reside in adapter.out.persistence.entity");

    @ArchTest
    static final ArchRule services_should_reside_in_application_service =
        classes().that().areAnnotatedWith(org.springframework.stereotype.Service.class)
            .should().resideInAPackage("..application.service..")
            .because("Spring @Service classes must reside in application.service");

    @ArchTest
    static final ArchRule repositories_should_reside_in_adapter_out =
        classes().that().areAnnotatedWith(org.springframework.stereotype.Repository.class)
            .should().resideInAPackage("..adapter.out..")
            .because("Spring @Repository classes must reside in adapter.out");

    @ArchTest
    static final ArchRule controllers_should_be_suffixed =
        classes().that().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
            .should().haveSimpleNameEndingWith("Controller")
            .because("REST controllers must end with 'Controller'");

    @ArchTest
    static final ArchRule services_should_be_suffixed =
        classes().that().areAnnotatedWith(org.springframework.stereotype.Service.class)
            .should().haveSimpleNameEndingWith("Service")
            .because("Spring services must end with 'Service'");
}
