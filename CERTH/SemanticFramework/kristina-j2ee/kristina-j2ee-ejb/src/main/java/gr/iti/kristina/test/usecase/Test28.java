package gr.iti.kristina.test.usecase;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import gr.iti.kristina.test.Base;

public class Test28 extends Base {

	@Override
	protected String getFileName() {
		return "dm2ki-output_28.ttl";
	}
	
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		Test28 t = new Test28();
		t.call();
	}

}
