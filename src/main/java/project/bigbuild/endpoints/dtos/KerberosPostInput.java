package project.bigbuild.endpoints.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KerberosPostInput
{
    String username;
    String passwd;
}
