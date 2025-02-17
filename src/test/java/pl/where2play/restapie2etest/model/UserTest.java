package pl.where2play.restapie2etest.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pl.where2play.restapie2etest.testdata.*;

import static org.junit.jupiter.api.Assertions.*;
import static pl.where2play.restapie2etest.testdata.UserTestDataStatic.adminJson;
import static pl.where2play.restapie2etest.testdata.UserTestDataStatic.withDepartment;

@Slf4j
class UserTest {

    @Test
    void testUser0() {
        //given
        User defaultUser = UserTestData0.user();
        User customUser = UserTestData0.user(u -> {
            u.setEmail("custom@email.com");
            u.setRole("ADMIN");
        });
        User adminUser = UserTestData0.admin();
        User hrUser = UserTestData0.withDepartment("HR");
        User emailUser = UserTestData0.withEmail("test@example.com");

        log.info("defaultUser: {}", defaultUser);
        log.info("customUser: {}", customUser);
        log.info("adminUser: {}", adminUser);
        log.info("hrUser: {}", hrUser);
        log.info("emailUser: {}", emailUser);

        //when
        //then
        assertNull(defaultUser.getAddress());
        assertEquals("HR", hrUser.getDepartment());
    }

    @Test
    void testUser1() {
        //given
        User defaultUser = UserTestData1.user();
        User customUser = UserTestData1.user(u -> {
            u.setEmail("custom@email.com");
            u.setRole("ADMIN");
        });
        User adminUser = UserTestData1.admin();
        User hrUser = UserTestData1.withDepartment("HR");

        String userJson = UserTestData1.defaultUserJson();
        String hrUserJson = UserTestData1.userJson(UserTestData1.withDepartment("HR"));
//        String hrUserJson = UserTestData1.userJson(defaultUser);
        String adminJson = UserTestData1.adminUserJson();

        log.info("defaultUser: {}", defaultUser);
        log.info("customUser: {}", customUser);
        log.info("adminUser: {}", adminUser);
        log.info("hrUser: {}", hrUser);
        log.info("hrUserJson: {}", hrUserJson);
        log.info("adminJson: {}", adminJson);

        //when
        //then
        assertNull(defaultUser.getAddress());
        assertTrue( hrUserJson.contains("department") );
    }

    @Test
    void testUser4() {
        //given
        UserTestData4 userTestData4 = new UserTestData4();

        User adminUser = userTestData4.withDepartment("HR");
        User hrUser = userTestData4.withDepartment("HR");
        User defaultUser = userTestData4.defaultUser();

        String userJson = userTestData4.defaultUserJson();
//        String hrUserJson = userTestData4.userJson(UserTestData4.withDepartment("HR"));
//        String hrUserJson = userTestData4.userJson(defaultUser);
//        String adminJson = userTestData4.adminUserJson();

//        log.info("defaultUser: {}", defaultUser);
//        log.info("customUser: {}", customUser);
        log.info("defaultUser: {}", defaultUser);
        log.info("adminUser: {}", adminUser);
        log.info("hrUser: {}", hrUser);
        log.info("userJson: {}", userJson);
//        log.info("adminJson: {}", adminJson);

        //when
        //then
        assertNull(adminUser.getAddress());
        assertTrue( hrUser.getDepartment().contains("HR") );
    }

    @Test
    void testStatuc() {
        //given

        User adminUser = withDepartment("HR");
        String json = adminJson();

        log.info("adminUser: {}", adminUser);
        log.info("json: {}", json);

        //when
        //then
        assertNull(adminUser.getAddress());
    }

        @Test
        void createBasicUser() {
            User user = TestData.build(User.class, u -> {});
            // Test with default user
        }

        @Test
        void createAdminUser() {
            User admin = TestData.build(User.class, u -> {
                u.setRole("ADMIN");
                u.setEmail("admin@example.com");
            });
        }

//    public class ProductControllerTest {
//        @Test
//        void createDiscountedProduct() {
//            Product product = TestData.build(Product.class, p -> {
//                p.setPrice(49.99);
//                p.setName("Spring Framework Masterclass");
//            });
//        }
//    }

//    // In OrderControllerTest
//    Order order = TestData.build(Order.class, o -> {
//        o.setUser(TestData.build(User.class));  // Reuse user creation
//        o.setProduct(TestData.build(Product.class));
//    });

//    Compile-time checks through generics:
//    TestData.build(User.class)  // Returns User type
//            TestData.build(Product.class)  // Returns Product type


    // In tests:
    User user = TestData.userTemplate()
            .firstName("John")
            .build();

    // In test:
//    Order order = TestData.createOrderWithUser(u ->
//            u.setEmail("special.user@domain.com")
//    );
}