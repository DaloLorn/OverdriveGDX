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
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.ftloverdrive.util.TextUtilities;


/**
 * Interprets scripts.
 *
 * @see bsh.Interpreter
 */
public class OVDScriptManager {

	private Interpreter bsh;


	public OVDScriptManager() {
		bsh = new Interpreter();
	}

	/**
	 * Evaluates the script file in a new, child namespace of the global namespace.
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
			try {
				if ( is != null ) is.close();
			}
			catch ( IOException e ) {
			}
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
	 * @param ns
	 *            namespace containing the methods from which to create the object
	 * @param interfClass
	 *            the desired interface
	 * 
	 * @throws IllegalArgumentException
	 *             if the class passed in argument is not an interface
	 */
	public <T> T getInterface( ScriptResource script, Class<T> interfClass ) {
		if ( !interfClass.isInterface() )
			throw new IllegalArgumentException( "Not an interface: " + interfClass.getName() );
		Object result = script.getNamespace().getThis( bsh ).getInterface( interfClass );
		if ( result == null ) return null;
		return interfClass.cast( result );
	}

	/**
	 * Same as getInterface(), but also asserts that the script implements all methods specified by the interface.
	 * Uses GDX reflection.
	 */
	public <T> T getInterfaceAsserted( ScriptResource script, Class<T> interfClass )
			throws EvalError, NoSuchMethodException {
		T result = getInterface( script, interfClass );
		for ( Method m : ClassReflection.getMethods( interfClass ) ) {
			try {
				BshMethod bm = script.getNamespace().getMethod( m.getName(), convertToBsh( m.getParameterTypes() ), false );
				if ( bm == null )
					throw new NoSuchMethodException( toString( m ) );
				if ( !m.getReturnType().isAssignableFrom( convertToBsh( bm.getReturnType() )[0] ) )
					throw new RuntimeException( String.format( "Return types mismatch: %s and %s", m.getReturnType(), bm.getReturnType() ) );
			}
			catch ( UtilEvalError e ) {
				throw e.toEvalError( null, new CallStack( script.getNamespace() ) );
			}
		}
		return result;
	}

	/**
	 * Invokes the specified method with the specified arguments and returns its value.
	 * 
	 * This method wraps arguments with Beanshell's wrappers as needed.
	 * 
	 * @param ns
	 *            namespace containing the method
	 * @param methodName
	 *            name of the invoked method
	 * @param args
	 *            arguments to pass to the method
	 * @return what the invoked method returned
	 * 
	 * @throws NoSuchMethodException
	 *             when the requested method could not be found
	 */
	public Object invoke( ScriptResource script, String methodName, Object... args )
			throws EvalError, NoSuchMethodException {
		// Wrap args in Beanshell's wrappers
		Object[] argsWrapped = new Object[args.length];
		for ( int i = 0; i < args.length; i++ )
			argsWrapped[i] = Primitive.wrap( args[i], args[i].getClass() );

		// Construct param signature
		Class[] sig = new Class[args.length];
		for ( int i = 0; i < argsWrapped.length; i++ )
			sig[i] = argsWrapped[i].getClass();

		NameSpace ns = script.getNamespace();
		BshMethod method = null;
		try {
			method = ns.getMethod( methodName, sig, true );
		}
		catch ( UtilEvalError e ) {
			// UtilEvalErrors are EvalErrors that have no interpreter context
			// They need to be re-thrown and supplied the current context
			throw e.toEvalError( null, new CallStack( ns ) );
		}

		if ( method == null )
			throw new NoSuchMethodException();

		return method.invoke( argsWrapped, bsh );
	}

	/**
	 * Converts classes to BeanShell equivalents, if needed.
	 */
	private Class[] convertToBsh( Class... sig ) {
		Class[] result = new Class[sig.length];

		// Wrap args in Beanshell's wrappers
		for ( int i = 0; i < sig.length; i++ ) {
			if ( sig[i].isPrimitive() )
				result[i] = new Primitive( Primitive.getDefaultValue( sig[i] ) ).getClass();
			else
				result[i] = sig[i];
		}

		return result;
	}

	private String toString( Method m ) {
		StringBuilder buf = new StringBuilder();

		buf.append( m.getReturnType().getSimpleName() );
		buf.append( " " );
		buf.append( m.getName() );
		buf.append( "(" );
		Class[] params = m.getParameterTypes();
		for ( int i = 0; i < params.length; ++i ) {
			if ( i > 0 )
				buf.append( "," );
			buf.append( " " );
			buf.append( params[i].getSimpleName() );
			if ( i + 1 == params.length )
				buf.append( " " );
		}
		buf.append( ")" );

		return buf.toString();
	}
}
