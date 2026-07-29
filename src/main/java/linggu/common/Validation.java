package linggu.common;

public class Validation {
    private Validation(){
    }
    public static final String DIANHUA=
            "^1[3-9]\\d{9}$";
    public static final String SHENFENZHENG=
            "^[1-9]\\d{5}(18|19|20)\\d{2}"
                    + "(0[1-9]|1[0-2])"
                    + "(0[1-9]|[12]\\d|3[01])"
                    + "\\d{3}[0-9Xx]$";
    public static final String YOUXIANG=
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
}
