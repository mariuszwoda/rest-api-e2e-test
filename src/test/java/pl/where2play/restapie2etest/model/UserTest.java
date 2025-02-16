package pl.where2play.restapie2etest.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pl.where2play.restapie2etest.testdata.UserTestData0;
import pl.where2play.restapie2etest.testdata.UserTestData1;
import pl.where2play.restapie2etest.testdata.UserTestData4;

import static org.junit.jupiter.api.Assertions.*;

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
//        String hrUserJson = UserTestData1.userJson(UserTestData1.withDepartment("HR"));
        String hrUserJson = UserTestData1.userJson(defaultUser);
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
}