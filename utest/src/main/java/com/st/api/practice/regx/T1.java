package com.st.api.practice.regx;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: st
 * @date: 2023/1/17 16:56
 * @version: 1.0
 * @description:
 */
public class T1 {

	public static void main(String[] args) {

		String src = "mask_path: \"${0[0]}_${0[1]}_${0[2]}_${0[3]}_json_${0[channel]}.png\"\n" +
				"sensor_name: \"0\"\n" +
				"calibration_file_path: \"cam_ground_extrinsic_calib_result_${0[2]}_${0[3]}_${0[channel]}.yaml\"";

		String s = pullconfigParser(src, null);
		System.out.println(s);
	}



	public static String pullconfigParser(String strSrc, String poleId) {

		String strReplaced = strSrc;
		Preconditions.checkArgument(ObjectUtil.isNotEmpty(strSrc), "配置下发:配置项内容不能为 " + strSrc);


		// 正则:  ${number}
		String rex_slot_number = "\\$\\{(\\d+)}";
		// 正则: ${number[number]}
		String rex_s_ip = "\\$\\{(\\d+)\\[(\\d)]}";
		// 正则: ${number[channel]}
		String rex_s_chanel = "\\$\\{(\\d+)\\[(channel)]}";
		ArrayList<String> rexs = new ArrayList<>();
		rexs.add(rex_slot_number);
		rexs.add(rex_s_ip);
		rexs.add(rex_s_chanel);

		for (String rex : rexs) {
			Pattern compile = Pattern.compile(rex);
			Matcher matcher = compile.matcher(strReplaced);
			int i = matcher.groupCount();

			if (i == 1) {
				while (matcher.find()) {
					String group = matcher.group();
					String group1 = matcher.group(1);

					//DeviceExVo deviceExVo = getDevicewithPoleId_slotNumb(poleId, group1);
					//String ip = deviceExVo.getIp();
					String ip = "172.20.40.4";

					//strReplaced = matcher.replaceAll(ip);
					//strReplaced = strReplaced.replaceAll(group,ip);
					strReplaced = StrUtil.replace(strReplaced,group,ip);
				}
			} else if (i == 2) {
				while (matcher.find()) {
					String group = matcher.group();
					String group1 = matcher.group(1);
					String group2 = matcher.group(2);

					//DeviceExVo deviceExVo = getDevicewithPoleId_slotNumb(poleId, group1);
					//String ip = deviceExVo.getIp();
					String ip = "172.20.40.4";
					String[] ip_splits = ip.split("\\.");

					//String channel = deviceExVo.getChannel();
					String channel = "1";

					// 正则: ${number[number]}
					if (isNumeric(group2)) {
						String ip_part = ip_splits[Integer.parseInt(group2)];
						//strReplaced = matcher.replaceAll(ip_part);
						//strReplaced = strReplaced.replaceAll(group,ip_part);
						strReplaced = StrUtil.replace(strReplaced,group,ip_part);

					} else if (ObjectUtil.equals("channel", group2)) {
						// 正则: ${number[channel]}
						//strReplaced = matcher.replaceAll(channel);
						//strReplaced = strReplaced.replaceAll(group,channel);
						strReplaced = StrUtil.replace(strReplaced,group,channel);
					}

				}
			}


		}


		return strReplaced;
	}
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

}
