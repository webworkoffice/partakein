package in.partake.mashup.hatena;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public final class HatenaBookmarkCountLoader {
	private static final URL XML_RPC_SERVER_URL;
	private static final String METHOD_NAME_TOTAL = "bookmark.getTotalCount";

	static {
		try {
			XML_RPC_SERVER_URL = new URL("http://b.hatena.ne.jp/xmlrpc");
		} catch (MalformedURLException notExpected) {
			throw new AssertionError(notExpected);
		}
	}

	/**
	 * <p>指定したサイトの被ブックマーク数を返す。
	 * 1ページだけでなく、サイトに含まれるすべてのページに対するブックマーク数の合計を返すことに注意。
	 * 
	 * <p>このメソッドははてなのサーバに対してXML-RPCを実行するものであり、
	 * 数多くあるデータ取得用メソッドの中でも特に低速であることに留意されたい。
	 *
	 * @param url ブックマーク数を調べるURL
	 * @return サイト全体の被ブックマーク数
	 * @throws RuntimeException APIが正常に動作しなかった場合
	 * @author eller86
	 * @see http://developer.hatena.ne.jp/ja/documents/bookmark/apis/getcount#total
	 */
	public int loadCountOfAllPages(String url) {
		if (url == null) {
			throw new NullPointerException("url should be not null.");
		}
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(XML_RPC_SERVER_URL);
		XmlRpcClient client = new XmlRpcClient();

		try {
			Object response = client.execute(config, METHOD_NAME_TOTAL, new String[]{url});
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
