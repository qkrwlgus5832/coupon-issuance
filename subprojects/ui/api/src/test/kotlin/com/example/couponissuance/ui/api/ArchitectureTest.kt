package com.example.couponissuance.ui.api

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class ArchitectureTest {
    lateinit var javaClasses: JavaClasses

    @BeforeEach
    fun beforeEach() {
        javaClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example.couponissuance")
    }

    @Test
    @DisplayName("Controller 패키지 내의 클래스의 이름은 'Controller'로 끝나야 한다.")
    fun controllersShouldBeNamedCorrectly() {
        val rule = ArchRuleDefinition.classes()
            .that().resideInAPackage("..controller..")
            .and().haveNameNotMatching(".*[$].*")
            .should().haveSimpleNameEndingWith("Controller")
            .allowEmptyShould(true)

        rule.check(javaClasses)
    }

    @Test
    @DisplayName("서비스 패키지 내의 클래스의 이름은 'Service'로 끝나야 한다.")
    fun servicesShouldBeNamedCorrectly() {
        val rule = ArchRuleDefinition.classes()
            .that().resideInAPackage("..service..")
            .and().haveNameNotMatching(".*[$].*")
            .should().haveSimpleNameEndingWith("Service")
            .allowEmptyShould(true)

        rule.check(javaClasses)
    }

    @Test
    @DisplayName("리포지토리 패키지 내의 클래스의 이름은 'Repository'로 끝나야 한다.")
    fun repositoriesShouldBeNamedCorrectly() {
        val rule = ArchRuleDefinition.classes()
            .that().resideInAPackage("..repository..")
            .and().haveNameNotMatching(".*[$].*")
            .should().haveSimpleNameEndingWith("Repository")
            .allowEmptyShould(true)

        rule.check(javaClasses)
    }
    
}
