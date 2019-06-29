public class Helper
{
    public static boolean IsEqualCaseInsensitive(String stringa, String stringb)
    {
        if(stringa.toUpperCase().equals(stringb.toUpperCase()))
        {
            return true;
        }

        return false;
    }


}