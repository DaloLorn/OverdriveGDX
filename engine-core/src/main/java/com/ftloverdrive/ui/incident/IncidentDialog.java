package com.ftloverdrive.ui.incident;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.engine.DisposeListener;
import com.ftloverdrive.event.incident.IncidentTriggerEvent;
import com.ftloverdrive.model.incident.PlotBranch;
import com.ftloverdrive.ui.TiledNinePatchDrawable;
import com.ftloverdrive.util.OVDConstants;


public class IncidentDialog extends Window implements Disposable, EventListener {

	private static final int textChoiceSeparator = 20;

	private static final int frameTopHeight = 28;
	private static final int frameBottomHeight = 27;
	private static final int frameLeftWidth = 27;
	private static final int frameRightWidth = 29;

	private int frameBodyWidth = 0;
	private int frameBodyHeight = 0;

	private Array<DisposeListener> disposeListeners = new Array<DisposeListener>( false, 1 );

	private OverdriveContext context;

	private LabelStyle textStyle;
	private LabelStyle hoverStyle;

	private Label lblIncText;

	// Only 10 keys on the numeric keyboard, so we can only support 10 hotkeyed plot branches
	private int choiceCount = 0;
	private PlotBranch[] choices = new PlotBranch[10];

	private boolean captureInput = false;


	public IncidentDialog( OverdriveContext srcContext ) {
		super( "", srcContext.getAssetManager().get( OVDConstants.INCIDENT_DIALOG_SKIN, Skin.class ) );
		context = srcContext;

		setKeepWithinStage( true );
		setMovable( true );

		context.getAssetManager().load( OVDConstants.PLOT_FONT, BitmapFont.class );
		context.getAssetManager().load( OVDConstants.ROOT_ATLAS, TextureAtlas.class );

		BitmapFont plotFont = context.getAssetManager().get( OVDConstants.PLOT_FONT, BitmapFont.class );
		TextureAtlas rootAtlas = context.getAssetManager().get( OVDConstants.ROOT_ATLAS, TextureAtlas.class );

		textStyle = new LabelStyle( plotFont, new Color( 1f, 1f, 1f, 1f ) );
		hoverStyle = new LabelStyle( plotFont, new Color( 0.95f, 1f, 0.31f, 1f ) );

		TextureRegion windowBase = rootAtlas.findRegion( "window-base" );
		frameBodyWidth = windowBase.getRegionWidth() - ( frameLeftWidth + frameRightWidth );
		frameBodyHeight = windowBase.getRegionHeight() - ( frameTopHeight + frameBottomHeight );
		TiledNinePatchDrawable windowDrawable = new TiledNinePatchDrawable( windowBase,
				frameLeftWidth, frameRightWidth, frameTopHeight, frameBottomHeight );

		WindowStyle style = new WindowStyle( plotFont, new Color( 1f, 1f, 1f, 1f ), windowDrawable );
		setStyle( style );
	}

	/**
	 * If set to true, this window will capture all input until it is dismissed.
	 */
	public void setCaptureInput( boolean block ) {
		captureInput = block;
	}

	public void setIncidentText( String text ) {
		Cell<Label> c = null;
		if ( lblIncText != null ) {
			c = getCell( lblIncText );
			removeActor( lblIncText );
		}

		lblIncText = new Label( text, textStyle );
		lblIncText.setAlignment( Align.top | Align.left, Align.center | Align.left );
		lblIncText.setWrap( true );

		if ( c == null ) {
			row().top().expandX().fillX().spaceBottom( textChoiceSeparator );
			add( lblIncText );
		}
		else {
			c.setActor( lblIncText );
		}
	}

