import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import com.ftloverdrive.bsh.ClickListener;
import com.ftloverdrive.util.OVDConstants;
import com.ftloverdrive.ui.ShatteredImage;

// Set up layers for later use
Array<String> mainLayerNames = new Array<String>();
mainLayerNames.add( "Background" );
mainLayerNames.add( "Menu" );

for ( String layerName : mainLayerNames ) {
	Group tmpGroup = new Group();
	tmpGroup.setName( layerName );
	stage.getRoot().addActor( tmpGroup );
}

// Set up the background image
TextureAtlas menuAtlas = context.getAssetManager().get( OVDConstants.MENU_ATLAS, TextureAtlas.class );
ShatteredImage bgImage = new ShatteredImage( menuAtlas.findRegions( "main-base2" ), 5 );
bgImage.setFillParent( true );

Group bg = stage.getRoot().findActor( "Background" );
// Groups don't get resized when actors are inserted into them, resize manually
bg.setSize( stage.getWidth(), stage.getHeight() );
bg.addActor( bgImage );

// Set up the main menu buttons
Table menuTable = new Table();
TextureRegionDrawable temp = null;

ButtonStyle btnStylePlay = new ButtonStyle();
btnStylePlay.up = new TextureRegionDrawable( menuAtlas.findRegion( "start-on" ) );
btnStylePlay.disabled = new TextureRegionDrawable( menuAtlas.findRegion( "start-off" ) );
temp = new TextureRegionDrawable( menuAtlas.findRegion( "start-select2" ) );
btnStylePlay.down = temp;
btnStylePlay.over = temp;
Button buttonPlay = new Button( btnStylePlay );

ButtonStyle btnStyleExit = new ButtonStyle();
btnStyleExit.up = new TextureRegionDrawable( menuAtlas.findRegion( "quit-on" ) );
btnStyleExit.disabled = new TextureRegionDrawable( menuAtlas.findRegion( "quit-off" ) );
temp = new TextureRegionDrawable( menuAtlas.findRegion( "quit-select2" ) );
btnStyleExit.down = temp;
btnStyleExit.over = temp;
Button buttonExit = new Button( btnStyleExit );

// Add the buttons to the stage
menuTable.add( buttonPlay );
menuTable.row();
menuTable.add( buttonExit );
menuTable.row();
menuTable.setFillParent( true );
menuTable.align( Align.right );
stage.addActor( menuTable );

buttonPlay.addListener( new ClickListener() {
	public void clicked( InputEvent event, Float x, Float y ) {
		context.getScreenManager().continueToNextScreen();
	}
});

buttonExit.addListener( new ClickListener() {
	public void clicked( InputEvent event, Float x, Float y ) {
		System.exit(0);
	}
});
