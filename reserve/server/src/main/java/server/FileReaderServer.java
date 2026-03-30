package server;

import com.example.demo.common.Result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileReaderServer {
    ArrayList<List<Double>> list = new ArrayList<List<Double>>();

    public void run() {
        String directoryPath = "E:/文件/网页空白框架/data"; // 设置目录路径
        File directory = new File(directoryPath);

        // 确保目录存在且确实是一个目录
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            // 遍历目录下的所有文件
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) { // 检查是否为文件且扩展名为.txt
                    System.out.println("Reading file: " + file.getName());
                    readFileContent(file);
                } else {
                    System.out.println("Directory does not exist or is not a directory.");
                }
            }



        }
    }
    public void readFileContent( File file){
//        String filePath = "E:/文件/网页空白框架/曲线.txt"; // 替换为你的文件路径
        String serchString = "Position";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(serchString)) {
                    ArrayList<Double> coordi = new ArrayList<Double>();//一维数组
                    // 控制正则表达式的匹配行为的参数(小数)
                    Pattern p = Pattern.compile("\\d+\\.?\\d*");
                    Matcher matcher = p.matcher(line);

                    while (matcher.find()) {
                        double num = Double.parseDouble(matcher.group());
                        coordi.add(num);
                    }
                    list.add(coordi);
                }
//                打印输出看效果
                for(int i=0;i<list.size();i++)
                {
                    for(int j=0;j<list.get(i).size();j++)
                        System.out.print(list.get(i).get(j)+" ");
                    System.out.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
