package formation.jaas.module;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import formation.jaas.SystemCredentialReader;
import formation.jaas.model.User;
import formation.jaas.principal.FormationPrincipal;

/**
 * <p>
 * This sample LoginModule authenticates users with a password.
 *
 * <p>
 * This LoginModule only recognizes one user: formation
 * <p>
 * testUser's password is: form@tion
 *
 * <p>
 * If testUser successfully authenticates itself, a
 * <code>FormationPrincipal</code> with the testUser's user name is added to the
 * Subject.
 *
 * <p>
 * This LoginModule recognizes the debug option. If set to true in the login
 * Configuration, debug messages will be output to the output stream,
 * System.out.
 *
 */
public class FormationLoginModule implements LoginModule {

	private static final String TEST_USER = "formation";
	// initial state
	private Subject subject;
	private CallbackHandler callbackHandler;
	@SuppressWarnings({ "rawtypes", "unused" })
	private Map sharedState;
	@SuppressWarnings({ "rawtypes", "unused" })
	private Map options;

	// configurable option
	private boolean debug = false;

	// the authentication status
	private boolean succeeded = false;
	private boolean commitSucceeded = false;

	// username and password
	private String username;
	private char[] password;

	// testUser's FormationPrincipal
	private FormationPrincipal userPrincipal;

	/**
	 * Initialize this <code>LoginModule</code>.
	 *
	 * <p>
	 *
	 * @param subject
	 *            the <code>Subject</code> to be authenticated.
	 *            <p>
	 *
	 * @param callbackHandler
	 *            a <code>CallbackHandler</code> for communicating with the end user
	 *            (prompting for user names and passwords, for example).
	 *            <p>
	 *
	 * @param sharedState
	 *            shared <code>LoginModule</code> state.
	 *            <p>
	 *
	 * @param options
	 *            options specified in the login <code>Configuration</code> for this
	 *            particular <code>LoginModule</code>.
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<java.lang.String, ?> sharedState,
			Map<java.lang.String, ?> options) {

		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;

		// initialize any configured options
		debug = "true".equalsIgnoreCase((String) options.get("debug"));
	}

	/**
	 * Authenticate the user by prompting for a user name and password.
	 *
	 * <p>
	 *
	 * @return true in all cases since this <code>LoginModule</code> should not be
	 *         ignored.
	 *
	 * @exception FailedLoginException
	 *                if the authentication fails.
	 *                <p>
	 *
	 * @exception LoginException
	 *                if this <code>LoginModule</code> is unable to perform the
	 *                authentication.
	 */
	public boolean login() throws LoginException {

		// prompt for a user name and password
		if (callbackHandler == null)
			throw new LoginException(
					"Error: no CallbackHandler available " + "to garner authentication information from the user");

		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("user name: ", TEST_USER);
		callbacks[1] = new PasswordCallback("password: ", false);

		try {
			// Retrieve or display the information requested in the provided Callbacks.
			//
			// The handle method implementation checks the instance(s) of the Callback
			// object(s) passed in to retrieve or display the requested information. The
			// following example is provided to help demonstrate what an handle method
			// implementation might look like. This example code is for guidance only. Many
			// details, including proper error handling, are left out for simplicity.

			callbackHandler.handle(callbacks);
			username = ((NameCallback) callbacks[0]).getName();
			char[] tmpPassword = ((PasswordCallback) callbacks[1]).getPassword();
			if (tmpPassword == null) {
				// treat a NULL password as an empty password
				tmpPassword = new char[0];
			}
			password = new char[tmpPassword.length];
			System.arraycopy(tmpPassword, 0, password, 0, tmpPassword.length);
			((PasswordCallback) callbacks[1]).clearPassword();

		} catch (java.io.IOException ioe) {
			throw new LoginException(ioe.toString());
		} catch (UnsupportedCallbackException uce) {
			throw new LoginException("Error: " + uce.getCallback().toString()
					+ " not available to garner authentication information " + "from the user");
		}

		// print debugging information
		if (debug) {
			System.out.println("\t\t[FormationLoginModule] " + "user entered user name: " + username);
			System.out.print("\t\t[FormationLoginModule] " + "user entered password: ");
			for (int i = 0; i < password.length; i++)
				System.out.print(password[i]);
			System.out.println();
		}

		SystemCredentialReader credentialReader = new SystemCredentialReader();
		User user = credentialReader.readCredentials();

		// verify the username/password
		boolean usernameCorrect = false;
		@SuppressWarnings("unused")
		boolean passwordCorrect = false;
		if (username.equals(user.getUsername()))
			usernameCorrect = true;

		// password[0] == 'f' &&
		// password[1] == 'o' &&
		// password[2] == 'r' &&
		// password[3] == 'm' &&
		// password[4] == '@' &&
		// password[5] == 't' &&
		// password[6] == 'i' &&
		// password[7] == 'o' &&
		// password[8] == 'n'
		if (usernameCorrect && password.length == 8 &&
				credentialReader.isPwdMatch(password)) {
			// authentication succeeded!!!
			passwordCorrect = true;
			if (debug)
				System.out.println("\t\t[FormationLoginModule] " + "authentication succeeded");
			succeeded = true;
			return true;
		} else {

			// authentication failed -- clean out state
			if (debug)
				System.out.println("\t\t[FormationLoginModule] " + "authentication failed");
			succeeded = false;
			username = null;
			for (int i = 0; i < password.length; i++)
				password[i] = ' ';
			password = null;
			if (!usernameCorrect) {
				throw new FailedLoginException("User Name Incorrect");
			} else {
				throw new FailedLoginException("Password Incorrect");
			}
		}
	}

