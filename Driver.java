import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Driver {
    public static void main(String[] args) throws IOException {
        String choice = null;

        InitialiseStorageFiles();

        while (true) {
            Driver.ConsolePrint();
            Scanner userin = new Scanner(System.in);
            choice = userin.nextLine();
            if (Helper.IsEqualCaseInsensitive(choice, "z")) {
                break;
            }

            if (Helper.IsEqualCaseInsensitive(choice, "A")) {
                Driver.AddVolunteer(userin);
                continue;
            }

            if (Helper.IsEqualCaseInsensitive(choice, "b")) {
                Driver.HelperRecord(userin);
                continue;
            }
        }

    }

    private static void HelperRecord(Scanner userin) throws IOException {
        File currentdirectory = new File("VolunteerRecords");
        File[] VolunteerFiles = currentdirectory.listFiles();
        int numberofVolunteer = VolunteerFiles.length;
    
        System.out.println("系统记录了 "  + numberofVolunteer+ "个志愿者");

        for(int i = 0;i<numberofVolunteer;i++)
        {
            String volunteer_name = VolunteerFiles[i].getName().split("_")[0];

            List<List<String>> volunteer_records = ReadCsv("VolunteerRecords/"+VolunteerFiles[i].getName());


            System.out.println(  volunteer_name +"的最近一次行程记录于"+GetLastDateOfRecords(volunteer_records));



        }
    }

    private static String GetLastDateOfRecords(List<List<String>> volunteer_records)
    {
        int numberofRecords = volunteer_records.size();

        return volunteer_records.get(numberofRecords-1).get(0);
    }





    private static void ConsolePrint() {
        System.out.println("为珍珍订做的行车管理系统 V2.0");
        System.out.println("现在是" + LocalDateTime.now());
        System.out.println("请选择一个选项，A-添加志愿者， B-小助手辅助记录行程, C-手动记录 Z-退出");
    }

    private static void InitialiseStorageFiles() throws IOException {
        File currentdirectory = new File(".");

        File[] files = currentdirectory.listFiles();
        int numberofinits = 0;
        boolean init = false;
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals("Volunteer.csv") || files[i].getName().equals("Dates.csv")) {
                numberofinits++;
            }

        }

        if (numberofinits == 2) {
            init = true;
        }

        if (!init) {
            FileWriter writer = new FileWriter("Volunteer.csv");
            String str = "Name,School";
            str += System.lineSeparator();
            writer.append(str);
            writer.flush();
            writer.close();


            CreateDataCSVToFriday(LocalDate.now());
                       
            FileWriter writer2 = new FileWriter("Dates.csv");
            str = "StartDate,EndDate" + System.lineSeparator();
            writer2.append(str);
            writer2.flush();
            writer2.close();
        }
    }

    private static void AddVolunteer(Scanner userin) throws IOException {
        while (true) {
            System.out.println("请输入志愿者信息，格式：名字-学校,比如：Jiamo-Forest Hill, Z-退出");
            String read = userin.nextLine();
            if (Helper.IsEqualCaseInsensitive(read, "z")) {
                break;
            }
            String[] info = ProcessVolunteerInput(read);
            if (info == null || info.length != 2) {
                System.out.println("Invalid Volunteer Info!");
                continue;
            }

            if (VolunteerExists(info[0])) {
                System.out.println("Volunteer Already Exists!");
                continue;
            }

            System.out.println("请输入" + info[0] + "的大致上课时间信息，格式：星期几,上课时间,下课时间,上课地点，下课地点,司机，比如：1,8:00,12:00,GS,Res,Freddy-3,8:00:13:00,Kenny Z-退出");
            read = userin.nextLine();
            if (Helper.IsEqualCaseInsensitive(read, "z")) {
                break;
            }

            String[] times = ProcessVolunteerInput(read);
            if (times == null) {
                System.out.println("Invalid Volunteer Time Info!");
                continue;
            }

            if (!IsTimesValid(times)) {
                System.out.println("Invalid Volunteer Time Info!");
                continue;
            }

            WriteToCsv("Volunteer.csv", info);
            WriteToCsv("Volunteer.csv", times);
            WriteToCsv("VolunteerRecords/" + info[0] + "_record.csv", null);
            WriteToCsv( "VolunteerRecords/" + info[0] + "_record.csv", new String[]{"Date","StartTime","EndTime","School","Res","Driver"});
            

        }
    }

    private static boolean VolunteerExists(String name) throws IOException {
        List<List<String>> res = ReadCsv("Volunteer.csv");

        for(int i =0;i<res.size();i++)
        {
            for(int j = 0;j< res.get(i).size();j++)
            {
                if(res.get(i).get(j).equals(name))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<List<String>> ReadCsv(String filename) throws IOException
    {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) 
        {
            String line;
            while ((line = br.readLine()) != null) 
            {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }

            
        }
        return records;
    }

    private static boolean IsTimesValid(String[] times) {
        for(int i = 0;i<times.length;i++)
        {
            String[] timeinfo = times[i].split(",");
            if(timeinfo.length != 6)
            {
                return false;
            }
            

        }
        return true;
    }

    private static void WriteToCsv(String filename, String[] info) throws IOException {
        String builder = "";

        if(info!=null)
        {
            for(int i = 0; i<info.length;i++)
            {
                builder+=info[i];
                if(i!= info.length-1)
                {
                    builder += ",";
                }
            }
            builder += System.lineSeparator();
    
            FileWriter writer = new FileWriter(filename,true);
            writer.append(builder);
            writer.flush();
            writer.close();
        }

        else
        {
            FileWriter writer = new FileWriter(filename);
            writer.flush();
            writer.close();
        }

    }

    private static String[] ProcessVolunteerInput(String volunteer)
    {
        if(volunteer.equals(null) || volunteer.equals(""))
        {
            return null;
        }

        String[] res = volunteer.split("-");
        return res;
    }

    private static LocalDate[] CreateDataCSVToFriday(LocalDate date) throws IOException
    {
        int DaysUntilNextWeekday= 0;
        LocalDate[] startfinish = new LocalDate[2];


        LocalDate Start;
        LocalDate End;
        if(date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY))
        {
            DaysUntilNextWeekday = 7- date.getDayOfWeek().getValue() +1; 
            Start = date.plusDays(DaysUntilNextWeekday);
            End = Start.plusDays(4);
        }
        else
        {
            Start = date;
            End = date.plusDays(5-date.getDayOfWeek().getValue());
        }

        String filename = Start.getDayOfMonth()+"-"+Start.getMonthValue()+" to " + End.getDayOfMonth()+"-"+ End.getMonthValue();

        WriteToCsv("Simon's Town " + filename+".csv",null);
        WriteToCsv("CI " + filename+".csv", null);
        startfinish[0] = Start;
        startfinish[1] =End;

        return startfinish;

    }




}