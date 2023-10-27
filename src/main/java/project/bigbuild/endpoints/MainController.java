package project.bigbuild.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.bigbuild.models.entities.Department;
import project.bigbuild.models.entities.Employee;
import project.bigbuild.services.AddDepartmentService;
import project.bigbuild.services.UserCreateService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class MainController
{
    @Autowired
    AddDepartmentService addDepartmentService;

    @Autowired
    UserCreateService userCreateService;

    @GetMapping("/")
    public String helloWorld()
    {
        return "Hello from master system";
    }

    @PostMapping("/createUsers")
    public Map<Employee, String> createUserFromExcel(@RequestParam("file") MultipartFile excelDataFile) throws Exception
    {
        return userCreateService.createNewUser(excelDataFile);
    }

    @PostMapping("/createDepartments")
    public Map<Department, String> createDepartmentFromExcel(@RequestParam("file") MultipartFile excelDataFile) throws IOException
    {
        return addDepartmentService.createNewDepartment(excelDataFile);
    }

    @GetMapping("/getAllUser")
    public List<Employee> getAllUser()
    {
        return userCreateService.getAllUser();
    }

    @PostMapping("/createOneDepartment")
    public Department createOneDepartment(@RequestBody Department department) throws IOException
    {
        return addDepartmentService.addNewDepartment(department);
    }

    @PostMapping("/createOneUser")
    public Map<Employee, String> createUserFromExcel(@RequestBody  Employee employee) throws Exception
    {
        return userCreateService.createOneUser(employee);
    }

    @PostMapping("deleteUser")
    public boolean deleteUser(@RequestBody String username)
    {
        return userCreateService.deleteUser(username);
    }
}
