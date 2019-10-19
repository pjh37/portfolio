package DatabaseUtil;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class Gmail extends Authenticator{
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication("epictroll2018@gmail.com","qkrwlgy37@#");
	}
}
