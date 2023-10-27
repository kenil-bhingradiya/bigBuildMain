package project.bigbuild.endpoints.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WindowsPostInput
{
    String name;
    String givenName;
    String surname;
    String samAccName;
    String passwd;
    String department;
    String displayName;
    String email;
}
