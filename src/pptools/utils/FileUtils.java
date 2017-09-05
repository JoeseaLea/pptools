package pptools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtils {
	public static boolean writeFile(String filePath, String data){
		OutputStreamWriter writer = null;
		boolean result = false;
		try{
			writer = new OutputStreamWriter(new FileOutputStream(new File(filePath)), "UTF-8");
			writer.write(data);
			writer.close();
			writer = null;
			
			result = true;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(writer!=null) {
				try {
					writer.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static String readFile(String filePath)throws Exception{
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
			String line = null;
			while((line=reader.readLine())!=null){
				sb.append(line);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(reader != null){
				reader.close();
			}
		}
		return sb.toString();
	}
	
}