	/**
	 * <p>
	 * This method is called if the LoginContext's overall authentication succeeded
	 * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
	 * succeeded).
	 *
	 * <p>
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> method), then
	 * this method associates a <code>FormationPrincipal</code> with the
	 * <code>Subject</code> located in the <code>LoginModule</code>. If this
	 * LoginModule's own authentication attempted failed, then this method removes
	 * any state that was originally saved.
	 *
	 * <p>
	 *
	 * @exception LoginException
	 *                if the commit fails.
	 *
	 * @return true if this LoginModule's own login and commit attempts succeeded,
	 *         or false otherwise.
	 */
	public boolean commit() throws LoginException {
		if (succeeded == false) {
			return false;
		} else {
			// add a Principal (authenticated identity)
			// to the Subject

			// assume the user we authenticated is the FormationPrincipal
			userPrincipal = new FormationPrincipal(username);
			if (!subject.getPrincipals().contains(userPrincipal))
				subject.getPrincipals().add(userPrincipal);

			if (debug) {
				System.out.println("\t\t[FormationLoginModule] " + "added FormationPrincipal to Subject");
			}

			// in any case, clean out state
			username = null;
			for (int i = 0; i < password.length; i++)
				password[i] = ' ';
			password = null;

			commitSucceeded = true;
			return true;
		}
	}

	/**
	 * <p>
	 * This method is called if the LoginContext's overall authentication failed.
	 * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules did
	 * not succeed).
	 *
	 * <p>
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> and
	 * <code>commit</code> methods), then this method cleans up any state that was
	 * originally saved.
	 *
	 * <p>
	 *
	 * @exception LoginException
	 *                if the abort fails.
	 *
	 * @return false if this LoginModule's own login and/or commit attempts failed,
	 *         and true otherwise.
	 */
	public boolean abort() throws LoginException {
		if (succeeded == false) {
			return false;
		} else if (succeeded == true && commitSucceeded == false) {
			// login succeeded but overall authentication failed
			succeeded = false;
			username = null;
			if (password != null) {
				for (int i = 0; i < password.length; i++)
					password[i] = ' ';
				password = null;
			}
			userPrincipal = null;
		} else {
			// overall authentication succeeded and commit succeeded,
			// but someone else's commit failed
			logout();
		}
		return true;
	}

	/**
	 * Logout the user.
	 *
	 * <p>
	 * This method removes the <code>FormationPrincipal</code> that was added by the
	 * <code>commit</code> method.
	 *
	 * <p>
	 *
	 * @exception LoginException
	 *                if the logout fails.
	 *
	 * @return true in all cases since this <code>LoginModule</code> should not be
	 *         ignored.
	 */
	public boolean logout() throws LoginException {

		subject.getPrincipals().remove(userPrincipal);
		succeeded = false;
		succeeded = commitSucceeded;
		username = null;
		if (password != null) {
			for (int i = 0; i < password.length; i++)
				password[i] = ' ';
			password = null;
		}
		userPrincipal = null;
		return true;
	}
}