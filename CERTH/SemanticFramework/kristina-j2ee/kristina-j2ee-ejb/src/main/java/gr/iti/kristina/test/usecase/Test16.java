package gr.iti.kristina.test.usecase;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import gr.iti.kristina.test.Base;

public class Test16 extends Base {

	@Override
	protected String getFileName() {
		return "dm2ki-output_16.ttl";
	}
	
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		Test16 t = new Test16();
		t.call();
	}

}
