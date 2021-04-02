package com.site.util;

import com.site.common.constant.Constant;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 文件工具
 */
public class FileUtil {

    //2M
    public static final int FILE_SIZE = 1000000;

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);


    private static ResourceBundle bundle = ResourceBundle.getBundle("config/constant");

    public static String fileUploadPath =bundle.getString("file-upload.dir");

    //------------------------------------ 压缩图片 start -------------------------------------
    /**
     * 压缩超过2m的图片
     * @param url
     * @return
     * @throws Exception
     */
    public static String savePreFile(String url) throws Exception {
        StringBuffer str = new StringBuffer();
        str.append("/img/");
        str.append(DateTimeUtil.formatDatetoString(new Date()));
        str.append("/pre/");
        str.append(url.substring(url.lastIndexOf("/")+1));
        String preUrl = fileUploadPath + str.toString();
        File filePath = new File(StringUtil.utf8Decoding(preUrl.substring(0,preUrl.lastIndexOf("/"))));
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        //其中的scale是可以指定图片的大小，值在0到1之间，1f就是原图大小，0.5就是原图的一半大小，这里的大小是指图片的长宽。
        //而outputQuality是图片的质量，值也是在0到1，越接近于1质量越好，越接近于0质量越差。
        Thumbnails.of(fileUploadPath+url)
                .scale(1f)
                .outputQuality(0.5f)
                .toFile(preUrl);
        return str.toString();
    }
    //------------------------------------ 压缩图片 end -------------------------------------

    //------------------------------------ 压缩文件 start -------------------------------------
    /**
     * 判断当前文件是否是zip文件
     * @param fileName 文件名
     * @return true 是
     */
    public static boolean isZip(String fileName) {
        return fileName.toLowerCase().endsWith(Constant.FilePostFix.ZIP_FILE);
    }

    /**
     * 验证待解压文件是否存在
     * @param sourcePath 源文件路径
     * @return
     */
    public static boolean checkZipFile(String sourcePath){
        System.setProperty("sun.zip.encoding", System.getProperty("sun.jnu.encoding"));
        ZipFile zipFile =null;
        try {
            File sourceFile = new File(sourcePath);
            zipFile = new ZipFile(sourcePath, "gbk");
            if ((!sourceFile.exists()) && (sourceFile.length() <= 0)) {
                throw new Exception("要解压的文件不存在!");
            }
            Enumeration<?> e = zipFile.getEntries();
            while (e.hasMoreElements()) {
                ZipEntry zipEnt = (ZipEntry) e.nextElement();
                if (zipEnt.isDirectory()) {
                    return false;
                }
                if(zipEnt.getName().endsWith(".shp")){
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                if(null!=zipFile){
                    zipFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /** 压缩文件或目录
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     * (把指定文件夹下的所有文件目录和文件都压缩到指定文件夹下)
     * @param sourceFilePath 待压缩的文件路径
     * @param zipFilePath 压缩后存放路径
     * @param fileName 压缩后文件的名称
     * @return
     */
    public static boolean fileToZip(String sourceFilePath, String zipFilePath, String fileName)throws Exception {
        boolean flag = false;
        FileOutputStream fos =null;
        ZipOutputStream zos =null;
        BufferedInputStream bis =null;
        FileInputStream fis =null;
        BufferedOutputStream bufferedOutputStream =null;
        File sourceFile = new File(sourceFilePath);
        if(sourceFile.exists() == false){
            throw new Exception("待压缩的文件目录："+sourceFilePath+"不存在.");
        }else{
            try {
                File zipFile = new File(zipFilePath +fileName );
                if(zipFile.exists()){
                    throw new Exception(zipFilePath + "目录下存在名字为:" + fileName +Constant.FilePostFix.ZIP_FILE +"打包文件.");
                }else{
                    File[] sourceFiles = sourceFile.listFiles();
                    if(null == sourceFiles || sourceFiles.length<1){
                        throw new Exception("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");
                    }else{
                        fos = new FileOutputStream(zipFile);
                        bufferedOutputStream = new BufferedOutputStream(fos);
                        zos = new ZipOutputStream(bufferedOutputStream);
                        byte[] bufs = new byte[1024*10];
                        for(int i=0;i<sourceFiles.length;i++){
                            //创建ZIP实体，并添加进压缩包
                            ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                            zos.putNextEntry(zipEntry);
                            //读取待压缩的文件并写进压缩包里
                            fis = new FileInputStream(sourceFiles[i]);
                            bis = new BufferedInputStream(fis, Constant.BYTE_BUFFER *Constant.BUFFER_MULTIPLE);
                            int read;
                            while((read=bis.read(bufs, 0, Constant.BYTE_BUFFER *Constant.BUFFER_MULTIPLE)) != -1){
                                zos.write(bufs,0,read);
                            }
                            fis.close();
                            bis.close();
                        }
                        flag = true;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally{
                //关闭流
                try {
                    if (null != bis) {
                        bis.close();
                    }
                    if (null != zos) {
                        zos.close();
                    }
                    if (null != bufferedOutputStream) {
                        bufferedOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 解压zip格式的压缩文件到指定位置
     * @param sourcePath 压缩文件
     * @param targetPath 解压目录
     * @throws Exception
     */
    public static boolean unZipFiles(String sourcePath, String targetPath) throws Exception {
        System.setProperty("sun.zip.encoding", System.getProperty("sun.jnu.encoding"));
        InputStream is =null;
        BufferedInputStream bis =null;
        try {
            (new File(targetPath)).mkdirs();
            File sourceFile = new File(sourcePath);
            // 处理中文文件名乱码的问题
            ZipFile zipFile = new ZipFile(sourcePath, "UTF-8");
            if ((!sourceFile.exists()) && (sourceFile.length() <= 0)) {
                throw new Exception("要解压的文件不存在!");
            }
            String strPath, gbkPath, strtemp;
            File tempFile = new File(targetPath);
            strPath = tempFile.getAbsolutePath();
            Enumeration<?> e = zipFile.getEntries();
            while (e.hasMoreElements()) {
                ZipEntry zipEnt = (ZipEntry) e.nextElement();
                gbkPath = zipEnt.getName();
                if (zipEnt.isDirectory()) {
                    strtemp = strPath + File.separator + gbkPath;
                    File dir = new File(strtemp);
                    dir.mkdirs();
                    continue;
                } else {
                    // 读写文件
                    is = zipFile.getInputStream((ZipEntry) zipEnt);
                    bis = new BufferedInputStream(is);
                    gbkPath = zipEnt.getName();
                    strtemp = strPath + File.separator + gbkPath;
                    // 建目录
                    String strsubdir = gbkPath;
                    for (int i = 0; i < strsubdir.length(); i++) {
                        if ("/".equalsIgnoreCase(strsubdir.substring(i, i + 1))) {
                            String temp = strPath + File.separator + strsubdir.substring(0, i);
                            File subdir = new File(temp);
                            if (!subdir.exists()) {
                                subdir.mkdir();
                            }
                        }
                    }
                    FileOutputStream fos = new FileOutputStream(strtemp);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    int c;
                    while ((c = bis.read()) != -1) {
                        bos.write((byte) c);
                    }
                    bos.flush();
                    fos.close();
                    bos.close();
                }
            }
            zipFile.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //------------------------------------ 压缩文件 end -------------------------------------

    //------------------------------------ 文件 start -------------------------------------
    /**
     * 获取目录下的所有文件
     * @param path
     */
    private static List<String> listFile = new ArrayList<>();
    public static void getFile(String path) {
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (File f : tempList) {
            if (f.isFile()) {
                listFile.add(f.getPath());
                continue;
            }
            if (f.isDirectory()) {
                getFile(f.getPath());
            }
        }
    }

    /**
     * 保存文件到临时目录
     * @param inputStream 文件输入流
     * @param fileName 文件名
     */
    public static void savePic(InputStream inputStream, String fileName) {
        OutputStream os = null;
        try {
            // 保存到临时文件
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流保存到本地文件
            File tempFile = new File(fileUploadPath);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            os = new FileOutputStream(tempFile.getPath() + File.separator + fileName);
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 完毕，关闭所有链接
            try {
                os.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件目录
     * @param path
     */
    private static void delDir(String path){
        File dir=new File(path);
        if(dir.exists()){
            File[] tmp=dir.listFiles();
            for(int i=0;i<tmp.length;i++){
                if(tmp[i].isDirectory()){
                    delDir(path+ File.separator+tmp[i].getName());
                }else{
                    tmp[i].delete();
                }
            }
            dir.delete();
        }
    }

    /**
     *  截取文件排除后缀名
     * @param fileName 文件名
     * @return
     */
    public static String cutNameSuffix(String fileName) {
        String suffix = fileName.substring(0,fileName.lastIndexOf("."));
        return suffix;
    }

    /**
     * 获取文件类型
     * @param originalFilename
     * @return
     */
    public static int getFileType(String originalFilename) {
        String postFix = originalFilename.split("//.")[originalFilename.split("//.").length-1];
        if(Arrays.asList(Constant.FilePostFix.IMAGES).contains(postFix)){
            return Constant.FileType.FILE_IMG;
        }
        if(Arrays.asList(Constant.FilePostFix.ZIP).contains(postFix)){
            return Constant.FileType.FILE_ZIP;
        }
        if(Arrays.asList(Constant.FilePostFix.VIDEO).contains(postFix)){
            return Constant.FileType.FILE_VEDIO;
        }
        if(Arrays.asList(Constant.FilePostFix.APK).contains(postFix)){
            return Constant.FileType.FILE_APK;
        }
        if(Arrays.asList(Constant.FilePostFix.OFFICE).contains(postFix)){
            return Constant.FileType.FIVE_OFFICE;
        }
        return Constant.FileType.FILE_IMG;
    }

    /**
     * 返回某目录下所有文件对象
     * @param str
     * @return
     */
    public static File[] getFiles(String str) {
        File dir = new File(StringUtil.utf8Decoding(str));
        File[] result = null;
        if (dir.isDirectory()) {
            result = dir.listFiles();
        }
        return result;
    }

    /**
     * 将oldFile移动到指定目录
     * @param oldFile
     * @param newDir
     * @return
     */
    public static boolean moveFileTo(File oldFile, String newDir) {
        StringBuilder sb = new StringBuilder(newDir);
        sb.append(File.separator).append(oldFile.getName());
        File toDir = new File(StringUtil.utf8Decoding(sb.toString()));
        boolean flag = false;
        if (!toDir.exists()) {
            flag = oldFile.renameTo(toDir);
        }
        return flag;
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     * @param sPath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean DeleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) { // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) { // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else { // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    /**
     * 删除单个文件
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查文件名是否合法
     * @param fileName
     * @return
     */
    public static boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.length() > 255)
            return false;
        else {
            return fileName.matches(
                    "[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
        }
    }

    /**
     * 取指定文件的扩展名
     * @param filePathName 文件路径
     * @return 扩展名
     */
    public static String getFileExt(String filePathName) {
        int pos = 0;
        pos = filePathName.lastIndexOf('.');
        if (pos != -1) {
            return filePathName.substring(pos + 1, filePathName.length());
        }
        else {
            return "";
        }
    }

    /**
     * 读取文件大小
     * @param filename 指定文件路径
     * @return 文件大小
     */
    public static int getFileSize(String filename) {
        try {
            File fl = new File(filename);
            int length = (int) fl.length();
            return length;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 判断是否是图片
     * @param file
     * @return
     */
    public static boolean isImage(File file) {
        boolean flag = false;
        try {
            ImageInputStream is = ImageIO.createImageInputStream(file);
            if (null == is) {
                return flag;
            }
            is.close();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 将图片转 Base64String 字符串
     * @param content
     * @return
     * @throws Exception
     */
    public static String image2Base64String(InputStream content) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int length = 0;
            byte[] buffer = new byte[1024];
            while((length = content.read(buffer)) > 0){
                out.write(buffer,0,length);
            }
        } finally {
            if (content != null) content.close();
            if(out != null) out.close();
        }
        //BASE64Encoder encoder = new BASE64Encoder();
        //return encoder.encode(out.toByteArray());
        // 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Encoder
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(out.toByteArray());
    }

    /**
     * 将图片 base64Strign字符串 转 byte 数组
    * @param base64String
     * @return
     * @throws Exception
     */
    public static byte[] base64String2Image(String base64String) throws Exception {
        if(ObjectUtils.isEmpty(base64String)) return null;
        base64String = base64String.replaceAll("data:image/(jpg|png|jpeg);base64,","");
        //BASE64Decoder decoder = new BASE64Decoder();
        //return decoder.decodeBuffer(base64String);
        // 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Decoder
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(base64String);
    }
    //------------------------------------ 文件 end -------------------------------------
}
