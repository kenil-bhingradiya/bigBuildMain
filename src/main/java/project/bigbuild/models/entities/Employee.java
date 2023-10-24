package project.bigbuild.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name="departments")
public class Employee
{
    String name;
    @Id
    String login;
    String dept;
    Date onboarded;
}
