package com.st.modules.file.wp.mogo;



import com.st.modules.file.FileTypeEnum;
import com.st.modules.json.jackson.JacksonUtils;

import java.util.List;


/**
 * @author: st
 * @date: 2023/11/6 13:21
 * @version: 1.0
 * @description:
 */
public class FileList {

	public static void main(String[] args) {

		System.out.println("hi");

		String fileStrs = "{\n" +
				"  \"fileList\": [\n" +
				"    {\n" +
				"      \"name\": \"a.pdf\",\n" +
				"      \"type\": \"pdf\",\n" +
				"      \"contentsStr\": \"we are good\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"name\": \"b.xml\",\n" +
				"      \"type\": \"xml\",\n" +
				"      \"contentsStr\": \"xxxxx\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"name\": \"c.zip\",\n" +
				"      \"type\": \"zip\",\n" +
				"      \"contentsStr\": \"xxxxx\"\n" +
				"    }\n" +
				"  ]\n" +
				"}";


		List<FilesInput> filesInputs = FileInputUitls.parseFileStrs(fileStrs);
		System.out.println(filesInputs.toString());
		for (FilesInput filesInput: filesInputs) {
			//System.out.println(filesInput.toString());

			//FileInputTools.o2j(filesInput);
//			System.out.println(	GsonUtils.o2j(filesInput));
		}

		FilesInput filesInput = FileInputUitls.queryTargetObj(filesInputs);
		System.out.println(filesInput.toString());

		System.out.println("-------------------------");
		FilesInput filesInput1 = JacksonUtils.convert(filesInput, FilesInput.class);
		System.out.println(filesInput1);
		System.out.println("-------------------------");

		System.out.println(FileTypeEnum.PDF.getCode());


	}



}
