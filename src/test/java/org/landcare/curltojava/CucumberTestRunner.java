package org.landcare.curltojava;

import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@Cucumber.Options(format = {/*"pretty",*/ /*"json:target/cucumber.json",*/ "html:build/cucumber-html-report"})
public class CucumberTestRunner {}