package org.kariioke.authenticationservice.config;

import lombok.RequiredArgsConstructor;
import org.kariioke.authenticationservice.model.Permission;
import org.kariioke.authenticationservice.model.Role;
import org.kariioke.authenticationservice.repository.PermissionRepository;
import org.kariioke.authenticationservice.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    private static final List<String> ALL_PERMISSIONS = List.of(
            "CREATE_ORDER", "VIEW_OWN_ORDERS", "VIEW_ALL_ORDERS", "CANCEL_ORDER", "UPDATE_ORDER_STATUS",
            "VIEW_INVENTORY", "ADD_INVENTORY", "UPDATE_INVENTORY", "DELETE_INVENTORY"
    );

    private static final Map<String, Set<String>> ROLE_PERMISSIONS = Map.of(
            "ADMIN", Set.of(
                    "CREATE_ORDER", "VIEW_OWN_ORDERS", "VIEW_ALL_ORDERS", "CANCEL_ORDER", "UPDATE_ORDER_STATUS",
                    "VIEW_INVENTORY", "ADD_INVENTORY", "UPDATE_INVENTORY", "DELETE_INVENTORY"
            ),
            "CUSTOMER", Set.of(
                    "CREATE_ORDER", "VIEW_OWN_ORDERS", "CANCEL_ORDER",
                    "VIEW_INVENTORY"
            ),
            "WAREHOUSE_MANAGER", Set.of(
                    "UPDATE_ORDER_STATUS",
                    "VIEW_INVENTORY", "ADD_INVENTORY", "UPDATE_INVENTORY"
            )
    );

    @Override
    public void run(String... args) {
        ALL_PERMISSIONS.forEach(name -> {
            if (permissionRepository.findByName(name).isEmpty()) {
                Permission p = new Permission();
                p.setName(name);
                permissionRepository.save(p);
            }
        });

        ROLE_PERMISSIONS.forEach((roleName, permNames) -> {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Set<Permission> permissions = permNames.stream()
                        .map(n -> permissionRepository.findByName(n).orElseThrow())
                        .collect(Collectors.toSet());
                Role role = new Role();
                role.setName(roleName);
                role.setPermissions(permissions);
                roleRepository.save(role);
            }
        });
    }
}
