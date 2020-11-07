/**
 * 
 */
package com.example.demo;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author mshaikh4
 *
 */
@Controller
public class MainController {

	@Autowired
	Environment environment;

	@GetMapping("/test")
	public ResponseEntity<String> name() throws UnknownHostException {
		// Local address
		try {

			System.out.println(InetAddress.getLocalHost().getHostAddress());
			System.out.println(InetAddress.getLocalHost().getHostName());
			System.out.println(InetAddress.getLoopbackAddress().getHostAddress());
			System.out.println(InetAddress.getLoopbackAddress().getHostName());

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity<String>("Hi there, you have launched the web application successfully! on "
				+ InetAddress.getLocalHost().getHostName() + ". Volla! on "
				+ InetAddress.getLocalHost().getHostAddress(), HttpStatus.OK);
	}

}
