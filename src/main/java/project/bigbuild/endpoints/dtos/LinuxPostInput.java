package project.bigbuild.endpoints.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinuxPostInput
{
    String user;
    String passwd;
    String department;
}
