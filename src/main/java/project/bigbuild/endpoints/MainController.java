package project.bigbuild.endpoints;

import com.fasterxml.jackson.databind.util.JSONPObject;
import netscape.javascript.JSObject;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import project.bigbuild.endpoints.dtos.ExcelDeptData;
import project.bigbuild.endpoints.dtos.ExcelUserData;
import project.bigbuild.endpoints.dtos.WindowsPostInput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MainController
{
    @Autowired
    RestTemplate restTemplate;
    @GetMapping("/")
    public String helloWorld()
    {
        return "Hello from master system";
    }

    @PostMapping("/createUsers")
    public String createUserFromExcel(@RequestParam("file") MultipartFile excelDataFile) throws IOException {
        List<ExcelUserData> userList = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(excelDataFile.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
            ExcelUserData tempStudent = new ExcelUserData();

            XSSFRow row = worksheet.getRow(i);

            tempStudent.setName(row.getCell(0).getStringCellValue());
            tempStudent.setLogin(row.getCell(1).getStringCellValue());
            tempStudent.setOnboarded(row.getCell(2).getDateCellValue());
            tempStudent.setDept(row.getCell(3).getStringCellValue());
            userList.add(tempStudent);
        }

        String createWindowsUserUrl = "http://10.2.38.104:8080/create/newUser";
        String tmp = "http://10.2.38.104:8080/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        WindowsPostInput winIp = new WindowsPostInput("abc", "abc", "abc", "abc", "Admin123", "IT", "abc abc");


        HttpEntity<WindowsPostInput> tt = new HttpEntity<>(winIp, headers);
        String resp =  this.restTemplate.postForObject(createWindowsUserUrl, tt, String.class);

        //String resp = this.restTemplate.getForObject(tmp, String.class);

        System.out.println(resp);
        return "Users Created!!";
    }

    @PostMapping("/createDepartments")
    public String createDepartmentFromExcel(@RequestParam("file") MultipartFile excelDataFile) throws IOException {
        List<ExcelDeptData> userList = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(excelDataFile.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
            ExcelDeptData tempStudent = new ExcelDeptData();

            XSSFRow row = worksheet.getRow(i);

            tempStudent.setDept(row.getCell(0).getStringCellValue());
            tempStudent.setCode(row.getCell(1).getStringCellValue());
            tempStudent.setDescription(row.getCell(2).getStringCellValue());

            userList.add(tempStudent);
        }

        String createWindowsUserUrl = "http://10.2.38.104:8080/create/newUser";
        String tmp = "http://10.2.38.104:8080/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        WindowsPostInput winIp = new WindowsPostInput("abc", "abc", "abc", "abc", "Admin123", "IT", "abc abc");


        HttpEntity<WindowsPostInput> tt = new HttpEntity<>(winIp, headers);
        String resp =  this.restTemplate.postForObject(createWindowsUserUrl, tt, String.class);

        //String resp = this.restTemplate.getForObject(tmp, String.class);

        System.out.println(resp);
        return "Users Created!!";
    }



}
