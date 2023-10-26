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
            String password = passwordGenerator.generatePassword(10);
            Boolean createWindowsUser = createWindowsUsers(employee, password);
            Boolean createLinuxUser = createLinuxUser(employee, password);
            Boolean createKerberosUser = createKerberosUser(employee, password);

            if(createWindowsUser && createLinuxUser && createKerberosUser)
            {
                result.put(employee, "User Created SuccessFully." + password);
            }
            else
            {
                result.put(employee, "Fail to Create User.");
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
    public void addEmployees(List<Employee> employeeList)
    {
        employeeRepo.saveAll(employeeList);
    }

    public Boolean createWindowsUsers(Employee employee, String password)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        WindowsPostInput winIp = new WindowsPostInput(employee.getName(), employee.getName(), employee.getName(), employee.getLogin(), password, employee.getDept(), employee.getName());
        System.out.println("creating windows user..");
        HttpEntity<WindowsPostInput> httpEntity = new HttpEntity<>(winIp, headers);
        String resp = this.restTemplate.postForObject(windowsServerUrl + "/create/newUser", httpEntity, String.class);

        return Objects.equals(resp, "User Created Successfully!!");
    }

    public Boolean createLinuxUser(Employee employee, String password)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        LinuxPostInput linuxIp = new LinuxPostInput(employee.getLogin(), password, employee.getDept());

        HttpEntity<LinuxPostInput> httpEntity = new HttpEntity<>(linuxIp, headers);
        System.out.println("creating linux user. " + linuxClientUrl + "/create/newLinuxUser");
        String resp = this.restTemplate.postForObject(linuxClientUrl + "/create/newLinuxUser", httpEntity, String.class);

        return Objects.equals(resp, "User Created Successfully!!");
    }

    public Boolean createKerberosUser(Employee employee, String password)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        KerberosPostInput kerberosIp = new KerberosPostInput(employee.getLogin(), password);

        HttpEntity<KerberosPostInput> httpEntity = new HttpEntity<>(kerberosIp, headers);
        String resp = this.restTemplate.postForObject(linuxServerUrl + "/create/newKerberosUser", httpEntity, String.class);

        return Objects.equals(resp, "User Created Successfully!!");
    }

    public List<Employee> getAllUser()
    {
        return employeeRepo.findAll();
    }
}
