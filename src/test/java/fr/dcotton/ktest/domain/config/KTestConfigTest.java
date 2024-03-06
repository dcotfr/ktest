package fr.dcotton.ktest.domain.config;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class KTestConfigTest {
    @Inject
    KTestConfig kConfig;

    @Test
    void test() {
        System.out.println(kConfig.registry("f4m_ind_car_registry"));
    }
}
