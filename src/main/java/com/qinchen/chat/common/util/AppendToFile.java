package com.qinchen.chat.common.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppendToFile {

	public static void appendMethodA(String fileName, String content) {
		try {
			// 打开一个随bai机访问文件流，按读写方式
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			randomFile.writeBytes(content);
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	  public static String getCurrentDate() {
	        SimpleDateFormat sm = new SimpleDateFormat("HH:mm:ss");
	        return sm.format(new Date());
	    }

	public static void appendMethodB(String liveId, String content) {
		String fileName = "/data/websocket/log/" + liveId + ".txt";
		try {
			File log = new File(fileName);
			if (!log.exists())// 如果文件不存在,则新建.
			{
				File parentDir = new File(log.getParent());
				if (!parentDir.exists())// 如果所在目录不存在,则新建.
				{
					parentDir.mkdirs();
				}
				log.createNewFile();
			}
			String enterStr = "\n";
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(enterStr +getCurrentDate() + "  " + content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void appendMethodC(String liveId, String content) {
		String fileName = "/data/websocket/log/"+liveId + ".txt";
		try {
			File log = new File(fileName);
			if (!log.exists())// 如果文件不存在,则新建.
			{
				File parentDir = new File(log.getParent());
				if (!parentDir.exists())// 如果所在目录不存在,则新建.
				{
					parentDir.mkdirs();
				}
				log.createNewFile();
			}
			List<String> list = new ArrayList<String>();
			list.add(getCurrentDate() + "  " + content);
			Path path = Paths.get(fileName);
			Files.write(path, list, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
