package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.UserRoles;
import controller.UserPermissionsController;
import entity.CommandType;
import entity.PermissionType;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Service layer responsible for user permission management
 */
public class PermissionService {

    public static Map<CommandType, PermissionType> getDefaultPermissions(final UserRoles userRoles) {
        Map<CommandType, PermissionType> permissions = new HashMap<>();
        if (userRoles.equals(UserRoles.PARENT)) {
            permissions.put(CommandType.WINDOWS, PermissionType.ANYWHERE);
            permissions.put(CommandType.DOORS, PermissionType.ANYWHERE);
            permissions.put(CommandType.LIGHTS, PermissionType.ANYWHERE);
            permissions.put(CommandType.TEMPERATURE, PermissionType.ANYWHERE);
            permissions.put(CommandType.SECURITY, PermissionType.ANYWHERE);
            permissions.put(CommandType.HEATING, PermissionType.ANYWHERE);
        }
        if (userRoles.equals(UserRoles.CHILD)) {
            permissions.put(CommandType.WINDOWS, PermissionType.IN_LOCATION);
            permissions.put(CommandType.DOORS, PermissionType.IN_LOCATION);
            permissions.put(CommandType.LIGHTS, PermissionType.IN_LOCATION);
            permissions.put(CommandType.TEMPERATURE, PermissionType.IN_LOCATION);
            permissions.put(CommandType.SECURITY, PermissionType.IN_LOCATION);
            permissions.put(CommandType.HEATING, PermissionType.IN_LOCATION);
        }
        if (userRoles.equals(UserRoles.GUEST)) {
            permissions.put(CommandType.WINDOWS, PermissionType.IN_LOCATION);
            permissions.put(CommandType.DOORS, PermissionType.RESTRICTED);
            permissions.put(CommandType.LIGHTS, PermissionType.IN_LOCATION);
            permissions.put(CommandType.TEMPERATURE, PermissionType.RESTRICTED);
            permissions.put(CommandType.SECURITY, PermissionType.RESTRICTED);
            permissions.put(CommandType.HEATING, PermissionType.RESTRICTED);
        }
        if (userRoles.equals(UserRoles.STRANGER)) {
            permissions.put(CommandType.WINDOWS, PermissionType.RESTRICTED);
            permissions.put(CommandType.DOORS, PermissionType.RESTRICTED);
            permissions.put(CommandType.LIGHTS, PermissionType.RESTRICTED);
            permissions.put(CommandType.TEMPERATURE, PermissionType.RESTRICTED);
            permissions.put(CommandType.SECURITY, PermissionType.RESTRICTED);
            permissions.put(CommandType.HEATING, PermissionType.RESTRICTED);
        }
        return permissions;
    }

    /**
     * Imports the permissions from a JSON object
     *
     * @param object The JSONObject obtained from the txt file
     * @throws JsonProcessingException Thrown if the obtained object is not in specified permissions format
     */
    public static void importPermissions(final JSONObject object) throws JsonProcessingException {
        JSONObject jsonMap = object.getJSONObject("permissions");
        HashMap<String, Object> map = new ObjectMapper().readValue(jsonMap.toString(), HashMap.class);
        Map<String, Map<CommandType, PermissionType>> result = new HashMap<>();
        map.keySet().forEach(key -> {
            Map commandPermissions = new HashMap();
            Map userPermissions = (Map) map.get(key);
            userPermissions.keySet().forEach(command -> {
                commandPermissions.put(CommandType.valueOf(command.toString()), PermissionType.valueOf(userPermissions.get(command).toString()));
            });
            result.put(key, commandPermissions);
        });

        Map<String, Map<CommandType, PermissionType>> existingPermissions = UserPermissionsController.getUserPermissions();
        existingPermissions.putAll(result);
        UserPermissionsController.setUserPermissions(existingPermissions);
    }
}
