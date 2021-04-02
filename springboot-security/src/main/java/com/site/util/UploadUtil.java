package com.site.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 上传文件工具类
 */
public class UploadUtil {

    /**
     * 魔数到文件类型的映射集合
     */
    public static final Map<String, String> TYPES = new HashMap<>();

    static {
        // 图片，此处只提取前六位作为魔数
        //TYPES.put("FFD8FF", "jpg");
        //TYPES.put("89504E", "png");
        //TYPES.put("474946", "gif");
        //TYPES.put("524946", "webp");
        TYPES.put("FFD8FFE0", "jpg");
        TYPES.put("89504E47", "png");
        TYPES.put("47494638", "gif");
        TYPES.put("49492A00", "tif");
        TYPES.put("424D", "bmp");
        TYPES.put("41433130", "dwg"); // CAD
        TYPES.put("38425053", "psd");
        TYPES.put("7B5C727466", "rtf"); // 日记本
        TYPES.put("3C3F786D6C", "xml");
        TYPES.put("68746D6C3E", "html");
        TYPES.put("44656C69766572792D646174653A", "eml"); // 邮件
        TYPES.put("D0CF11E0", "doc");
        TYPES.put("D0CF11E0", "xls");//excel2003版本文件
        TYPES.put("5374616E64617264204A", "mdb");
        TYPES.put("252150532D41646F6265", "ps");
        TYPES.put("255044462D312E", "pdf");
        TYPES.put("504B0304", "docx");
        TYPES.put("504B0304", "xlsx");//excel2007以上版本文件
        TYPES.put("52617221", "rar");
        TYPES.put("57415645", "wav");
        TYPES.put("41564920", "avi");
        TYPES.put("2E524D46", "rm");
        TYPES.put("000001BA", "mpg");
        TYPES.put("000001B3", "mpg");
        TYPES.put("6D6F6F76", "mov");
        TYPES.put("3026B2758E66CF11", "asf");
        TYPES.put("4D546864", "mid");
        TYPES.put("1F8B08", "gz");
    }

    /**
     * 根据文件的字节数据获取文件类型
     * @param filePath 文件路径
     * @return
     */
    public static String getFileType(String filePath) throws IOException {
        //提取前六位作为魔数
        String magicNumberHex = getHex(getFileBytes(filePath), 8);
        return TYPES.get(magicNumberHex);
    }

    /**
     * 根据文件的字节数据获取文件类型
     * @param data 字节数组形式的文件数据
     * @return
     */
    public static String getFileType(byte[] data) throws IOException {
        //提取前六位作为魔数
        String magicNumberHex = getHex(data, 8);
        return TYPES.get(magicNumberHex);
    }

    /**
     * 上传文件(单)
     * @param file 文件对象
     * @param path 保存路径
     * @return
     * @throws IOException
     */
    public static String upload(MultipartFile file, String path) throws IOException {
        String originalFilename = file.getOriginalFilename();// 原文件名称
        String date = new SimpleDateFormat("yyyy/MM").format(new Date()).toString();
        String fileName = date + "/" + UUID.randomUUID().toString().replace("-","") + originalFilename.substring(originalFilename.lastIndexOf("."));
        File dest = new File(path + "/" + fileName);
        if (!dest.getParentFile().exists()){
            dest.setWritable(true);
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        return fileName;
    }

    /**
     * 上传多张
     * @param files file数组
     * @param path  保存路径
     * @return
     */
    public static List<String> upload(MultipartFile[] files, String path) throws IOException {
        List<String> imagePathList = new ArrayList<>();
        //判断file数组不能为空并且长度大于0
        if (files != null && files.length > 0){
            String date = new SimpleDateFormat("yyyy/MM").format(new Date()).toString();
            // 处理路径并判断目录是否存在
            File dir = new File(path + "/" + date);
            if (!dir.exists()) {
                // 设置写权限
                dir.setWritable(true);
                dir.mkdirs();
            }
            for (MultipartFile file : files){
                if (!file.isEmpty()){
                    // 原文件名称
                    String fileName = file.getOriginalFilename();
                    String newFileName = date + "/" + UUID.randomUUID().toString().replace("-","") + fileName.substring(fileName.lastIndexOf("."));
                    file.transferTo(new File(path + "/" + newFileName));
                    imagePathList.add(newFileName);
                }
            }
        }
        return imagePathList;
    }

    /**
     * 删除文件
     * @param filePath 文件路径
     * @return
     */
    public static boolean delete(String filePath){
        File file = new File(filePath);
        if (file.isFile()){
            return file.delete();
        }
        return false;
    }

    /**
     * 验证文件大小
     * @param fileLen 文件大小
     * @param fileSize 规定文件大小
     * @param fileUnit 单位
     * @return
     */
    public static boolean checkFileSize(Long fileLen, int fileSize, String fileUnit) {
        double fileSizeCom = 0;
        if ("B".equals(fileUnit.toUpperCase())) {
            fileSizeCom = (double) fileLen;
        } else if ("K".equals(fileUnit.toUpperCase())) {
            fileSizeCom = (double) fileLen / 1024;
        } else if ("M".equals(fileUnit.toUpperCase())) {
            fileSizeCom = (double) fileLen / (1024 * 1024);
        } else if ("G".equals(fileUnit.toUpperCase())) {
            fileSizeCom = (double) fileLen / (1024 * 1024 * 1024);
        }
        if (fileSizeCom > fileSize) {
            return false;
        }
        return true;
    }

    /**
     * 读取文件字节数据
     * @param filePath
     * @return
     * @throws IOException
     */
    private static byte[] getFileBytes(String filePath) throws IOException {
        InputStream fs = new FileInputStream(filePath);
        byte[] b = new byte[fs.available()];
        fs.read(b);
        return b;
    }

    /**
     * 获取16进制表示的魔数
     * @param data              字节数组形式的文件数据
     * @param magicNumberLength 魔数长度
     * @return
     */
    public static String getHex(byte[] data, int magicNumberLength) {
        //提取文件的魔数
        StringBuilder magicNumber = new StringBuilder();
        //一个字节对应魔数的两位
        int magicNumberByteLength = magicNumberLength / 2;
        for (int i = 0; i < magicNumberByteLength; i++) {
            magicNumber.append(Integer.toHexString(data[i] >> 4 & 0xF));
            magicNumber.append(Integer.toHexString(data[i] & 0xF));
        }
        return magicNumber.toString().toUpperCase();
    }
}
