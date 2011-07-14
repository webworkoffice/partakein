package in.partake.model.dao;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class HatenaDao {
	private final URL XML_RPC_SERVER_URL;

	public HatenaDao() {
		try {
			XML_RPC_SERVER_URL = new URL("http://b.hatena.ne.jp/xmlrpc");
		} catch (MalformedURLException notExpected) {
			throw new AssertionError(notExpected);
		}
	}

	/**
	 * 指定したサイトの被ブックマーク数を返す。
	 * 1ページだけでなく、サイトに含まれるすべてのページに対するブックマーク数の合計を返すことに注意。
	 * 
	 * @param url ブックマーク数を調べるURL
	 * @return 指定したURLの被ブックマーク数
	 * @throws RuntimeException APIが正常に動作しなかった場合
	 * @see http://developer.hatena.ne.jp/ja/documents/bookmark/apis/getcount#total
	 */
	public int getTotalBookmarkCount(String url) throws RuntimeException {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(XML_RPC_SERVER_URL);
		XmlRpcClient client = new XmlRpcClient();
		String methodName = "bookmark.getTotalCount";

		try {
			Object response = client.execute(config, methodName, new String[]{url});
			if (!(response instanceof Integer)) {
				throw new RuntimeException(String.format(
						"API response(%s) is invalid. API server responsed %s but we need Integer.",
						response.toString(),
						response.getClass()
				));
			}
			Integer bookmarkCount = (Integer) response;
			return bookmarkCount.intValue();
		} catch (XmlRpcException e) {
			throw new RuntimeException(e);
		}
	}
}
