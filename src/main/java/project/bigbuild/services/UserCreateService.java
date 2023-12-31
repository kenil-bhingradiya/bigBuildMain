package project.bigbuild.services;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import project.bigbuild.endpoints.dtos.KerberosPostInput;
import project.bigbuild.endpoints.dtos.LinuxPostInput;
import project.bigbuild.endpoints.dtos.WindowsPostInput;
import project.bigbuild.models.entities.Employee;
import project.bigbuild.repositories.EmployeeRepository;

import java.io.IOException;
import java.util.*;

@Service
public class UserCreateService
{
    @Autowired
    EmployeeRepository employeeRepo;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    PasswordGenerator passwordGenerator;

    @Autowired
    EmailService emailService;
    
    @Value("${url.windowsserver}")
    private String windowsServerUrl;

    @Value("${url.linuxserver}")
    private String linuxServerUrl;

    @Value("${url.linuxclient}")
    private String linuxClientUrl;

    public Map<Employee, String> createNewUser(MultipartFile excelDataFile) throws IOException
    {
        Map<Employee, String> result = new HashMap<>();
        List<Employee> userList = convertExcelToEmployees(excelDataFile);

        for (Employee employee: userList)
        {
            if(employeeRepo.findById(employee.getLogin()).isEmpty())
            {
                String password = passwordGenerator.generatePassword(10);
                Boolean createWindowsUser = createWindowsUsers(employee, password);
                Boolean createLinuxUser = createLinuxUser(employee, password);
                Boolean createKerberosUser = createKerberosUser(employee, password);

                System.out.println("Username: - " + employee.getLogin() + " Password: - " + password);
                System.out.println(createWindowsUser + " " + createLinuxUser + " " + createKerberosUser);
                if (createWindowsUser && createLinuxUser && createKerberosUser)
                {
                    result.put(employee, "User Created SuccessFully." + password);
                    employeeRepo.save(employee);
                    emailService.sendEmail(
                            employee.getEmailId(),
                            "Username and Password",
                            "Hello " + employee.getName() + ",\n\tHere is your login id and password for linux and Windows user,\n\nLoginId: - " + employee.getLogin() + "\nPassword: - " + password + "\n\n Please reset your password on first login\n\nThank You.\nSystem Administrator - Kenil");
                } else {
                    result.put(employee, "Fail to Create User.");
                }
            }
        }

        return result;
    }

    public List<Employee> convertExcelToEmployees(MultipartFile excelDataFile) throws IOException
    {
        List<Employee> userList = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(excelDataFile.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
            Employee tempStudent = new Employee();

            XSSFRow row = worksheet.getRow(i);

            tempStudent.setName(row.getCell(0).getStringCellValue());
            tempStudent.setLogin(row.getCell(1).getStringCellValue());
            tempStudent.setOnboarded(row.getCell(2).getDateCellValue());
            tempStudent.setDept(row.getCell(3).getStringCellValue());
            tempStudent.setEmailId(row.getCell(4).getStringCellValue());
            userList.add(tempStudent);
        }
        System.out.println(userList);
        return userList;
    }

    public Boolean createWindowsUsers(Employee employee, String password)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String[] names = employee.getName().split(" ");

        // Get the first name.
        String firstName = names[0];

        // Get the last name.
        String lastName = names[names.length - 1];
        WindowsPostInput winIp = new WindowsPostInput(employee.getName(), firstName, lastName, employee.getLogin(), password, employee.getDept(), employee.getName(), employee.getEmailId());
        System.out.println("creating windows user..");
        HttpEntity<WindowsPostInput> httpEntity = new HttpEntity<>(winIp, headers);
        String resp = this.restTemplate.postForObject(windowsServerUrl + "/create/newUser", httpEntity, String.class);
        System.out.println(resp);
        return Objects.equals(resp, "User Created Successfully!!");
    }

    public Boolean createLinuxUser(Employee employee, String password)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        LinuxPostInput linuxIp = new LinuxPostInput(employee.getLogin(), password, employee.getDept());
        HttpEntity<LinuxPostInput> httpEntity = new HttpEntity<>(linuxIp, headers);
        System.out.println("creating linux user... ");
        String resp = this.restTemplate.postForObject(linuxClientUrl + "/create/newLinuxUser", httpEntity, String.class);
        System.out.println(resp);
        return Objects.equals(resp, "User Created Successfully!!");
    }

    public Boolean createKerberosUser(Employee employee, String password)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        System.out.println("creating kerberos user..");
        KerberosPostInput kerberosIp = new KerberosPostInput(employee.getLogin(), password);

        HttpEntity<KerberosPostInput> httpEntity = new HttpEntity<>(kerberosIp, headers);
        String resp = this.restTemplate.postForObject(linuxServerUrl + "/create/newKerberosUser", httpEntity, String.class);
        System.out.println(resp);
        return Objects.equals(resp, "Kerberos User Created Successfully!!");
    }

    public List<Employee> getAllUser()
    {
        return employeeRepo.findAll();
    }

    public Map<Employee, String> createOneUser(Employee employee)
    {
        Map<Employee, String> result = new HashMap<>();
        String password = passwordGenerator.generatePassword(10);
        Boolean createWindowsUser = createWindowsUsers(employee, password);
        Boolean createLinuxUser = createLinuxUser(employee, password);
        Boolean createKerberosUser = createKerberosUser(employee, password);

        System.out.println("Username: - " + employee.getLogin() + " Password: - " + password);
        System.out.println(createWindowsUser + " "+ createLinuxUser + " " + createKerberosUser);
        if(createWindowsUser && createLinuxUser && createKerberosUser)
        {
            result.put(employee, "User Created SuccessFully." + password);
            employeeRepo.save(employee);
        }
        else
        {
            result.put(employee, "Fail to Create User.");
        }
        return result;
    }

    public boolean deleteUser(String username)
    {
        employeeRepo.deleteById(username);
        return true;
    }

    public List<Employee> allEmp()
    {
        return employeeRepo.findAll();
    }
}
