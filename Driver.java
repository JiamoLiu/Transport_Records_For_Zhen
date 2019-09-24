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
    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        System.out.println("欢迎使用行车记录仪2.0");
        while(true)
        {
            System.out.println("A-智能填写，B-手动填写，C-添加志愿者，D-删除志愿者，E-查看志愿者, Z-退出");
            InitialiseSystem();
            String Input = GetUserInput();
            if (Input.equalsIgnoreCase("A")) {
    
            }
    
            if (Input.equalsIgnoreCase("C")) {
                AddVolunteer();
            }

            if(Input.equalsIgnoreCase("D"))
            {
                DeleteVolunteer();
            }
    
            if(Input.equalsIgnoreCase("Z"))
            {
                break;
            }
        }


    }

    private static void DeleteVolunteer() throws IOException {
        System.out.println("志愿者英文名字是什么？");
        String VolunteerName = GetUserInput();
        DeleteDirRecursiveIfExist(VolunteerName);
        RemoveRowContainsKeyFromFile(GetTxtFileName(VolunteerNameFile),VolunteerName);
    }

    private static void RemoveRowContainsKeyFromFile(String fileName, String key) throws IOException {
        ArrayList<String> originalFile = ReadLinesFromFile(fileName);
        String Builder = "";
        for(int i = 0;i<originalFile.size();i++ )
        {
            String row = originalFile.get(i);
            if (row.contains(key))
            {
                continue;
            }
            if (i != originalFile.size() -1)
            {
                Builder += row+System.lineSeparator();
            }
            else
            {
                Builder+= row;
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
               res.add(line);
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
        AppendToTxtFile(GetTxtFileName(VolunteerNameFile),VolunteerName);
        CreateFileIfNotExist(GetPersonalTxtFilePath(VolunteerName, LastestVolunteerDateFile), "LastDate");
        CreateFileIfNotExist(GetPersonalTxtFilePath(VolunteerName, PersonalInfoFile), "DayOfWeek,Time,From,To,Purpose,Driver");
    }

    private static boolean VolunteerAlreadyInSystem(String VolunteerName) throws IOException
    {
        ArrayList<String> people = ReadLinesFromFile(GetTxtFileName(VolunteerNameFile));
        for(int i = 0;i<people.size();i++)
        {
            if (people.get(i).equalsIgnoreCase(VolunteerName))
            {
                return true;
            }
        }
        return false;
    }

    private static String GetPersonalTxtFilePath(String VolunteerName, String FileName)
    {
        return VolunteerName+"/"+FileName+".txt";
    }

	private static void AppendToTxtFile(String fileName, String row) throws IOException {
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
        CreateFileIfNotExist(GetTxtFileName(VolunteerNameFile), "Name");
    }

    private static String GetTxtFileName(String filename)
    {
        return filename + ".txt";
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