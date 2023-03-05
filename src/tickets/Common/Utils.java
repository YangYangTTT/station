package tickets.Common;

import org.apache.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utils {
    private static Logger logger = Logger.getLogger(Utils.class);
    private static MessageDigest messageDigest = null;
    private static final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat format3 = new SimpleDateFormat("yyyyMMdd");
    static {
        try {
            messageDigest = MessageDigest.getInstance("sha-256");
            logger.debug("创建了摘要对象.");

        } catch (NoSuchAlgorithmException e) {
            logger.error("实例化摘要对象时出错:" + e.getMessage());
        }
    }
    /**
     * 解析字符串为日期对象，
     *
     * @param str 日期字符串，可以是 yyyy-MM-dd 或 yyyy/MM/dd 格式的字符串
     * @return
     */
    public static Date parseString(String str) {
        Date result = null;
        if (str != null && str.length() > 0) {
            int index = str.indexOf("-");
            if (index > 0) {
                try {
                    result = format1.parse(str);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                index = str.indexOf("/");
                if (index > 0) {
                    try {
                        result = format2.parse(str);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    /**
     * 将日期对象转换为字符串
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        String str = "";
        if (date != null) {
            str = format1.format(date);
        }
        return str;
    }

    /**计算密码的摘要数据并转换为Base64编码*/
    public static String digestPassword(String plainText) {
        String result = null;
        if (plainText != null) {
            //计算摘要
            byte[] bytes = plainText.getBytes();
            for (int i = 0; i < 5; i++)
                bytes = messageDigest.digest(bytes);
            //将字节数组转换为Base64编码的字符串
            result = Base64.getEncoder().encodeToString(bytes);
        }
        return result;
    }
    /**对比密码和摘要是否一致*/
    public static boolean matchPassword(String plainText, String encodedText) {
        String result = digestPassword(plainText);
        if (result != null) {
            return result.equals(encodedText);
        }
        return false;
    }
    public static void main(String[] args) {
        logger.debug("123:" + digestPassword("123"));
        //System.out.println(digestPassword("123"));
        logger.debug("结果:" + matchPassword("123", "k9gpNUyzWSdDF0EzEEtUBbppkrZ7shn73j45TXBQWRM="));
    }
    /**
     * 根据传入的日期对象，返回一个新的日期对象，其中的时分秒都被清零
     *
     * @param
     * @return
     */
    public static Date clearHMS(Date date) {
        Calendar calendar = null;
        Date result = null;
        if (date != null) {
            calendar = new GregorianCalendar();
            calendar.setTimeInMillis(date.getTime());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            result = calendar.getTime();
        }
        return result;
    }
    /**
     * 给传入的日期对象加上n天，返回新的日期对象
     *
     * @param date
     * @param
     * @return
     */
    public static Date addDays(Date date, int i) {
        Date result = null;
        Calendar calendar = null;
        if (date != null) {
            calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+i);
            result = calendar.getTime();
        }
        return result;
    }
    /**
     * 将日期对象转换为字符串
     *
     * @param date
     * @return 字符串格式"yyyyMMdd"
     */
    public static String formatDate3(Date date) {
        String str = "";
        if (date != null) {
            str = format3.format(date);
        }
        return str;
    }
}



