package com.st.api.practice.javadoc;

import java.io.IOException;
/**   ,
 * <h1>Add Two Numbers!</h1>
 *
 * The AddNum program implements an application that simply adds two given integer numbers and
 * Prints the output on the screen.
 * <P>
 * <P>
 * <b>Note:</b> Giving proper comments in your program makes it more user friendly and it is
 * assumed as a high quality code.
 * <p>
 * @date 12
 * @newT    ags
 * @custom.mytag hey ho...
 * <P>
 * {@code <h1> title h1 </h1>}
 * <P>
 * @see com.st.api.practice.javadoc.AddNum#minus(int, int)
 * <P>
 * @author st
 * @version 2021.11.10
 * @since 2021.11.10
 * @dateTime 1234
 */
public class AddNum {


  /**
   * 博客地址
   * <p>
   *{ @value h}
   */
    public static final String BLOG="www.andyqian.com";

  /**
   * This method is used to add two integers. This is a the simplest form of a class method, just to
   * show the usage of various javadoc Tags.
   *
   * <p>
   *
   * <pre>{@code
   * Person[] men = people.stream()
   *                    .filter(p -> p.getGender() == MALE)
   *                    .toArray(Person[]::new);
   * }</pre>
   *<p>
   * @see com.st.api.practice.javadoc.AddNum#minus(int, int) <br>
   * @see <a href="http://www.andyqian.com">博客地址</a>
   *     <p>{@link com.st.api.practice.javadoc.AddNum#minus(int, int)}
   *     <p>
   * @param numA This is the first paramter to addNum method
   * @param numB This is the second parameter to addNum method
   * @return int This returns sum of numA and numB.
   *
   * @deprecated since 2021.11.10
   */
  public int addNum(int numA, int numB) {
    return numA + numB;
  }

  /**
   * @cusATag 111
   * @date 1222
   * @param A
   * @param B
   */
  public static void minus(int A, int B) {
    System.out.println("result");
  }

  /**
   * This is the main method which makes use of addNum method.
   *
   * @param args Unused.
   * @exception IOException On input error.
   * @see IOException
   */
  public static void main(String args[]) throws IOException {

    AddNum obj = new AddNum();
    int sum = obj.addNum(10, 20);

    System.out.println("Sum of 10 and 20 is :" + sum);
  }
}
