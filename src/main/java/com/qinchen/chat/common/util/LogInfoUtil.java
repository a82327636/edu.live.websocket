package com.qinchen.chat.common.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

/**
 * @author: zyq
 * @date: 2020/4/27 0027 上午 11:19
 */
public class LogInfoUtil {
    public static void main(String[] args) {
        String newLog = " Date:" + new Date() + "  |";
        appendLog(newLog,"11");
    }

    public static String getCurrentDate() {
        SimpleDateFormat sm = new SimpleDateFormat("HH:mm:ss");
        return sm.format(new Date());
    }

    public static String strRight(String value) {
        return value.substring(value.length() - 2, value.length());
    }

    public static void appendLog(String newLog,String key) {
        Scanner sc = null;
        PrintWriter pw = null;
        Calendar c = new GregorianCalendar();
		/*
		 * File log = new File("/data/websocket/log/" +
		 * String.valueOf(c.get(Calendar.YEAR)) + strRight("00" +
		 * String.valueOf(c.get(Calendar.MONTH)+1)) + strRight("00" +
		 * String.valueOf(c.get(Calendar.DAY_OF_MONTH))) + "_" + key +".log");
		 */
        File log = new File("/data/websocket/log/" +
       		key +".txt");
        try {
            if (!log.exists())//如果文件不存在,则新建.
            {
                File parentDir = new File(log.getParent());
                if (!parentDir.exists())//如果所在目录不存在,则新建.
                {
                    parentDir.mkdirs();
                }
                log.createNewFile();
            }
            sc = new Scanner(log);
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine())//先读出旧文件内容,并暂存sb中;
            {
                sb.append(sc.nextLine());
                sb.append("\r\n");//换行符作为间隔,扫描器读不出来,因此要自己添加.
            }
            sc.close();

            pw = new PrintWriter(new FileWriter(log), true);
            /*
             * A.
             */
            pw.println(sb.toString());//,写入旧文件内容.
            /*
             * B.
             */
            pw.println(getCurrentDate() + "       " +newLog );//写入新日志.
            /*
             * 如果先写入A,最近日志在文件最后. 如是先写入B,最近日志在文件最前.
             */
            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
