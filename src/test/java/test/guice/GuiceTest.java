package test.guice;

import static org.assertj.core.api.Assertions.assertThat;
import org.testng.TestNG;
import org.testng.annotations.Test;

import org.testng.xml.XmlSuite;
import test.SimpleBaseTest;
import test.guice.issue2343.Person;
import test.guice.issue2343.SampleA;
import test.guice.issue2343.SampleB;
import test.guice.issue2355.AnotherParentModule;
import test.guice.issue2343.modules.ParentModule;

public class GuiceTest extends SimpleBaseTest {

  @Test
  public void guiceTest() {
    TestNG tng = create(Guice1Test.class, Guice2Test.class);
    Guice1Test.m_object = null;
    Guice2Test.m_object = null;
    tng.run();

    assertThat(Guice1Test.m_object).isNotNull();
    assertThat(Guice2Test.m_object).isNotNull();
    assertThat(Guice1Test.m_object).isEqualTo(Guice2Test.m_object);
  }

  @Test
  public void guiceWithNoModules() {
    TestNG tng = create(GuiceNoModuleTest.class);
    tng.run();
  }

  @Test(description = "GITHUB-2199")
  public void guiceWithExternalDependencyInjector() {
    TestNG testng = create(Guice1Test.class);
    testng.setInjectorFactory((stage, modules) -> new FakeInjector());
    testng.run();
    assertThat(FakeInjector.getInstance()).isNotNull();
  }

  @Test(description = "GITHUB-2343")
  public void ensureInjectorsAreReUsed() {
    XmlSuite suite = createXmlSuite("sample_suite", "sample_test", SampleA.class, SampleB.class);
    suite.setParentModule(ParentModule.class.getCanonicalName());
    TestNG testng = create(suite);
    testng.run();
    assertThat(Person.counter).isEqualTo(1);
  }

  @Test(description = "GITHUB-2355")
  public void ensureMultipleInjectorsAreNotCreated() {
    Person.counter = 0;
    XmlSuite suite = createXmlSuite("sample_suite", "sample_test", SampleA.class, SampleB.class);
    suite.setParentModule(AnotherParentModule.class.getCanonicalName());
    TestNG testng = create(suite);
    testng.run();
    assertThat(AnotherParentModule.getCounter()).isEqualTo(1);
    assertThat(Person.counter).isEqualTo(1);
  }
}
