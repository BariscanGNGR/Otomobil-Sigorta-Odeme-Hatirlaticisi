package com.bariscan.sigorta_hatirlatici.service;

import com.bariscan.sigorta_hatirlatici.dto.userDtos.ChangePassDto;
import com.bariscan.sigorta_hatirlatici.dto.userDtos.UserDto;
import com.bariscan.sigorta_hatirlatici.entity.User;
import com.bariscan.sigorta_hatirlatici.exceptions.NotFoundException;
import com.bariscan.sigorta_hatirlatici.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Optional;
import java.util.Set;


@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<CharSequence> charArgumentCaptor;
    @Captor
    ArgumentCaptor<String> stringArgumentCaptor, stringArgumentCaptor2;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @CsvSource({"Bariscan,Gungor,barisan@test.com,123456789",
            "testFirstName,testLastName,testEmail@mail.com,123124151",
            "testname,test,mail@gmail.com,11111111"})
    public void createUser_should_createUser(ArgumentsAccessor argumentsAccessor) {
        //given
        UserDto userDto = UserDto.builder()
                .firstName(argumentsAccessor.getString(0))
                .lastName(argumentsAccessor.getString(1))
                .email(argumentsAccessor.getString(2))
                .password(argumentsAccessor.getString(3))
                .build();

        User excepted = User.builder()
                .id(null)
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password("hashedPass:" + userDto.getPassword())
                .roles(null)
                .enabled(true)
                .build();

        //when
        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn(excepted.getPassword());
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(excepted);

        User actual = userService.createNewUser(userDto);


        //then
        Mockito.verify(passwordEncoder).encode(charArgumentCaptor.capture());
        Mockito.verify(userRepository).save(userArgumentCaptor.capture());

        Mockito.verifyNoMoreInteractions(passwordEncoder, userRepository);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(actual),
                () -> Assertions.assertTrue(actual.equals(userArgumentCaptor.getValue())),
                () -> Assertions.assertEquals(userArgumentCaptor.getValue().getPassword(), excepted.getPassword()),
                () -> Assertions.assertEquals(actual, excepted)
        );

    }

    @ParameterizedTest
    @CsvSource({"123456789,987654321,987654321",
            "12a1sd3a1sda,a5s4d8asd89a4sd8,a5s4d8asd89a4sd8",
            "asdfgh,qwertyu,qwertyu"})
    public void changePassword_success(ArgumentsAccessor argumentsAccessor) {
        //given
        String oldPassword = argumentsAccessor.getString(0);
        String newPassword = argumentsAccessor.getString(1);
        String newPasswordAgain = argumentsAccessor.getString(2);
        User user = User.builder()
                .id(1L)
                .firstName("testName")
                .lastName("testLastName")
                .email("testMail@test.com")
                .password(oldPassword)
                .build();

        ChangePassDto changePassDto = ChangePassDto.builder()
                .id(1L)
                .password(oldPassword)
                .newPass(newPassword)
                .newPassAgain(newPasswordAgain)
                .build();

        //when
        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn("hashedPass:" + newPassword);
        Mockito.when(passwordEncoder.matches(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.ofNullable(user));

        String returnMessage = userService.changePassword(changePassDto);

        //then
        Mockito.verify(passwordEncoder).encode(charArgumentCaptor.capture());
        Mockito.verify(userRepository).save(userArgumentCaptor.capture());
        Mockito.verify(userRepository).findById(longArgumentCaptor.capture());
        Mockito.verify(passwordEncoder).matches(stringArgumentCaptor.capture(), stringArgumentCaptor2.capture());
        Mockito.verifyNoMoreInteractions(passwordEncoder, userRepository);

        Assertions.assertAll(
                () -> Assertions.assertEquals("Password changed.", returnMessage),
                () -> Assertions.assertEquals("hashedPass:" + newPassword, userArgumentCaptor.getValue().getPassword()),
                () -> Assertions.assertEquals(longArgumentCaptor.getValue(), changePassDto.getId()),
                () -> Assertions.assertEquals(stringArgumentCaptor.getValue(), charArgumentCaptor.getValue())
        );
    }

    @ParameterizedTest
    @CsvSource({"11", "12", "13"})
    public void changePassword_throws_NotFoundException(ArgumentsAccessor argumentsAccessor) {
        //given
        Long id = argumentsAccessor.getLong(0);
        ChangePassDto changePassDto = ChangePassDto.builder()
                .id(id)
                .build();

        //when
        Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.ofNullable(null));
        Throwable exception = Assertions.assertThrows(NotFoundException.class, () -> userService.changePassword(changePassDto));

        //then
        Mockito.verify(userRepository).findById(Mockito.any());
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    //TODO : changePassword, new password is not same with old password

    @ParameterizedTest
    @CsvSource({"123456789,98765432221,987654321",
            "12a1sd3a1sda,a5s4d8asd89a4sd8,a5s4d8asd89a4sd1",
            "asdfgh,qwertyu,qwert1yu"})
    public void changePassword_newPassAndNewPassAgainAreNotSame(ArgumentsAccessor argumentsAccessor) {
        //given
        String oldPassword = argumentsAccessor.getString(0);
        String newPassword = argumentsAccessor.getString(1);
        String newPasswordAgain = argumentsAccessor.getString(2);
        User user = User.builder()
                .id(1L)
                .firstName("testName")
                .lastName("testLastName")
                .email("testMail@test.com")
                .password(oldPassword)
                .build();

        ChangePassDto changePassDto = ChangePassDto.builder()
                .id(1L)
                .password(oldPassword)
                .newPass(newPassword)
                .newPassAgain(newPasswordAgain)
                .build();

        //when
        Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.ofNullable(user));
        String returnMessage = userService.changePassword(changePassDto);

        //then
        Mockito.verify(userRepository).findById(longArgumentCaptor.capture());
        Mockito.verifyNoMoreInteractions(userRepository);

        Assertions.assertAll(
                ()-> Assertions.assertEquals("Passwords are not same.", returnMessage),
                () -> Assertions.assertEquals(longArgumentCaptor.getValue(), changePassDto.getId())
        );
    }
}
