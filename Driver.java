import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;
import java.io.Writer;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
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

            userin.close();
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
            boolean HasRecords = IfVolunteerHasRecords(volunteer_records,volunteer_name);
            HelpCompleteUntilToday(userin,HasRecords,volunteer_records,volunteer_name);


        }
    }

    private static void HelpCompleteUntilToday(Scanner userin,boolean HasRecords,List<List<String>> volunteer_records,String volunteer_name) throws IOException
    {
        String Userdate = null;
        LocalDateTime initialDate = null;

        while(true)
        {
            if(!HasRecords)
            {
                System.out.println(volunteer_name+"没有任何记录，请告诉我起始日期，比如2019-07-08, Z-退出");
                Userdate = userin.nextLine();
                if(Helper.IsEqualCaseInsensitive(Userdate, "z"))
                {
                    break;  
                }
                initialDate = GetInitialDate(Userdate);
                if(initialDate==null)
                {
                    System.out.println("输入日期不符合规格!(有可能是周末)");
                    continue;
                }
                break;
            }

            else
            {
                String date = GetLastDateOfRecords(volunteer_records);
                initialDate = GetInitialDate(date).plusDays(1);
                break;    
            }
        }
        
        LocalDateTime currentDateTime = initialDate;
        while(currentDateTime.isBefore(LocalDateTime.now()))
        {
            if(IsWeekend(currentDateTime))
            {
                currentDateTime.plusDays(1);
                continue;
            }

            GetVolunteerInfoOnThiDay(volunteer_name,currentDateTime.getDayOfWeek().getValue());




            System.out.println(GetDateStrFromLocalDateTime(currentDateTime)+ " 星期"+ currentDateTime.getDayOfWeek()+ " 智能建议:");
            //Implement From Here

        }
    }

    private static Dictionary<String, String> GetVolunteerInfoOnThiDay(String name, int DayOfWeek)
    {
        //Implement From here
        return null;
    }

    private static String GetDateStrFromLocalDateTime(LocalDateTime date)
    {
        return date.getYear() + "-" + date.getMonth() +"-" + date.getDayOfMonth();
    }

    private static boolean IsWeekend(LocalDateTime date)
    {
        if(date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7)
        {
            return true;
        }
        return false;

    }  

    private static LocalDateTime GetInitialDate(String Userdate)
    {
        String[] date = Userdate.split("-");
        if(date.length !=3 )
        {
            return null;
        }

        LocalDateTime userDate =GetDateFromDateStr(Userdate);

        if(userDate.isAfter(LocalDateTime.now()))
        {
            return null;
        }

        if(IsWeekend(userDate))
        {
            return null;
        }

        return userDate;
    }

    private static LocalDateTime GetDateFromDateStr(String Userdate)
    {
        String[] date = Userdate.split("-");
        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);
        LocalDateTime userDate = LocalDateTime.of(year,month,day,0,0);

        return userDate;
    }

    private static boolean IfVolunteerHasRecords(List<List<String>> volunteer_records, String volunteer_name)
    {
        if(Helper.IsEqualCaseInsensitive(GetLastDateOfRecords(volunteer_records),"Date"))
        {
            System.out.println(  volunteer_name +"还没有行程记记录");
            return false;
        }
        else
        {
            System.out.println(  volunteer_name +"的最近一次行程记录于"+GetLastDateOfRecords(volunteer_records));
            return true;
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
            String str = "Name,DayOfWeek,StartTime,EndTime,School,AfterSchoolAddress,Driver";
            str+= System.lineSeparator();
            writer.append(str);
            writer.flush();
            writer.close();
            LocalDate[] startToEnd = CreateDataCSVToFriday(LocalDate.now());
                       
            FileWriter writer2 = new FileWriter("Dates.csv");
            str = "StartDate,EndDate" + System.lineSeparator();
            str+=startToEnd[0].toString() +"," + startToEnd[1].toString();
            writer2.append(str);
            writer2.flush();
            writer2.close();
        }
    }

    private static void AddVolunteer(Scanner userin) throws IOException {
        while (true) {
            System.out.println("请输入志愿者信息，格式：名字,比如：Jiamo, Z-退出");
            String read = userin.nextLine();
            if (Helper.IsEqualCaseInsensitive(read, "z")) {
                break;
            }
            String[] info = ProcessVolunteerInput(read);
            if (info.equals(null) || info.length != 1) {
                System.out.println("Invalid Volunteer Info!");
                continue;
            }

            if (VolunteerExists(info[0])) {
                System.out.println("Volunteer Already Exists!");
                continue;
            }




            System.out.println("请输入" + info[0] + "的大致上课时间信息，格式：星期几,上课时间,下课时间,上课地点，下课地点,司机，比如：1,8:00,12:00,GS,Res,Freddy-3,8:00:13:00,Kenny Z-退出");
            read =  userin.nextLine();
            if (Helper.IsEqualCaseInsensitive(read, "z")) {
                break;
            }
    
            String[] times = ProcessVolunteerInput(info[0],read);
            if (times== null) 
            {
                System.out.println("Invalid Volunteer Time Info!");
                continue;
            }
    
            if (!IsTimesValid(times))
            {
                System.out.println("Invalid Volunteer Time Info!");
                continue;
            }                
            WriteRowToCsv("Volunteer.csv", times);
            
           
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
            while ((line = br.readLine())!= null ) 
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
            if(timeinfo.length != 7)
            {
                return false;
            }
            

        }
        return true;
    }

    private static void WriteRowToCsv(String filename, String[] info) throws IOException {
        String builder = "";

        for(int i = 0; i<info.length;i++)
        {
            builder+=info[i];
            if(i!= info.length-1)
            {
                builder += System.lineSeparator();
            }
        }

        FileWriter writer = new FileWriter(filename,true);
        writer.append(builder);
        writer.flush();
        writer.close();
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

    private static String[] ProcessVolunteerInput(String name)
    {
        if(name == null || name.equals(""))
        {
            return null;
        }
        return name.split("-");
    }

    private static String[] ProcessVolunteerInput(String name,String timeStr)
    {
        if((timeStr == null) || timeStr.equals(""))
        {
            return null;
        }

        String[] times = timeStr.split("-");
        String[] res = new String[times.length]; 

        for(int i = 0;i<times.length;i++)
        {
            res[i] = name+ ","+times[i];
        }


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