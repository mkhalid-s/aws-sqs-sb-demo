/**
 * 
 */
package com.example.demo;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageResult;

/**
 * @author mshaikh4
 *
 */
@Controller
public class QueueController {

	@Autowired
	Environment environment;

	// @Value("${aws.accesskey}")
	private String accessKeyId;

	// @Value("${aws.secretkey}")
	private String secretAccessKey;

	// @Value("${aws.sessiontoken}")
	private String sessionToken;

	private BasicSessionCredentials credentials = null;

	private AmazonSQS sqlClient = null;

	
	@PostConstruct
	public void createQueue() {

		if (credentials == null) {

			credentials = new BasicSessionCredentials(getAccessKeyId(), getSecretAccessKey(), getSessionToken());

			sqlClient = AmazonSQSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
					.withRegion(Regions.US_EAST_1).build();
		}

		if (credentials != null && credentials.getAWSAccessKeyId() != null && credentials.getAWSSecretKey() != null
				&& credentials.getSessionToken() != null && sqlClient != null
				&& sqlClient.listQueues("test-queue") != null) {
			CreateQueueResult res = sqlClient.createQueue("test-queue");
			System.out.println(res.getQueueUrl());
		}
	}

	@GetMapping("/send")
	public ResponseEntity<String> sendMessage(@RequestParam(name = "msg") String messageBody) {
		if (sqlClient != null) {
			CreateQueueResult res = sqlClient.createQueue("test-queue");
			SendMessageResult result = sqlClient.sendMessage(res.getQueueUrl(), messageBody);
			System.out.println(result.toString());
			return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
		}
		return null;
	}

	@GetMapping("/receive")
	public ResponseEntity<String> receiveMessage() {
		if (sqlClient != null) {
			CreateQueueResult res = sqlClient.createQueue("test-queue");
			ReceiveMessageRequest req = new ReceiveMessageRequest();
			req.setMaxNumberOfMessages(10);
			req.setQueueUrl(res.getQueueUrl());
			req.setWaitTimeSeconds(20);
			ReceiveMessageResult result = sqlClient.receiveMessage(req);
			System.out.println(result.toString());
			
			java.util.List<Message> listMsg = result.getMessages();
			for (Message message : listMsg) {
				System.out.println(message.getBody());
				sqlClient.deleteMessage(res.getQueueUrl(), message.getReceiptHandle());
			}
			
			return new ResponseEntity<String>(listMsg.toString(), HttpStatus.OK);
		}
		return null;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	@Value("${aws.accesskey}")
	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getSecretAccessKey() {
		return secretAccessKey;
	}

	@Value("${aws.secretkey}")
	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	@Value("${aws.sessiontoken}")
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}
	

}
