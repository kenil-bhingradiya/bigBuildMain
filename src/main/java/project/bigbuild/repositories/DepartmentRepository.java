package project.bigbuild.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.bigbuild.models.entities.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String>
{
}
