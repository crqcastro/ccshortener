package architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
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
    static final ArchRule LAYERED = layeredArchitecture()
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
    static final ArchRule NO_CYCLES_BETWEEN_TOP_MODULES =
            slices().matching("br.com.cesarcastro.apis.ccshortener.(*)..")
                    .should().beFreeOfCycles();

    // --------- Regras de dependência úteis
    @ArchTest
    static final ArchRule CONTROLLER_NAO_DEPENDE_DE_REPOSITORY =
            noClasses().that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..repository..");

    @ArchTest
    static final ArchRule CONTROLLER_NAO_DEPENDE_DE_ENTITIES_DO_DOMAIN =
            noClasses().that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAnyPackage("..domain.entities..");

    @ArchTest
    static final ArchRule SERVICES_SOMENTE_ACESSADOS_POR_CONTROLLER_SERVICE_CONFIG =
            classes().that().resideInAPackage("..service..")
                    .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..", "..config..");

    @ArchTest
    static final ArchRule REPOSITORIES_SOMENTE_ACESSADOS_POR_SERVICE_CONFIG =
            classes().that().resideInAPackage("..repository..")
                    .should().onlyBeAccessed().byAnyPackage("..service..", "..config..");

    // --------- Convenções de nomes/anotações
    @ArchTest
    static final ArchRule NOMES_CONTROLLERS =
            classes().that().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                    .should().haveSimpleNameEndingWith("Controller")
                    .andShould().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule SERVICES_BEM_DEFINIDOS =
            classes().that().resideInAPackage("..service..")
                    .and().areNotInterfaces()
                    .should().beAnnotatedWith(org.springframework.stereotype.Service.class)
                    .andShould().haveSimpleNameEndingWith("Service");

    @ArchTest
    static final ArchRule REPOSITORIES_BEM_DEFINIDOS =
            classes().that().areAnnotatedWith(org.springframework.stereotype.Repository.class)
                    .or().haveSimpleNameEndingWith("Repository")
                    .should().resideInAPackage("..repository..");

    // --------- Boas práticas Spring
    // Proíbe injeção por campo (@Autowired em fields)
    @ArchTest
    static final ArchRule NO_FIELD_INJECTION =
            noFields().should().beAnnotatedWith(org.springframework.beans.factory.annotation.Autowired.class);

    // @Transactional não deve estar em controllers (classe OU métodos)
    @ArchTest
    static final ArchRule TRANSACTIONAL_NAO_EM_CONTROLLER_CLASSE =
            noClasses().that().resideInAPackage("..controller..")
                    .should().beAnnotatedWith(org.springframework.transaction.annotation.Transactional.class);

    @ArchTest
    static final ArchRule TRANSACTIONAL_NAO_EM_CONTROLLER_METODO =
            methods().that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                    .should().notBeAnnotatedWith(org.springframework.transaction.annotation.Transactional.class);
}
