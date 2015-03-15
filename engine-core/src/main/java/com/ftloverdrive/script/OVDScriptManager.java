package com.ftloverdrive.script;

import java.io.IOException;
import java.io.InputStream;

import bsh.BshMethod;
import bsh.CallStack;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.Primitive;
import bsh.UtilEvalError;

import com.badlogic.gdx.files.FileHandle;
import com.ftloverdrive.util.TextUtilities;


/**
 * Interprets scripts.
 *
 * TODO: Stub
 *
 * @see bsh.Interpreter
 */
public class OVDScriptManager {
	private Interpreter bsh;

	public OVDScriptManager() {
		bsh = new Interpreter();
	}

	/**
	 * Evaluates a script file in the specified namespace (global namespace is still visible).
	 * 
	 * @see TextUtilities.decodeText(InputStream srcStream, String srcDescription)
	 */
	public ScriptResource eval( FileHandle f ) throws EvalError, IOException {
		InputStream is = null;
		String id = f.path();
		NameSpace ns = new NameSpace( bsh.getNameSpace(), id );
		ScriptResource script = new DefaultScriptResource( ns );
		try {
			is = f.read();
			bsh.eval( TextUtilities.decodeText( is, id ).text, ns );
		}
		finally {
			try {if ( is != null ) is.close();}
			catch ( IOException e ) {}
		}
		return script;
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
	 * 
	 * @throws IllegalArgumentException if the class passed in argument is not an interface
	 */
	public <T> T getInterface( ScriptResource script, Class<T> interfClass ) {
		if ( !interfClass.isInterface() )
			throw new IllegalArgumentException( "Not an interface: " + interfClass.getName() );
		Object result = script.getNamespace().getThis( bsh ).getInterface( interfClass );
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
	public Object invoke( ScriptResource script, String methodName, Object... args )
			throws EvalError, NoSuchMethodException {
		// Wrap args in Beanshell's wrappers
		for ( int i = 0; i < args.length; i++ )
			args[i] = Primitive.wrap( args[i], args[i].getClass() );

		// Construct param signature
		Class[] sig = new Class[args.length];
		for ( int i = 0; i < args.length; i++ )
			sig[i] = args[i].getClass();

		NameSpace ns = script.getNamespace();
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
