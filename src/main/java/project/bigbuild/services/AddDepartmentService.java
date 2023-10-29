package project.bigbuild.services;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.bigbuild.models.entities.Department;
import project.bigbuild.repositories.DepartmentRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AddDepartmentService
{
    @Autowired
    DepartmentRepository departmentRepo;

    @Value("${url.windowsserver}")
    private String windowsServerUrl;

    @Value("${url.linuxserver}")
    private String linuxServerUrl;

    @Value("${url.linuxclient}")
    private String linuxClientUrl;

    public Map<Department, String> createNewDepartment(MultipartFile excelDataFile) throws IOException
    {
        Map<Department, String> ret = new HashMap<>();
        List<Department> departmentList = convertFromExcelToDepartment(excelDataFile);
        addDepartments(departmentList);
        departmentList.forEach(v -> ret.put(v, "User Created!"));
        return ret;
    }

    public List<Department> convertFromExcelToDepartment(MultipartFile excelDataFile) throws IOException
    {
        List<Department> departmentList = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(excelDataFile.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for(int i=1;i<worksheet.getPhysicalNumberOfRows();i++)
        {
            Department department = new Department();

            XSSFRow row = worksheet.getRow(i);

            department.setDept(row.getCell(0).getStringCellValue());
            department.setCode(row.getCell(1).getStringCellValue());
            department.setDescription(row.getCell(2).getStringCellValue());
            department.setEmailId(row.getCell(3).getStringCellValue());

            departmentList.add(department);
        }
        return departmentList;
    }
    public void addDepartments(List<Department> departmentList) throws IOException
    {
        departmentRepo.saveAll(departmentList);
    }

    public Department addNewDepartment(Department department)
    {
        return departmentRepo.save(department);
    }

    public List<Department> allDept()
    {
        return departmentRepo.findAll();
    }
}
