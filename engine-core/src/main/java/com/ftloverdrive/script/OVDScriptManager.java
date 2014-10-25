package com.ftloverdrive.script;

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Logger;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.Primitive;
import bsh.UtilEvalError;

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
	 * Evaluates a script file in the specified namespace (global namespace is still accessible)
	 * 
	 * @see TextUtilities.decodeText(InputStream srcStream, String srcDescription)
	 */
	public void eval( FileHandle f, Map<String, Object> map ) throws EvalError, IOException {
		NameSpace ns = new NameSpace(bsh.getNameSpace(), "nsTemp");

		try {
			for ( Map.Entry<String, Object> entry : map.entrySet() ) {
				ns.setVariable( entry.getKey(),
						Primitive.wrap( entry.getValue(), entry.getValue().getClass() ), false );
			}
		} catch ( UtilEvalError e ) {
			log.error( "Error while evaluating variables.", e );
		}

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
}
