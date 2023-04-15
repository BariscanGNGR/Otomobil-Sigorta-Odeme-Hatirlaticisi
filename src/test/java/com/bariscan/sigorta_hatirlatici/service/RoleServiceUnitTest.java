package com.bariscan.sigorta_hatirlatici.service;

import com.bariscan.sigorta_hatirlatici.entity.Privilege;
import com.bariscan.sigorta_hatirlatici.entity.Role;
import com.bariscan.sigorta_hatirlatici.exceptions.NotFoundException;
import com.bariscan.sigorta_hatirlatici.repository.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RoleServiceUnitTest {

    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private RoleService roleService;

    @Test
    public void createRoleIfNotFound_should_createNewRole() {
        //given
        String roleName = "ROLE_USER";
        String privilegeName = "READ_PRIVILEGE", privilegeName2 = "WRITE_PRIVILEGE";

        List<Privilege> privileges = List.of(
                Privilege.builder()
                        .id(1L)
                        .name(privilegeName)
                        .roles(null)
                        .build(),
                Privilege.builder()
                        .id(2L)
                        .name(privilegeName2)
                        .roles(null)
                        .build()
        );

        //when
        Mockito.when(roleRepository.findByName(Mockito.any(String.class))).thenReturn(Optional.ofNullable(null));
        Mockito.when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(new Answer<Role>() {
            @Override
            public Role answer(InvocationOnMock invocationOnMock) throws Throwable {
                Role role = invocationOnMock.getArgument(0);
                role.setId(1L);
                return role;
            }
        });
        Role role = roleService.createRoleIfNotFound(roleName, privileges);

        //then
        Mockito.verify(roleRepository).findByName(Mockito.any(String.class));
        Mockito.verify(roleRepository).save(Mockito.any(Role.class));
        Mockito.verifyNoMoreInteractions(roleRepository);

        Assertions.assertAll(
                () -> Assertions.assertEquals(roleName, role.getName()),
                () -> Assertions.assertEquals(1L, role.getId()),
                () -> Assertions.assertEquals(2, role.getPrivileges().size())
        );
    }

    @Test
    public void createRoleIfNotFound_should_foundRoleAndReturned() {
        //given
        String roleName = "ROLE_USER";
        String privilegeName = "READ_PRIVILEGE", privilegeName2 = "WRITE_PRIVILEGE";

        List<Privilege> privileges = List.of(
                Privilege.builder()
                        .id(1L)
                        .name(privilegeName)
                        .roles(null)
                        .build(),
                Privilege.builder()
                        .id(2L)
                        .name(privilegeName2)
                        .roles(null)
                        .build()
        );

        //when
        Mockito.when(roleRepository.findByName(Mockito.any(String.class))).thenReturn(Optional.ofNullable(Role.builder()
                .id(1L)
                .name(roleName)
                .privileges(privileges)
                .build()));
        Role role = roleService.createRoleIfNotFound(roleName, privileges);

        //then
        Mockito.verify(roleRepository).findByName(Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(roleRepository);

        Assertions.assertAll(
                () -> Assertions.assertEquals(roleName, role.getName()),
                () -> Assertions.assertEquals(1L, role.getId()),
                () -> Assertions.assertEquals(2, role.getPrivileges().size())
        );
    }

    @Test
    public void findByRole_should_returnNull_when_ifHasNotCreatedRole(){
        //given
        String roleName = "ROLE_USER";

        //when
        Mockito.when(roleRepository.findByName(Mockito.any(String.class))).thenReturn(Optional.ofNullable(null));
        Optional<Role> role = roleService.findRoleByName(roleName);

        //then
        Mockito.verify(roleRepository).findByName(Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(roleRepository);

        Assertions.assertTrue(role.isEmpty());
    }

    @Test
    public void findByRole_should_returnRole(){
        //given
        String roleName = "ROLE_USER";
        String privilegeName = "READ_PRIVILEGE", privilegeName2 = "WRITE_PRIVILEGE";

        List<Privilege> privileges = List.of(
                Privilege.builder()
                        .id(1L)
                        .name(privilegeName)
                        .roles(null)
                        .build(),
                Privilege.builder()
                        .id(2L)
                        .name(privilegeName2)
                        .roles(null)
                        .build()
        );

        //when
        Mockito.when(roleRepository.findByName(Mockito.any(String.class))).thenReturn(Optional.ofNullable(Role.builder()
                .id(1L)
                .name(roleName)
                .privileges(privileges)
                .build()));
        Optional<Role> role = roleService.findRoleByName(roleName);

        //then
        Mockito.verify(roleRepository).findByName(Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(roleRepository);

        Assertions.assertAll(
                () -> Assertions.assertEquals(roleName, role.get().getName()),
                () -> Assertions.assertEquals(1L, role.get().getId()),
                () -> Assertions.assertEquals(2, role.get().getPrivileges().size())
        );
    }

    @Test
    public void deleteRole_should_delete(){
        //given
        String roleName = "ROLE_USER";
        String privilegeName = "READ_PRIVILEGE", privilegeName2 = "WRITE_PRIVILEGE";

        List<Privilege> privileges = List.of(
                Privilege.builder()
                        .id(1L)
                        .name(privilegeName)
                        .roles(null)
                        .build(),
                Privilege.builder()
                        .id(2L)
                        .name(privilegeName2)
                        .roles(null)
                        .build()
        );

        //when
        Mockito.when(roleRepository.findByName(Mockito.any(String.class))).thenReturn(Optional.ofNullable(Role.builder()
                .id(1L)
                .name(roleName)
                .privileges(privileges)
                .build()));
        roleService.deleteRole(roleName);

        //then
        Mockito.verify(roleRepository).findByName(Mockito.any(String.class));
        Mockito.verify(roleRepository).delete(Mockito.any(Role.class));
        Mockito.verifyNoMoreInteractions(roleRepository);
    }

    @Test
    public void deleteRole_should_throwException_when_roleNotFound(){
        //given
        String roleName = "ROLE_USER";

        //when
        Mockito.when(roleRepository.findByName(Mockito.any(String.class))).thenReturn(
                Optional.ofNullable(null));


        //then
        Throwable exception = Assertions.assertThrows(NotFoundException.class,
                () -> roleService.deleteRole(roleName)
        );
        Assertions.assertEquals(exception.getMessage(), "Role is not found.");
        Mockito.verify(roleRepository).findByName(Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(roleRepository);
    }


}
