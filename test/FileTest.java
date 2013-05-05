import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileTest {
  @Rule
  public TemporaryFolder temp = new TemporaryFolder();
 
  @Test
  public void basicTest() throws IOException {
    //the temporary folder is created before this test method runs
      String tempPath = temp.getRoot().toString();
      System.out.format("created temp folder: %s", tempPath);
 
    File fileWithoutName = temp.newFile();
    File fileWithName = temp.newFile("myfile.txt");
 
    File dirWithoutName = temp.newFolder();
    File dirWithName = temp.newFolder("myfolder");

      File myFolder2 = new File(dirWithName, "myFolder2");
      myFolder2.mkdir();

      File myFile3 = new File(myFolder2, "myFile3.txt");
      fillFileToSize(myFile3, 3);

      File fileInsideCreatedDir = new File(dirWithName, "myfile2.txt");

    fillFileToSize(fileInsideCreatedDir, 1 );

    assert(fileInsideCreatedDir != null);
 
    //the temporary folder is deleted when this test method finishes
  }
    private void fillFileToSize(File file,int size) throws IOException {
        FileOutputStream s = new FileOutputStream(file);
        byte[] buf = new byte[size*1024*1024];
        s.write(buf);
        s.flush();
        s.close();
    }

}