import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.Scanner;



//Date,Time,From,To,People,Purpose,Driver

//CRUD Volunteer and Info
//Last Date Recorded For Each Volunteer
//Individual Volunteer Records, total Records

public class Driver {
    private static String VolunteerNameFile = "Volunteers";
    private static String LastestVolunteerDateFile = "LatestDate";
    private static String PersonalInfoFile = "PersonalInfo";
    private static String PersonalRecordFile = "PersonalRecord";

    private static String PersonalRecordHeader = "Date,Time,From,To,People,Purpose,Driver";
    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        System.out.println("欢迎使用行车记录仪2.0");
        while(true)
        {
            System.out.println("A-智能填写，B-手动填写，C-添加志愿者，D-删除志愿者，E-查看志愿者, Z-退出");
            InitialiseSystem();
            String Input = GetUserInput();
            if (Input.equalsIgnoreCase("A")) {

            }

            if(Input.equalsIgnoreCase("B"))
            {
                ManualFillIn();
            }
    
            if (Input.equalsIgnoreCase("C")) {
                AddVolunteer();
            }

            if(Input.equalsIgnoreCase("D"))
            {
                DeleteVolunteer();
            }

            if(Input.equalsIgnoreCase("E"))
            {
                ListVolunteers();
            }
    
            if(Input.equalsIgnoreCase("Z"))
            {
                break;
            }
        }


    }

    private static void ManualFillIn() throws Exception {
        System.out.println("志愿者的名字是什么？");
        String name = GetUserInput();
        if(!VolunteerExists(name))
        {
            System.out.println("没有这个志愿者");
            return;
        }
        System.out.println("请输入他的行程，格式为：Date(2019-01-01),Time(15:30),From,To,People(Saparate with &),Purpose,Driver");
        String Input = GetUserInput();
        String[] data = Input.split(",");
        LocalDate date = GetDateFromUserString(data[0]);
        if(IsWeekend(date))
        {
            System.out.println("这一天是周末！");
            return;
        }
        AppendToPersonalRecord(name,Input);
        AppendToAggregateRecords(name, Input);
        UpdateLastestDate(name,date);
    }

    //Date,Time,From,To,People,Purpose,Driver

    private static void UpdateLastestDate(String VolunteerName, LocalDate date) throws IOException {
        ArrayList<String> file = ReadLinesFromFile(GetPersonalCSVFilePath(VolunteerName, LastestVolunteerDateFile));

        if(file.size() == 1)
        {
            AppendToCSVFile(GetPersonalCSVFilePath(VolunteerName, LastestVolunteerDateFile), LocalDateToString(date));
        }
        else
        {
            String LastDateStr = file.get(file.size()-1);
            LocalDate LastDate = DateStrToLocalDate(LastDateStr.split("-"));
            if (date.isAfter(LastDate))
            {
                RemoveRowContainsKeyFromFile(GetPersonalCSVFilePath(VolunteerName, LastestVolunteerDateFile), LastDateStr);
                AppendToCSVFile(GetPersonalCSVFilePath(VolunteerName, LastestVolunteerDateFile), LocalDateToString(date));
            }
        }
    }

    private static void AppendToAggregateRecords(String name, String UserInput) throws Exception {

        String FileName = GetFileNameForAggregateRecord(UserInput);
        CreateFileIfNotExist(FileName, PersonalRecordHeader);
        AppendToCSVFile(FileName, UserInput);
    }

    private static String GetFileNameForAggregateRecord(String userInput) {
        
        try
        {
            String[] data = userInput.split(",");
            LocalDate date = GetDateFromUserString(data[0]);
            int DaysFromMonday = date.getDayOfWeek().getValue()- 1;
            int DaysTillFriday = 5 - date.getDayOfWeek().getValue();
            LocalDate start = date.minusDays(DaysFromMonday);
            LocalDate end = date.plusDays(DaysTillFriday);
            return GetCSVFileName(LocalDateToString(start) + " "+ LocalDateToString(end));
            
        }
        catch(Exception e)
        {
            System.out.println("不能转换成日期或者日期是周末");
            return null;        
        }
    }

    private static LocalDate GetDateFromUserString(String dateString) throws Exception {
        String[] StrDate = dateString.split("-");
        LocalDate date = DateStrToLocalDate(StrDate);
        return date;
    }

    private static LocalDate DateStrToLocalDate(String[] StrDate) {
        LocalDate date = LocalDate.of(Integer.parseInt(StrDate[0]), Integer.parseInt(StrDate[1]) , Integer.parseInt(StrDate[2]));
        return date;
    }

    private static String LocalDateToString(LocalDate date)
    {
        return date.getYear()+"-"+date.getMonthValue()+"-"+date.getDayOfMonth();
    }

    private static boolean IsWeekend(LocalDate date) {
        if(date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7)
        {return true;}
        return false;
    }

    private static void AppendToPersonalRecord(String name, String input) throws IOException {
        String path = GetPersonalCSVFilePath(name, PersonalRecordFile);
        CreateFileIfNotExist(path, PersonalRecordHeader);
        AppendToCSVFile(path, input);
    }


    private static boolean VolunteerExists(String VolunteerName) throws IOException {
        ArrayList<String> names = ReadLinesFromFile(GetCSVFileName(VolunteerNameFile));
        return names.contains(VolunteerName);
    }

    private static void ListVolunteers() throws IOException {
        ArrayList<String> names = ReadLinesFromFile(GetCSVFileName(VolunteerNameFile));
        if(names.size()==1)
        {
            System.out.println("没有志愿者");
            return;
        }
        names.remove(0);
        System.out.print("志愿者有： "+String.join(",", names)+System.lineSeparator()); 
    }

    private static void DeleteVolunteer() throws IOException {
        System.out.println("志愿者英文名字是什么？");
        String VolunteerName = GetUserInput();
        DeleteDirRecursiveIfExist(VolunteerName);
        RemoveRowContainsKeyFromFile(GetCSVFileName(VolunteerNameFile),VolunteerName);
    }

    private static void RemoveRowContainsKeyFromFile(String fileName, String key) throws IOException {
        ArrayList<String> originalFile = ReadLinesFromFile(fileName);
        String Builder = "";
        originalFile.remove(key);

        for(int i = 0;i<originalFile.size();i++ )
        {
            String row = originalFile.get(i);
            if((i != originalFile.size() -1))
            {
                Builder += row+System.lineSeparator();
            }
            else
            {
                Builder += row;
            }
        }
        WriteToFile(fileName, Builder);
    }

    private static void DeleteDirRecursiveIfExist(String Path) {
        File folder = new File(Path);
        File[] allContents = folder.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                DeleteDirRecursiveIfExist(file.getPath());
            }
        }

        if (!folder.delete())
        {
            System.out.println("无法删除文件夹");
        }

	}

	private static ArrayList<String> ReadLinesFromFile(String FileName) throws IOException
    {
        ArrayList<String> res = new ArrayList<String>();

        try (BufferedReader br = new BufferedReader(new FileReader(FileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.equals(""))
                {
                    res.add(line);
                }   
            }
            br.close();
        }
        return res;
    }
    
    private static void AddVolunteer() throws IOException {
        System.out.println("志愿者英文名字是什么？");
        String VolunteerName = GetUserInput();
        CreateDirIfNotExistFor(VolunteerName);
        AddVolunteerToSystem(VolunteerName);                
	}

    private static void AddVolunteerToSystem(String VolunteerName) throws IOException {
        if (VolunteerAlreadyInSystem(VolunteerName))
        {
            System.out.println("不能添加志愿者，他已经在系统内");
            return;
        }
        AppendToCSVFile(GetCSVFileName(VolunteerNameFile),VolunteerName);
        CreateFileIfNotExist(GetPersonalCSVFilePath(VolunteerName, LastestVolunteerDateFile), "LastDate");
        CreateFileIfNotExist(GetPersonalCSVFilePath(VolunteerName, PersonalInfoFile), "DayOfWeek,Time,From,To,Purpose,Driver");
    }

    private static boolean VolunteerAlreadyInSystem(String VolunteerName) throws IOException
    {
        ArrayList<String> people = ReadLinesFromFile(GetCSVFileName(VolunteerNameFile));
        for(int i = 0;i<people.size();i++)
        {
            if (people.get(i).equalsIgnoreCase(VolunteerName))
            {
                return true;
            }
        }
        return false;
    }

    private static String GetPersonalCSVFilePath(String VolunteerName, String FileName)
    {
        return VolunteerName+"/"+FileName+".csv";
    }

	private static void AppendToCSVFile(String fileName, String row) throws IOException {
        FileWriter fw = new FileWriter(fileName,true);
        fw.write(row+System.lineSeparator());
        fw.close();
    }

    private static void CreateDirIfNotExistFor(String Path) {
        File folder = new File(Path);
        if(!folder.exists())
        {
            if(!folder.mkdir())
            {
                System.out.println("Can Not Create Directory");
            }
        }
    }

    private static String GetUserInput() {
        String input = scan.nextLine();
        return input;
	}

	private static void InitialiseSystem() throws IOException {
        CreateFileIfNotExist(GetCSVFileName(VolunteerNameFile), "Name");
    }

    private static String GetCSVFileName(String filename)
    {
        return filename + ".csv";
    } 

    private static void CreateFileIfNotExist(String fileName, String InitialData) throws IOException {
        if(CurrentDirectoryHasFile(fileName))
        {
            return;
        }

        WriteToFile(fileName, InitialData);
    }

    private static void WriteToFile(String fileName, String InitialData) throws IOException {
        File file = new File(fileName);
        if(InitialData != null)
        {
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(InitialData+System.lineSeparator());
            output.close();
        }
    }


    private static boolean CurrentDirectoryHasFile(String fileName) {
        String[] filenames = GetFileNamesInCurrentDirectory();
        for(int i = 0;i<filenames.length;i++)
        {
            if(filenames[i].equals(fileName))
            {return true;}
        }
        return false;
    }

    private static String[] GetFileNamesInCurrentDirectory() {
        File[] files = GetFilesFromCurrentDirectory();
        String[] fileNames = new String[files.length];
        for (int i =0;i<files.length;i++)
        {
            fileNames[i] = files[i].getName();
        }
        return fileNames;
    }

    private static File[] GetFilesFromCurrentDirectory() {
        File current = new File(".");
        File[] files = current.listFiles();
        return files;
    }
}