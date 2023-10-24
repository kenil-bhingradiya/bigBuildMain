package project.bigbuild.endpoints.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class ExcelUserData
{
    String name;
    String login;
    String dept;
    Date onboarded;
}
