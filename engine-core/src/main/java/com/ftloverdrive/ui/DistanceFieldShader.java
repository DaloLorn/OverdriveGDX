package com.ftloverdrive.ui;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;


/**
 * Shader for distance field fonts, with adjustable smoothing factor.
 * 
 * @see https://github.com/libgdx/libgdx/wiki/Distance-field-fonts
 * @author http://badlogicgames.com/forum/viewtopic.php?f=11&t=9881#p44705
 *
 */
public class DistanceFieldShader extends ShaderProgram {

	private static final String vertex;
	private static final String fragment;

	static {
		StringBuilder buf = new StringBuilder();
		buf.append( "uniform mat4 u_projTrans;" ).append( "\n" );
		buf.append( "\n" );
		buf.append( "attribute vec4 a_position;" ).append( "\n" );
		buf.append( "attribute vec2 a_texCoord0;" ).append( "\n" );
		buf.append( "attribute vec4 a_color;" ).append( "\n" );
		buf.append( "\n" );
		buf.append( "varying vec4 v_color;" ).append( "\n" );
		buf.append( "varying vec2 v_texCoord;" ).append( "\n" );
		buf.append( "\n" );
		buf.append( "void main() {" ).append( "\n" );
		buf.append( "    gl_Position = u_projTrans * a_position;" ).append( "\n" );
		buf.append( "    v_texCoord = a_texCoord0;" ).append( "\n" );
		buf.append( "    v_color = a_color;" ).append( "\n" );
		buf.append( "}" );
		vertex = buf.toString();

		buf.setLength( 0 );

		buf.append( "#ifdef GL_ES" ).append( "\n" );
		buf.append( "precision mediump float;" ).append( "\n" );
		buf.append( "#endif" ).append( "\n" );
		buf.append( "\n" );
		buf.append( "uniform sampler2D u_texture;" ).append( "\n" );
		buf.append( "\n" );
		buf.append( "varying vec4 v_color;" ).append( "\n" );
		buf.append( "varying vec2 v_texCoord;" ).append( "\n" );
		buf.append( "\n" );
		buf.append( "const float smoothing = %s;" ).append( "\n" );
		buf.append( "\n" );
		buf.append( "void main() {" ).append( "\n" );
		buf.append( "    float distance = texture2D(u_texture, v_texCoord).a;" ).append( "\n" );
		buf.append( "    float alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);" ).append( "\n" );
		buf.append( "    gl_FragColor = vec4(v_color.rgb, alpha);" ).append( "\n" );
		buf.append( "}" );
		fragment = buf.toString();

		buf.setLength( 0 );
		buf = null;
	}


	public DistanceFieldShader( float smoothingFactor ) {
		super( vertex, String.format( fragment, smoothingFactor ) );
		if ( !isCompiled() ) {
			throw new RuntimeException( "Failed to compile shader:\n" + getLog() );
		}
	}
}
