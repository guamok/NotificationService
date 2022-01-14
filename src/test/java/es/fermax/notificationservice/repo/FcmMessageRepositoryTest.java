package es.fermax.notificationservice.repo;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import es.fermax.notificationservice.enums.TargetEnum;
import es.fermax.notificationservice.model.FcmMessage;

@RunWith(SpringRunner.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class FcmMessageRepositoryTest {

	// @Autowired
	// FcmMessageRepository subject;

	@Test
	public void saveTest() {

		FcmMessage msg = new FcmMessage();
		msg.setTarget(TargetEnum.TOKEN.target);
		msg.setTargetValue("tokenvalue");

		Map<String, String> dataEntries = new HashMap<>();
		dataEntries.put("property1", "value1");
		msg.setData(dataEntries);

		Assert.assertNotNull(msg);

		// FcmMessage msgSaved = subject.save(msg);
		// String SENTBYFCM_1 = "sentbyfcm1";
		// msgSaved.setFcmId(SENTBYFCM_1);

		// subject.save(msgSaved);

		// System.out.println("MSG:" + msgSaved.toString());

		// Assert.assertNotNull(msgSaved);
		// Assert.assertNotNull(msgSaved.getId());

		// Assert.assertNotNull(msgSaved.getData());

		// Optional<FcmMessage> found = subject.findByFcmId(SENTBYFCM_1);

		// Assert.assertTrue(found.isPresent());
	}

}