	public void addChoice( final PlotBranch branch ) {
		if ( choiceCount < 10 )
			choices[choiceCount] = branch;
		choiceCount++;

		StringBuilder buf = new StringBuilder();
		buf.append( choiceCount );
		buf.append( ". " );
		buf.append( branch.getText() );

		/**
		 * // TODO: In order to be able to get spoiler text, we need to create
		 * // incidents models for every choice first :/
		 * // Branches also need to reference their incidents by refId (to get the consequences),
		 * // so change the architecture to pre-create all immediate incident & consqs, and then
		 * // add branches after the incident is triggered.
		 * // To achieve this, adding plot branches to incident blueprint could instead store them
		 * // in the blueprint manager, as the incident's ID + choice number, then their models
		 * // created when the incident is triggered.
		 * 
		 * Alternatively, branches can look at blueprint instead of models, but then there's no
		 * way to predict the result of a random consequence (whereas FTL did that [I think so?] )
		 */

		String spoilerText = branch.getSpoilerText( context );
		if ( branch.isSpoilerVisible() && spoilerText != null ) {
			// TODO: AE introduced spoiler boxes, implement them here somehow
			buf.append( "  [ " );
			buf.append( spoilerText );
			buf.append( " ]" );
		}

		final BranchChoice choice = new BranchChoice( buf.toString(), textStyle, hoverStyle );

		addListener( new InputListener() {

			public boolean touchDown( InputEvent e, float x, float y, int pointer, int button ) {
				if ( e.getTarget() == choice && e.getButton() == Buttons.LEFT ) {
					choiceSelected( context, branch );
					return true;
				}
				return false;
			}
		} );

		row().top().left().width( choice.getWidth() );
		add( choice );
	}

	/**
	 * Filler to take up the bottom part of the dialog, and push the incident text and choices up.
	 */
	public void addFiller() {
		final Label filler = new Label( "", textStyle );
		row().top().expand().fill();
		add( filler );
	}

	/**
	 * Computes the preferred width of this window, modifying the width passed in argument so that the
	 * background image is tiled correctly.
	 * 
	 * The value returned is always greater than or equal to the one passed.
	 */
	public float computePreferredWidth( int w ) {
		int bw = w - ( frameLeftWidth + frameRightWidth );
		if ( bw % frameBodyWidth != 0 )
			w += frameBodyWidth - bw % frameBodyWidth;
		return w;
	}

	/**
	 * Computes the preferred height of this window, accounting for all actors currently contained within it.
	 * This function assumes actors are lined in rows one after another (one actor per column), and that they
	 * accurately return their own height.
	 * 
	 * Using the value returned by this function as height for the dialog will also guarantee that the
	 * background image is tiled correctly.
	 */
	public float computePreferredHeight() {
		float h = textChoiceSeparator;
		for ( Actor a : getChildren() )
			h += a.getHeight();
		h += lblIncText.getHeight();
		if ( h % frameBodyHeight != 0 )
			h += frameBodyHeight - h % frameBodyHeight;
		h += frameTopHeight + frameBottomHeight;
		return h;
	}

	@Override
	public void dispose() {
		context.getAssetManager().unload( OVDConstants.PLOT_FONT );
		context.getAssetManager().unload( OVDConstants.ROOT_ATLAS );

		Stage stage = getStage();
		stage.getRoot().removeActor( this );
		stage.removeListener( this );

		for ( DisposeListener listener : disposeListeners ) {
			listener.objectDisposed( context, this );
		}
	}

	public void addDisposeListener( DisposeListener listener ) {
		disposeListeners.add( listener );
	}

	@Override
	public boolean handle( Event event ) {
		if ( event instanceof InputEvent ) {
			InputEvent e = (InputEvent)event;
			if ( e.getType() == Type.keyDown && e.getTarget() == this ) {
				int choiceNumber = e.getKeyCode();

				if ( choiceNumber >= Keys.NUM_0 && choiceNumber <= Keys.NUM_9 ) {
					choiceNumber -= Keys.NUM_0;
					if ( choiceNumber == 0 )
						choiceNumber = 10;
					if ( choiceNumber > choiceCount )
						return captureInput;

					choiceSelected( context, choices[choiceNumber - 1] );
					return true;
				}
			}

			return captureInput;
		}
		return false;
	}

	/*
	 * ==========================================================================
	 */

	private void choiceSelected( OverdriveContext context, PlotBranch branch ) {
		String incidentId = branch.getIncidentId();
		if ( incidentId == null ) {
			// TODO: Send notification that incident ended, to unpause the game?
		}
		else {
			int incRefId = context.getBlueprintManager().getBlueprint( branch.getIncidentId() ).construct( context );

			// TODO: Create and add branches

			IncidentTriggerEvent incTriggerE = Pools.get( IncidentTriggerEvent.class ).obtain();
			incTriggerE.init( incRefId );
			context.getScreenEventManager().postDelayedEvent( incTriggerE );
		}

		dispose();
	}
}
