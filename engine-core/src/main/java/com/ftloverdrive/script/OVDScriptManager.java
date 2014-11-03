package com.ftloverdrive.script;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import bsh.BshMethod;
import bsh.CallStack;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.Primitive;
import bsh.UtilEvalError;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Logger;
import com.ftloverdrive.util.TextUtilities;


/**
 * Interprets scripts.
 *
 * TODO: Stub
 *
 * @see bsh.Interpreter
 */
public class OVDScriptManager {
	private Logger log;
	private Interpreter bsh;


	public OVDScriptManager() {
		log = new Logger( OVDScriptManager.class.getCanonicalName(), Logger.INFO );
		bsh = new Interpreter();
	}

	/**
	 * Returns a new NameSpace object that can be used to hold variables and methods loaded
	 * by scripts that use the same namespace. Global namespace is still visible.
	 * 
	 * Remember to call namespace.clear() when done.
	 * 
	 * @param name identifier of the namespace
	 */
	public NameSpace requestNewNameSpace( String name ) {
		return new NameSpace( bsh.getNameSpace(), name );
	}

	/**
	 * Returns a new NameSpace object that can be used to hold variables and methods loaded
	 * by scripts that use the same namespace. Global namespace is still visible.
	 * 
	 * Remember to call namespace.clear() when done.
	 * 
	 * @param name identifier of the namespace
	 * @param map
	 *            A map of identifiers to objects -- the script will be able to access the objects
	 *            by these identifiers. Basically, a way to setup variables available in the namespace.
	 */
	public NameSpace requestNewNameSpace( String name, Map<String, Object> vars ) {
		NameSpace ns = new NameSpace( bsh.getNameSpace(), name );

		try {
			for ( Map.Entry<String, Object> entry : vars.entrySet() ) {
				ns.setVariable( entry.getKey(),
						Primitive.wrap( entry.getValue(), entry.getValue().getClass() ), false );
			}
		} catch ( UtilEvalError e ) {
			log.error( "Error while evaluating variables.", e );
		}

		return ns;
	}

	/**
	 * Evaluates a script file in the global namespace.
	 *
	 * @see TextUtilities.decodeText(InputStream srcStream, String srcDescription)
	 */
	public void eval( FileHandle f ) throws IOException, EvalError {
		InputStream is = null;
		try {
			is = f.read();
			bsh.eval( TextUtilities.decodeText( is, f.name() ).text );
		}
		finally {
			try {if ( is != null ) is.close();}
			catch ( IOException e ) {}
		}
	}

	/**
	 * Evaluates a script file in the specified namespace (global namespace is still visible).
	 * 
	 * @see TextUtilities.decodeText(InputStream srcStream, String srcDescription)
	 */
	public void eval( FileHandle f, NameSpace ns ) throws EvalError, IOException {
		InputStream is = null;
		try {
			is = f.read();
			bsh.eval( TextUtilities.decodeText( is, f.name() ).text, ns );
		}
		finally {
			try {if ( is != null ) is.close();}
			catch ( IOException e ) {}
		}
	}

	/**
	 * Creates and returns a dynamic proxy for the specified interface type.
	 * 
	 * This should allow to create new objects from scripts that can then be
	 * used in Java code like normal objects.
	 * 
	 * However, objects created this way are not required to contain all
	 * methods specified by the interface.
	 * 
	 * @param ns namespace containing the methods from which to create the object
	 * @param interfClass the desired interface
	 */
	public <T> T getInterface( NameSpace ns, Class<T> interfClass ) {
		if ( !interfClass.isInterface() )
			throw new IllegalArgumentException( "Not an interface: " + interfClass.getName() );
		Object result = ns.getThis( bsh ).getInterface( interfClass );
		if ( result == null ) return null;
		return interfClass.cast( result );
	}

	/**
	 * Invokes the specified method with the specified arguments and returns its value.
	 * 
	 * This method wraps arguments with Beanshell's wrappers as needed.
	 * 
	 * @param ns namespace containing the method
	 * @param methodName name of the invoked method
	 * @param args arguments to pass to the method
	 * @return what the invoked method returned
	 * 
	 * @throws NoSuchMethodException when the requested method could not be found
	 */
	public Object invoke( NameSpace ns, String methodName, Object... args )
			throws EvalError, NoSuchMethodException {
		// Wrap args in Beanshell's wrappers
		for ( int i = 0; i < args.length; i++ )
			args[i] = Primitive.wrap( args[i], args[i].getClass() );

		// Construct param signature
		Class[] sig = new Class[args.length];
		for ( int i = 0; i < args.length; i++ )
			sig[i] = args[i].getClass();

		BshMethod method = null;
		try {
			method = ns.getMethod(methodName, sig, true);
		}
		catch ( UtilEvalError e ) {
			// UtilEvalErrors are EvalErrors that have no interpreter context
			// They need to be re-thrown and supplied the current context
			throw e.toEvalError( null, new CallStack( ns ) );
		}

		if ( method == null )
			throw new NoSuchMethodException();

		return method.invoke( args, bsh );
	}
}
