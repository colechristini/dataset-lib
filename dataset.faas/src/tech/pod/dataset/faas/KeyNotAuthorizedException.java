package tech.pod.dataset.faas;


public class KeyNotAuthorizedException extends Exception {

    private static final long serialVersionUID = 1L;

	public KeyNotAuthorizedException(String unauthorizedKey) {
        super(unauthorizedKey);
    }

}