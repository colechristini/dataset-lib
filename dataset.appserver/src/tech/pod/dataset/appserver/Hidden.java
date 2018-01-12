package tech.pod.dataset.appserver;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/*@Hidden marks a method for an AppServlet that should not be accessible over the server. 
AppServer will check if the method has this annoation and if so, it won't put it in the 'methods' ConcurrentHashMap' 
of an AppServer.
*/
public @interface Hidden{}