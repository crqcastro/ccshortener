package architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(
        packagesOf = br.com.cesarcastro.apis.ccshortener.CcshortenerApplication.class,
        importOptions = {
                com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests.class,
                com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeJars.class
        }
)
class ArchitectureTest {

    @ArchTest
    static final ArchRule layered = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Controller").definedBy("..controller..")
            .layer("Service").definedBy("..service..")
            .layer("Repository").definedBy("..repository..")
            .layer("Configuration").definedBy("..config..")
            // quem pode acessar cada layer (regras de entrada)
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Configuration").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service");

    @ArchTest
    static final ArchRule no_cycles_between_top_modules =
            slices().matching("br.com.cesarcastro.apis.ccshortener.(*)..")
                    .should().beFreeOfCycles();

    // --------- Regras de dependência úteis
    @ArchTest
    static final ArchRule controller_nao_depende_de_repository =
            noClasses().that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..repository..");

    @ArchTest
    static final ArchRule controller_nao_depende_de_entities_do_domain =
            noClasses().that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAnyPackage("..domain.entities..");

    @ArchTest
    static final ArchRule services_somente_acessados_por_controller_service_config =
            classes().that().resideInAPackage("..service..")
                    .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..", "..config..");

    @ArchTest
    static final ArchRule repositories_somente_acessados_por_service_config =
            classes().that().resideInAPackage("..repository..")
                    .should().onlyBeAccessed().byAnyPackage("..service..", "..config..");

    // --------- Convenções de nomes/anotações
    @ArchTest
    static final ArchRule nomes_controllers =
            classes().that().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                    .should().haveSimpleNameEndingWith("Controller")
                    .andShould().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule services_bem_definidos =
            classes().that().resideInAPackage("..service..")
                    .and().areNotInterfaces()
                    .should().beAnnotatedWith(org.springframework.stereotype.Service.class)
                    .andShould().haveSimpleNameEndingWith("Service");

    @ArchTest
    static final ArchRule repositories_bem_definidos =
            classes().that().areAnnotatedWith(org.springframework.stereotype.Repository.class)
                    .or().haveSimpleNameEndingWith("Repository")
                    .should().resideInAPackage("..repository..");

    // --------- Boas práticas Spring
    // Proíbe injeção por campo (@Autowired em fields)
    @ArchTest
    static final ArchRule no_field_injection =
            noFields().should().beAnnotatedWith(org.springframework.beans.factory.annotation.Autowired.class);

    // @Transactional não deve estar em controllers (classe OU métodos)
    @ArchTest
    static final ArchRule transactional_nao_em_controller_classe =
            noClasses().that().resideInAPackage("..controller..")
                    .should().beAnnotatedWith(org.springframework.transaction.annotation.Transactional.class);

    @ArchTest
    static final ArchRule transactional_nao_em_controller_metodo =
            methods().that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                    .should().notBeAnnotatedWith(org.springframework.transaction.annotation.Transactional.class);
}
