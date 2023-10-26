package project.bigbuild.endpoints.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinuxPostInput
{
    String username;
    String passwd;
    String department;
}
