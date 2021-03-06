package com.yjyz.iot.mq.consumer.action;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjyz.iot.mq.producer.queue.QueueSender;
import com.yjyz.iot.mq.producer.topic.TopicSender;

@Controller
@RequestMapping("/activemq")
public class ActivemqController {

	@Resource
	QueueSender queueSender;
	@Resource
	TopicSender topicSender;

	@ResponseBody
	@RequestMapping("queueSender")
	public String queueSender(@RequestParam("message") String message) {
		String opt = "";
		try {
			queueSender.send("YJIOT.queue", message);
			opt = "suc";
		} catch (Exception e) {
			opt = e.getCause().toString();
		}
		return opt;
	}

	@ResponseBody
	@RequestMapping("topicSenderD2C")
	public String topicSenderD2C(@RequestParam("message") String message) {
		String opt = "";
		try {
			topicSender.send("d2c.95017b74-893e-11e7-9baf-00163e120d98.status", message);
			opt = "suc";
		} catch (Exception e) {
			opt = e.getCause().toString();
		}
		return opt;
	}

	@ResponseBody
	@RequestMapping("topicSenderC2D")
	public String topicSenderC2D(@RequestParam("message") String message) {
		String opt = "";
		try {
			topicSender.send("c2d.95017b74-893e-11e7-9baf-00163e120d98.commands", message);
			opt = "suc";
		} catch (Exception e) {
			opt = e.getCause().toString();
		}
		return opt;
	}
}
