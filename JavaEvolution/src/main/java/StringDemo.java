/**
 * @author: st
 * @date: 2021/11/12 20:33
 * @version: 1.0
 * @description:
 */
public class StringDemo {


	public static void main(String[] args) {
		String str = """
				hi, this is good
				ideas
				do 
				you 
				have
				""";
		System.out.println(str);


		String jsonStr = """
				{
				"k1":"v1",
				"k2":"v2",
				"k3":"v3",
				}
				""";

		System.out.println(jsonStr);
	}

}
