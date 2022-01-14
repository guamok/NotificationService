package es.fermax.notificationservice.repo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import es.fermax.notificationservice.model.AppToken;

@RunWith(SpringRunner.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AppTokenRepositoryTest {

	// @Autowired
	// AppTokenRepository subject;

	@Test
	public void saveTest() {

		AppToken token = new AppToken(1000, "token1");

		Assert.assertNotNull(token);

		// AppToken savedToken = subject.save(token);

		// Assert.assertNotNull(savedToken);

		// Optional<AppToken> found = subject.findByTokenAndUserId("token1", 1000);

		// Assert.assertTrue(found.isPresent());
	}
}
