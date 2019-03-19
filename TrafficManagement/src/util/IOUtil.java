package util;
import java.io.*;
import java.util.LinkedList;
import java.util.List;


public class IOUtil {
    /** 
     * ��ȡ�ļ����������
     * @param filePath
     * @param spec ������������������ spec==nullʱ������������
     * @return
     * @version 2019-3-15
     */
    public static String[] read(final String filePath, final Integer spec)
    {
        File file = new File(filePath);
        // ���ļ������ڻ��߲��ɶ�ʱ
        if ((!isFileExists(file)) || (!file.canRead()))
        {
            System.out.println("file [" + filePath + "] is not exist or cannot read!!!");
            return null;
        }
        
        List<String> lines = new LinkedList<>();
        try(FileReader fb = new FileReader(file);BufferedReader br=new BufferedReader(fb))
        {
        	String line=null;
        	while((line=br.readLine())!=null) {
        		lines.add(line);
        		
        	}
        	
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("Successful file[\" + filePath + \"] reading!!!");
        return lines.toArray(new String[lines.size()]);
    }
    /** 
     * д�ļ�
     * @param filePath ����ļ�·��
     * @param content Ҫд�������
     * @param append �Ƿ�׷��
     * @return
     * @author lulu
     * @version 2018-3-15
     */
    public static void write(final String filePath, final String[] contents, final boolean append)
    {
        File file = new File(filePath);
        if (contents == null)
        {
            System.out.println("file [" + filePath + "] invalid!!!");
            
        }

        // ���ļ����ڵ�����дʱ
        if (isFileExists(file) && (!file.canRead()))
        {
        	System.out.println("file [" + filePath + "] is readonly!!!");
        }
        try(FileWriter fw = new FileWriter(file, append); BufferedWriter bw = new BufferedWriter(fw);)
        {
        	if (!isFileExists(file))
            {
                file.createNewFile();
            }
        	 for (String content : contents)
             {
                 if (content == null)
                 {
                     continue;
                 }
                 bw.write(content);
                 bw.newLine();
             }
        	
        }

        catch (IOException e)
        {
            e.printStackTrace();
            
        }
        
        System.out.println("Successful file [\" + filePath + \"] writing!!!");
    }

  

    private static boolean isFileExists(final File file)
    {
        if (file.exists() && file.isFile())
        {
            return true;
        }

        return false;
    }

}