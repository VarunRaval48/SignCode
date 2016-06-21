package testing;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignPatternTest {

	public static void main(String args[]) throws Exception {

		BufferedInputStream bStream = new BufferedInputStream(
				new FileInputStream("/home/varun/Documents/projects/GSoC/T1_testData/signed_files/hello.js"));

		int c;
		StringBuffer strBuf = new StringBuffer();
		while ((c = bStream.read()) != -1) {
			strBuf.append((char) c);
		}

		/*Signature to be tested must be in this format

		beginCommentString
		Hash:hash Provider:provider

		signature(64)bytes

		certificate(multiple lines, each line having 48 bytes except last line)(certificates separated by :(colon) )

		endCommentString

		For trying directly:
		For signature only: \n.+\n-----BEGIN SIGNATURE-----\n(Hash:[\w^_]+ Provider:[\w^_]+)\n\n([\w+/=^_]{64})\n\n(([\w+/=^_:]{48}\n)+[\w+/=^_:]*)\n\n-----END SIGNATURE-----\n.+
		For contents ahead: ([\s\S]+)(?=(\n.+\n-----BEGIN SIGNATURE-----\n(Hash:[\w^_]+ Provider:[\w^_]+)\n\n([\w+/=^_]{64})\n\n(([\w+/=^_:]{48}\n)+[\w+/=^_:]*)\n\n-----END SIGNATURE-----\n.+))
		 */
		// String pattern = "(\n.+\n-----BEGIN SIGNATURE-----\n(Hash:[\\w^_]+ Provider:[\\w^_]+)\n\n([\\w+/=^_]{64})\n\n(([\\w+/=^_:]{48}\n)+[\\w+/=^_:]*)\n\n-----END SIGNATURE-----\n.+\n)";

		String pattern = "([\\s\\S]*)(?=(\n.+\n-----BEGIN SIGNATURE-----\n(Hash:[\\w^_]+ Provider:[\\w^_]+)\n\n([\\w+/=^_]{64})\n\n(([\\w+/=^_:]{48}\n)+[\\w+/=^_:]*)\n\n-----END SIGNATURE-----\n.+\n))";

		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(strBuf.toString());

		System.out.print(m.find());
		//		System.out.println(m.groupCount());

		String contentAhead = m.group(1);
		String signBlock = m.group(2);
		String signParam = m.group(3);
		String signature = m.group(4);
		String certificate = m.group(5);

		System.out.print(contentAhead);
		System.out.println(signBlock);
		System.out.println(signParam);
		System.out.println(signature);
		System.out.print(certificate);

		bStream.close();

		bStream = new BufferedInputStream(
				new FileInputStream("/home/varun/Documents/projects/GSoC/T1_testData/signed_files/hello.js"));

		int i = 0, end = m.end(2);
		do {
			bStream.read();
			i++;
		} while (i < end - 1);

		StringBuffer cBuffer = new StringBuffer();

		while ((c = bStream.read()) != -1) {
			cBuffer.append((char) c);
		}

		String endContent = cBuffer.toString();

		System.out.print(endContent);
	}
}