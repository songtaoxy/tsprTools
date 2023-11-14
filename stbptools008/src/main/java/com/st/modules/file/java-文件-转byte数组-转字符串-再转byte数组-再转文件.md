- [[java-string与byte数组回转不一致问题]]

# Case

> java中, 现将文件转成byte[], 然后将byte[]转成String, 然后将String重新转成byte[], 最后将byte[]写成文件

好的,java中文件转换为byte数组再转换为String,最后重新写入文件的代码如下:

```java
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FileTransform {

  public static void main(String[] args) throws IOException {
    // 读取文件为字节数组
    File file = new File("test.txt");
    byte[] fileBytes = new byte[(int)file.length()];
    FileInputStream fis = new FileInputStream(file);
    fis.read(fileBytes);
    fis.close();
    
    // 字节数组转为Base64编码的String
    String encodedStr = Base64.getEncoder().encodeToString(fileBytes); 

    // String转为字节数组
    byte[] decodedBytes = Base64.getDecoder().decode(encodedStr);

    // 字节数组写入新文件 
    File newFile = new File("test_new.txt");
    FileOutputStream fos = new FileOutputStream(newFile);
    fos.write(decodedBytes);
    fos.close();

    System.out.println("文件转换完成");
  }

}
```

转换的关键是:

1. 使用FileInputStream读取文件为字节数组
2. 使用Base64编码为String 
3. 使用Base64解码String为字节数组
4. 使用FileOutputStream将字节数组写入新文件

这样实现了文件到字节数组到String再到字节数组的双向转换。