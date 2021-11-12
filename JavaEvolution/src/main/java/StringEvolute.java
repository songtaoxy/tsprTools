/**
 * @author: st
 * @date: 2021/11/12 20:33
 * @version: 1.0
 * @description:
 */
public class StringEvolute {


	public static void main(String[] args) {
	}


	/**
	 * String: 文本块
	 *
	 * @since java 15
	 */
	public static void strBlock(){
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
