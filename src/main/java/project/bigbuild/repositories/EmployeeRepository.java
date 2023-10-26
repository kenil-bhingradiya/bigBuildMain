package project.bigbuild.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.bigbuild.models.entities.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String>
{

}
