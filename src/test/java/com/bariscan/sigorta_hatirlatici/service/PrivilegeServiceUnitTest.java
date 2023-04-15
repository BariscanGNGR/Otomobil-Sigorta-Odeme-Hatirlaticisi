package com.bariscan.sigorta_hatirlatici.service;

import com.bariscan.sigorta_hatirlatici.entity.Privilege;
import com.bariscan.sigorta_hatirlatici.exceptions.NotFoundException;
import com.bariscan.sigorta_hatirlatici.repository.PrivilegeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PrivilegeServiceUnitTest {

    @Mock
    private PrivilegeRepository privilegeRepository;
    @InjectMocks
    private PrivilegeService privilegeService;


    @Test
    public void findByPrivilege_should_returnNull_when_ifPrivilegeHasNotCreated() {
        //given
        String name = "NULL_PRIVILEGE";

        //when
        Mockito.when(privilegeRepository.findByName(Mockito.any(String.class))).thenReturn(Optional.ofNullable(null));
        Optional<Privilege> privilege = privilegeService.findPrivilegeByName(name);

        //then
        Mockito.verify(privilegeRepository).findByName(Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(privilegeRepository);

        Assert.isTrue(privilege.isEmpty());
    }

    @Test
    public void findByPrivilege_should_returnPrivilege() {
        //given
        String name = "READ_PRIVILEGE";

        //when
        Mockito.when(privilegeRepository.findByName(Mockito.any(String.class))).thenReturn(
                Optional.ofNullable(Privilege.builder()
                        .id(2L)
                        .name("READ_PRIVILEGE")
                        .roles(null)
                        .build()));
        Optional<Privilege> privilege = privilegeService.findPrivilegeByName(name);

        //then
        Mockito.verify(privilegeRepository).findByName(Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(privilegeRepository);
        Assertions.assertAll(
                () -> Assertions.assertFalse(privilege.isEmpty()),
                () -> Assertions.assertEquals(name, privilege.get().getName())
        );
    }

    @Test
    public void createPrivilegeIfNotFound_should_createNewPrivilege() {
        //given
        String name = "READ_PRIVILEGE";

        //when
        Mockito.when(privilegeRepository.findByName(Mockito.any(String.class))).thenReturn(
                Optional.ofNullable(null));
        Mockito.when(privilegeRepository.save(Mockito.any())).thenReturn(Privilege.builder()
                .name(name)
                .id(11L)
                .build());
        Privilege privilege = privilegeService.createPrivilegeIfNotFound(name);

        //then
        Mockito.verify(privilegeRepository).findByName(Mockito.any(String.class));
        Mockito.verify(privilegeRepository).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(privilegeRepository);
        Assertions.assertAll(
                () -> Assertions.assertNotNull(privilege),
                () -> Assertions.assertEquals(name, privilege.getName())
        );
    }

    @Test
    public void createPrivilegeIfNotFound_should_foundPrivilegeAndReturned() {
        //given
        String name = "READ_PRIVILEGE";

        //when
        Mockito.when(privilegeRepository.findByName(Mockito.any(String.class))).thenReturn(
                Optional.ofNullable(null));
        Mockito.when(privilegeRepository.save(Mockito.any())).thenReturn(Privilege.builder()
                .name(name)
                .id(11L)
                .build());
        Privilege privilege = privilegeService.createPrivilegeIfNotFound(name);

        //then
        Mockito.verify(privilegeRepository).findByName(Mockito.any(String.class));
        Mockito.verify(privilegeRepository).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(privilegeRepository);
        Assertions.assertAll(
                () -> Assertions.assertNotNull(privilege),
                () -> Assertions.assertEquals(name, privilege.getName())
        );
    }

    @Test
    void deletePrivilege_should_delete() {
        //given
        String name = "READ_PRIVILEGE";

        //when
        Mockito.when(privilegeRepository.findByName(Mockito.any(String.class))).thenReturn(
                Optional.ofNullable(Privilege.builder()
                        .id(11L)
                        .name(name)
                        .build())
        );
        String result = privilegeService.deletePrivilege(name);

        //then
        Mockito.verify(privilegeRepository).delete(Mockito.any(Privilege.class));
        Mockito.verify(privilegeRepository).findByName(Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(privilegeRepository);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals("Privilege deleted.", result)
        );
    }

    @Test
    public void deletePrivilege_throwException_when_privilegeNotFound() {
        //given
        String name = "READ_PRIVILEGE";

        //when
        Mockito.when(privilegeRepository.findByName(Mockito.any(String.class))).thenReturn(
                Optional.ofNullable(null)
        );

        //then
        Throwable exception = Assertions.assertThrows(NotFoundException.class, () -> {
            privilegeService.deletePrivilege(name);
        });
        Assertions.assertEquals(exception.getMessage(), "Privilege is not found.");
        Mockito.verify(privilegeRepository).findByName(Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(privilegeRepository);

    }
}
