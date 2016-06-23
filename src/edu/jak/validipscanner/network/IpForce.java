package edu.jak.validipscanner.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import edu.jak.validipscanner.network.pojo.IpResponse;

public class IpForce {

	private List<String> generateIpList() {
		System.out.println("Ip List Started To Generation ...");
		List<String> ipList = new ArrayList<String>();
		for (int i = 1; i < Constants.FIRST_TAIL_LIMIT; i++) {
			for (int j = 1; j < Constants.SECOND_TAIL_LIMIT; j++) {
				String genIp = Constants.IP_HEADER_OF_ISTANBUL + "." + i + "." + j;
				ipList.add(genIp);
			}
		}
		System.out.println("All Ip's Generated ...");

		return ipList;
	}

	private List<String> generateIpListWithUserInput(String ipHeader) {
		System.out.println("Ip List Started To Generation ...");
		List<String> ipList = new ArrayList<String>();
		for (int i = 1; i < Constants.SECOND_TAIL_LIMIT; i++) {
			String genIp = ipHeader + "." + i;
			ipList.add(genIp);
		}
		System.out.println("All Ip's Generated ...");

		return ipList;
	}

	private HttpClient getProperlySetClient() {
		HttpClient httpClient = new HttpClient();
		// HttpConnectionParams httpConnectionParams = new
		// HttpConnectionParams();
		// httpConnectionParams.setConnectionTimeout(Constants.FILE_URL_CONNECTION_TIMEOUT);
		// httpConnectionParams.setSoTimeout(Constants.HTTP_SO_TIMEOUT);
		// httpClient.setParams(new HttpClientParams(httpConnectionParams));
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(Constants.FILE_URL_CONNECTION_TIMEOUT);
		System.out.println("Client Set Properly ...");
		return httpClient;
	}

	private String getSingleIpResponse(String forcedIp, HttpClient client) {

		GetMethod method = new GetMethod(forcedIp);
		try {
			client.executeMethod(method);
			InputStream responseBodyAsInputStream = method.getResponseBodyAsStream();
			String responseBodyAsString = convertCharStreamToString(responseBodyAsInputStream);

			return responseBodyAsString;
		} catch (Exception e) {
		}
		return "FailedX";

	}

	private String convertCharStreamToString(InputStream responseBodyAsInputStream) throws IOException {
		StringBuffer sb = new StringBuffer();
		int b = 0;
		while ((b = responseBodyAsInputStream.read()) != -1) {
			sb.append((char) b);
		}
		return sb.toString();
	}

	private List<IpResponse> generateMultipleIpRequests(List<String> ipList, HttpClient client) {
		List<IpResponse> ipResponseList = new ArrayList<IpResponse>();
		System.out.println("Started To Multiple Ip Requests ...");
		for (String ip : ipList) {
			String response = getSingleIpResponse(ip, client);

			if (response.length() > 100) {
				// System.out.println("Succeed Response... -> " + ip);
				System.out.println(ip);
				IpResponse ipResponse = new IpResponse();
				ipResponse.setIp(ip);
				ipResponse.setResponse(response);
				ipResponseList.add(ipResponse);
			} else {
				// System.out.println("Single Response is smaller than 100! ");
			}
		}
		System.out.println("End Of Multiple Ip Requests ...");
		return ipResponseList;
	}

	private List<IpResponse> createClientAndReturnResponse(String ipHeader) {
		HttpClient client = getProperlySetClient();
		List<String> ipList = generateIpListWithUserInput(ipHeader);
		List<IpResponse> ipResponseList = generateMultipleIpRequests(ipList, client);
		for (IpResponse ipResponse : ipResponseList) {
			System.out.println("IPResponse : IP -" + ipResponse.getIp());// +
			// ",Response -"
			// +
			// ipResponse.getResponse());
		}

		return ipResponseList;
	}

	public static void main(String[] args) {
		System.out.print("Enter First 3 part of IP4 address like http://88.246.1 : \n");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String ipheader = null;

		try {
			ipheader = br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO error trying to read your name!");
			System.exit(1);
		}

		IpForce ipForce = new IpForce();
		ipForce.createClientAndReturnResponse(ipheader);
	}

}
