package project.bigbuild.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="department")
public class Department
{
    @Id
    String code;
    String dept;
    String description;
    String emailId;
}
