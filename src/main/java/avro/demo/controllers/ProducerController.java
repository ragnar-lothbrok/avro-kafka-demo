package avro.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import avro.demo.model.Feed;
import avro.demo.services.ProducerService;

@RestController
@RequestMapping(
	value = "/produce")
public class ProducerController {

	@Autowired
	private ProducerService producerService;

	@RequestMapping(
		method = RequestMethod.POST)
	public boolean sendMessage(@RequestBody Feed message) {
		return producerService.dispatch(message);
	}

}
