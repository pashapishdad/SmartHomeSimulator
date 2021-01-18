package service;

import constants.UserRoles;
import org.apache.commons.lang3.EnumUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoleServiceTest {

    @Test
    public void should_allow_valid_role_value() {
        assertTrue(EnumUtils.isValidEnum(UserRoles.class, "GUEST"));
    }

    @Test
    public void should_not_allow_non_role_value() {
        assertFalse(EnumUtils.isValidEnum(UserRoles.class, "incorrect"));
    }

    @Test
    public void should_not_allow_role_with_incorrect_capitalization() {
        assertFalse(EnumUtils.isValidEnum(UserRoles.class, "Guest"));
    }
}
