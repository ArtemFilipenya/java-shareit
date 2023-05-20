package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void userDtoTest() throws Exception {
        UserDto userDto = new UserDto(
                1L,
                "Maggie",
                "maggie@mail.com");

        JsonContent<UserDto> jsonContent = json.write(userDto);
        assertThat(jsonContent).extractingJsonPathStringValue("$.email")
                .isEqualTo(userDto.getEmail());
        assertThat(jsonContent).extractingJsonPathStringValue("$.name")
                .isEqualTo(userDto.getName());
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(userDto.getId().intValue());
    }
}